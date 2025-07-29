package com.garudauav.forestrysurvey.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.garudauav.forestrysurvey.db.dao.TreeDataDao;
import com.garudauav.forestrysurvey.db.database.ForestryDataBase;
import com.garudauav.forestrysurvey.models.TreeData;

import java.util.Date;
import java.util.List;

public class TreeDataViewRepository {

    private TreeDataDao treeDataDao;
    private LiveData<List<TreeData>> treeData;

    public TreeDataViewRepository(Application application) {
        ForestryDataBase dataBase = ForestryDataBase.getInstance(application);
        treeDataDao = dataBase.treeDataDao();
      //  treeData = treeDataDao.getTreeData();
    }

    public LiveData<List<TreeData>> getTreeData() {

        return treeData;
    }

    public LiveData<List<TreeData>> getTreeDataBetweenDates(Date startDate, Date endDate) {
        treeData = treeDataDao.getTreeDataBetweenDates(startDate, endDate);
        Log.d("newCheck___2", "getTreeDataBetweenDates:in repo " + treeData.getValue());
        return treeDataDao.getTreeDataBetweenDates(startDate, endDate);
    }
}
