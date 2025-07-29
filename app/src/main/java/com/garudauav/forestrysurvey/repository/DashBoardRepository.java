package com.garudauav.forestrysurvey.repository;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.garudauav.forestrysurvey.db.dao.SpeciesDataDao;
import com.garudauav.forestrysurvey.db.dao.TreeDataDao;
import com.garudauav.forestrysurvey.db.dao.master_dao.DistrictMasterDao;
import com.garudauav.forestrysurvey.db.dao.master_dao.RFMasterDao;
import com.garudauav.forestrysurvey.db.dao.master_dao.SpeciesMasterDao;
import com.garudauav.forestrysurvey.db.database.ForestryDataBase;
import com.garudauav.forestrysurvey.models.DashboardData;
import com.garudauav.forestrysurvey.models.DistrictMaster;
import com.garudauav.forestrysurvey.models.RFMaster;
import com.garudauav.forestrysurvey.models.SpeciesData;
import com.garudauav.forestrysurvey.models.SpeciesMaster;
import com.garudauav.forestrysurvey.models.SyncHistory;
import com.garudauav.forestrysurvey.models.TreeData;
import com.garudauav.forestrysurvey.models.request_models.AddSpeciesValue;
import com.garudauav.forestrysurvey.models.request_models.DashboardDataRequest;
import com.garudauav.forestrysurvey.models.request_models.ExportListRequest;
import com.garudauav.forestrysurvey.models.response_models.AddSpeciesResponse;
import com.garudauav.forestrysurvey.models.response_models.BaseResponse;
import com.garudauav.forestrysurvey.models.response_models.ExportData;
import com.garudauav.forestrysurvey.models.response_models.ExportList;
import com.garudauav.forestrysurvey.models.response_models.SyncHistoryList;
import com.garudauav.forestrysurvey.network.ApiService;
import com.garudauav.forestrysurvey.network.RetrofitClient;
import com.garudauav.forestrysurvey.network.SealedNetworkResult;
import com.garudauav.forestrysurvey.ui.activities.LoginActivity;
import com.garudauav.forestrysurvey.utils.CommonFunctions;
import com.garudauav.forestrysurvey.utils.PrefManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashBoardRepository {

    private final PrefManager prefManager;
    private final TreeDataDao treeDataDao;
    private final SpeciesDataDao speciesDataDao;

    private final RFMasterDao rfMasterDao;
    private final DistrictMasterDao districtMasterDao;
    private final SpeciesMasterDao speciesMasterDao;
    private final ApiService apiService = RetrofitClient.getApiService();
    private Application application;
    private static final int pageSize = 50;
    private static final int PAGE_START = 1;
    private int TOTAL_PAGES = 0;
    private static final int PAGE_SIZE = 50;
    private boolean alreadyCheckedTotalRecords = false;


    public MutableLiveData<List<TreeData>> treeData = new MutableLiveData<>();
    private LiveData<List<TreeData>> customTreeDataListDialog;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();


    public DashBoardRepository(Application application, PrefManager prefManager) {
        this.application = application;
        this.prefManager = prefManager;
        ForestryDataBase dataBase = ForestryDataBase.getInstance(application);
        treeDataDao = dataBase.treeDataDao();
        speciesDataDao = dataBase.speciesDataDao();
        rfMasterDao = dataBase.rfMasterDao();
        districtMasterDao = dataBase.districtMasterDao();
        speciesMasterDao = dataBase.speciesMasterDao();
        getTreeValue();
        customTreeDataListDialog = treeData;
    }

    public void getTreeValue() {
        treeDataDao.getTreeData(prefManager.getUserCode()).observeForever(new Observer<List<TreeData>>() {
            @Override
            public void onChanged(List<TreeData> treeDataList) {
                treeData.postValue(treeDataList);
            }
        });
    }

    public LiveData<List<TreeData>> getTreeData() {

        return treeData;
    }

    /*    public LiveData<List<TreeData>> getCustomTreeDataList(){

            return customTreeDataList;
        }*/
    public LiveData<List<TreeData>> getCustomTreeDataListDialog() {

        return customTreeDataListDialog;
    }

 /*   public List<RFMaster> getRFNameList() {
        return Arrays.asList(new RFMaster("3", "Umkhuti", "",true), new RFMaster("4", "random", "",true));
    }*/


  /*  public List<DistrictMaster> getDistrictNameList() {
        return Arrays.asList(new DistrictMaster("3", "East Khasi Hills", "", true), new DistrictMaster("4", "West Khasi Hills", "", true));
    }*/


    public LiveData<List<SpeciesMaster>> getSpeciesNameList() {
        return speciesMasterDao.getSpeciesMaster();

    }
/*
    public LiveData<SealedNetworkResult<List<SpeciesMaster>>> getSpeciesNameList() {

        MutableLiveData<SealedNetworkResult<List<SpeciesMaster>>> liveData = new MutableLiveData<>();
        if (CommonFunctions.isNetworkConnectionAvailable(application)) {
            liveData.setValue(new SealedNetworkResult.Loading<>());
            apiService.getSpeciesMaster().enqueue(new Callback<BaseResponse<List<SpeciesMaster>>>() {
                @Override
                public void onResponse(Call<BaseResponse<List<SpeciesMaster>>> call, Response<BaseResponse<List<SpeciesMaster>>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<SpeciesMaster> speciesMasterList = response.body().getData();
                        Log.d("123edxcgh", "onResponse:species " + speciesMasterList.get(0).getSpeciesName());
                        executorService.execute(new Runnable() {
                            @Override
                            public void run() {
                                speciesMasterDao.clearSpeciesMaster();
                                for (SpeciesMaster speciesMaster : speciesMasterList) {
                                    if (speciesMaster.isActive()) {
                                        speciesMasterDao.insertSpeciesMaster(speciesMaster);

                                    }

                                }

                            }

                        });

                        liveData.postValue(new SealedNetworkResult.Success<>(speciesMasterList));
                    } else {
                        liveData.postValue(new SealedNetworkResult.Error<>("Error in Fetching Data"));

                    }
                }

                @Override
                public void onFailure(Call<BaseResponse<List<SpeciesMaster>>> call, Throwable t) {
                    liveData.postValue(new SealedNetworkResult.Error<>(t.getMessage() + ""));


                }
            });

        } else {
            speciesMasterDao.getSpeciesMaster().observeForever(new Observer<List<SpeciesMaster>>() {
                @Override
                public void onChanged(List<SpeciesMaster> speciesMasters) {
                    if (speciesMasters != null) {
                        liveData.setValue(new SealedNetworkResult.Success<>(speciesMasters));

                    }

                }
            });
        }
        return liveData;

    }
*/

    public LiveData<List<DistrictMaster>> getDistrictNameList() {
        return districtMasterDao.getDistrictMaster();

    }
   /* public LiveData<SealedNetworkResult<List<DistrictMaster>>> getDistrictNameList() {

        MutableLiveData<SealedNetworkResult<List<DistrictMaster>>> liveData = new MutableLiveData<>();
        if (CommonFunctions.isNetworkConnectionAvailable(application)) {
            liveData.setValue(new SealedNetworkResult.Loading<>());
            apiService.getDistrictMaster().enqueue(new Callback<BaseResponse<List<DistrictMaster>>>() {
                @Override
                public void onResponse(Call<BaseResponse<List<DistrictMaster>>> call, Response<BaseResponse<List<DistrictMaster>>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<DistrictMaster> districtMasterList = response.body().getData();
                        executorService.execute(new Runnable() {
                            @Override
                            public void run() {
                                districtMasterDao.clearDistrictMaster();
                                for (DistrictMaster districtMaster : districtMasterList) {
                                    if (districtMaster.isActive()) {
                                        districtMasterDao.insertDistrictMater(districtMaster);

                                    }

                                }

                            }

                        });

                        liveData.postValue(new SealedNetworkResult.Success<>(districtMasterList));
                    } else {
                        liveData.postValue(new SealedNetworkResult.Error<>("Error in Fetching Data"));

                    }
                }

                @Override
                public void onFailure(Call<BaseResponse<List<DistrictMaster>>> call, Throwable t) {
                    liveData.postValue(new SealedNetworkResult.Error<>(t.getMessage() + ""));


                }
            });

        } else {
            districtMasterDao.getDistrictMaster().observeForever(new Observer<List<DistrictMaster>>() {
                @Override
                public void onChanged(List<DistrictMaster> districtMasters) {
                    if (districtMasters != null) {
                        liveData.setValue(new SealedNetworkResult.Success<>(districtMasters));

                    }

                }
            });
        }
        return liveData;

    }*/

    public LiveData<List<RFMaster>> getRFNameList() {
        return rfMasterDao.getRfMaster();

    }
 /*   public LiveData<SealedNetworkResult<List<RFMaster>>> getRFNameList() {

        MutableLiveData<SealedNetworkResult<List<RFMaster>>> liveData = new MutableLiveData<>();
        if (CommonFunctions.isNetworkConnectionAvailable(application)) {
            liveData.setValue(new SealedNetworkResult.Loading<>());
            apiService.getRFMaster().enqueue(new Callback<BaseResponse<List<RFMaster>>>() {
                @Override
                public void onResponse(Call<BaseResponse<List<RFMaster>>> call, Response<BaseResponse<List<RFMaster>>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<RFMaster> rfMasterList = response.body().getData();
                        Log.d("123edxcgh", "onResponse: " + rfMasterList.get(0).getReservForestMasterName());


                        executorService.execute(new Runnable() {
                            @Override
                            public void run() {
                                rfMasterDao.clearRfMaster();
                                for (RFMaster rfMaster : rfMasterList) {
                                    if (rfMaster.isActive()) {
                                        rfMasterDao.insertRfMater(rfMaster);

                                    }

                                }

                            }

                        });

                        liveData.postValue(new SealedNetworkResult.Success<>(rfMasterList));
                    } else {
                        liveData.postValue(new SealedNetworkResult.Error<>("Error in Fetching Data"));

                    }
                }

                @Override
                public void onFailure(Call<BaseResponse<List<RFMaster>>> call, Throwable t) {
                    liveData.postValue(new SealedNetworkResult.Error<>(t.getMessage() + ""));


                }
            });

        } else {
            rfMasterDao.getRfMaster().observeForever(new Observer<List<RFMaster>>() {
                @Override
                public void onChanged(List<RFMaster> rfMasters) {
                    if (rfMasters != null) {
                        liveData.setValue(new SealedNetworkResult.Success<>(rfMasters));

                    }

                }
            });
        }
        return liveData;

    }*/

    public LiveData<SealedNetworkResult<BaseResponse>> addSpeciesValue(AddSpeciesValue addSpeciesValue) {
        MutableLiveData<SealedNetworkResult<BaseResponse>> liveData = new MutableLiveData<>();
        if (CommonFunctions.isNetworkConnectionAvailable(application)) {
            liveData.setValue(new SealedNetworkResult.Loading<>());
            apiService.addSpeciesValue(addSpeciesValue).enqueue(new Callback<BaseResponse<Void>>() {
                @Override
                public void onResponse(Call<BaseResponse<Void>> call, Response<BaseResponse<Void>> response) {
                    if (response.isSuccessful() && response.code() == 200) {
                        if (response.body() != null) {
                            try {


                                BaseResponse response1 = response.body();


                                liveData.postValue(new SealedNetworkResult.Success<>(response1));


                            } catch (Exception e) {
                                liveData.postValue(new SealedNetworkResult.Error<>("Error in Fetching Data"));

                            }
                        } else {
                            liveData.postValue(new SealedNetworkResult.Error<>("Something went wrong"));

                        }


                    } else if (response.code() == 400) {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            JSONArray jsonArray = jObjError.getJSONArray("Errors");
                            String errorMessage = jsonArray.getString(0);
                            Toast.makeText(application, errorMessage, Toast.LENGTH_SHORT).show();
                            liveData.postValue(new SealedNetworkResult.Error<>("Something went wrong"));

                        } catch (Exception e) {
                            liveData.postValue(new SealedNetworkResult.Error<>("Something went wrong"));

                            Toast.makeText(application, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    } else if (response.code() == 401) {
                        liveData.postValue(new SealedNetworkResult.Error<>("Something went wrong"));

                        Toast.makeText(application, "Invalid or missing access_token provided", Toast.LENGTH_SHORT).show();
                    } else if (response.code() == 403) {
                        liveData.postValue(new SealedNetworkResult.Error<>("Something went wrong"));

                        Toast.makeText(application, "The access token was decoded successfully but did not include a scope appropriate to this endpoint", Toast.LENGTH_SHORT).show();
                    } else if (response.code() == 404) {
                        liveData.postValue(new SealedNetworkResult.Error<>("Something went wrong"));

                        Toast.makeText(application, "Resource not found", Toast.LENGTH_SHORT).show();
                    } else if (response.code() == 429) {
                        liveData.postValue(new SealedNetworkResult.Error<>("Something went wrong"));

                        Toast.makeText(application, "Too many recent requests from you. Wait to make further submissions", Toast.LENGTH_SHORT).show();
                    } else if (response.code() == 500) {
                        liveData.postValue(new SealedNetworkResult.Error<>("Something went wrong"));

                        Toast.makeText(application, "Internal server error.The request was not completed due to an internal error on the server side", Toast.LENGTH_SHORT).show();
                    } else if (response.code() == 503) {
                        liveData.postValue(new SealedNetworkResult.Error<>("Something went wrong"));

                        Toast.makeText(application, "Service unavailable.The server was unavailable", Toast.LENGTH_SHORT).show();
                    } else {
                        liveData.postValue(new SealedNetworkResult.Error<>("Something went wrong"));
                        Toast.makeText(application, "Something went wrong ! Please contact to administration !", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse<Void>> call, Throwable t) {

                }
            });


        } else {
            Toast.makeText(application, "No internet..", Toast.LENGTH_SHORT).show();

/*
            speciesDataDao.getSpeciesDate().observeForever(new Observer<List<SpeciesData>>() {
                @Override
                public void onChanged(List<SpeciesData> speciesDataList) {
                    if (speciesDataList != null) {
                        liveData.setValue(new SealedNetworkResult.Success<>(speciesDataList));

                    }

                }
            });
*/


        }
        return liveData;

    }


    public LiveData<SealedNetworkResult<List<SpeciesData>>> getDashBoardData(DashboardDataRequest dashboardDataRequest, int pageIndex, boolean isFromPagination) {
        MutableLiveData<SealedNetworkResult<List<SpeciesData>>> liveData = new MutableLiveData<>();
        if (CommonFunctions.isNetworkConnectionAvailable(application)) {
            liveData.setValue(new SealedNetworkResult.Loading<>());
            apiService.getTreeData(pageIndex, pageSize, dashboardDataRequest).enqueue(new Callback<BaseResponse<DashboardData>>() {
                @Override
                public void onResponse(Call<BaseResponse<DashboardData>> call, Response<BaseResponse<DashboardData>> response) {
                    try {
                        if (response.isSuccessful() && response.code() == 200) {
                            if (response.body().getData() != null && response.body().getData().getSpeciesData() != null) {

                                DashboardData dashboardData = response.body().getData();

                                prefManager.setTreeCapturedCount(dashboardData.getTreeCount());
                                prefManager.setTotalSpeciesCount(dashboardData.getTotalSpecies());


                                executorService.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!isFromPagination) {
                                            speciesDataDao.clearSpeciesData();

                                        }
                                        List<SpeciesData> speciesDataList = dashboardData.getSpeciesData();
                                        for (SpeciesData speciesData : speciesDataList) {
                                            speciesDataDao.insertSpeciesData(speciesData);

                                        }
                                        // treeDataDao.insertTreeData(dashboardData.getSpeciesData());
                                    }
                                });
                                liveData.postValue(new SealedNetworkResult.Success<>(dashboardData.getSpeciesData()));

                                if (!alreadyCheckedTotalRecords) {
                                    double isNextPage = (double) prefManager.getTotalSpeciesCount() / PAGE_SIZE;
                                    int replaceFloat = (int) Math.round(isNextPage);
                                    if (replaceFloat < isNextPage) {
                                        replaceFloat += 1;
                                    }
                                    TOTAL_PAGES = replaceFloat;
                                    alreadyCheckedTotalRecords = true;
                                }


                            } else {
                                liveData.postValue(new SealedNetworkResult.Error<>("Something went wrong"));

                            }


                        } else if (response.code() == 400) {
                            try {
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                JSONArray jsonArray = jObjError.getJSONArray("Errors");
                                String errorMessage = jsonArray.getString(0);
                                Toast.makeText(application, errorMessage, Toast.LENGTH_SHORT).show();
                                liveData.postValue(new SealedNetworkResult.Error<>("Something went wrong"));

                            } catch (Exception e) {
                                liveData.postValue(new SealedNetworkResult.Error<>("Something went wrong"));

                                Toast.makeText(application, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        } else if (response.code() == 401) {
                            liveData.postValue(new SealedNetworkResult.Error<>("Something went wrong"));

                            Toast.makeText(application, "Invalid or missing access_token provided", Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 403) {
                            liveData.postValue(new SealedNetworkResult.Error<>("Something went wrong"));

                            Toast.makeText(application, "The access token was decoded successfully but did not include a scope appropriate to this endpoint", Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 404) {
                            liveData.postValue(new SealedNetworkResult.Error<>("Something went wrong"));

                            Toast.makeText(application, "Resource not found", Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 429) {
                            liveData.postValue(new SealedNetworkResult.Error<>("Something went wrong"));

                            Toast.makeText(application, "Too many recent requests from you. Wait to make further submissions", Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 500) {
                            liveData.postValue(new SealedNetworkResult.Error<>("Something went wrong"));

                            Toast.makeText(application, "Internal server error.The request was not completed due to an internal error on the server side", Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 503) {
                            liveData.postValue(new SealedNetworkResult.Error<>("Something went wrong"));

                            Toast.makeText(application, "Service unavailable.The server was unavailable", Toast.LENGTH_SHORT).show();
                        } else {
                            liveData.postValue(new SealedNetworkResult.Error<>("Something went wrong"));
                            Toast.makeText(application, "Something went wrong ! Please contact to administration !", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        liveData.postValue(new SealedNetworkResult.Error<>("Error in Fetching Data"));

                    }


                }

                @Override
                public void onFailure(Call<BaseResponse<DashboardData>> call, Throwable t) {
                    liveData.postValue(new SealedNetworkResult.Error<>(t.getMessage()));
                }
            });

        } else {
            speciesDataDao.getSpeciesDate().observeForever(new Observer<List<SpeciesData>>() {
                @Override
                public void onChanged(List<SpeciesData> speciesDataList) {
                    if (!isFromPagination) {
                        if (speciesDataList != null) {
                            liveData.setValue(new SealedNetworkResult.Success<>(speciesDataList));

                        }
                    }

                }
            });


        }
        return liveData;

    }

    public LiveData<SealedNetworkResult<List<SpeciesData>>> getDashBoardDataDateWise(DashboardDataRequest dashboardDataRequest, int pageIndex) {
        MutableLiveData<SealedNetworkResult<List<SpeciesData>>> liveData = new MutableLiveData<>();
        if (CommonFunctions.isNetworkConnectionAvailable(application)) {
            liveData.setValue(new SealedNetworkResult.Loading<>());
            apiService.getCustomTreeData(pageIndex, pageSize, dashboardDataRequest).enqueue(new Callback<BaseResponse<DashboardData>>() {
                @Override
                public void onResponse(Call<BaseResponse<DashboardData>> call, Response<BaseResponse<DashboardData>> response) {
                    try {


                        if (response.isSuccessful() && response.code() == 200) {
                            if (response.body().getData().getSpeciesData() != null) {
                                try {
                                    DashboardData dashboardData = response.body().getData();

                                    prefManager.setTreeCapturedCount(dashboardData.getTreeCount());
                                    prefManager.setTotalSpeciesCount(dashboardData.getTotalSpecies());


                         /*       executorService.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        speciesDataDao.clearSpeciesData();
                                        List<SpeciesData> speciesDataList = new ArrayList<>(dashboardData.getSpeciesData()); // Create a local copy
                                        for (SpeciesData speciesData : speciesDataList) {
                                            speciesDataDao.insertSpeciesData(speciesData);
                                        }
                                        // treeDataDao.insertTreeData(dashboardData.getSpeciesData());
                                    }
                                });*/
                                    liveData.postValue(new SealedNetworkResult.Success<>(dashboardData.getSpeciesData()));

                                } catch (Exception e) {
                                    liveData.postValue(new SealedNetworkResult.Error<>("Error in Fetching Data"));

                                }
                            } else {
                                liveData.postValue(new SealedNetworkResult.Error<>("Something went wrong"));

                            }


                        } else if (response.code() == 400) {
                            try {
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                JSONArray jsonArray = jObjError.getJSONArray("Errors");
                                String errorMessage = jsonArray.getString(0);
                                Toast.makeText(application, errorMessage, Toast.LENGTH_SHORT).show();
                                liveData.postValue(new SealedNetworkResult.Error<>("Something went wrong"));

                            } catch (Exception e) {
                                liveData.postValue(new SealedNetworkResult.Error<>("Something went wrong"));

                                Toast.makeText(application, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        } else if (response.code() == 401) {
                            liveData.postValue(new SealedNetworkResult.Error<>("Something went wrong"));

                            Toast.makeText(application, "Invalid or missing access_token provided", Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 403) {
                            liveData.postValue(new SealedNetworkResult.Error<>("Something went wrong"));

                            Toast.makeText(application, "The access token was decoded successfully but did not include a scope appropriate to this endpoint", Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 404) {
                            liveData.postValue(new SealedNetworkResult.Error<>("Something went wrong"));

                            Toast.makeText(application, "Resource not found", Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 429) {
                            liveData.postValue(new SealedNetworkResult.Error<>("Something went wrong"));

                            Toast.makeText(application, "Too many recent requests from you. Wait to make further submissions", Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 500) {
                            liveData.postValue(new SealedNetworkResult.Error<>("Something went wrong"));

                            Toast.makeText(application, "Internal server error.The request was not completed due to an internal error on the server side", Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 503) {
                            liveData.postValue(new SealedNetworkResult.Error<>("Something went wrong"));

                            Toast.makeText(application, "Service unavailable.The server was unavailable", Toast.LENGTH_SHORT).show();
                        } else {
                            liveData.postValue(new SealedNetworkResult.Error<>("Something went wrong"));
                            Toast.makeText(application, "Something went wrong ! Please contact to administration !", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        liveData.postValue(new SealedNetworkResult.Error<>("Error in Fetching Data"));

                    }
                }

                @Override
                public void onFailure(Call<BaseResponse<DashboardData>> call, Throwable t) {
                    liveData.postValue(new SealedNetworkResult.Error<>(t.getMessage()));
                }
            });

        } else {

            Toast.makeText(application, "You are offline", Toast.LENGTH_SHORT).show();

            liveData.setValue(new SealedNetworkResult.Error<>("Offline"));
           /* speciesDataDao.getSpeciesDate().observeForever(new Observer<List<SpeciesData>>() {
                @Override
                public void onChanged(List<SpeciesData> speciesDataList) {
                    if (speciesDataList != null) {
                        liveData.setValue(new SealedNetworkResult.Success<>(speciesDataList));

                    }

                }
            });*/


        }
        return liveData;

    }


    public LiveData<List<TreeData>> getTreeDataBetweenDates(Date startDate, Date endDate) {


        return treeDataDao.getTreeDataBetweenDates(startDate, endDate);
    }

    public void getTreeDataDialog(List<String> speciesList, String rf, String distict, Date startDate, Date endDate) {


        executorService.execute(new Runnable() {
            @Override
            public void run() {
                customTreeDataListDialog = treeDataDao.getExportData(speciesList, rf, distict, startDate, endDate);
            }
        });

      /*  customTreeDataListDialog.observeForever(treeDataList -> {

            Log.d("newCheck___21133", "getTreeDataBetweenDates: Observed Data:getTreeDataDialog " + treeDataList.size());
        });*/


    }

    public LiveData<SealedNetworkResult<List<ExportData>>> getExportList(ExportListRequest exportListRequest) {
        MutableLiveData<SealedNetworkResult<List<ExportData>>> liveData = new MutableLiveData<>();

        liveData.setValue(new SealedNetworkResult.Loading<>());
        apiService.getExportList(exportListRequest).enqueue(new Callback<BaseResponse<ExportList>>() {
            @Override
            public void onResponse(Call<BaseResponse<ExportList>> call, Response<BaseResponse<ExportList>> response) {
                if (response.isSuccessful() && response.body() != null && response.code() == 200) {

                    if (response.body().getData()!=null && response.body().getData().getExportDataList() != null && response.body().getData().getExportDataList().size() > 0) {
                        ExportList exportList = response.body().getData();
                        liveData.postValue(new SealedNetworkResult.Success<>(exportList.getExportDataList()));

                    } else {
                        liveData.postValue(new SealedNetworkResult.Error<>("No Data Found"));
                        Toast.makeText(application, "No Data Found!", Toast.LENGTH_SHORT).show();


                    }


                } else if (response.isSuccessful() && response.body() != null) {
                    for (int i = 0; i < response.body().getErrors().size(); i++) {
                        liveData.postValue(new SealedNetworkResult.Error<>(response.body().getErrors().get(i)));

                    }

                } else if (response.code() == 400) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        JSONArray jsonArray = jObjError.getJSONArray("Errors");
                        String errorMessage = jsonArray.getString(0);
                        Toast.makeText(application, errorMessage, Toast.LENGTH_SHORT).show();
                        liveData.postValue(new SealedNetworkResult.Error<>("Something went wrong"));

                    } catch (Exception e) {
                        liveData.postValue(new SealedNetworkResult.Error<>("Something went wrong"));

                        Toast.makeText(application, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } else if (response.code() == 401) {
                    liveData.postValue(new SealedNetworkResult.Error<>("Something went wrong"));

                    Toast.makeText(application, "Invalid or missing access_token provided", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 403) {
                    liveData.postValue(new SealedNetworkResult.Error<>("Something went wrong"));

                    Toast.makeText(application, "The access token was decoded successfully but did not include a scope appropriate to this endpoint", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 404) {
                    liveData.postValue(new SealedNetworkResult.Error<>("Something went wrong"));

                    Toast.makeText(application, "Resource not found", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 429) {
                    liveData.postValue(new SealedNetworkResult.Error<>("Something went wrong"));

                    Toast.makeText(application, "Too many recent requests from you. Wait to make further submissions", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 500) {
                    liveData.postValue(new SealedNetworkResult.Error<>("Something went wrong"));

                    Toast.makeText(application, "Internal server error.The request was not completed due to an internal error on the server side", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 503) {
                    liveData.postValue(new SealedNetworkResult.Error<>("Something went wrong"));

                    Toast.makeText(application, "Service unavailable.The server was unavailable", Toast.LENGTH_SHORT).show();
                } else {
                    liveData.postValue(new SealedNetworkResult.Error<>("Something went wrong"));
                    Toast.makeText(application, "Something went wrong ! Please contact to administration !", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<BaseResponse<ExportList>> call, Throwable t) {
                liveData.postValue(new SealedNetworkResult.Error<>(t.getMessage()));
            }
        });

        return liveData;

    }


}
