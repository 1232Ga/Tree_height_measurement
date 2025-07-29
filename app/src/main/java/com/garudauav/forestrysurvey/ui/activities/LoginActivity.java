package com.garudauav.forestrysurvey.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.garudauav.forestrysurvey.BaseClass;
import com.garudauav.forestrysurvey.R;
import com.garudauav.forestrysurvey.databinding.ActivityLoginBinding;
import com.garudauav.forestrysurvey.databinding.CodeWarningDialogBinding;
import com.garudauav.forestrysurvey.models.LoginAuthRootResponse;
import com.garudauav.forestrysurvey.models.request_models.LoginAuthRequest;
import com.garudauav.forestrysurvey.models.response_models.BaseResponse;
import com.garudauav.forestrysurvey.models.response_models.LoginResponse;
import com.garudauav.forestrysurvey.network.ApiService;
import com.garudauav.forestrysurvey.network.RetrofitClient;
import com.garudauav.forestrysurvey.network.SealedNetworkResult;
import com.garudauav.forestrysurvey.repository.LoginRepository;
import com.garudauav.forestrysurvey.utils.CommonFunctions;
import com.garudauav.forestrysurvey.utils.CustomTypefaceSpan;
import com.garudauav.forestrysurvey.utils.PrefManager;
import com.garudauav.forestrysurvey.viewmodelfactories.LoginViewModelFactory;
import com.garudauav.forestrysurvey.viewmodels.LoginViewModel;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseClass {

    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;
    private PrefManager prefManager;

    private LoginRepository repository;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        prefManager = new PrefManager(this);
        repository = new LoginRepository();
        Intent intent = getIntent();

        String loginType = intent.getStringExtra("login_type");
        prefManager.setFirstTimeLaunch(false);
        assert loginType != null;
        if (loginType.equalsIgnoreCase("code")) {
            binding.userCodeLay.setVisibility(View.VISIBLE);
            binding.credentialLay.setVisibility(View.GONE);
            binding.txtEnterDetails.setText(R.string.enter_generated);

        } else {
            binding.userCodeLay.setVisibility(View.GONE);
            binding.credentialLay.setVisibility(View.VISIBLE);

        }

        viewModel = new ViewModelProvider(this, new LoginViewModelFactory(repository)).get(LoginViewModel.class);
        // currently this is not in use

     /*   viewModel.loginResponse.observe(LoginActivity.this, new Observer<LoginResponse>() {
            @Override
            public void onChanged(LoginResponse loginResponse) {
                if(loginResponse!=null && !loginResponse.getName().isEmpty()){
                    prefManager.setUserLogin(true);
                    prefManager.setUserName(loginResponse.getName());
                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                    finish();
                }
            }
        });*/


        //login api response Live data
        // currently this is not in use
        viewModel.getLoginResponse().observe(LoginActivity.this, new Observer<SealedNetworkResult<BaseResponse<LoginResponse>>>() {
            @Override
            public void onChanged(SealedNetworkResult<BaseResponse<LoginResponse>> result) {
                //if api hit successfully
                if (result instanceof SealedNetworkResult.Success) {
                    BaseResponse<LoginResponse> loginResponse = ((SealedNetworkResult.Success<BaseResponse<LoginResponse>>) result).getData();
                    //when api give response

                    if (loginResponse != null && !loginResponse.isSuccess()) {
                        prefManager.setUserLogin(true);
                        prefManager.setUserName(loginResponse.getData().getName());
                        startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                        finish();
                        //Toast.makeText(LoginActivity.this, "success", Toast.LENGTH_SHORT).show();
                    }
                }
                //when their is error in hitting api
                else if (result instanceof SealedNetworkResult.Error) {
                    String errorMessage = ((SealedNetworkResult.Error<BaseResponse<LoginResponse>>) result).getMessage();
                    //   Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));

                }
                //onLoading
                else if (result instanceof SealedNetworkResult.Loading) {
                    // Toast.makeText(LoginActivity.this, "Loading", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (loginType.equalsIgnoreCase("code")) {

                    if (binding.userCode.getText().toString().isEmpty()) {
                        binding.userCode.setError("Please enter code");

                    } else if (binding.userCode.getText().toString().length() < 8) {
                        binding.userCode.setError("Please enter valid code");

                    } else {
                        String input = binding.userCode.getText().toString();
                        if (!input.matches("^(?=.*[0-9])(?=.*[a-zA-Z])[a-zA-Z0-9]+$")) {
                            binding.userCode.setError("Please enter valid code.");
                        } else {
                            showCodeWarningDialog(input);

                        }


                    }

                } else {
                    if (!validateEmail(binding.usernames)) {
                        return;
                    } else if (!isValidPassword(binding.password.getText().toString(), binding.password, true)) {
                        return;
                    } else {
                        if (CommonFunctions.isNetworkConnectionAvailable(LoginActivity.this)) {

                            doLogin(binding.usernames.getText().toString(),
                                    binding.password.getText().toString(), "");
                        } else {
                            Toast.makeText(LoginActivity.this, "No internet..", Toast.LENGTH_SHORT).show();
                        }
                        //viewModel.login(new LoginRequest(binding.usernames.getText().toString(), binding.password.getText().toString()));

                    }
                }


            }
        });


    }

    public static boolean isAlphanumeric(String str) {
        return str != null && str.matches("^[a-zA-Z0-9]+$");
    }

    @Override
    public void onBackPressed() {
        // moveTaskToBack(true);
        super.onBackPressed();

    }


    //function for login
    private void doLogin(String usernames, String password, String garuda12) {

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        LoginAuthRequest loginAuthRequest = new LoginAuthRequest(usernames, password,
                garuda12);
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<LoginAuthRootResponse> call = apiService.doLogin(loginAuthRequest);
        call.enqueue(new Callback<LoginAuthRootResponse>() {
            @Override
            public void onResponse(Call<LoginAuthRootResponse> call, Response<LoginAuthRootResponse> response) {
                progressDialog.dismiss();

                if (response.code() == 200) {
                    try {
                        if (response.body().success) {

                            prefManager.setUserLogin(true);
                            prefManager.setUserName(response.body().data.firstName);
                            prefManager.setUserId(response.body().data.userId);
                            prefManager.setUserCode(response.body().data.otp);
                            prefManager.setLoginWith("user_login");
                            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                            startActivity(intent);
                            Toast.makeText(LoginActivity.this, "Login successfully !", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Invalid username or password.", Toast.LENGTH_SHORT).show();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 400) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        JSONArray jsonArray = jObjError.getJSONArray("Errors");
                        String errorMessage = jsonArray.getString(0);
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } else if (response.code() == 401) {
                    Toast.makeText(LoginActivity.this, "Invalid or missing access_token provided", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 403) {
                    Toast.makeText(LoginActivity.this, "The access token was decoded successfully but did not include a scope appropriate to this endpoint", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 404) {
                    Toast.makeText(LoginActivity.this, "Resource not found", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 429) {
                    Toast.makeText(LoginActivity.this, "Too many recent requests from you. Wait to make further submissions", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 500) {
                    Toast.makeText(LoginActivity.this, "Internal server error.The request was not completed due to an internal error on the server side", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 503) {
                    Toast.makeText(LoginActivity.this, "Service unavailable.The server was unavailable", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Something went wrong ! Please contact to administration !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginAuthRootResponse> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    private AlertDialog exportDialog;

    private void showCodeWarningDialog(String input) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.MyDialogStyle);
        CodeWarningDialogBinding codeWarningDialogBinding = CodeWarningDialogBinding.inflate(LayoutInflater.from(this));
        builder.setView(codeWarningDialogBinding.getRoot());
        builder.setCancelable(false);
        String inputCode = input; // Assuming input is your variable for the code
        String warningMessage2 = "The entered code " + inputCode + " cannot be changed, please recheck once again.";
        SpannableString spannableString2 = new SpannableString(warningMessage2);
        int start2 = warningMessage2.indexOf(inputCode);
        int end2 = start2 + inputCode.length();
        Typeface customFont = ResourcesCompat.getFont(this, R.font.montserrat_bold);
        spannableString2.setSpan(new CustomTypefaceSpan("",customFont), start2, end2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        codeWarningDialogBinding.warningText.setText(spannableString2);
// codeWarningDialogBinding.warningText.setText("The entered code "+input + " can not be changed,\nplease recheck once again.");

        codeWarningDialogBinding.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportDialog.dismiss();
                binding.userCode.setFocusable(true);
            }
        });

        codeWarningDialogBinding.continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefManager.setUserLogin(true);
                prefManager.setUserCode(input);
                prefManager.setLoginWith("user_code");
                startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                finish();
            }
        });
        exportDialog = builder.create();
        exportDialog.show();
    }

}