package com.garudauav.forestrysurvey.viewmodelfactories;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.garudauav.forestrysurvey.repository.DashBoardRepository;
import com.garudauav.forestrysurvey.repository.SyncRepository;
import com.garudauav.forestrysurvey.utils.PrefManager;
import com.garudauav.forestrysurvey.viewmodels.SyncViewModel;

public class SyncViewModelFactory implements ViewModelProvider.Factory {
    private final PrefManager prefManager;
    private final SyncRepository repository;

    public SyncViewModelFactory(PrefManager prefManager, SyncRepository repository) {
        this.prefManager = prefManager;
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SyncViewModel.class)) {
            return (T) new SyncViewModel(prefManager,repository);
        }
        try {
            throw new IllegalAccessException("UnKnown View Model");
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
