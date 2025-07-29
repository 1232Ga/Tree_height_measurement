package com.garudauav.forestrysurvey.db.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.garudauav.forestrysurvey.db.convertors.Convertors;
import com.garudauav.forestrysurvey.db.dao.SpeciesDataDao;
import com.garudauav.forestrysurvey.db.dao.TreeDataDao;
import com.garudauav.forestrysurvey.db.dao.master_dao.DistrictMasterDao;
import com.garudauav.forestrysurvey.db.dao.master_dao.RFMasterDao;
import com.garudauav.forestrysurvey.db.dao.master_dao.SpeciesMasterDao;
import com.garudauav.forestrysurvey.db.retry_module.RetryTable;
import com.garudauav.forestrysurvey.db.retry_module.RetryTableDao;
import com.garudauav.forestrysurvey.models.DistrictMaster;
import com.garudauav.forestrysurvey.models.RFMaster;
import com.garudauav.forestrysurvey.models.SpeciesData;
import com.garudauav.forestrysurvey.models.SpeciesMaster;
import com.garudauav.forestrysurvey.models.TreeData;

import java.util.concurrent.Executors;

@Database(entities = {TreeData.class, SpeciesData.class, SpeciesMaster.class, DistrictMaster.class, RFMaster.class, RetryTable.class}, version = 1)
@TypeConverters(Convertors.class)
public abstract class ForestryDataBase extends RoomDatabase {

    private static ForestryDataBase instance;

    public abstract TreeDataDao treeDataDao();
    public abstract RetryTableDao retryTableDao();
    public abstract SpeciesDataDao speciesDataDao();

    public abstract SpeciesMasterDao speciesMasterDao();
    public abstract RFMasterDao rfMasterDao();
    public abstract DistrictMasterDao districtMasterDao();

    public static synchronized ForestryDataBase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), ForestryDataBase.class, "forestry_database")
                    .fallbackToDestructiveMigration()
                    .setJournalMode(RoomDatabase.JournalMode.AUTOMATIC)
                    .setQueryExecutor(Executors.newSingleThreadExecutor())
                    .addCallback(roomCallback)
                    .build();
        }

        return instance;
    }

    // below line is to create a callback for our room database.
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            // this method is called when database is created
            // and below line is to populate our data.
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    // we are creating an async task class to perform task in background.
    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        PopulateDbAsyncTask(ForestryDataBase instance) {
            TreeDataDao dao = instance.treeDataDao();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}
