package com.garudauav.forestrysurvey.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.garudauav.forestrysurvey.models.request_models.LoginRequest;
import com.garudauav.forestrysurvey.models.response_models.BaseResponse;
import com.garudauav.forestrysurvey.network.SealedNetworkResult;
import com.garudauav.forestrysurvey.models.response_models.LoginResponse;
import com.garudauav.forestrysurvey.repository.LoginRepository;
import com.garudauav.forestrysurvey.utils.PrefManager;

public class LoginViewModel extends ViewModel {

    private final LoginRepository repository;

    private MutableLiveData<SealedNetworkResult<BaseResponse<LoginResponse>>> sealedLoginResponse = new MutableLiveData<>();


    public LoginViewModel(LoginRepository repository) {
        this.repository = repository;
    }

    public LiveData<SealedNetworkResult<BaseResponse<LoginResponse>>> getLoginResponse() {
        return sealedLoginResponse;
    }


    public void login(LoginRequest request) {
        repository.getLoginResponse(request, sealedLoginResponse);
    }
}
