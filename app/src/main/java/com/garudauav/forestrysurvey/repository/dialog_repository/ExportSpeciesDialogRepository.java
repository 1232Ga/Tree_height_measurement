package com.garudauav.forestrysurvey.repository.dialog_repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.garudauav.forestrysurvey.db.dao.TreeDataDao;
import com.garudauav.forestrysurvey.db.database.ForestryDataBase;
import com.garudauav.forestrysurvey.models.DistrictMaster;
import com.garudauav.forestrysurvey.models.RFMaster;
import com.garudauav.forestrysurvey.models.TreeData;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ExportSpeciesDialogRepository {

    private final TreeDataDao treeDataDao;
    private LiveData<List<TreeData>> treeData;

    public ExportSpeciesDialogRepository(Application application) {

        ForestryDataBase dataBase = ForestryDataBase.getInstance(application);
        treeDataDao = dataBase.treeDataDao();
        //  treeData = treeDataDao.getTreeData();
    }

    public LiveData<List<TreeData>> getTreeData(){

        return treeData;
    }
    public LiveData<List<TreeData>> getTreeDataBetweenDates(Date startDate, Date endDate) {
        treeData = treeDataDao.getTreeDataBetweenDates(startDate, endDate);
        Log.d("newCheck___2", "getTreeDataBetweenDates:in repo "+treeData.getValue());
        return treeDataDao.getTreeDataBetweenDates(startDate, endDate);
    }

    public List<RFMaster> getRFNameList() {
        return Arrays.asList(new RFMaster("34","Umkhuti_block1","abc","",true,"45678chjk","Umkhuti"),
                new RFMaster("43","random_block2","def","",true,"7ihvbnkl","random"));
    }

    public List<DistrictMaster> getDistrictNameList() {
        return Arrays.asList(new DistrictMaster("35","East Khasi Hills","",true),new DistrictMaster("45","West Khasi Hills","",true));
    }
}
