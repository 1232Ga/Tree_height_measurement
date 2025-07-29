package com.garudauav.forestrysurvey.ui.fragments.dashboard_module;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.garudauav.forestrysurvey.adapters.recyclerview_adapters.SpeciesAdapter;
import com.garudauav.forestrysurvey.adapters.recyclerview_adapters.SpeciesCheckBoxAdapter;
import com.garudauav.forestrysurvey.databinding.ExportSpeciesDialogBinding;
import com.garudauav.forestrysurvey.databinding.ExportSpeciesSpinnerDialogBinding;
import com.garudauav.forestrysurvey.databinding.FragmentCustomDataBinding;
import com.garudauav.forestrysurvey.models.DistrictMaster;
import com.garudauav.forestrysurvey.models.RFMaster;
import com.garudauav.forestrysurvey.models.RFMaster2;
import com.garudauav.forestrysurvey.models.SpeciesData;
import com.garudauav.forestrysurvey.models.SpeciesMaster;
import com.garudauav.forestrysurvey.models.TreeData;
import com.garudauav.forestrysurvey.models.request_models.DashboardDataRequest;
import com.garudauav.forestrysurvey.models.request_models.ExportListRequest;
import com.garudauav.forestrysurvey.models.response_models.ExportData;
import com.garudauav.forestrysurvey.network.SealedNetworkResult;
import com.garudauav.forestrysurvey.pagination.PaginationScrollListener;
import com.garudauav.forestrysurvey.repository.DashBoardRepository;
import com.garudauav.forestrysurvey.utils.CommonFunctions;
import com.garudauav.forestrysurvey.utils.IntegerValueFormatter;
import com.garudauav.forestrysurvey.utils.PrefManager;
import com.garudauav.forestrysurvey.utils.callback_interface.DatePickerCallback;
import com.garudauav.forestrysurvey.viewmodelfactories.DashboardViewModelFactory;
import com.garudauav.forestrysurvey.viewmodels.DashboardViewModel;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

public class CustomDataFragment extends Fragment implements SpeciesCheckBoxAdapter.CheckSpecies, SpeciesAdapter.FilterResultsListener {
    private PieData pieData;
    private PieDataSet pieDataSet;
    private boolean isSelectAll = false;
    private static final int PAGE_START = 1;
    private int TOTAL_PAGES = 0;
    private static final int PAGE_SIZE = 50;
    private int currentPage = PAGE_START;

    private DashboardViewModel viewModel;
    private PrefManager prefManager;
    private SpeciesAdapter adapter;
    private DashBoardRepository repository;
    private Date selectedDateInUTC;

    private FragmentCustomDataBinding binding;
    private ExportSpeciesDialogBinding exportDialogBinding;
    private ExportSpeciesSpinnerDialogBinding speciesDialogBinding;
    private AlertDialog exportDialog;


    private String district = "";
    private String fromDateExp = "";
    private String toDateExp = "";
    private String fromdate = "", todate = "";
    private String rfName = "";
    private ProgressDialog progressDialog;

    public CustomDataFragment() {
        // Required empty public constructor
    }

    private boolean isFromPagination = false;
    private String TAG = CustomDataFragment.class.getName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCustomDataBinding.inflate(inflater, container, false);


        prefManager = new PrefManager(requireActivity());
        repository = new DashBoardRepository(requireActivity().getApplication(), prefManager);
        viewModel = new ViewModelProvider(requireActivity(), new DashboardViewModelFactory(prefManager, repository)).get(DashboardViewModel.class);


        progressDialog = new ProgressDialog(requireActivity());

        isFromPagination = false;
        getDashboardDataDateWise(fromdate, todate, true);
        binding.fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag = -1;
                long date = 0;

                if (viewModel.getToDate().getValue() != null) {
                    date = viewModel.getToDate().getValue().getTime();
                    viewModel.getToDateDialog().setValue(null);
                    binding.toDate.setText(
                            ""
                    );
                    flag = -1;
                }
                CommonFunctions.showDatePickerDialog(requireActivity(), date, flag, new DatePickerCallback() {
                                            @Override
                                            public void onDateSelected(Date date) {
                                                viewModel.setFromDate(date);
                                                Log.d(TAG, "showDatePickerDialog: " + date);
                                                Log.d("new_date__", "showDatePickerDialog: " + CommonFunctions.convertDateFormat(date.toString(), /*"E MMM dd HH:mm:ss Z yyyy",*/"dd-MM-yyyy"));


                                                binding.fromDate.setText(CommonFunctions.
                                                        convertDateFormat
                                                                (date.toString(),
                                                                        /*"E MMM dd HH:mm:ss Z yyyy"
                                                                        ,*/
                                                                        "dd-MM-yyyy"
                                                                ));
                                            }
                                        });

            }
        });

        binding.toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag = -1;
                long date = 0;


                if (viewModel.getFromDate().getValue() != null) {
                    date = viewModel.getFromDate().getValue().getTime();
                    flag = 1;
                }
                CommonFunctions.showDatePickerDialog(requireActivity(), date, flag, new DatePickerCallback() {
                    @Override
                    public void onDateSelected(Date date) {
                        Log.d(TAG, "showDatePickerDialog1: " + date);
                        viewModel.setToDate(date);
                        binding.toDate.setText(CommonFunctions.convertDateFormat(date.toString(), /*"E MMM dd HH:mm:ss Z yyyy", */"dd-MM-yyyy"));

                    }
                });
                //showDatePickerDialog1();
            }
        });




     /*   viewModel.getCustomTreeDataListDialog().observe(getViewLifecycleOwner(), new Observer<List<TreeData>>() {
            @Override
            public void onChanged(List<TreeData> treeData) {
                if (treeData != null && treeData.size() > 0) {
                    getEntriesDialog(treeData);
                }
            }
        });*/
/*
        viewModel.getCustomTreeDataList().observe(getViewLifecycleOwner(), new Observer<List<TreeData>>() {
            @Override
            public void onChanged(List<TreeData> treeData) {
                if (treeData != null && treeData.size() > 0) {
                    Log.d(TAG, "onChanged: from db" + treeData.size());
                    //  prefManager.setSyncCount(treeData.size());

                    viewModel.processTreeDataList(treeData);

                    getEntries(treeData);
                    pieDataSet = new PieDataSet(viewModel.getPieEntries().getValue(), "");
                    pieData = new PieData(pieDataSet);


                    setUpPieChart();

                    pieData.notifyDataChanged();
                    pieDataSet.notifyDataSetChanged();
                    binding.mainLayout.setVisibility(View.VISIBLE);
                    binding.noDataLayout.setVisibility(View.GONE);

                } else {
                    binding.mainLayout.setVisibility(View.GONE);
                    binding.noDataLayout.setVisibility(View.VISIBLE);
                }
            }
        });
*/

      /*  viewModel.getTotalSpeciesCount().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer totalSpeciesCount) {
                binding.pieChart.setCenterText(" Total Species \n \n" + totalSpeciesCount);
            }
        });*/

    /*    viewModel.getTotalTreeCount().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer totalTreeCount) {
                binding.txtTreeCount.setText(totalTreeCount + "");

            }
        });*/

        viewModel.getFromDate().observe(requireActivity(), new Observer<Date>() {
            @Override
            public void onChanged(Date date) {
                Date toDate = viewModel.getToDate().getValue();


                if (date != null && toDate != null) {

                    if (!date.after(toDate)) {  // This checks if 'date' is not after 'toDate'
                        fromdate = convertDateInUTCFormat(date.toString());
                        todate = convertDateInUTCFormat(toDate.toString());
                        viewModel.clearData();
                        if (fromdate != null && !fromdate.isEmpty() && todate != null && !todate.isEmpty()) {
                            isFromPagination = false;
                            getDashboardDataDateWise(fromdate, todate, false);
                        }
                    } else {
                        Toast.makeText(requireActivity(), "Please select date range.", Toast.LENGTH_SHORT).show();
                    }


                }
            }
        });

        viewModel.getToDate().observe(requireActivity(), new Observer<Date>() {
            @Override
            public void onChanged(Date date) {
                Date fromDate = viewModel.getFromDate().getValue();
                if (date != null && fromDate != null) {
                    /*adapter.clearData();
                    adapter.notifyDataSetChanged();*/
                    fromdate = convertDateInUTCFormat(fromDate.toString());
                    todate = convertDateInUTCFormat(date.toString());
                    viewModel.clearData();
                    isFromPagination = false;
                    if (fromdate != null && !fromdate.isEmpty() && todate != null && !todate.isEmpty()) {
                        getDashboardDataDateWise(fromdate, todate, false);

                    }


                    //viewModel.getTreeDataBetweenDates(viewModel.getFromDate().getValue(), date);
                }
            }
        });


        binding.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExportSpeciesDialog();
            }
        });
        return binding.getRoot();
    }


    private String convertDateInUTCFormat(String inputDate) {
        try {
            // Create a SimpleDateFormat object with the input date format
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy", Locale.ENGLISH);

            // Parse the input date string
            Date date = inputDateFormat.parse(inputDate);

            // Create a Calendar instance and set its time and timezone
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+0530"));
            calendar.setTime(date);

            // Reset hour, minute, second, and millisecond to zero
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            // Now the calendar instance represents the date at 00:00:00.000 in the UTC+5:30 timezone

            // Create a SimpleDateFormat object with the desired output format
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z", Locale.ENGLISH);

            // Set the timezone to ensure the formatted string represents the correct timezone offset
            outputDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0530"));

            // Format the date into the desired output format
            String formattedDate = outputDateFormat.format(calendar.getTime());

            System.out.println("Formatted Date: " + formattedDate);

            return formattedDate;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

/*
    private void getDashboardData() {

        viewModel.todayDashboardData.observe(requireActivity(), new Observer<SealedNetworkResult<List<SpeciesData>>>() {
            @Override
            public void onChanged(SealedNetworkResult<List<SpeciesData>> result) {
                //when api hit successfully
                if (result instanceof SealedNetworkResult.Success) {
                    progressDialog.dismiss();

                    List<SpeciesData> speciesDataList = ((SealedNetworkResult.Success<List<SpeciesData>>) result).getData();
                    if (speciesDataList != null && speciesDataList.size() > 0) {
                        getEntries(speciesDataList);
                        pieDataSet = new PieDataSet(viewModel.getPieEntries().getValue(), "");
                        pieData = new PieData(pieDataSet);
                        binding.mainLayout.setVisibility(View.VISIBLE);
                        binding.noDataLayout.setVisibility(View.GONE);
                        binding.pieChart.setCenterText(" Total Species \n \n" + prefManager.getTotalSpeciesCount());
                        binding.txtTreeCount.setText(prefManager.getTreeCapturedCount() + "");
                        setUpPieChart(false);

                    } else {
                        progressDialog.dismiss();
                        binding.mainLayout.setVisibility(View.GONE);
                        binding.noDataLayout.setVisibility(View.VISIBLE);
                    }
                } else if (result instanceof SealedNetworkResult.Error) {
                    progressDialog.dismiss();
                    binding.mainLayout.setVisibility(View.GONE);
                    binding.noDataLayout.setVisibility(View.VISIBLE);
                    //  Toast.makeText(DashboardActivity.this, "error dd", Toast.LENGTH_SHORT).show();
                } else if (result instanceof SealedNetworkResult.Loading) {
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    //  Toast.makeText(DashboardActivity.this, "Loading dd", Toast.LENGTH_SHORT).show();

                }
            }
        });


    }
*/

    private void getDashboardDataDateWise(String startDate, String endDate, boolean isFirsttime) {
        currentPage = 1;
       /* String date1 = convertDateInUTCFormat(startDate.toString());
        String date2 = convertDateInUTCFormat(endDate.toString());*/

        //getting data for dashboard
        if (isFirsttime) {
            viewModel.getTreeDataBetweenDates(new DashboardDataRequest(/*"0b611896-fbe3-4723-ba3d-97523b88e191"*/prefManager.getUserId()), currentPage, false);

        } else if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            viewModel.getTreeDataBetweenDates(new DashboardDataRequest(/*"0b611896-fbe3-4723-ba3d-97523b88e191"*/prefManager.getUserId(), startDate, endDate), currentPage, false);

        }
        viewModel.customDashboardData.observe(requireActivity(), new Observer<SealedNetworkResult<List<SpeciesData>>>() {
            @Override
            public void onChanged(SealedNetworkResult<List<SpeciesData>> result) {
                //when api hit successfully
                if (result instanceof SealedNetworkResult.Success) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.progressCircular.setVisibility(View.GONE);
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();

                            }

                        }
                    }, 1000);
                    //  viewModel.clearPieEntries();
                  /*  viewModel.clearData();
                    adapter.notifyDataSetChanged();*/

                    List<SpeciesData> speciesDataList = ((SealedNetworkResult.Success<List<SpeciesData>>) result).getData();
                    if (speciesDataList != null && speciesDataList.size() > 0) {
                        getEntries(speciesDataList);
                        Log.d("speciesSIze__", "onChanged: speciesSIze " + speciesDataList.size());
                        pieDataSet = new PieDataSet(viewModel.getPieEntries().getValue(), "");
                        pieData = new PieData(pieDataSet);
                        binding.mainLayout.setVisibility(View.VISIBLE);
                        binding.noDataLayout.setVisibility(View.GONE);
                        binding.txtTreeCount.setText(prefManager.getTreeCapturedCount() + "");
                        setUpPieChart(true);

                    } else {
                        binding.mainLayout.setVisibility(View.GONE);
                        binding.noDataLayout.setVisibility(View.VISIBLE);
                    }
                } else if (result instanceof SealedNetworkResult.Error) {
                    binding.progressCircular.setVisibility(View.GONE);

                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    binding.mainLayout.setVisibility(View.GONE);
                    binding.noDataLayout.setVisibility(View.VISIBLE);
                    //  Toast.makeText(DashboardActivity.this, "error dd", Toast.LENGTH_SHORT).show();
                } else if (result instanceof SealedNetworkResult.Loading) {

                    if (!isFromPagination) {

                        progressDialog.setMessage("Please wait...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                    } else {
                        binding.progressCircular.setVisibility(View.VISIBLE);

                    }
                    //  Toast.makeText(DashboardActivity.this, "Loading dd", Toast.LENGTH_SHORT).show();

                }
            }
        });


    }

    private void getEntries(List<SpeciesData> speciesDataList) {
        speciesDataList.size();
        viewModel.clearPieEntries();
        //viewModel.clearData();
        int size = viewModel.getPieEntries().getValue().size();
        for (SpeciesData speciesData : speciesDataList) {

            int colorCode = 0;
            try {
                colorCode = Color.parseColor(speciesData.getColorCode());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid color format");
            }
            viewModel.addPieEntry(speciesData.getNumberOfTree(), speciesData.getSpeciesName(), colorCode);
            viewModel.addSpeciesColor(colorCode);
        }
        size = viewModel.getPieEntries().getValue().size();


    }

   /* private void getEntries(List<TreeData> treeData) {


        int totalTree = 0;
        viewModel.getSpeciesColors().setValue(new ArrayList<>());
        viewModel.getPieEntries().setValue(new ArrayList<>());


        for (int i = 0; i < treeData.size(); i++) {
            boolean speciesExists = false;

            totalTree = totalTree + treeData.get(i).noOfTree;

            for (int j = 0; j < viewModel.getPieEntries().getValue().size(); j++) {
                if (treeData.get(i).getSpecies().equals(viewModel.getPieEntries().getValue().get(j).getLabel())) {
                    // Species already exists, update the value
                    float newValue = treeData.get(i).getNoOfTree() + viewModel.getPieEntries().getValue().get(j).getValue();
                    viewModel.updatePieEntryValue(j, newValue);

                    speciesExists = true;
                    break;
                }
            }

            if (!speciesExists) {
                // Species doesn't exist, add a new entry
                viewModel.addPieEntry(treeData.get(i).getNoOfTree(), treeData.get(i).getSpecies(), treeData.get(i).getSpeciesColor());
                viewModel.addSpeciesColor(treeData.get(i).getSpeciesColor());
            }
        }
        //ddddddddd
       // viewModel.setTotalTreeCount(totalTree);
     //   viewModel.setTotalSpeciesCount(viewModel.getPieEntries().getValue().size());


    }*/

    private void getEntriesDialog(List<TreeData> treeData) {


        viewModel.getPieEntriesDialog().setValue(new ArrayList<>());


        for (int i = 0; i < treeData.size(); i++) {
            boolean speciesExists = false;


            for (int j = 0; j < viewModel.getPieEntriesDialog().getValue().size(); j++) {
                if (treeData.get(i).getSpecies().equals(viewModel.getPieEntriesDialog().getValue().get(j).getLabel())) {
                    // Species already exists, update the value
                    float newValue = treeData.get(i).getNoOfTree() + viewModel.getPieEntriesDialog().getValue().get(j).getValue();
                    viewModel.updatePieEntryValueDialog(j, newValue);

                    speciesExists = true;
                    break;
                }
            }

            if (!speciesExists) {
                // Species doesn't exist, add a new entry
                viewModel.addPieEntryDialog(treeData.get(i).getNoOfTree(), treeData.get(i).getSpecies(), treeData.get(i).getSpeciesColor());
            }
        }


    }

    private void setUpPieChart(boolean datset) {

        pieDataSet.setColors(viewModel.getSpeciesColors().getValue());
        pieDataSet.setDrawValues(true);
        pieDataSet.setValueTextSize(15);
        pieDataSet.setValueTextColor(0xffffffff);
        pieDataSet.setValueFormatter(new IntegerValueFormatter());

        double isNextPage = (double) prefManager.getTotalSpeciesCount() / PAGE_SIZE;
        int replaceFloat = (int) Math.round(isNextPage);
        if (replaceFloat < isNextPage) {
            replaceFloat += 1;
        }
        TOTAL_PAGES = replaceFloat;
        if (!isFromPagination) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
            binding.speciesRecycler.setLayoutManager(linearLayoutManager);
            adapter = new SpeciesAdapter(requireActivity(), viewModel.getPieEntries().getValue(), CustomDataFragment.this, datset);

            binding.speciesRecycler.setAdapter(adapter);
            binding.speciesRecycler.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
                @Override
                protected void loadMoreItems() {
                    currentPage++;
                    if (TOTAL_PAGES >= currentPage) {
                        isFromPagination = true;
                        viewModel.getTreeDataBetweenDates(new DashboardDataRequest(prefManager.getUserId(), fromdate, todate), currentPage, true);

                    }
                }

                @Override
                public int getTotalPageCount() {
                    return TOTAL_PAGES;
                }

                @Override
                public boolean isLastPage() {
                    return false;
                }

                @Override
                public boolean isLoading() {
                    return false;
                }
            });

        } else {
            adapter.addData(viewModel.getPieEntries().getValue());
        }


        binding.searchSpecies.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }


    //export data functionality
    private void showExportSpeciesDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        exportDialogBinding = ExportSpeciesDialogBinding.inflate(LayoutInflater.from(requireActivity()));

        builder.setView(exportDialogBinding.getRoot());

        builder.setCancelable(false);


        exportDialogBinding.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                exportDialog.dismiss();
                viewModel.selectedSpeciesId.clear();
                viewModel.setFromDateDialog(null);
                viewModel.setToDateDialog(null);

            }

        });

        viewModel.RfMaster.observe(getViewLifecycleOwner(), new Observer<List<RFMaster>>() {
            @Override

            public void onChanged(List<RFMaster> rfMasters) {

                if (rfMasters != null) {

                    setRFNameSpinnerItems(rfMasters);

                }

            }

        });


        viewModel.districtMaster.observe(getViewLifecycleOwner(), new Observer<List<DistrictMaster>>() {
            @Override

            public void onChanged(List<DistrictMaster> districtMasters) {

                if (districtMasters != null && districtMasters.size() > 0) {

                    setDistrictNameSpinnerItems(districtMasters);

                }

            }

        });

        exportDialogBinding.exportBtn.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

// CommonFunctions.generateExcelSheet(viewModel.treeDataList.getValue(),requireActivity(),viewModel.getFromDate().getValue().toString(),viewModel.getToDate().getValue().toString(),"All");

                if (checkValidation()) {

                    if (CommonFunctions.isNetworkConnectionAvailable(requireActivity())) {
                        Date dateCheckFrom = viewModel.getFromDateDialog().getValue();
                        Date dateCheckTo = viewModel.getToDateDialog().getValue();


                        if (dateCheckFrom != null) {

                            fromDateExp = convertDateInUTCFormat(dateCheckFrom.toString());

                        }

                        if (dateCheckTo != null) {

                            toDateExp = convertDateInUTCFormat(dateCheckTo.toString());

                        }

                        List<String> speciesListId = viewModel.selectedSpeciesId;
                        /*speciesListId.add("950175d8-6a61-4a1c-a5a2-cc7b58e3b04a");
                        speciesListId.add("9be0b5e7-a6b9-4062-86e3-bb2ca03b4e1a");*/


                        //"950175d8-6a61-4a1c-a5a2-cc7b58e3b04a";//viewModel.selectedSpecies;
                        rfName = viewModel.getRfName().getValue();

                        if (rfName == null) {
                            rfName = "";

                        }
                        district = viewModel.getDistrictName().getValue();

                        if (district == null) {
                            district = "";


                        }


                        ExportListRequest exportListRequest = new ExportListRequest(prefManager.getUserId()/*"0b611896-fbe3-4723-ba3d-97523b88e191"*/, fromDateExp, toDateExp, speciesListId, rfName, district/*, fromDate, toDate, speciesListId, "9be0b5e8-a6b9-4062-86e3-bb2ca03b4a4a", "ec4d0c68-3861-4ba5-b8b9-41e92b70b9d1"*/);

                        viewModel.getExportData(exportListRequest).observe(requireActivity(), new Observer<SealedNetworkResult<List<ExportData>>>() {
                            @Override

                            public void onChanged(SealedNetworkResult<List<ExportData>> result) {

//when api hit successfully

                                if (result instanceof SealedNetworkResult.Success) {
                                    try {


                                        exportDialog.dismiss();
                                        CommonFunctions.generateExcelSheet(((SealedNetworkResult.Success<List<ExportData>>) result).getData(), requireActivity(), fromDateExp, toDateExp, "forestrySurvey");
                                        progressDialog.dismiss();
                                        exportDialog.dismiss();
                                        viewModel.selectedSpeciesId.clear();
                                        viewModel.setFromDateDialog(null);
                                        viewModel.setToDateDialog(null);

                                        viewModel.setToDateDialog(null);

                                        viewModel.setFromDateDialog(null);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                } else if (result instanceof SealedNetworkResult.Error) {
                                    progressDialog.dismiss();

                                    Log.d(TAG, "onChanged: error" + ((SealedNetworkResult.Error<List<ExportData>>) result).getMessage());
                                    //Toast.makeText(requireActivity(), ((SealedNetworkResult.Error<List<ExportData>>) result).getMessage() , Toast.LENGTH_SHORT).show();

                                } else if (result instanceof SealedNetworkResult.Loading) {
                                    progressDialog.setMessage("Please wait...");
                                    progressDialog.setCancelable(false);
                                    progressDialog.show();
                                    // Toast.makeText(requireActivity(), "Loading", Toast.LENGTH_SHORT).show();

                                }

                            }

                        });


                    } else {

                        Toast.makeText(requireActivity(), "Internet connection is not available", Toast.LENGTH_SHORT).show();

                    }

                } else {

                    Toast.makeText(requireActivity(), "Please select date range", Toast.LENGTH_SHORT).show();

                }

            }

        });

        exportDialogBinding.fromDate.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                int flag = -1;

                long date = 0;


                if (viewModel.getToDateDialog().getValue() != null) {

                    date = viewModel.getToDateDialog().getValue().getTime();

                    viewModel.setToDateDialog(null);

                    exportDialogBinding.toDate.setText("");

                    flag = -1;

                }

                CommonFunctions.showDatePickerDialog(requireActivity(),

                        date, flag,

                        new DatePickerCallback() {
                            @Override

                            public void onDateSelected(Date date) {

// viewModel.setFromDate(date.getTime());

                                Log.d(TAG, "showDatePickerDialog: " + date);

                                exportDialogBinding.fromDate.setText(CommonFunctions.convertDateFormat(date.toString(), /*"E MMM dd HH:mm:ss Z yyyy", */"dd-MM-yyyy"));

                                viewModel.setFromDateDialog(date);

                            }

                        });


            }

        });

        exportDialogBinding.toDate.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                int flag = -1;

                long date = 0;


                if (viewModel.getFromDateDialog().getValue() != null) {

                    date = viewModel.getFromDateDialog().getValue().getTime();

                    flag = 1;

                }

                CommonFunctions.showDatePickerDialog(requireActivity(),

                        date, flag,

                        new DatePickerCallback() {
                            @Override

                            public void onDateSelected(Date date) {

// viewModel.setFromDate(date.getTime());

                                Log.d(TAG, "showDatePickerDialog: " + date);

                                viewModel.setToDateDialog(date);

                                exportDialogBinding.toDate.setText(CommonFunctions.convertDateFormat(date.toString(), /*"E MMM dd HH:mm:ss Z yyyy", */"dd-MM-yyyy"));

                            }

                        });

            }

        });


        exportDialogBinding.districtNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                viewModel.setDistrictName(viewModel.districtMaster.getValue().get(position).getDistrictMasterID());

            }

            @Override

            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        exportDialogBinding.rfNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                viewModel.setRFName(viewModel.RfMaster.getValue().get(position).getReservForestMasterID());

            }

            @Override

            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        exportDialogBinding.speciesNameSpinner.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

                speciesDialogBinding = ExportSpeciesSpinnerDialogBinding.inflate(getLayoutInflater());

                builder.setView(speciesDialogBinding.getRoot());

                builder.setCancelable(false);

                AlertDialog dialog = builder.create();
                // List<String> speciesMasterId = new ArrayList<>();
                viewModel.selectedSpeciesId.clear();
                viewModel.selectedSpeciesName.clear();

                viewModel.speciesMaster.observe(getViewLifecycleOwner(), new Observer<List<SpeciesMaster>>() {
                    @Override

                    public void onChanged(List<SpeciesMaster> speciesMasters) {

                        speciesDialogBinding.speciesCheckRecycler.setLayoutManager(new LinearLayoutManager(requireActivity()));
                        speciesDialogBinding.speciesCheckRecycler.setAdapter(new SpeciesCheckBoxAdapter(speciesMasters, CustomDataFragment.this, false));

                        speciesDialogBinding.selectAllCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                            isSelectAll = isChecked;
                            viewModel.selectedSpeciesId.clear();
                            viewModel.selectedSpeciesName.clear();
                            if (isSelectAll) {

                                for (SpeciesMaster items : speciesMasters) {

                                    viewModel.selectedSpeciesId.add(items.getSpeciesMasterID());
                                    viewModel.selectedSpeciesName.add(items.getSpeciesName());
                                }
                            }

                            speciesDialogBinding.speciesCheckRecycler.setAdapter(new SpeciesCheckBoxAdapter(speciesMasters, CustomDataFragment.this, isChecked));


                      /*      for (int i = 0; i < speciesDialogBinding.checkboxContainer.getChildCount(); i++) {

                                CheckBox checkBox = (CheckBox) speciesDialogBinding.checkboxContainer.getChildAt(i);

                                checkBox.setChecked(isChecked);

                            }
*/
                        });

/*
                        for (SpeciesMaster speciesMaster : speciesMasters) {
                            speciesMasterId.add(speciesMaster.getSpeciesMasterID());

                            CheckBox checkBox = new CheckBox(requireActivity());

                            checkBox.setText(speciesMaster.getSpeciesName());

                            speciesDialogBinding.checkboxContainer.addView(checkBox);

                        }*/
                        speciesDialogBinding.okBtn.setOnClickListener(c -> {

                            viewModel.selectedSpecies = "";
                            if (!viewModel.selectedSpeciesName.isEmpty()) {
                                for (int i = 0; i < viewModel.selectedSpeciesName.size(); i++) {
                                    if (i == 0) {
                                        viewModel.selectedSpecies = viewModel.selectedSpeciesName.get(i);
                                    } else {
                                        viewModel.selectedSpecies = viewModel.selectedSpecies + "," + viewModel.selectedSpeciesName.get(i);

                                    }
                                }
                            }
                            //    viewModel.selectedSpeciesId.clear();


                            //viewModel.selectedSpecies = "";


                            //  List<String> selectedItems = new ArrayList<>();


/*
                            for (int i = 0; i < speciesDialogBinding.checkboxContainer.getChildCount(); i++) {

                                CheckBox checkBox = (CheckBox) speciesDialogBinding.checkboxContainer.getChildAt(i);

                                if (checkBox.isChecked()) {


                                    selectedItems.add(checkBox.getText().toString());
                                    viewModel.selectedSpeciesId.add(speciesMasterId.get(i));

                                }

                            }
*/

                            //speciesMasterId.clear();

// Handle selected items (use the 'selectedItems' list)

/*
                            for (String selectedItem : selectedItems) {

                                if (viewModel.selectedSpecies.isEmpty()) {

                                    viewModel.selectedSpecies = selectedItem;

                                } else {

                                    viewModel.selectedSpecies = viewModel.selectedSpecies + "," + selectedItem;

                                }

                                Log.d("Selected Item", selectedItem);

                            }
*/

                            //    viewModel.setSelectedSpeciesList(selectedItems);


                            exportDialogBinding.speciesTxt.setText(viewModel.selectedSpecies);

                            dialog.dismiss();

                        });

                    }

                });


                speciesDialogBinding.cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override

                    public void onClick(View v) {

                        viewModel.setToDateDialog(null);

                        viewModel.setFromDateDialog(null);

                        dialog.dismiss();

                    }

                });


// Set "Select All" checkbox behavior


// Show the dialog

                dialog.show();


            }

        });


        exportDialog = builder.create();

        exportDialog.show();

    }


    private void setRFNameSpinnerItems(List<RFMaster> items) {

        Set<RFMaster2> rfSet = new HashSet<>();
        for (RFMaster item : items) {
            rfSet.add(new RFMaster2(item.reservForestMasterID, item.reservForestMasterName));
        }

        List<RFMaster2> rfList = new ArrayList<>(rfSet);
        ArrayAdapter<RFMaster2> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, rfList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exportDialogBinding.rfNameSpinner.setAdapter(adapter);

    }

    private void setDistrictNameSpinnerItems(List<DistrictMaster> items) {
        ArrayAdapter<DistrictMaster> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exportDialogBinding.districtNameSpinner.setAdapter(adapter);

    }


    private boolean checkValidation() {
        // Check if Date values are not null
        if (viewModel.getFromDateDialog().getValue() == null ||
                viewModel.getToDateDialog().getValue() == null) {
            return false;
        }

        if (viewModel.selectedSpeciesId == null || viewModel.selectedSpeciesId.isEmpty()) {
            for (SpeciesMaster speciesMast : viewModel.speciesMaster1) {
                viewModel.selectedSpeciesId.add(speciesMast.getSpeciesMasterID());

            }

        }

        // Check if String values are not null and not empty
        if ((viewModel.getRfName().getValue() == null || viewModel.getRfName().getValue().isEmpty()) ||
                (viewModel.getDistrictName().getValue() == null || viewModel.getDistrictName().getValue().isEmpty())) {
            return false;
        }

        // If none of the conditions for being "unfilled" are met, return true
        return true;
    }

    @Override
    public void onItemCheck(SpeciesMaster speciesMaster, boolean isChecked) {
        if (isChecked) {
            viewModel.selectedSpeciesId.add(speciesMaster.getSpeciesMasterID());
            viewModel.selectedSpeciesName.add(speciesMaster.getSpeciesName());

        } else {
            viewModel.selectedSpeciesId.remove(speciesMaster.getSpeciesMasterID());
            if (viewModel.selectedSpeciesName.contains(speciesMaster.getSpeciesName())) {
                viewModel.selectedSpeciesName.remove(speciesMaster.getSpeciesName());
            }
        }
    }

    @Override
    public void onFilterComplete(int count) {
        if (count == 0) {

            binding.noData.setVisibility(View.VISIBLE);
        } else {
            binding.noData.setVisibility(View.GONE);

        }


    }

}