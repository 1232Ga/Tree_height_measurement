package com.garudauav.forestrysurvey.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.garudauav.forestrysurvey.models.SpeciesData;

import java.util.List;

@Dao
public interface SpeciesDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSpeciesData(SpeciesData speciesData);

    @Delete
    void deleteSpeciesData(SpeciesData speciesData);

    @Query("DELETE FROM species_data")
    void clearSpeciesData();

    @Query("SELECT * FROM species_data")
    LiveData<List<SpeciesData>> getSpeciesDate();
}
