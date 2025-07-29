package com.garudauav.forestrysurvey.repository;

import android.app.Application;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.garudauav.forestrysurvey.db.dao.TreeDataDao;
import com.garudauav.forestrysurvey.db.database.ForestryDataBase;
import com.garudauav.forestrysurvey.db.retry_module.RetryTable;
import com.garudauav.forestrysurvey.db.retry_module.RetryTableDao;
import com.garudauav.forestrysurvey.models.SyncData;
import com.garudauav.forestrysurvey.models.SyncHistory;
import com.garudauav.forestrysurvey.models.TreeData;
import com.garudauav.forestrysurvey.models.request_models.ExportListRequest;
import com.garudauav.forestrysurvey.models.request_models.UserIdRequest;
import com.garudauav.forestrysurvey.models.response_models.BaseResponse;
import com.garudauav.forestrysurvey.models.response_models.ExportData;
import com.garudauav.forestrysurvey.models.response_models.ExportList;
import com.garudauav.forestrysurvey.models.response_models.SyncHistoryList;
import com.garudauav.forestrysurvey.network.ApiService;
import com.garudauav.forestrysurvey.network.RetrofitClient;
import com.garudauav.forestrysurvey.network.SealedNetworkResult;
import com.garudauav.forestrysurvey.utils.PrefManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SyncRepository {
    private MutableLiveData<List<TreeData>> _treeDataList = new MutableLiveData<>();
    public LiveData<List<TreeData>> treeDataList = _treeDataList;
    private MutableLiveData<List<RetryTable>> _retryDataList = new MutableLiveData<>();
    public LiveData<List<RetryTable>> retryDataList = _retryDataList;
    private final ApiService apiService = RetrofitClient.getApiService();
    private final Application application;


    private TreeDataDao treeDataDao;
    private RetryTableDao retryTableDao;
    private PrefManager prefManager;


    public SyncRepository(Application application) {
        ForestryDataBase dataBase = ForestryDataBase.getInstance(application);
        treeDataDao = dataBase.treeDataDao();
        retryTableDao = dataBase.retryTableDao();
        this.application = application;
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        prefManager = new PrefManager(application);

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                _retryDataList.postValue(retryTableDao.getTreeData());
            }
        });
        treeDataDao.getTreeDataByDate(prefManager.getUserCode()).observeForever(new Observer<List<TreeData>>() {
            @Override
            public void onChanged(List<TreeData> treeData) {
                _treeDataList.postValue(treeData);
            }
        });

    }


    public LiveData<SealedNetworkResult<List<SyncHistory>>> getSyncHistory(UserIdRequest userIdRequest) {
        MutableLiveData<SealedNetworkResult<List<SyncHistory>>> liveData = new MutableLiveData<>();

        liveData.setValue(new SealedNetworkResult.Loading<>());
        apiService.getSyncHistory(userIdRequest).enqueue(new Callback<BaseResponse<SyncHistoryList>>() {
            @Override
            public void onResponse(Call<BaseResponse<SyncHistoryList>> call, Response<BaseResponse<SyncHistoryList>> response) {

                try {
                    if (response.isSuccessful() && response.body().isSuccess() && response.body() != null && response.code() == 200) {

                        if (response.body().getData().getSyncHistoryList() != null && response.body().getData().getSyncHistoryList().size() > 0) {
                            SyncHistoryList syncHistoryList = response.body().getData();


                            liveData.postValue(new SealedNetworkResult.Success<>(syncHistoryList.getSyncHistoryList()));

                        } else {
                            liveData.postValue(new SealedNetworkResult.Error<>("No Data Found"));
                            //     Toast.makeText(application, "No Data Found!", Toast.LENGTH_SHORT).show();


                        }


                    } else if (response.isSuccessful() && response.body() != null) {
                        for (int i = 0; i < response.body().getErrors().size(); i++) {
                            liveData.postValue(new SealedNetworkResult.Error<>(response.body().getErrors().get(i)));

                        }
                        if (response.body().getErrors().size() == 0) {
                            liveData.postValue(new SealedNetworkResult.Error<>(response.body().getMessage()));

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

                            //  Toast.makeText(application, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    liveData.postValue(new SealedNetworkResult.Error<>("No Data Found"));
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<SyncHistoryList>> call, Throwable t) {
                liveData.postValue(new SealedNetworkResult.Error<>(t.getMessage()));
            }
        });

        return liveData;

    }


}
