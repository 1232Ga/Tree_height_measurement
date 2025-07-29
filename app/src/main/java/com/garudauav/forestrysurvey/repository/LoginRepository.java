package com.garudauav.forestrysurvey.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.garudauav.forestrysurvey.models.request_models.LoginRequest;
import com.garudauav.forestrysurvey.models.response_models.BaseResponse;
import com.garudauav.forestrysurvey.network.SealedNetworkResult;
import com.garudauav.forestrysurvey.models.response_models.LoginResponse;
import com.garudauav.forestrysurvey.network.ApiService;
import com.garudauav.forestrysurvey.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginRepository {
   private final ApiService apiService= RetrofitClient.getApiService();

    /*private MutableLiveData<LoginResponse> _loginResponse=new MutableLiveData<>();
    public LiveData<LoginResponse> loginResponse=_loginResponse;
*/
    public void getLoginResponse(LoginRequest loginRequest, MutableLiveData<SealedNetworkResult<BaseResponse<LoginResponse>>> sealedLoginResponse){
        sealedLoginResponse.setValue(new SealedNetworkResult.Loading<>());

        apiService.loginUser(loginRequest).enqueue(new Callback<BaseResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<BaseResponse<LoginResponse>> call, Response<BaseResponse<LoginResponse>> response) {
                try{
                    if(response.isSuccessful() && response.code()==200){
                        sealedLoginResponse.setValue(new SealedNetworkResult.Success<>(response.body()));

                        //     _loginResponse.postValue(response.body());
                    }else{
                        sealedLoginResponse.setValue(new SealedNetworkResult.Error<>("Login failed"));

                        //  _loginResponse.postValue(new LoginResponse("deepak","password"));
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<LoginResponse>> call, Throwable t) {
                Log.d("erorr___", "onFailure: "+t.getMessage());
                sealedLoginResponse.setValue(new SealedNetworkResult.Error<>("Network error"));

            }
        });

/*        apiService.loginUser(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

              //  _loginResponse.postValue(new LoginResponse("deepak","password"));

            }
        });*/
        //return loginResponse;
    }



}
