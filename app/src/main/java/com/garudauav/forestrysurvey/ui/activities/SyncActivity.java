package com.garudauav.forestrysurvey.ui.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.garudauav.forestrysurvey.R;
import com.garudauav.forestrysurvey.adapters.recyclerview_adapters.HistoryItemAdapter;
import com.garudauav.forestrysurvey.adapters.recyclerview_adapters.SyncItemAdapter;
import com.garudauav.forestrysurvey.databinding.ActivitySyncBinding;
import com.garudauav.forestrysurvey.databinding.CantUploadDataDialogBinding;
import com.garudauav.forestrysurvey.databinding.CodeNotFoundDialogBinding;
import com.garudauav.forestrysurvey.databinding.CodeUsedDialogBinding;
import com.garudauav.forestrysurvey.databinding.OfflineDialogBinding;
import com.garudauav.forestrysurvey.databinding.UploadingDataProgressBarBinding;
import com.garudauav.forestrysurvey.db.dao.SpeciesDataDao;
import com.garudauav.forestrysurvey.db.dao.TreeDataDao;
import com.garudauav.forestrysurvey.db.database.ForestryDataBase;
import com.garudauav.forestrysurvey.db.retry_module.RetryTable;
import com.garudauav.forestrysurvey.db.retry_module.RetryTableDao;
import com.garudauav.forestrysurvey.models.BatchData;
import com.garudauav.forestrysurvey.models.SyncData;
import com.garudauav.forestrysurvey.models.SyncHistory;
import com.garudauav.forestrysurvey.models.TreeData;
import com.garudauav.forestrysurvey.models.request_models.UserIdRequest;
import com.garudauav.forestrysurvey.models.response_models.UploadSpeciesDataResponse;
import com.garudauav.forestrysurvey.network.ApiService;
import com.garudauav.forestrysurvey.network.RetrofitClient;
import com.garudauav.forestrysurvey.network.SealedNetworkResult;
import com.garudauav.forestrysurvey.repository.SyncRepository;
import com.garudauav.forestrysurvey.utils.CommonFunctions;
import com.garudauav.forestrysurvey.utils.CustomTypefaceSpan;
import com.garudauav.forestrysurvey.utils.PrefManager;
import com.garudauav.forestrysurvey.viewmodelfactories.SyncViewModelFactory;
import com.garudauav.forestrysurvey.viewmodels.SyncViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SyncActivity extends AppCompatActivity implements SyncItemAdapter.CheckTreeData, HistoryItemAdapter.RetryData {
    private ActivitySyncBinding binding;
    private SyncViewModel viewModel;
    private PrefManager prefManager;
    private SyncRepository repository;
    private boolean isUploadingExpand = true;
    private boolean isHistoryExpand = true;
    private File file1, file2;
    private AlertDialog alertDialog;
    private List<TreeData> treeDataTemp;

    private BatchData batchDataObj;
    private SyncHistory syncHistoryObj;
    private HistoryItemAdapter historyItemAdapter;


    private ForestryDataBase dataBase;

    public OfflineDialogBinding offlineDialogBinding;
    private TreeDataDao treeDataDao;

    private RetryTableDao retryTableDao;
    private ExecutorService executorService;
    private ProgressDialog progressDialog;
    private UploadingDataProgressBarBinding uploadBinding;
    private AlertDialog uploadDialog;
    private List<SyncHistory> syncHistoryList;
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySyncBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dataBase = ForestryDataBase.getInstance(getApplicationContext());
        treeDataDao = dataBase.treeDataDao();
        retryTableDao = dataBase.retryTableDao();
        executorService = Executors.newSingleThreadExecutor();
        treeDataTemp = new ArrayList<>();

        prefManager = new PrefManager(this);
        repository = new SyncRepository(getApplication());

        progressDialog = new ProgressDialog(this);

        progressDialog.setMessage("Please wait... ");

        progressDialog.setCancelable(false);

        viewModel = new ViewModelProvider(this, new SyncViewModelFactory(prefManager, repository)).get(SyncViewModel.class);
        syncHistoryList = new ArrayList<>();
        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.backArrowImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        binding.expandUploading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUploadingExpand) {
                    binding.uploadingCard.setVisibility(View.GONE);
                    binding.expandUploading.setBackgroundResource(R.drawable.ic_expand);
                    isUploadingExpand = false;

                } else {
                    binding.uploadingCard.setVisibility(View.VISIBLE);
                    binding.expandUploading.setBackgroundResource(R.drawable.ic_collapse);
                    isUploadingExpand = true;
                }
            }
        });


        binding.expandHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHistoryExpand) {
                    binding.historyCard.setVisibility(View.GONE);
                    binding.expandHistory.setBackgroundResource(R.drawable.ic_expand);
                    isHistoryExpand = false;

                } else {
                    binding.historyCard.setVisibility(View.VISIBLE);
                    binding.expandHistory.setBackgroundResource(R.drawable.ic_collapse);
                    isHistoryExpand = true;
                }
            }
        });

        binding.syncRecycler.setLayoutManager(new LinearLayoutManager(this));
        viewModel.treeDataList.observe(this, new Observer<List<TreeData>>() {
            @Override
            public void onChanged(List<TreeData> treeData) {
                if (treeData != null) {

                    if (treeData.size() == 0) {

                        binding.uploadingStatus.setVisibility(View.GONE);
                        binding.uploadingCard.setVisibility(View.GONE);


                    }
                    viewModel.processTreeDataList(treeData);


                } else {
                    binding.uploadingStatus.setVisibility(View.VISIBLE);
                    binding.uploadingCard.setVisibility(View.VISIBLE);


                }
            }
        });

        viewModel.batchDataListLiveData.observe(this, new Observer<List<BatchData>>() {
            @Override
            public void onChanged(List<BatchData> batchData) {
                if (batchData != null && batchData.size() > 0) {
                    binding.noDataLayout.setVisibility(View.GONE);
                    binding.uploadingStatus.setVisibility(View.VISIBLE);
                    binding.uploadingCard.setVisibility(View.VISIBLE);

                    binding.syncRecycler.setAdapter(new SyncItemAdapter(batchData, SyncActivity.this));
                } else {
                    binding.uploadingStatus.setVisibility(View.GONE);
                    binding.uploadingCard.setVisibility(View.GONE);
                    if (!CommonFunctions.isNetworkConnectionAvailable(SyncActivity.this)) {
                        binding.noDataLayout.setVisibility(View.VISIBLE);
                        binding.mainLayout.setVisibility(View.GONE);
                    }
                }

            }
        });

        //History Recycler setup
        if (CommonFunctions.isNetworkConnectionAvailable(SyncActivity.this)) {
            viewModel.getExportData(new UserIdRequest( prefManager.getUserId()));

        } else {

            offLineDialog();
        }
        binding.histroyRecycler.setLayoutManager(new LinearLayoutManager(this));

        viewModel.syncHistoryList.observe(this, new Observer<SealedNetworkResult<List<SyncHistory>>>() {
            @Override
            public void onChanged(SealedNetworkResult<List<SyncHistory>> result) {
                syncHistoryList.clear();
                binding.histroyRecycler.setVisibility(View.GONE);
                for (RetryTable retryTable : viewModel.retryTableList) {
                    syncHistoryList.add(new SyncHistory(retryTable.noOfTree, retryTable.getDate().toString(), true, retryTable.id));
                }
                //when api hit
                if (result instanceof SealedNetworkResult.Success) {

                    try {
                        binding.loader.setVisibility(View.GONE);


                        syncHistoryList.addAll(((SealedNetworkResult.Success<List<SyncHistory>>) result).getData());
                        if (syncHistoryList.size() > 0) {
                            historyItemAdapter = new HistoryItemAdapter(syncHistoryList, SyncActivity.this);
                            binding.histroyRecycler.setAdapter(historyItemAdapter);
                            // binding.mainLayout.setVisibility(View.VISIBLE);
                            binding.historyCard.setVisibility(View.VISIBLE);
                            binding.historyStatus.setVisibility(View.VISIBLE);
                            binding.histroyRecycler.setVisibility(View.VISIBLE);
                        } else {
                            List<BatchData> batchDataList = viewModel.batchDataListLiveData.getValue();
                            if (batchDataList == null || batchDataList.size() == 0) {
                                binding.mainLayout.setVisibility(View.GONE);
                                binding.noDataLayout.setVisibility(View.VISIBLE);

                            }
                            binding.historyStatus.setVisibility(View.GONE);

                            binding.historyCard.setVisibility(View.GONE);
                            binding.histroyRecycler.setVisibility(View.GONE);
                        }

                    } catch (Exception e) {
                        List<SyncData> syncDataList = viewModel.syncDataListLiveData.getValue();
                        if (syncDataList == null || syncDataList.size() == 0) {
                            binding.mainLayout.setVisibility(View.GONE);
                            binding.noDataLayout.setVisibility(View.VISIBLE);

                        }
                        binding.historyCard.setVisibility(View.GONE);
                        binding.histroyRecycler.setVisibility(View.GONE);
                        binding.loader.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                } else if (result instanceof SealedNetworkResult.Error) {
                    List<BatchData> batchDataList = viewModel.batchDataListLiveData.getValue();
                    if (batchDataList == null || batchDataList.size() == 0) {
                        binding.mainLayout.setVisibility(View.GONE);
                        binding.noDataLayout.setVisibility(View.VISIBLE);

                    }
                    binding.historyCard.setVisibility(View.GONE);
                    binding.histroyRecycler.setVisibility(View.GONE);
                    binding.historyStatus.setVisibility(View.GONE);
                    binding.loader.setVisibility(View.GONE);

                } else if (result instanceof SealedNetworkResult.Loading) {
                    binding.loader.setVisibility(View.VISIBLE);

                } else {
                    Toast.makeText(SyncActivity.this, "another case", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }


    void writeByte1(byte[] bytes) {

        // Try block to check for exceptions
        try {

            // Initialize a pointer in file
            // using OutputStream
            file1 = new File(this.getCacheDir(), "photo1");
            OutputStream os = new FileOutputStream(file1);

            // Starting writing the bytes in it
            os.write(bytes);


            // Close the file connections
            os.close();
        }


        // Catch block to handle the exceptions
        catch (Exception e) {
            // Display exception on console
            System.out.println("Exception:_____ " + e);
        }
    }

    void writeByte2(byte[] bytes) {

        // Try block to check for exceptions
        try {

            // Initialize a pointer in file
            // using OutputStream

            file2 = new File(this.getCacheDir(), "photo2");

            OutputStream os = new FileOutputStream(file2);

            // Starting writing the bytes in it
            os.write(bytes);


            // Close the file connections
            os.close();
        }

        // Catch block to handle the exceptions
        catch (Exception e) {

            // Display exception on console
            System.out.println("Exception: " + e);
        }
    }

    private void getDataFromDb(BatchData batchData) {

        executorService.execute(new Runnable() {
            @Override
            public void run() {

                treeDataTemp = treeDataDao.getTreeDataByIds(batchData.getTreeDataIds());

                if (treeDataTemp.size() != 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showUploading();
                            if (!uploadDialog.isShowing()) {
                                uploadDialog.show();
                            }
                        }
                    });
                    writeByte1(treeDataTemp.get(i).photo1);
                    writeByte2(treeDataTemp.get(i).photo2);
                    String datecheck = dateConversion(treeDataTemp.get(i).date.toString());
                    uploadSpeciesData(file1, file2, treeDataTemp.get(i).height, treeDataTemp.get(i).heightType,
                            treeDataTemp.get(i).girthType, treeDataTemp.get(i).girth,
                            treeDataTemp.get(i).speciesId, treeDataTemp.get(i).latitude + "," + treeDataTemp.get(i).longitude,
                            datecheck, treeDataTemp.get(i).noOfTree, treeDataTemp.get(i).blockId, treeDataTemp.get(i).districtId, batchData.getBatchId(), batchData.getTreeDataIds().size());
                }
            }
        });

    }

    public String dateConversion(String inputDate) {
        try {
            // Create a SimpleDateFormat object with the input date format
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy", Locale.ENGLISH);

            // Parse the input date string
            Date date = inputDateFormat.parse(inputDate);

            // Create a SimpleDateFormat object with the desired output format
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z", Locale.ENGLISH);

            // Set the timezone
            outputDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0530"));

            // Format the date into the desired output format
            String formattedDate = outputDateFormat.format(date);
            return formattedDate;

            // System.out.println("Formatted Date: " + formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }

    }

    private void uploadSpeciesDataRetry(File file1, File file2, Float height, int heightType, int girthType, Float girth, String species, String coordinates, String date, Integer noOfTree, String blockId, String district, String batchNumber, int batchCount, int treeId) {


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showUploading();

                uploadDialog.show();


            }
        });

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);


        RequestBody requestFile1 =

                RequestBody.create(MediaType.parse("multipart/form-data"), file1);

        RequestBody requestFile2 =

                RequestBody.create(MediaType.parse("multipart/form-data"), file2);

        RequestBody SpeciesMasterId =

                RequestBody.create(MediaType.parse("multipart/form-data"), species /*"950175d8-6a61-4a1c-a5a2-cc7b58e3b04a"*/);

        RequestBody Coordinates =

                RequestBody.create(MediaType.parse("multipart/form-data"), coordinates /*"28.56789,77.345678"*/);

        if (height == null) {
            height = 0f;
        }
        RequestBody Height =

                RequestBody.create(MediaType.parse("multipart/form-data"), height + ""/*"30.22"*/);


        RequestBody CapturedDate =

                RequestBody.create(MediaType.parse("multipart/form-data"), date + "" /*"2024-01-26 14:34:57.559 +0530"*/);

        RequestBody UnitHeight =

                RequestBody.create(MediaType.parse("multipart/form-data"), heightType + "");

        RequestBody NumberOftree =

                RequestBody.create(MediaType.parse("multipart/form-data"), noOfTree + "");

        RequestBody Girth =

                RequestBody.create(MediaType.parse("multipart/form-data"), girth + "");

        RequestBody UnitGirth =

                RequestBody.create(MediaType.parse("multipart/form-data"), girthType + "");

        RequestBody BlockId =

                RequestBody.create(MediaType.parse("multipart/form-data"), blockId/*"9be0b5e8-a6b9-4062-86e3-bb2ca03b4a4a"*/);

        RequestBody District =

                RequestBody.create(MediaType.parse("multipart/form-data"), district /*"ec4d0c68-3861-4ba5-b8b9-41e92b70b9d1"*/);

        String userIdCheck = prefManager.getUserId();
        RequestBody UserId =

                RequestBody.create(MediaType.parse("multipart/form-data"), prefManager.getUserId()/*"cd0114ee-0b73-417b-ba88-8df275757368"*/);

        String codeCheck = prefManager.getUserCode();

        RequestBody OtpCode =

                RequestBody.create(MediaType.parse("multipart/form-data"), prefManager.getUserCode());

        RequestBody BatchNumber =

                RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(batchNumber));
        RequestBody BatchRecordCount =

                RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(batchCount));


        MultipartBody.Part img1 =

                MultipartBody.Part.createFormData("Img1", this.file1.getName(), requestFile1);

        MultipartBody.Part img2 =

                MultipartBody.Part.createFormData("Img2", this.file2.getName(), requestFile2);


        Call<UploadSpeciesDataResponse> call = apiService.uploadSpeciesData1(img1, img2,

                SpeciesMasterId, Coordinates, Height, CapturedDate, UnitHeight, NumberOftree,

                Girth, UnitGirth, BlockId, District, UserId, OtpCode, BatchNumber, BatchRecordCount);

        call.enqueue(new Callback<UploadSpeciesDataResponse>() {

            @Override

            public void onResponse(Call<UploadSpeciesDataResponse> call, Response<UploadSpeciesDataResponse> response) {


                if (response.code() == 200 && response.body() != null && response.body().isSuccess()) {

                    i++;
                    uploadBinding.progressBar.setProgress(100);


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            uploadDialog.dismiss();
                        }
                    });
                    i = 0;

                    //change
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                syncHistoryList.remove(syncHistoryObj);
                                retryTableDao.deleteTreeDataById(treeId);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    //prefManager.setUserCode("");


                    /*                       */
                    prefManager.setUserId(response.body().getData().getUserId());


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                          //  Toast.makeText(SyncActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
                } else if (response.code() == 200 && response.body() != null && response.body().getData().getErrorType().equalsIgnoreCase("UserNotExist")) {
                    uploadDialog.dismiss();
                    //  progressDialog.dismiss();
                    //this code run when user not found
                   inCorrectCode();

                   // codeNotFoundDialog();

                } else if (response.code() == 200 && response.body() != null && response.body().getData().getErrorType().equalsIgnoreCase("OtpUsed")) {
                    //this code run when user code already used
                    uploadDialog.dismiss();
                    //    progressDialog.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(SyncActivity.this);
                    builder.setTitle("Code Used")
                            .setMessage("This code has been used by you\n please login via mail Id")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    prefManager.clearPreferences();
                                    startActivity(new Intent(SyncActivity.this, PreLoginActivity.class));
                                }
                            });
                    alertDialog = builder.create();
                    alertDialog.show();


                } else if (response.code() == 400) {

                    uploadDialog.dismiss();
                    Toast.makeText(SyncActivity.this, "Something went wrong..", Toast.LENGTH_SHORT).show();




                } else {
                    uploadDialog.dismiss();
                    // progressDialog.dismiss();
                    Toast.makeText(SyncActivity.this, "Something went wrong..", Toast.LENGTH_SHORT).show();
                }


            }

            @Override

            public void onFailure(Call<UploadSpeciesDataResponse> call, Throwable t) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //  progressDialog.dismiss();
                        uploadDialog.dismiss();
                        Toast.makeText(SyncActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            }

        });

    }

    private void uploadSpeciesData(File file1, File file2, Float height, int heightType, int girthType, Float girth, String species, String coordinates, String date, Integer noOfTree, String blockId, String district, String batchNumber, int batchCount) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!uploadDialog.isShowing()) {
                    uploadDialog.show();
                }

            }
        });


        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);


        RequestBody requestFile1 = RequestBody.create(MediaType.parse("multipart/form-data"), file1);

        RequestBody requestFile2 = RequestBody.create(MediaType.parse("multipart/form-data"), file2);

        RequestBody SpeciesMasterId = RequestBody.create(MediaType.parse("multipart/form-data"), species);

        RequestBody Coordinates = RequestBody.create(MediaType.parse("multipart/form-data"), coordinates);

        if (height == null) {
            height = 0f;
        }
        RequestBody Height = RequestBody.create(MediaType.parse("multipart/form-data"), height + "");


        RequestBody CapturedDate = RequestBody.create(MediaType.parse("multipart/form-data"), date + "" );

        RequestBody UnitHeight = RequestBody.create(MediaType.parse("multipart/form-data"), heightType + "");

        RequestBody NumberOftree = RequestBody.create(MediaType.parse("multipart/form-data"), noOfTree + "");

        RequestBody Girth = RequestBody.create(MediaType.parse("multipart/form-data"), girth + "");

        RequestBody UnitGirth = RequestBody.create(MediaType.parse("multipart/form-data"), girthType + "");

        RequestBody Blockid = RequestBody.create(MediaType.parse("multipart/form-data"), blockId);

        RequestBody District = RequestBody.create(MediaType.parse("multipart/form-data"), district );

        String userIdCheck = prefManager.getUserId();
        RequestBody UserId = RequestBody.create(MediaType.parse("multipart/form-data"), prefManager.getUserId());

        String codeCheck = prefManager.getUserCode();

        RequestBody OtpCode = RequestBody.create(MediaType.parse("multipart/form-data"), prefManager.getUserCode());

        RequestBody BatchNumber = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(batchNumber));
        RequestBody BatchRecordCount = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(batchCount));


        MultipartBody.Part img1 = MultipartBody.Part.createFormData("Img1", this.file1.getName(), requestFile1);

        MultipartBody.Part img2 = MultipartBody.Part.createFormData("Img2", this.file2.getName(), requestFile2);


        Call<UploadSpeciesDataResponse> call = apiService.uploadSpeciesData1(img1, img2,

                SpeciesMasterId, Coordinates, Height, CapturedDate, UnitHeight, NumberOftree,

                Girth, UnitGirth, Blockid, District, UserId, OtpCode, BatchNumber, BatchRecordCount);
        call.enqueue(new Callback<UploadSpeciesDataResponse>() {

            @Override

            public void onResponse(Call<UploadSpeciesDataResponse> call, Response<UploadSpeciesDataResponse> response) {


                if (response.code() == 200 && response.body() != null && response.body().isSuccess()) {

                    i++;
                    uploadBinding.progressBar.setProgress(calculatepercentage());
                    uploadBinding.percent.setText(calculatepercentage() + "%");

                    prefManager.setUserId(response.body().getData().getUserId());

                    try {
                        if (i < treeDataTemp.size()) {
                            writeByte1(treeDataTemp.get(i).photo1);
                            writeByte2(treeDataTemp.get(i).photo2);
                            String datecheck = dateConversion(treeDataTemp.get(i).date.toString());


                            uploadSpeciesData(file1, file2, treeDataTemp.get(i).height, treeDataTemp.get(i).heightType,
                                    treeDataTemp.get(i).girthType, treeDataTemp.get(i).girth,
                                    treeDataTemp.get(i).speciesId, treeDataTemp.get(i).latitude + "," + treeDataTemp.get(i).longitude,
                                    datecheck, treeDataTemp.get(i).noOfTree, treeDataTemp.get(i).blockId, treeDataTemp.get(i).districtId, batchDataObj.getBatchId(), batchDataObj.getTreeDataIds().size());
                        } else {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    uploadDialog.dismiss();
                                    viewModel.getExportData(new UserIdRequest( prefManager.getUserId()));
                                }
                            });
                            i = 0;


                            //change
                            executorService.execute(new Runnable() {
                                @Override
                                public void run() {
                                    //  treeDataDao.clearTreeData();
                                    treeDataDao.deleteTreeDataByIds(batchDataObj.getTreeDataIds());
                                }
                            });
                           // prefManager.setUserCode("");

                        }
                    } catch (IndexOutOfBoundsException ei) {
                        ei.getMessage().toString();
                    } catch (Exception e) {

                        e.printStackTrace();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SyncActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
                } else if (response.code() == 200 && response.body() != null && response.body().getData().getErrorType().equalsIgnoreCase("UserNotExist")) {
                    uploadDialog.dismiss();
                    //  progressDialog.dismiss();
                    //this code run when user not found
                    inCorrectCode();
                   // codeNotFoundDialog();

                } else if (response.code() == 200 && response.body() != null && response.body().getData().getErrorType().equalsIgnoreCase("OtpUsed")) {
                    //this code run when user code already used
                    uploadDialog.dismiss();
                    //    progressDialog.dismiss();
                    CodeUsedDialogBinding dialogBinding = CodeUsedDialogBinding.inflate(LayoutInflater.from(SyncActivity.this));

                    AlertDialog.Builder builder = new AlertDialog.Builder(SyncActivity.this, R.style.MyDialogStyle);
                    builder.setView(dialogBinding.getRoot());
                    builder.setCancelable(false);

                    dialogBinding.continueBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            prefManager.clearPreferences();

                            executorService.execute(new Runnable() {
                                @Override
                                public void run() {
                                    SpeciesDataDao speciesDataDao;
                                    ForestryDataBase dataBase = ForestryDataBase.getInstance(getApplicationContext());
                                    speciesDataDao = dataBase.speciesDataDao();
                                    speciesDataDao.clearSpeciesData();
                                }
                            });
                            startActivity(new Intent(SyncActivity.this, PreLoginActivity.class));

                        }
                    });
                    alertDialog = builder.create();
                    alertDialog.show();


                } else if (response.code() == 400) {

                    try {

                        executorService.execute(new Runnable() {
                            @Override
                            public void run() {
                            }
                        });

                        i++;

                        if (i < treeDataTemp.size()) {
                            writeByte1(treeDataTemp.get(i).photo1);
                            writeByte2(treeDataTemp.get(i).photo2);
                            String datecheck = dateConversion(treeDataTemp.get(i).date.toString());


                            uploadSpeciesData(file1, file2, treeDataTemp.get(i).height, treeDataTemp.get(i).heightType,
                                    treeDataTemp.get(i).girthType, treeDataTemp.get(i).girth,
                                    treeDataTemp.get(i).speciesId, treeDataTemp.get(i).latitude + "," + treeDataTemp.get(i).longitude,
                                    datecheck, treeDataTemp.get(i).noOfTree, treeDataTemp.get(i).blockId, treeDataTemp.get(i).districtId, batchDataObj.getBatchId(), batchDataObj.getTreeDataIds().size());


                        } else {


                            uploadDialog.dismiss();
                            i = 0;
                            executorService.execute(new Runnable() {
                                @Override
                                public void run() {
                                    treeDataDao.deleteTreeDataByIds(batchDataObj.getTreeDataIds());

                                }
                            });

                        }
                    } catch (Exception e) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                uploadDialog.dismiss();
                                Toast.makeText(SyncActivity.this, "Something went wrong..", Toast.LENGTH_SHORT).show();

                            }
                        });


                    }


                } else {
                    uploadDialog.dismiss();
                    Toast.makeText(SyncActivity.this, "Something went wrong..", Toast.LENGTH_SHORT).show();
                }


            }

            @Override

            public void onFailure(Call<UploadSpeciesDataResponse> call, Throwable t) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        uploadDialog.dismiss();
                        Toast.makeText(SyncActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            }

        });

    }

    public void inCorrectCode(){

        CantUploadDataDialogBinding dialogBinding=CantUploadDataDialogBinding.inflate(LayoutInflater.from(this));

        AlertDialog.Builder builder=new AlertDialog.Builder(SyncActivity.this,R.style.MyDialogStyle);

        builder.setView(dialogBinding.getRoot());

        String userCode = prefManager.getUserCode();

        String warningMessage = "The entered code " + userCode + " is not valid, please contact to Web Administrator.";

        SpannableString spannableString = new SpannableString(warningMessage);

        int start = warningMessage.indexOf(userCode);

        int end = start + userCode.length();

        Typeface customFont = ResourcesCompat.getFont(this, R.font.montserrat_bold);

// Apply bold style to the userCode part

        spannableString.setSpan(new CustomTypefaceSpan("",customFont), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        dialogBinding.warningText.setText(spannableString);

// dialogBinding.warningText.setText("The entered code "+prefManager.getUserCode()+" is not correct,\nPlease contact to Web Administrator");

        dialogBinding.okBtn.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                alertDialog.dismiss();

            }

        });

        alertDialog = builder.create();

        alertDialog.show();

    }


    public void codeNotFoundDialog() {
        CodeNotFoundDialogBinding dialogBinding = CodeNotFoundDialogBinding.inflate(LayoutInflater.from(this));

        AlertDialog.Builder builder = new AlertDialog.Builder(SyncActivity.this, R.style.MyDialogStyle);
        builder.setView(dialogBinding.getRoot());
        dialogBinding.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        dialogBinding.clearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBinding.userCode.setText("");
            }
        });
        dialogBinding.okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dialogBinding.userCode.getText().toString().isEmpty()) {
                    dialogBinding.userCode.setError("Please enter code");

                } else if (dialogBinding.userCode.getText().toString().length() < 8) {
                    dialogBinding.userCode.setError("Please enter valid code");

                } else {
                    String input = dialogBinding.userCode.getText().toString();
                    if (!input.matches("^(?=.*[0-9])(?=.*[a-zA-Z])[a-zA-Z0-9]+$")) {
                        dialogBinding.userCode.setError("Please enter valid code.");
                    } else {
                        prefManager.setUserCode(input);
                        alertDialog.dismiss();
                        getDataFromDb(batchDataObj);
                    }


                }
            }
        });

        dialogBinding.userCode.setText(prefManager.getUserCode());


        alertDialog = builder.create();
        alertDialog.show();

    }

    public void offLineDialog() {
        offlineDialogBinding = OfflineDialogBinding.inflate(getLayoutInflater());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(offlineDialogBinding.getRoot());
        AlertDialog alertDialog = builder.create();

        offlineDialogBinding.continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

            }
        });
        alertDialog.show();

    }


    @Override
    public void onItemClick(SyncHistory syncHistory) {
        if (CommonFunctions.isNetworkConnectionAvailable(this)) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    syncHistoryObj = syncHistory;
                    RetryTable retryTableItem = retryTableDao.getTreeDataById(syncHistory.getTreeId());
                    writeByte1(retryTableItem.photo1);
                    writeByte2(retryTableItem.photo2);

                    String datecheck = dateConversion(retryTableItem.date.toString());


                    uploadSpeciesDataRetry(file1, file2, retryTableItem.height, retryTableItem.heightType,
                            retryTableItem.girthType, retryTableItem.girth,
                            retryTableItem.speciesId, retryTableItem.latitude + "," + retryTableItem.longitude,
                            datecheck, retryTableItem.noOfTree, retryTableItem.blockId, retryTableItem.districtId, retryTableItem.getBatchId(), retryTableItem.getBatchCount(), syncHistory.getTreeId());

                }
            });
        } else {
            Toast.makeText(this, "No internet..", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onItemChecked(BatchData batchData) {

        if (CommonFunctions.isNetworkConnectionAvailable(this)) {
            if (batchData != null && batchData.getTreeDataIds().size() > 0) {
                batchDataObj = batchData;

                getDataFromDb(batchData);

            } else {
                Toast.makeText(SyncActivity.this, "Something went wrong..", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No internet..", Toast.LENGTH_SHORT).show();
        }
    }

    private int calculatepercentage() {
        int treeDataTempSize = treeDataTemp.size();
        int value = 0;
        if (treeDataTempSize != 0) {
            value = (i * 100) / treeDataTemp.size();
        }


        return value;

    }

    private void showUploading() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SyncActivity.this);
        uploadBinding = UploadingDataProgressBarBinding.inflate(getLayoutInflater());
        builder.setView(uploadBinding.getRoot());
        builder.setCancelable(false);
        uploadBinding.progressBar.setProgress(calculatepercentage());
        uploadBinding.percent.setText(calculatepercentage() + "%");
        uploadDialog = builder.create();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }

}


