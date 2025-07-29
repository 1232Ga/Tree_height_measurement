package com.garudauav.forestrysurvey.db.dao.master_dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.garudauav.forestrysurvey.models.SpeciesData;
import com.garudauav.forestrysurvey.models.SpeciesMaster;

import java.util.List;

@Dao
public interface SpeciesMasterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSpeciesMaster(SpeciesMaster speciesMaster);

    @Delete
    void deleteSpeciesMaster(SpeciesMaster speciesMaster);

    @Query("DELETE FROM species_master")
    void clearSpeciesMaster();

    @Query("SELECT * FROM species_master")
    LiveData<List<SpeciesMaster>> getSpeciesMaster();
}
