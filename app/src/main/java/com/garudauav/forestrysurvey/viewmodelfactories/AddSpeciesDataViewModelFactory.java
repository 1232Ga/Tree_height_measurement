package com.garudauav.forestrysurvey.viewmodelfactories;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.garudauav.forestrysurvey.repository.AddSpeciesDataRepository;
import com.garudauav.forestrysurvey.utils.PrefManager;
import com.garudauav.forestrysurvey.viewmodels.AddSpeciesDataViewModel;

public class AddSpeciesDataViewModelFactory implements ViewModelProvider.Factory {
    private final PrefManager prefManager;
    private final AddSpeciesDataRepository repository;

    public AddSpeciesDataViewModelFactory(PrefManager prefManager, AddSpeciesDataRepository repository) {
        this.prefManager = prefManager;
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
       if(modelClass.isAssignableFrom(AddSpeciesDataViewModel.class)){
           return (T) new AddSpeciesDataViewModel(prefManager, repository);
       }
       try {
            throw new IllegalAccessException("UnKnown View Model");
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }


}
