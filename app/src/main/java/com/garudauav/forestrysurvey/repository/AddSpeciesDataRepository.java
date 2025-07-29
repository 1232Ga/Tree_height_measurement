package com.garudauav.forestrysurvey.repository;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.garudauav.forestrysurvey.db.dao.TreeDataDao;
import com.garudauav.forestrysurvey.db.dao.master_dao.DistrictMasterDao;
import com.garudauav.forestrysurvey.db.dao.master_dao.RFMasterDao;
import com.garudauav.forestrysurvey.db.dao.master_dao.SpeciesMasterDao;
import com.garudauav.forestrysurvey.db.database.ForestryDataBase;
import com.garudauav.forestrysurvey.models.DistrictMaster;
import com.garudauav.forestrysurvey.models.RFMaster;
import com.garudauav.forestrysurvey.models.SpeciesMaster;
import com.garudauav.forestrysurvey.models.TreeData;
import com.garudauav.forestrysurvey.models.request_models.AddSpeciesValue;
import com.garudauav.forestrysurvey.models.request_models.UploadSpeciesDataRequest;
import com.garudauav.forestrysurvey.models.response_models.AddSpeciesResponse;
import com.garudauav.forestrysurvey.models.response_models.BaseResponse;
import com.garudauav.forestrysurvey.models.response_models.UploadSpeciesDataResponse;
import com.garudauav.forestrysurvey.network.ApiService;
import com.garudauav.forestrysurvey.network.RetrofitClient;
import com.garudauav.forestrysurvey.network.SealedNetworkResult;
import com.garudauav.forestrysurvey.utils.CommonFunctions;
import com.garudauav.forestrysurvey.utils.PrefManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddSpeciesDataRepository {

    private final TreeDataDao treeDataDao;
    private LiveData<List<TreeData>> treeData;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final RFMasterDao rfMasterDao;
    private final DistrictMasterDao districtMasterDao;
    private final SpeciesMasterDao speciesMasterDao;

    private PrefManager prefManager;

    public AddSpeciesDataRepository(Application application) {
        ForestryDataBase dataBase = ForestryDataBase.getInstance(application);
        prefManager=new PrefManager(application);
        treeDataDao = dataBase.treeDataDao();
        treeData = treeDataDao.getTreeData(prefManager.getUserCode());

        rfMasterDao = dataBase.rfMasterDao();
        districtMasterDao = dataBase.districtMasterDao();
        speciesMasterDao = dataBase.speciesMasterDao();
    }


    public void insertTreeData(TreeData data) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                treeDataDao.insertTreeData(data);
                treeData = treeDataDao.getTreeData(prefManager.getUserCode());

            }
        });
    }

    public void deleteTreeData(TreeData data){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                treeDataDao.deleteTreeData(data);
            }
        });
    }

    public void updateTreeData(TreeData data){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                treeDataDao.updateTreeData(data);
            }
        });
    }

    public LiveData<List<TreeData>> getTreeData(){

        return treeData;
    }


    public LiveData<List<SpeciesMaster> >getSpeciesNameList() {
        return speciesMasterDao.getSpeciesMaster() ;
    }

    public LiveData<List<RFMaster>> getRFNameList() {
        return rfMasterDao.getRfMaster();
    }

    public LiveData<List<DistrictMaster>> getDistrictNameList() {

        return districtMasterDao.getDistrictMaster();
    }
}
