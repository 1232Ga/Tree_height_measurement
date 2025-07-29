package com.garudauav.forestrysurvey.repository;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.garudauav.forestrysurvey.db.dao.master_dao.DistrictMasterDao;
import com.garudauav.forestrysurvey.db.dao.master_dao.RFMasterDao;
import com.garudauav.forestrysurvey.db.dao.master_dao.SpeciesMasterDao;
import com.garudauav.forestrysurvey.db.database.ForestryDataBase;
import com.garudauav.forestrysurvey.models.BlockData;
import com.garudauav.forestrysurvey.models.DistrictMaster;
import com.garudauav.forestrysurvey.models.RFMaster;
import com.garudauav.forestrysurvey.models.SpeciesMaster;
import com.garudauav.forestrysurvey.models.response_models.BaseResponse;
import com.garudauav.forestrysurvey.network.ApiService;
import com.garudauav.forestrysurvey.network.RetrofitClient;
import com.garudauav.forestrysurvey.network.SealedNetworkResult;
import com.garudauav.forestrysurvey.utils.CommonFunctions;
import com.garudauav.forestrysurvey.utils.PrefManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashRepository {

    private final RFMasterDao rfMasterDao;
    private final DistrictMasterDao districtMasterDao;
    private final SpeciesMasterDao speciesMasterDao;
    private final ApiService apiService = RetrofitClient.getApiService();

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();


    Application application;
    PrefManager prefManager;

    public SplashRepository(Application application, PrefManager prefManager) {
        this.application = application;
        this.prefManager = prefManager;

        //initialize database and DAOs
        ForestryDataBase dataBase = ForestryDataBase.getInstance(application);
        rfMasterDao = dataBase.rfMasterDao();
        districtMasterDao = dataBase.districtMasterDao();
        speciesMasterDao = dataBase.speciesMasterDao();

    }

    public void getSpeciesNameList() {

        MutableLiveData<SealedNetworkResult<List<SpeciesMaster>>> liveData = new MutableLiveData<>();
        //when user is online
        if (CommonFunctions.isNetworkConnectionAvailable(application)) {
            // when loading data
            liveData.setValue(new SealedNetworkResult.Loading<>());


            try {
                apiService.getSpeciesMaster().enqueue(new Callback<BaseResponse<List<SpeciesMaster>>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<List<SpeciesMaster>>> call, Response<BaseResponse<List<SpeciesMaster>>> response) {
                        try {
                            if (response.isSuccessful() && response.body() != null && response.code() == 200) {
                                List<SpeciesMaster> speciesMasterList = response.body().getData();
                                if (speciesMasterList != null && prefManager.getSpeciesCount() != speciesMasterList.size()) {
                                    executorService.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            prefManager.setSpeciesMasterCount(speciesMasterList.size());
                                            prefManager.setSpeciesCount(speciesMasterList.size());
                                            speciesMasterDao.clearSpeciesMaster();
                                            for (SpeciesMaster speciesMaster : speciesMasterList) {
                                                if (speciesMaster.isActive()) {
                                                    speciesMasterDao.insertSpeciesMaster(speciesMaster);

                                                }

                                            }

                                        }

                                    });
                                }
                                //when api hit successfully
                                liveData.postValue(new SealedNetworkResult.Success<>(speciesMasterList));
                            } else {
                                //when api hit with some error
                                liveData.postValue(new SealedNetworkResult.Error<>("Error in fetching fata"));

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            liveData.postValue(new SealedNetworkResult.Error<>("Something went wrong"));

                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<List<SpeciesMaster>>> call, Throwable t) {
                        //when api not hit successfully
                        liveData.postValue(new SealedNetworkResult.Error<>(t.getMessage() + ""));


                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //when user is offline
            try {
                speciesMasterDao.getSpeciesMaster().observeForever(new Observer<List<SpeciesMaster>>() {
                    @Override
                    public void onChanged(List<SpeciesMaster> speciesMasters) {

                        executorService.execute(new Runnable() {
                            @Override
                            public void run() {
                                //when user comes first time and he/she is offline
                                if (speciesMasters == null || speciesMasters.size() == 0) {
                                    prefManager.setSpeciesMasterCount(0);
                                    speciesMasterDao.clearSpeciesMaster();
                                }
                            }

                        });


                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    public void getDistrictNameList() {

        MutableLiveData<SealedNetworkResult<List<DistrictMaster>>> liveData = new MutableLiveData<>();
        //when network available
        if (CommonFunctions.isNetworkConnectionAvailable(application)) {
            //on loading
            liveData.setValue(new SealedNetworkResult.Loading<>());
            try {
                apiService.getDistrictMaster().enqueue(new Callback<BaseResponse<List<DistrictMaster>>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<List<DistrictMaster>>> call, Response<BaseResponse<List<DistrictMaster>>> response) {
                        if (response.isSuccessful() && response.body() != null && response.code()==200) {
                            List<DistrictMaster> districtMasterList = response.body().getData();
                            if (districtMasterList != null && prefManager.getDestrictCount() != districtMasterList.size()) {
                                prefManager.setDestrictCount(districtMasterList.size());
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

                            }
                            //when api hit successfully
                            liveData.postValue(new SealedNetworkResult.Success<>(districtMasterList));
                        } else {
                            //when api hit with some error
                            liveData.postValue(new SealedNetworkResult.Error<>("Error in Fetching Data"));

                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<List<DistrictMaster>>> call, Throwable t) {
                        //when api doesn't hit successfully
                        liveData.postValue(new SealedNetworkResult.Error<>(t.getMessage() + ""));


                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

            districtMasterDao.getDistrictMaster().observeForever(new Observer<List<DistrictMaster>>() {
                @Override
                public void onChanged(List<DistrictMaster> districtMasters) {


                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            //when user comes first time and he/she is offline
                            if (districtMasters == null || districtMasters.size() == 0) {
                                prefManager.setSpeciesMasterCount(0);
                                districtMasterDao.clearDistrictMaster();
                                //  districtMasterDao.insertDistrictMater(new DistrictMaster("ec4d0c68-3861-4ba5-b8b9-41e92b70b9a1", "other", "", true));

                            }


                        }

                    });


                }
            });


        }


    }

    public void getRFNameList() {

        MutableLiveData<SealedNetworkResult<List<RFMaster>>> liveData = new MutableLiveData<>();
        //when user is online
        if (CommonFunctions.isNetworkConnectionAvailable(application)) {
            //on loading
            liveData.setValue(new SealedNetworkResult.Loading<>());
            try {
                apiService.getRFMaster().enqueue(new Callback<BaseResponse<BlockData>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<BlockData>> call, Response<BaseResponse<BlockData>> response) {

                        if (response.isSuccessful() && response.body() != null &&response.code()==200 && response.body().isSuccess() && response.body().getData()!=null) {
                            List<RFMaster> rfMasterList = response.body().getData().getBlockList();
                            prefManager.setRfCount(11);
                            if (rfMasterList != null && prefManager.getRfCount() != rfMasterList.size()) {
                                prefManager.setRfCount(rfMasterList.size());
                                executorService.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        rfMasterDao.clearRfMaster();
                                        for (RFMaster rfMaster : rfMasterList) {
                                           // if (rfMaster.isActive()) {
                                                rfMasterDao.insertRfMater(rfMaster);

                                           // }

                                        }

                                    }

                                });

                            }
                            //when api hit successfully
                            liveData.postValue(new SealedNetworkResult.Success<>(rfMasterList));
                        } else {
                            //when api hit with some error
                            liveData.postValue(new SealedNetworkResult.Error<>("Error in Fetching Data"));

                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<BlockData>> call, Throwable t) {
                        //when api doesn't hit successfully

                        liveData.postValue(new SealedNetworkResult.Error<>(t.getMessage() + ""));


                    }
                });

            } catch (Exception e) {
                e.printStackTrace();

            }
        } else {

            rfMasterDao.getRfMaster().observeForever(new Observer<List<RFMaster>>() {
                @Override
                public void onChanged(List<RFMaster> rfMasters) {


                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            //when user comes first time and he/she is offline

                            if (rfMasters == null || rfMasters.size() == 0) {
                                rfMasterDao.clearRfMaster();
                                prefManager.setSpeciesMasterCount(0);
                                //  rfMasterDao.insertRfMater(new RFMaster("9be0b5e4-a6b9-4062-86e3-bb2ca03b4a3d", "other", "", true));

                            }
                        }
                    });
                }
            });

        }


    }
}
