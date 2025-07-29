package com.garudauav.forestrysurvey.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.garudauav.forestrysurvey.models.DistrictMaster;
import com.garudauav.forestrysurvey.models.RFMaster;
import com.garudauav.forestrysurvey.models.SpeciesData;
import com.garudauav.forestrysurvey.models.SpeciesMaster;
import com.garudauav.forestrysurvey.models.SyncData;
import com.garudauav.forestrysurvey.models.TreeData;
import com.garudauav.forestrysurvey.models.request_models.AddSpeciesValue;
import com.garudauav.forestrysurvey.models.request_models.DashboardDataRequest;
import com.garudauav.forestrysurvey.models.request_models.ExportListRequest;
import com.garudauav.forestrysurvey.models.response_models.BaseResponse;
import com.garudauav.forestrysurvey.models.response_models.ExportData;
import com.garudauav.forestrysurvey.network.SealedNetworkResult;
import com.garudauav.forestrysurvey.repository.DashBoardRepository;
import com.garudauav.forestrysurvey.utils.PrefManager;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DashboardViewModel extends ViewModel {

    private boolean isRequestInProgress = false;
    public List<String> selectedSpeciesId = new ArrayList<>();
    public List<String> selectedSpeciesName = new ArrayList<>();

    public MutableLiveData<String> syncBadgeCount = new MutableLiveData<>("0");
    MutableLiveData<List<PieEntry>> pieEntries = new MutableLiveData<>();
    List<SpeciesData> speciesDataCustom = new ArrayList<>();
    List<SpeciesData> speciesDataToday = new ArrayList<>();

    MutableLiveData<List<PieEntry>> pieEntriesDialog = new MutableLiveData<>();
    MutableLiveData<Date> fromDate = new MutableLiveData<>();
    MutableLiveData<Date> toDate = new MutableLiveData<>();

    MutableLiveData<Date> fromDateDialog = new MutableLiveData<>();
    MutableLiveData<Date> toDateDialog = new MutableLiveData<>();
    MutableLiveData<List<Integer>> speciesColor = new MutableLiveData<>();
    MutableLiveData<String> rfName = new MutableLiveData<>();
    MutableLiveData<String> districtName = new MutableLiveData<>();
    MutableLiveData<List<String>> selectedSpeciesListDialog = new MutableLiveData<>();


    private final DashBoardRepository repository;

    public DashboardViewModel(PrefManager prefManager, DashBoardRepository repository) {

        this.repository = repository;
        setTreeValue();

    }


    public void clearData() {
        treeDataList.setValue(new ArrayList<>());
        pieEntries.setValue(new ArrayList<>());
        speciesColor.setValue(new ArrayList<>());

    }

    void setTreeValue() {
        repository.getTreeData().observeForever(new Observer<List<TreeData>>() {
            @Override
            public void onChanged(List<TreeData> data) {
                if (data != null) {
                    treeDataList.postValue(data);
                }
            }
        });
    }


    public MutableLiveData<List<TreeData>> treeDataList = new MutableLiveData<>();


    public String selectedSpecies = "";



    //New LiveData
    public MutableLiveData<List<SpeciesMaster>> speciesMaster = new MutableLiveData<>();
    public List<SpeciesMaster> speciesMaster1 = new ArrayList<>();
    public MutableLiveData<List<RFMaster>> RfMaster = new MutableLiveData<>();
    public MutableLiveData<List<DistrictMaster>> districtMaster = new MutableLiveData<>();


    //Today Fragment Livedata

    public MutableLiveData<SealedNetworkResult<List<SpeciesData>>> todayDashboardData = new MutableLiveData<>();
    public MutableLiveData<SealedNetworkResult<BaseResponse>> addSpeciesResponse = new MutableLiveData<>();


    //customData Fragment livedata
    public MutableLiveData<SealedNetworkResult<List<SpeciesData>>> customDashboardData = new MutableLiveData<>();







    private MutableLiveData<List<String>> selectedSpeciesList = new MutableLiveData<>();

    public void setSelectedSpeciesList(List<String> totalTree) {
        selectedSpeciesList.setValue(totalTree);
    }

    public LiveData<List<String>> getSelectedSpeciesList() {
        return selectedSpeciesList;
    }


    public MutableLiveData<Date> getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate.setValue(fromDate);
    }

    public MutableLiveData<Date> getFromDateDialog() {
        return fromDateDialog;
    }

    public void setFromDateDialog(Date fromDateDialog) {
        this.fromDateDialog.setValue(fromDateDialog);
    }


    public MutableLiveData<Date> getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate.setValue(toDate);
    }

    public MutableLiveData<Date> getToDateDialog() {
        return toDateDialog;
    }

    public void setToDateDialog(Date toDateDialog) {
        this.toDateDialog.setValue(toDateDialog);
    }


    public MutableLiveData<List<Integer>> getSpeciesColors() {
        return speciesColor;
    }

    private final MutableLiveData<Boolean> isLastPage = new MutableLiveData<>(false);

    public LiveData<Boolean> getIsLastPage() {
        return isLastPage;
    }

    public boolean isRequestInProgress() {
        return isRequestInProgress;
    }

    public void addSpeciesColor(int colorCode) {
        List<Integer> currentEntries = speciesColor.getValue();
        if (currentEntries == null) {
            currentEntries = new ArrayList<>();
        }
        currentEntries.add(colorCode);
        speciesColor.setValue(currentEntries);
    }

    public MutableLiveData<List<PieEntry>> getPieEntries() {
        if (pieEntries.getValue() == null) {
            pieEntries.setValue(new ArrayList<>());
        }
        return pieEntries;
    }

    public void clearPieEntries() {
        pieEntries.setValue(new ArrayList<>());
        speciesColor.setValue(new ArrayList<>());
    }

    public MutableLiveData<List<String>> getSelectedSpeciesListDialog() {
        if (selectedSpeciesListDialog.getValue() == null) {
            selectedSpeciesListDialog.setValue(new ArrayList<>());
        }
        return selectedSpeciesListDialog;
    }

    public MutableLiveData<List<PieEntry>> getPieEntriesDialog() {
        if (pieEntriesDialog.getValue() == null) {
            pieEntriesDialog.setValue(new ArrayList<>());
        }
        return pieEntriesDialog;
    }

    public MutableLiveData<String> getDistrictName() {
        return districtName;
    }

    public MutableLiveData<String> getRfName() {
        return rfName;
    }

    public void setRFName(String rf) {
        rfName.setValue(rf);
    }

    public void setDistrictName(String district) {
        districtName.setValue(district);
    }

    public void setSelectedSpeciesListDialog(List<String> speciesList) {
        selectedSpeciesListDialog.setValue(speciesList);
    }

    public void addPieEntry(float value, String label, int colorCode) {
        List<PieEntry> currentEntries = pieEntries.getValue();
        if (currentEntries == null) {
            currentEntries = new ArrayList<>();
        }
        currentEntries.add(new PieEntry(value, label, colorCode));
        pieEntries.setValue(currentEntries);
    }

    public void addPieEntryDialog(float value, String label, int colorCode) {
        List<PieEntry> currentEntries = pieEntriesDialog.getValue();
        if (currentEntries == null) {
            currentEntries = new ArrayList<>();
        }
        currentEntries.add(new PieEntry(value, label, colorCode));
        pieEntriesDialog.setValue(currentEntries);
    }


    public void updatePieEntryValueDialog(int index, float newValue) {
        List<PieEntry> currentEntries = pieEntriesDialog.getValue();
        if (currentEntries != null && index >= 0 && index < currentEntries.size()) {
            PieEntry existingEntry = currentEntries.get(index);
            currentEntries.set(index, new PieEntry(newValue, existingEntry.getLabel(), existingEntry.getData()));
            pieEntriesDialog.setValue(currentEntries);
        }
    }

    public LiveData<SealedNetworkResult<List<ExportData>>> getExportData(ExportListRequest exportListRequest) {
        return repository.getExportList(exportListRequest);
    }






  /*  public void getTreeDataBetweenDates(Date startDate, Date endDate) {
        repository.getTreeDataBetweenDates(startDate, endDate).observeForever(treeDataList -> {
            customTreeDataList.postValue(treeDataList);
        });
    }*/


    public void addSpeciesValue(AddSpeciesValue addSpeciesValue) {
        repository.addSpeciesValue(addSpeciesValue).observeForever(new Observer<SealedNetworkResult<BaseResponse>>() {
            @Override
            public void onChanged(SealedNetworkResult<BaseResponse> result) {
                addSpeciesResponse.setValue(result);
            }
        });
    }

    public void getTreeDataBetweenDates(DashboardDataRequest dashboardDataRequest, int currentPageIndex, boolean fromPagination) {
        repository.getDashBoardDataDateWise(dashboardDataRequest, currentPageIndex).observeForever(new Observer<SealedNetworkResult<List<SpeciesData>>>() {
            @Override
            public void onChanged(SealedNetworkResult<List<SpeciesData>> result) {

               // customDashboardData.postValue(result);

                if (result instanceof SealedNetworkResult.Success) {

                    if (!fromPagination) {

                        speciesDataCustom.clear();
                    }/*else{
                        clearPieEntries();
                    }*/
                    List<SpeciesData> currentData = speciesDataCustom != null ? speciesDataCustom : new ArrayList<>();

                    //List<SpeciesData> currentData = customDashboardData.getValue() instanceof SealedNetworkResult.Success ? ((SealedNetworkResult.Success<List<SpeciesData>>) customDashboardData.getValue()).getData() : new ArrayList<>();

                    List<SpeciesData> newData = ((SealedNetworkResult.Success<List<SpeciesData>>) result).getData();


                 /*   if (!isNextPage) {
                        currentData.clear(); // Clear current data if it's not a next page request
                    }*/
                    currentData.addAll(newData);
                    speciesDataCustom = currentData;

                    customDashboardData.setValue(new SealedNetworkResult.Success<>(currentData));
                    isLastPage.setValue(newData.isEmpty());
                    // currentPageIndex++;// Assuming if no data returned, it's the last page
                } else {
                    Log.d("customDashboardData__", "onChanged: CL");

                    customDashboardData.setValue(result); // Directly pass error or loading states
                }
                isRequestInProgress = false;
              //  customDashboardData.setValue(result);
            }


        });
    }

    public void getDashboardData(DashboardDataRequest dashboardDataRequest, int currentPageIndex, boolean fromPagination) {

        repository.getDashBoardData(dashboardDataRequest, currentPageIndex,fromPagination).observeForever(new Observer<SealedNetworkResult<List<SpeciesData>>>() {
            public void onChanged(SealedNetworkResult<List<SpeciesData>> result) {
                if (result instanceof SealedNetworkResult.Success) {

                    if (!fromPagination) {

                        speciesDataToday.clear();
                    }
                    List<SpeciesData> currentData = speciesDataToday != null ? speciesDataToday : new ArrayList<>();
                    List<SpeciesData> newData = ((SealedNetworkResult.Success<List<SpeciesData>>) result).getData();

                    currentData.addAll(newData);
                    speciesDataToday = currentData;


                    todayDashboardData.setValue(new SealedNetworkResult.Success<>(currentData));
                    isLastPage.setValue(newData.isEmpty());
                    // currentPageIndex++;// Assuming if no data returned, it's the last page
                } else {
                    todayDashboardData.setValue(result); // Directly pass error or loading states
                }
                isRequestInProgress = false;
            }
        });
    }


    public void processTreeDataList(List<TreeData> treeDataList) {
        List<SyncData> syncDataList = new ArrayList<>();


        //  HashMap to group TreeData by date
        Map<Date, List<TreeData>> treeDataMap = new HashMap<>();
        for (TreeData treeData : treeDataList) {
            Date date = treeData.getDate();
            if (!treeDataMap.containsKey(date)) {
                treeDataMap.put(date, new ArrayList<>());
            }
            treeDataMap.get(date).add(treeData);
        }

        // Process the grouped data and update syncDataList
        for (Map.Entry<Date, List<TreeData>> entry : treeDataMap.entrySet()) {
            Date date = entry.getKey();
            List<TreeData> treeDataGroup = entry.getValue();

            List<Integer> idList = new ArrayList<>();
            int imageCount = 0;

            for (TreeData treeData : treeDataGroup) {
                idList.add(treeData.getId());
                imageCount += 2;
            }

            // Add the processed data to syncDataList
            syncDataList.add(new SyncData(idList, date, imageCount, false));
        }

        //setSyncBadgeCount(syncDataList.size()+"");
        syncBadgeCount.setValue(syncDataList.size() + "");

    }

 /*   public void getTreeDataDialog(List<String> SpeciesList, String Rf, String district, Date startDate, Date endDate) {
        repository.getTreeDataDialog(SpeciesList, Rf, district, startDate, endDate);

        repository.getCustomTreeDataListDialog().observeForever(new Observer<List<TreeData>>() {
            @Override
            public void onChanged(List<TreeData> treeData) {
                if (treeData != null) {
                    customTreeDataListDialog.postValue(treeData);
                }
            }
        });
    }*/


    /* public void getTreeDataBetweenDates(Date startDate, Date endDate) {




         customTreeDataList =repository.getTreeDataBetweenDates(startDate, endDate);
         //Log.d("newCheck___123", "onChanged: in viewmodel before " + repository.getTreeData().getValue().size());
         *//*customTreeDataList.observeForever(treeDataList -> {
            Log.d("newCheck___211 ", "getTreeDataBetweenDates: Observed Data: in viewmodel" + treeDataList.size());
        });*//*

      //  Log.d("newCheck___211 ", "getTreeDataBetweenDates: Observed Data: after observer" + customTreeDataList.getValue().size());


        //return customTreeDataList;



    }*/


    public void loadRFNameList() {
        try {
            repository.getRFNameList().observeForever(new Observer<List<RFMaster>>() {
                @Override
                public void onChanged(List<RFMaster> rfMasters) {

                    if (rfMasters != null) {
                        RfMaster.postValue(rfMasters);

                        //  Log.d("waxfghkbv", "onChanged: "+rfMasters.get(0).getReservForestMasterName());

                    }

                }
            });


        } catch (Exception e) {
            // Handle errors, if any
            e.printStackTrace();
        }
    }
   /* private void loadRFNameList() {

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<RFMaster> items = repository.getRFNameList();

                    RFNameList.postValue(items);
                } catch (Exception e) {
                    // Handle errors, if any
                    e.printStackTrace();
                }
            }
        });


    }*/

    public void loadDistrictNameList() {

        try {
            repository.getDistrictNameList().observeForever(new Observer<List<DistrictMaster>>() {
                @Override
                public void onChanged(List<DistrictMaster> districtMasters) {

                    if (districtMasters != null && districtMasters.size() > 0) {
                        districtMaster.postValue(districtMasters);

//                        Log.d("waxfghkbv", "onChanged: "+districtMasters.get(0).getDistrictName());

                    }

                }
            });


        } catch (Exception e) {
            // Handle errors, if any
            e.printStackTrace();
        }
    }

 /*   private void loadDistrictNameList() {

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<DistrictMaster> items = repository.getDistrictNameList();

                    districtNameList.postValue(items);
                } catch (Exception e) {
                    // Handle errors, if any
                    e.printStackTrace();
                }
            }
        });


    }*/


    public void loadSpeciesNameList() {


        try {
            repository.getSpeciesNameList().observeForever(new Observer<List<SpeciesMaster>>() {
                @Override
                public void onChanged(List<SpeciesMaster> speciesMasters) {

                    if (speciesMasters != null) {
                        speciesMaster.postValue(speciesMasters);
                        speciesMaster1 = speciesMasters;

//                     Log.d("waxfghkbv", "onChanged: "+speciesMasters.get(0).getSpeciesName());

                    }

                }
            });


        } catch (Exception e) {
            // Handle errors, if any
            e.printStackTrace();
        }

        //   return repository.getSpeciesNameList( );
    }


}
