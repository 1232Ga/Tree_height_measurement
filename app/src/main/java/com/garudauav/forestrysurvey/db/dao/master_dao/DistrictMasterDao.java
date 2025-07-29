package com.garudauav.forestrysurvey.db.dao.master_dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.garudauav.forestrysurvey.models.DistrictMaster;
import com.garudauav.forestrysurvey.models.RFMaster;

import java.util.List;

@Dao
public interface DistrictMasterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDistrictMater(DistrictMaster districtMaster);

    @Delete
    void deleteDistrictMaster(DistrictMaster districtMaster);

    @Query("DELETE FROM district_master")
    void clearDistrictMaster();

    @Query("SELECT * FROM district_master")
    LiveData<List<DistrictMaster>> getDistrictMaster();
}
