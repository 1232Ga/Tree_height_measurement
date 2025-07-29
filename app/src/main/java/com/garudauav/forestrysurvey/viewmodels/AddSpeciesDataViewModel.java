package com.garudauav.forestrysurvey.viewmodels;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.garudauav.forestrysurvey.models.DistrictMaster;
import com.garudauav.forestrysurvey.models.RFMaster;
import com.garudauav.forestrysurvey.models.SpeciesMaster;
import com.garudauav.forestrysurvey.models.TreeData;
import com.garudauav.forestrysurvey.models.response_models.BaseResponse;
import com.garudauav.forestrysurvey.models.response_models.UploadSpeciesDataResponse;
import com.garudauav.forestrysurvey.network.SealedNetworkResult;
import com.garudauav.forestrysurvey.repository.AddSpeciesDataRepository;
import com.garudauav.forestrysurvey.utils.PrefManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class AddSpeciesDataViewModel extends ViewModel {
    private final PrefManager prefManager;
    private final AddSpeciesDataRepository repository;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    public AddSpeciesDataViewModel(PrefManager prefManager, AddSpeciesDataRepository repository) {
        this.prefManager = prefManager;
        this.repository = repository;
        treeDataList = repository.getTreeData();
        loadSpeciesNameList();
        loadDistrictNameList();
        loadRFNameList();
    }
    public MutableLiveData<SealedNetworkResult<BaseResponse>> addSpeciesResponse = new MutableLiveData<>();

    MutableLiveData<List<SpeciesMaster>> speciesNameList = new MutableLiveData<>();
    MutableLiveData<List<RFMaster>> RFNameList = new MutableLiveData<>();
    MutableLiveData<List<DistrictMaster>> districtNameList = new MutableLiveData<>();
    MutableLiveData<Double> latitude = new MutableLiveData<>();
    MutableLiveData<Double> longitude = new MutableLiveData<>();
    int speciesColor= 0xFF00FF00;
    int girthType= 0;
    int heightType= 3;
    MutableLiveData<Float> height = new MutableLiveData<>();
    MutableLiveData<Integer> noOfTree = new MutableLiveData<>(1);
    MutableLiveData<Float> girth = new MutableLiveData<>();
    MutableLiveData<String> speciesName = new MutableLiveData<>();
    MutableLiveData<String> rfName = new MutableLiveData<>();
    MutableLiveData<String> districtName = new MutableLiveData<>();
    MutableLiveData<Bitmap> photo1 = new MutableLiveData<>();
    MutableLiveData<Bitmap> photo2 = new MutableLiveData<>();

    public String blockId,speciesId,districtId;
    private MutableLiveData<SealedNetworkResult<BaseResponse<UploadSpeciesDataResponse>>> uploadResponse = new MutableLiveData<>();

    public LiveData<List<TreeData>> treeDataList = new MutableLiveData<List<TreeData>>();


    public int getGirthType() {
        return girthType;
    }

    public void setGirthType(int girthType) {
        this.girthType = girthType;
    }

    public int getHeightType() {
        return heightType;
    }

    public void setHeightType(int heightType) {
        this.heightType = heightType;
    }



    // Method to set data in the ViewModel
    public void setLatitude(double data) {
        latitude.setValue(data);
    }

    public void setLongitude(double data) {
        longitude.setValue(data);
    }

    public void setHeight(Float newHeight) {
        height.setValue(newHeight);
    }

    public void setNoOfTree(Integer newNoOfTree) {
        noOfTree.setValue(newNoOfTree);
    }

    public void setGirth(Float newGirth) {
        girth.setValue(newGirth);
    }

    public void setSpeciesName(String species) {
        speciesName.setValue(species);
    }

    public void setRFName(String rf) {
        rfName.setValue(rf);
    }

    public void setDistrictName(String district) {
        districtName.setValue(district);
    }

    public void setPhoto1(Bitmap photo) {
        photo1.setValue(photo);
    }

    public void setPhoto2(Bitmap photo) {
        photo2.setValue(photo);
    }

    // Method to get LiveData for observing in the View
    public MutableLiveData<Double> getLatitude() {
        return latitude;
    }

    public MutableLiveData<Double> getLongitude() {
        return longitude;
    }

    public MutableLiveData<Float> getHeight() {
        return height;
    }

    public MutableLiveData<Integer> getNoOfTree() {
        return noOfTree;
    }

    public MutableLiveData<Float> getGirth() {
        return girth;
    }

    public MutableLiveData<String> getSpeciesName() {
        return speciesName;
    }

    public MutableLiveData<String> getDistrictName() {
        return districtName;
    }

    public MutableLiveData<String> getRfName() {
        return rfName;
    }

    public MutableLiveData<Bitmap> getPhoto1() {
        return photo1;
    }

    public MutableLiveData<Bitmap> getPhoto2() {
        return photo2;
    }


    public LiveData<List<SpeciesMaster>> getSpeciesNameList() {

        return speciesNameList;

    }

    public LiveData<List<RFMaster>> getRFNameList() {

        return RFNameList;

    }

    public LiveData<List<DistrictMaster>> getDistrictNameList() {

        return districtNameList;

    }
    public LiveData<SealedNetworkResult<BaseResponse<UploadSpeciesDataResponse>>> getUploadResponse() {
        return uploadResponse;
    }
    //Method to load speciesNameList data from Repository
    private void loadSpeciesNameList() {

        try {
            repository.getSpeciesNameList().observeForever(new Observer<List<SpeciesMaster>>() {
                @Override
                public void onChanged(List<SpeciesMaster> speciesMasters) {

                    if(speciesMasters!=null){
                        speciesNameList.postValue(speciesMasters);

                    }

                }
            });


        } catch (Exception e) {
            // Handle errors, if any
            e.printStackTrace();
        }


    }

    private void loadRFNameList() {

      repository.getRFNameList().observeForever(new Observer<List<RFMaster>>() {
          @Override
          public void onChanged(List<RFMaster> rfMasters) {
              try {


                  RFNameList.postValue(rfMasters);
              } catch (Exception e) {
                  // Handle errors, if any
                  e.printStackTrace();
              }
          }
      });


    }

    private void loadDistrictNameList() {

        try {
            repository.getDistrictNameList().observeForever(new Observer<List<DistrictMaster>>() {
                @Override
                public void onChanged(List<DistrictMaster> districtMasters) {
                    districtNameList.postValue(districtMasters);
                }
            });


        } catch (Exception e) {
            // Handle errors, if any
            e.printStackTrace();
        }


    }


    public boolean insertTreeData() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

// Create a Date object representing the current date and time
        Date currentDate = new Date();

// Format the Date object in UTC format
        String utcDateString = sdf.format(currentDate);
        repository.insertTreeData(new TreeData(prefManager.getUserCode(),latitude.getValue(), longitude.getValue(),
                height.getValue(), heightType,noOfTree.getValue(), girth.getValue(),girthType,
                speciesName.getValue(), rfName.getValue(), districtName.getValue(),
                sdf.parse(utcDateString),speciesColor, photo1.getValue(),
                photo2.getValue(),districtId,speciesId, blockId));
        return true;
    }



}
