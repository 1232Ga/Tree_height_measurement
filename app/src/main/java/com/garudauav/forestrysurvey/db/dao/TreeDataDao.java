package com.garudauav.forestrysurvey.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.garudauav.forestrysurvey.models.TreeData;

import java.util.Date;
import java.util.List;

@Dao
public interface TreeDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTreeData(TreeData data);

    @Delete
    void deleteTreeData(TreeData data);

    @Query("DELETE  FROM tree_data")
    void clearTreeData();

    @Query("DELETE FROM tree_data WHERE id IN (:idList)")
    void deleteTreeDataByIds(List<Integer> idList);

    @Query("DELETE FROM tree_data WHERE id = :id")
    void deleteTreeDataById(Integer id);


    @Update
    void updateTreeData(TreeData data);

    @Query("SELECT * FROM tree_data WHERE userCode=:userCode")
    LiveData<List<TreeData>> getTreeData(String userCode);

    @Query("SELECT * FROM tree_data WHERE userCode=:userCode  ORDER BY date DESC")
    LiveData<List<TreeData>> getTreeDataByDate(String userCode);

    @Query("SELECT * FROM tree_data WHERE date BETWEEN :startDate AND :endDate")
    LiveData<List<TreeData>> getTreeDataBetweenDates(Date startDate, Date endDate);

    @Query("SELECT * FROM tree_data WHERE date IN (:selectedDates)")
    List<TreeData> getTreeSyncData(List<Date> selectedDates);

    @Query("SELECT * FROM tree_data WHERE id IN (:idList)")
    List<TreeData> getTreeDataByIds(List<Integer> idList);

/*    @Query("SELECT * FROM tree_data WHERE species = :species")
    LiveData<List<TreeData>> getDataBySpecies(String species);*/

    @Query("SELECT * FROM tree_data WHERE species IN (:speciesList) AND rf = :rf AND district = :district AND date BETWEEN :startDate AND :endDate")
    LiveData<List<TreeData>> getExportData(List<String> speciesList, String rf, String district, Date startDate, Date endDate);
}
//