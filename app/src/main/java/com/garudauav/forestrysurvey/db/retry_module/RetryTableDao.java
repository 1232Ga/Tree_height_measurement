package com.garudauav.forestrysurvey.db.retry_module;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.garudauav.forestrysurvey.models.TreeData;

import java.util.List;

@Dao
public interface RetryTableDao {

    @Insert
    void insertTreeData(RetryTable data);

    @Delete
    void deleteTreeData(RetryTable data);

    @Query("DELETE  FROM retry_table")
    void clearTreeData();

    @Query("DELETE FROM retry_table WHERE id IN (:idList)")
    void deleteTreeDataByIds(List<Integer> idList);

    @Query("DELETE FROM retry_table WHERE id = :id")
    void deleteTreeDataById(Integer id);

    @Query("SELECT * FROM retry_table WHERE id = :id")
    RetryTable getTreeDataById(Integer id);
    @Query("SELECT * FROM retry_table")
    List<RetryTable> getTreeData();

    @Query("SELECT * FROM retry_table ORDER BY date DESC")
    LiveData<List<RetryTable>> getTreeDataByDate();



}
