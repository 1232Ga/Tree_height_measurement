package com.garudauav.forestrysurvey.viewmodelfactories;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.garudauav.forestrysurvey.repository.SplashRepository;
import com.garudauav.forestrysurvey.utils.PrefManager;
import com.garudauav.forestrysurvey.viewmodels.SplashViewModel;

public class SplashViewModelFactory implements ViewModelProvider.Factory {
    private final PrefManager prefManager;
    private final SplashRepository repository;

    public SplashViewModelFactory(PrefManager prefManager,SplashRepository repository) {
        this.prefManager = prefManager;
        this.repository=repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SplashViewModel.class)) {
            return (T) new SplashViewModel(prefManager,repository);
        }
        try {
            throw new IllegalAccessException("UnKnown View Model");
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }
}
