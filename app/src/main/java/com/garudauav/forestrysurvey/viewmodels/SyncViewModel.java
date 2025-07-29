package com.garudauav.forestrysurvey.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.garudauav.forestrysurvey.db.retry_module.RetryTable;
import com.garudauav.forestrysurvey.models.BatchData;
import com.garudauav.forestrysurvey.models.SyncData;
import com.garudauav.forestrysurvey.models.SyncHistory;
import com.garudauav.forestrysurvey.models.TreeData;
import com.garudauav.forestrysurvey.models.request_models.ExportListRequest;
import com.garudauav.forestrysurvey.models.request_models.UserIdRequest;
import com.garudauav.forestrysurvey.models.response_models.ExportData;
import com.garudauav.forestrysurvey.network.SealedNetworkResult;
import com.garudauav.forestrysurvey.repository.DashBoardRepository;
import com.garudauav.forestrysurvey.repository.SyncRepository;
import com.garudauav.forestrysurvey.utils.PrefManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SyncViewModel extends ViewModel {

    private final PrefManager prefManager;
    private final SyncRepository repository;

    public List<RetryTable> retryTableList=new ArrayList<>();

    public LiveData<List<TreeData>> treeDataList;
    public MutableLiveData<SealedNetworkResult<List<SyncHistory>>> syncHistoryList=new MutableLiveData<>();

    public MutableLiveData<List<SyncData>> syncDataListLiveData=new MutableLiveData<>();
    public MutableLiveData<List<BatchData>> batchDataListLiveData=new MutableLiveData<>();

    public SyncViewModel(PrefManager prefManager, SyncRepository repository) {
        this.prefManager = prefManager;
        this.repository = repository;
        treeDataList = repository.treeDataList;
        repository.retryDataList.observeForever(new Observer<List<RetryTable>>() {
            @Override
            public void onChanged(List<RetryTable> retryTables) {
                retryTableList=retryTables;
            }
        });
    }
    public void getExportData(UserIdRequest userIdRequest){
         repository.getSyncHistory(userIdRequest).observeForever(new Observer<SealedNetworkResult<List<SyncHistory>>>() {
             @Override
             public void onChanged(SealedNetworkResult<List<SyncHistory>> listSealedNetworkResult) {
                 if(listSealedNetworkResult !=null){
                     syncHistoryList.postValue(listSealedNetworkResult);
                 };

             }
         });
    }


    public void processTreeDataList(List<TreeData> treeDataList) {
        List<BatchData> batchDataList = new ArrayList<>();
        Map<Date, List<TreeData>> treeDataMap = new HashMap<>();

        // Group TreeData by date
        for (TreeData treeData : treeDataList) {
            Date date = treeData.getDate();
            treeDataMap.computeIfAbsent(date, k -> new ArrayList<>()).add(treeData);
        }

        // Process each date's TreeData
        for (Map.Entry<Date, List<TreeData>> entry : treeDataMap.entrySet()) {
            Date date = entry.getKey();
            List<TreeData> treeDataGroup = entry.getValue();

            List<Integer> currentBatchIds = new ArrayList<>();
            int treeCount = 0;
            for (int i = 0; i < treeDataGroup.size(); i++) {
                TreeData treeData = treeDataGroup.get(i);
                currentBatchIds.add(treeData.getId());
                treeCount += treeData.getNoOfTree();

                boolean isBatchFull = currentBatchIds.size() == 50;
                boolean isLastItem = i == treeDataGroup.size() - 1;

                // Check if the batch is full or if it's the last item in the list
                if (isBatchFull || isLastItem) {
                    String batchId = generateBatchId();
                    batchDataList.add(new BatchData(batchId, date, new ArrayList<>(currentBatchIds), treeCount, false));

                    // Reset for next batch
                    currentBatchIds.clear();
                    treeCount = 0;
                }
            }
        }

        // Notify observers that batchDataList has changed
        // Assuming you have a MutableLiveData for batchDataList
        batchDataListLiveData.setValue(batchDataList);
    }


    private String generateBatchId() {
        long timestampPart = System.currentTimeMillis() / 1000; // First 10 digits
        int randomPart = new Random().nextInt(9000) + 1000;
        return timestampPart + "" + randomPart; // Concatenates the parts
    }
}
