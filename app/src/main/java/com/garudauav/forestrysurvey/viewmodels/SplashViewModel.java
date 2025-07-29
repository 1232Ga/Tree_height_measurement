package com.garudauav.forestrysurvey.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.garudauav.forestrysurvey.repository.SplashRepository;
import com.garudauav.forestrysurvey.utils.PrefManager;

public class SplashViewModel extends ViewModel {

    private final PrefManager prefManager;
    private final SplashRepository repository;

    public SplashViewModel(PrefManager prefManager,SplashRepository repository) {
        this.prefManager = prefManager;
        this.repository=repository;
        repository.getSpeciesNameList();
        repository.getDistrictNameList();
        repository.getRFNameList();
    }

    private MutableLiveData<Boolean> isUserLoggedIn = new MutableLiveData<>();

    public LiveData<Boolean> getIsUserLoggedIn() {
        if (prefManager.isUserLogin()) {
            isUserLoggedIn.setValue(true);
        } else {
            isUserLoggedIn.setValue(false);

        }
        return isUserLoggedIn;
    }

    public void refreshSpeciesList(){
        repository.getSpeciesNameList();

    }
}
