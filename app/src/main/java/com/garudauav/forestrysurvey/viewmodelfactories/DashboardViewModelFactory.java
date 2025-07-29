package com.garudauav.forestrysurvey.viewmodelfactories;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.garudauav.forestrysurvey.repository.DashBoardRepository;
import com.garudauav.forestrysurvey.utils.PrefManager;
import com.garudauav.forestrysurvey.viewmodels.DashboardViewModel;

public class DashboardViewModelFactory implements ViewModelProvider.Factory {

    private final PrefManager prefManager;
    private final DashBoardRepository repository;

    public DashboardViewModelFactory(PrefManager prefManager, DashBoardRepository repository) {
        this.prefManager = prefManager;
        this.repository = repository;
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DashboardViewModel.class)) {
            return (T) new DashboardViewModel(prefManager,repository);
        }
        try {
            throw new IllegalAccessException("UnKnown View Model");
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }
}
