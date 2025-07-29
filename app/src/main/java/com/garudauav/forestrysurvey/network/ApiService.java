package com.garudauav.forestrysurvey.network;

import com.garudauav.forestrysurvey.models.BlockData;
import com.garudauav.forestrysurvey.models.DashboardData;
import com.garudauav.forestrysurvey.models.DistrictMaster;
import com.garudauav.forestrysurvey.models.LoginAuthRootResponse;
import com.garudauav.forestrysurvey.models.RFMaster;
import com.garudauav.forestrysurvey.models.SpeciesMaster;
import com.garudauav.forestrysurvey.models.TreeData;
import com.garudauav.forestrysurvey.models.request_models.AddSpeciesValue;
import com.garudauav.forestrysurvey.models.request_models.DashboardDataRequest;
import com.garudauav.forestrysurvey.models.request_models.ExportListRequest;
import com.garudauav.forestrysurvey.models.request_models.LoginAuthRequest;
import com.garudauav.forestrysurvey.models.request_models.LoginRequest;
import com.garudauav.forestrysurvey.models.request_models.TreeDataRequest;
import com.garudauav.forestrysurvey.models.request_models.UploadSpeciesDataRequest;
import com.garudauav.forestrysurvey.models.request_models.UserIdRequest;
import com.garudauav.forestrysurvey.models.response_models.AddSpeciesResponse;
import com.garudauav.forestrysurvey.models.response_models.BaseResponse;
import com.garudauav.forestrysurvey.models.response_models.ExportList;
import com.garudauav.forestrysurvey.models.response_models.LoginResponse;
import com.garudauav.forestrysurvey.models.response_models.SyncHistoryList;
import com.garudauav.forestrysurvey.models.response_models.UploadSpeciesDataResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {

    //login api
    @POST("login")
    Call<BaseResponse<LoginResponse>> loginUser(@Body LoginRequest loginRequest);

    // add data of tree
    @POST("upload")
    Call<BaseResponse<UploadSpeciesDataResponse>> uploadSpeciesData(@Body UploadSpeciesDataRequest uploadTreeDataRequest);

    //get all data for dashboard
    @POST("Species/data")
    Call<BaseResponse<DashboardData>> getTreeData( @Query("pageIndex") int pageIndex,
                                                   @Query("pageSize") int pageSize,@Body DashboardDataRequest dashboardDataRequest);

    //get data for dashboard date wise
    @POST("Species/data")
    Call<BaseResponse<DashboardData>> getCustomTreeData( @Query("pageIndex") int pageIndex,
                                                         @Query("pageSize") int pageSize,@Body DashboardDataRequest dashboardDataRequest);


    //ExportDataList
    @POST("Species/dataToExport")
    Call<BaseResponse<ExportList>> getExportList(@Body ExportListRequest exportListRequest);


    //species Master data
    @POST("SpeciesMaster/list")
    Call<BaseResponse<List<SpeciesMaster>>> getSpeciesMaster();

    //District Master data
    @GET("districts")
    Call<BaseResponse<List<DistrictMaster>>> getDistrictMaster();

    //RF Master data
    @GET("app/block/list")
    Call<BaseResponse<BlockData>> getRFMaster();
    /*@POST("reservforests/list")
    Call<BaseResponse<List<RFMaster>>> getRFMaster();*/

    //login api
    @POST("login")
    Call<LoginAuthRootResponse> doLogin(@Body LoginAuthRequest loginAuthRequest);

    //history sync api
    @POST("GetUploadingData")
    Call<BaseResponse<SyncHistoryList>> getSyncHistory(@Body UserIdRequest userIdRequest);

    //add species value
    @POST("Species/addSpeciesMaster")
    Call<BaseResponse<Void>> addSpeciesValue(@Body AddSpeciesValue addSpeciesValue);

    //upload sync api
    @Multipart
    @POST("upload/data")
    Call<UploadSpeciesDataResponse> uploadSpeciesData1(@Part MultipartBody.Part Img1,
                                                       @Part MultipartBody.Part Img2,
                                                       @Part("SpeciesMasterId") RequestBody SpeciesMasterId,
                                                       @Part("Coordinates") RequestBody Coordinates,
                                                       @Part("Height") RequestBody Height,
                                                       @Part("CapturedDate") RequestBody CapturedDate,
                                                       @Part("UnitHeight") RequestBody UnitHeight,
                                                       @Part("NumberOftree") RequestBody NumberOftree,
                                                       @Part("Girth") RequestBody Girth,
                                                       @Part("UnitGirth") RequestBody UnitGirth,
                                                       @Part("blockId") RequestBody blockId,
                                                       @Part("District") RequestBody District,
                                                       @Part("UserId") RequestBody UserId,
                                                       @Part("OtpCode") RequestBody OtpCode,
                                                       @Part("BatchNumber") RequestBody batchNumber,
                                                       @Part("BatchRecordCount") RequestBody batchRecordCount

    );

}
