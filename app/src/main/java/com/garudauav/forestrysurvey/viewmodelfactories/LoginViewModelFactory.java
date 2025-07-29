package com.garudauav.forestrysurvey.viewmodelfactories;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.garudauav.forestrysurvey.repository.DashBoardRepository;
import com.garudauav.forestrysurvey.repository.LoginRepository;
import com.garudauav.forestrysurvey.utils.PrefManager;
import com.garudauav.forestrysurvey.viewmodels.DashboardViewModel;
import com.garudauav.forestrysurvey.viewmodels.LoginViewModel;

public class LoginViewModelFactory implements ViewModelProvider.Factory {

    private final LoginRepository repository;

    public LoginViewModelFactory( LoginRepository repository) {

        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel( repository);
        }
        try {
            throw new IllegalAccessException("UnKnown View Model");
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
