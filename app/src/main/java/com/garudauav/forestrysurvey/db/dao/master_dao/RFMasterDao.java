package com.garudauav.forestrysurvey.db.dao.master_dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.garudauav.forestrysurvey.models.RFMaster;
import com.garudauav.forestrysurvey.models.SpeciesData;

import java.util.List;

@Dao
public interface RFMasterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRfMater(RFMaster rfMaster);

    @Delete
    void deleteRfMaster(RFMaster rfMaster);

    @Query("DELETE FROM rf_master")
    void clearRfMaster();

    @Query("SELECT * FROM rf_master")
    LiveData<List<RFMaster>> getRfMaster();
}
