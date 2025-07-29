package com.garudauav.forestrysurvey.ui.fragments.dashboard_module;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.garudauav.forestrysurvey.adapters.recyclerview_adapters.SpeciesAdapter;
import com.garudauav.forestrysurvey.adapters.recyclerview_adapters.SpeciesCheckBoxAdapter;
import com.garudauav.forestrysurvey.databinding.ExportSpeciesDialogBinding;
import com.garudauav.forestrysurvey.databinding.ExportSpeciesSpinnerDialogBinding;
import com.garudauav.forestrysurvey.databinding.FragmentTodayDataBinding;
import com.garudauav.forestrysurvey.models.DistrictMaster;
import com.garudauav.forestrysurvey.models.RFMaster;
import com.garudauav.forestrysurvey.models.RFMaster2;
import com.garudauav.forestrysurvey.models.SpeciesData;
import com.garudauav.forestrysurvey.models.SpeciesMaster;
import com.garudauav.forestrysurvey.models.TreeData;
import com.garudauav.forestrysurvey.models.request_models.DashboardDataRequest;
import com.garudauav.forestrysurvey.models.request_models.ExportListRequest;
import com.garudauav.forestrysurvey.models.response_models.ExportData;
import com.garudauav.forestrysurvey.network.SealedNetworkResult;
import com.garudauav.forestrysurvey.pagination.PaginationScrollListener;
import com.garudauav.forestrysurvey.repository.DashBoardRepository;
import com.garudauav.forestrysurvey.utils.CommonFunctions;
import com.garudauav.forestrysurvey.utils.IntegerValueFormatter;
import com.garudauav.forestrysurvey.utils.PrefManager;
import com.garudauav.forestrysurvey.utils.callback_interface.DatePickerCallback;
import com.garudauav.forestrysurvey.viewmodelfactories.DashboardViewModelFactory;
import com.garudauav.forestrysurvey.viewmodels.DashboardViewModel;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;


public class TodayDataFragment extends Fragment implements SpeciesCheckBoxAdapter.CheckSpecies, SpeciesAdapter.FilterResultsListener {
    private PieData pieData;
    private PieDataSet pieDataSet;

    private DashboardViewModel viewModel;

    private PrefManager prefManager;
    private DashBoardRepository repository;
    private String TAG = TodayDataFragment.class.getName();

    boolean isSelectAll = false;
    private FragmentTodayDataBinding binding;
    private SpeciesAdapter adapter;
    private ExportSpeciesDialogBinding exportDialogBinding;
    private ExportSpeciesSpinnerDialogBinding speciesDialogBinding;
    private AlertDialog exportDialog;
    private boolean isFromPagination = false;


    private static final int PAGE_START = 1;
    private int TOTAL_PAGES = 0;
    private static final int PAGE_SIZE = 50;
    private int currentPage = PAGE_START;
    private boolean alreadyCheckedTotalRecords = false;
    private String district = "";
    private String fromDate = "";
    private String toDate = "";
    private String rfName = "";
    private ProgressDialog progressDialog;

    public TodayDataFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        progressDialog = new ProgressDialog(requireActivity());
        binding = FragmentTodayDataBinding.inflate(inflater, container, false);

        prefManager = new PrefManager(requireActivity());
        repository = new DashBoardRepository(requireActivity().getApplication(), prefManager);
        viewModel = new ViewModelProvider(requireActivity(), new DashboardViewModelFactory(prefManager, repository)).get(DashboardViewModel.class);


        //for sync badge
        viewModel.treeDataList.observe(requireActivity(), new Observer<List<TreeData>>() {
            @Override
            public void onChanged(List<TreeData> treeData) {
                if (treeData != null && treeData.size() > 0) {
                    viewModel.processTreeDataList(treeData);
                }
            }
        });

        //getting lists from db

        viewModel.loadDistrictNameList();
        viewModel.loadSpeciesNameList();
        viewModel.loadRFNameList();

        //getting data for dashboard
        isFromPagination = false;
        viewModel.getDashboardData(new DashboardDataRequest(prefManager.getUserId()), currentPage, false);


        viewModel.todayDashboardData.observe(requireActivity(), new Observer<SealedNetworkResult<List<SpeciesData>>>() {
            @Override
            public void onChanged(SealedNetworkResult<List<SpeciesData>> result) {
                //when api hit successfully
                if (result instanceof SealedNetworkResult.Success) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            binding.progressCircular.setVisibility(View.GONE);


                        }
                    }, 1000);
                    List<SpeciesData> speciesDataList = ((SealedNetworkResult.Success<List<SpeciesData>>) result).getData();
                    if (speciesDataList != null && speciesDataList.size() > 0) {
                        getEntries(speciesDataList);
                        pieDataSet = new PieDataSet(viewModel.getPieEntries().getValue(), "");
                        pieData = new PieData(pieDataSet);
                        binding.mainLayout.setVisibility(View.VISIBLE);
                        binding.noDataLayout.setVisibility(View.GONE);
                        binding.txtTreeCount.setText(prefManager.getTreeCapturedCount() + "");
                        setUpPieChart();

                    } else {
                        binding.mainLayout.setVisibility(View.GONE);
                        binding.noDataLayout.setVisibility(View.VISIBLE);
                    }
                } else if (result instanceof SealedNetworkResult.Error) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    binding.progressCircular.setVisibility(View.GONE);
                    binding.mainLayout.setVisibility(View.GONE);
                    binding.noDataLayout.setVisibility(View.VISIBLE);
                } else if (result instanceof SealedNetworkResult.Loading) {

                    if (!isFromPagination) {
                        progressDialog.setMessage("Please wait...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                    } else {
                        binding.progressCircular.setVisibility(View.VISIBLE);

                    }


                }
            }
        });


        binding.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExportSpeciesDialog();
            }
        });
        return binding.getRoot();
    }

    private void getEntries(List<SpeciesData> speciesDataList) {
        viewModel.clearPieEntries();
        for (SpeciesData speciesData : speciesDataList) {
            int colorCode = 0;
            try {
                colorCode = Color.parseColor(speciesData.getColorCode());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid color format");
            }
            viewModel.addPieEntry(speciesData.getNumberOfTree(), speciesData.getSpeciesName(), colorCode);
            viewModel.addSpeciesColor(colorCode);
        }


    }

    private void setUpPieChart() {
        pieDataSet.setColors(viewModel.getSpeciesColors().getValue());
        pieDataSet.setDrawValues(true);
        pieDataSet.setValueTextSize(15);
        pieDataSet.setValueTextColor(0xffffffff);
        pieDataSet.setValueFormatter(new IntegerValueFormatter());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        if (!alreadyCheckedTotalRecords) {
            double isNextPage = (double) prefManager.getTotalSpeciesCount() / PAGE_SIZE;
            int replaceFloat = (int) Math.round(isNextPage);
            if (replaceFloat < isNextPage) {
                replaceFloat += 1;
            }
            TOTAL_PAGES = replaceFloat;
            alreadyCheckedTotalRecords = true;
        }
        if (!isFromPagination) {
            binding.speciesRecycler.setLayoutManager(linearLayoutManager);
            adapter = new SpeciesAdapter(requireActivity(), viewModel.getPieEntries().getValue(), TodayDataFragment.this, false);
            binding.speciesRecycler.setAdapter(adapter);

            binding.speciesRecycler.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
                @Override
                protected void loadMoreItems() {
                    currentPage++;
                    if (TOTAL_PAGES >= currentPage) {
                        isFromPagination = true;
                        viewModel.getDashboardData(new DashboardDataRequest(prefManager.getUserId()), currentPage, true);

                    }
                }

                @Override
                public int getTotalPageCount() {
                    return TOTAL_PAGES;
                }

                @Override
                public boolean isLastPage() {
                    return false;
                }

                @Override
                public boolean isLoading() {
                    return false;
                }
            });

        } else {
            adapter.addData(viewModel.getPieEntries().getValue());

        }

        binding.searchSpecies.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }


    private void showExportSpeciesDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        exportDialogBinding = ExportSpeciesDialogBinding.inflate(LayoutInflater.from(requireActivity()));

        builder.setView(exportDialogBinding.getRoot());

        builder.setCancelable(false);


        exportDialogBinding.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                exportDialog.dismiss();
                viewModel.selectedSpeciesId.clear();
                viewModel.setFromDateDialog(null);
                viewModel.setToDateDialog(null);

            }

        });

        viewModel.RfMaster.observe(getViewLifecycleOwner(), new Observer<List<RFMaster>>() {
            @Override

            public void onChanged(List<RFMaster> rfMasters) {

                if (rfMasters != null) {

                    setRFNameSpinnerItems(rfMasters);

                }

            }

        });


        viewModel.districtMaster.observe(getViewLifecycleOwner(), new Observer<List<DistrictMaster>>() {
            @Override

            public void onChanged(List<DistrictMaster> districtMasters) {

                if (districtMasters != null && districtMasters.size() > 0) {

                    setDistrictNameSpinnerItems(districtMasters);

                }

            }

        });

        exportDialogBinding.exportBtn.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                if (checkValidation()) {

                    if (CommonFunctions.isNetworkConnectionAvailable(requireActivity())) {
                        Date dateCheckFrom = viewModel.getFromDateDialog().getValue();
                        Date dateCheckTo = viewModel.getToDateDialog().getValue();


                        if (dateCheckFrom != null) {

                            fromDate = convertDateInUTCFormat(dateCheckFrom.toString());

                        }

                        if (dateCheckTo != null) {

                            toDate = convertDateInUTCFormat(dateCheckTo.toString());

                        }

                        List<String> speciesListId = viewModel.selectedSpeciesId;
                        rfName = viewModel.getRfName().getValue();

                        if (rfName == null) {
                            rfName = "";

                        }
                        district = viewModel.getDistrictName().getValue();

                        if (district == null) {
                            district = "";


                        }

                        ExportListRequest exportListRequest = new ExportListRequest(prefManager.getUserId()/*"0b611896-fbe3-4723-ba3d-97523b88e191"*/, fromDate, toDate, speciesListId, rfName, district/*, fromDate, toDate, speciesListId, "9be0b5e8-a6b9-4062-86e3-bb2ca03b4a4a", "ec4d0c68-3861-4ba5-b8b9-41e92b70b9d1"*/);

                        viewModel.getExportData(exportListRequest).observe(requireActivity(), new Observer<SealedNetworkResult<List<ExportData>>>() {
                            @Override

                            public void onChanged(SealedNetworkResult<List<ExportData>> result) {

                                //when api hit successfully

                                if (result instanceof SealedNetworkResult.Success) {
                                    try {

                                        exportDialog.dismiss();
                                        CommonFunctions.generateExcelSheet(((SealedNetworkResult.Success<List<ExportData>>) result).getData(), requireActivity(), fromDate, toDate, "forestrySurvey");
                                        progressDialog.dismiss();
                                        exportDialog.dismiss();
                                        viewModel.selectedSpeciesId.clear();
                                        viewModel.setFromDateDialog(null);
                                        viewModel.setToDateDialog(null);

                                        viewModel.setToDateDialog(null);

                                        viewModel.setFromDateDialog(null);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                } else if (result instanceof SealedNetworkResult.Error) {
                                    progressDialog.dismiss();

                                } else if (result instanceof SealedNetworkResult.Loading) {
                                    progressDialog.setMessage("Please wait...");
                                    progressDialog.setCancelable(false);
                                    progressDialog.show();

                                }

                            }

                        });


                    } else {

                        Toast.makeText(requireActivity(), "Internet connection is not available", Toast.LENGTH_SHORT).show();

                    }

                } else {

                    Toast.makeText(requireActivity(), "Please select date range", Toast.LENGTH_SHORT).show();

                }

            }

        });

        exportDialogBinding.fromDate.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                int flag = -1;

                long date = 0;


                if (viewModel.getToDateDialog().getValue() != null) {

                    date = viewModel.getToDateDialog().getValue().getTime();

                    viewModel.setToDateDialog(null);

                    exportDialogBinding.toDate.setText("");

                    flag = -1;

                }

                CommonFunctions.showDatePickerDialog(requireActivity(),

                        date, flag,

                        new DatePickerCallback() {
                            @Override

                            public void onDateSelected(Date date) {

                                exportDialogBinding.fromDate.setText(CommonFunctions.convertDateFormat(date.toString(), /*"E MMM dd HH:mm:ss Z yyyy",*/ "dd-MM-yyyy"));

                                viewModel.setFromDateDialog(date);

                            }

                        });


            }

        });

        exportDialogBinding.toDate.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                int flag = -1;

                long date = 0;


                if (viewModel.getFromDateDialog().getValue() != null) {

                    date = viewModel.getFromDateDialog().getValue().getTime();

                    flag = 1;

                }

                CommonFunctions.showDatePickerDialog(requireActivity(),

                        date, flag,

                        new DatePickerCallback() {
                            @Override

                            public void onDateSelected(Date date) {

                                viewModel.setToDateDialog(date);

                                exportDialogBinding.toDate.setText(CommonFunctions.convertDateFormat(date.toString(), /*"E MMM dd HH:mm:ss Z yyyy",*/ "dd-MM-yyyy"));

                            }

                        });

            }

        });


        exportDialogBinding.districtNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                viewModel.setDistrictName(viewModel.districtMaster.getValue().get(position).getDistrictMasterID());

            }

            @Override

            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        exportDialogBinding.rfNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                viewModel.setRFName(viewModel.RfMaster.getValue().get(position).getReservForestMasterID());

            }

            @Override

            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        exportDialogBinding.speciesNameSpinner.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

                speciesDialogBinding = ExportSpeciesSpinnerDialogBinding.inflate(getLayoutInflater());

                builder.setView(speciesDialogBinding.getRoot());

                builder.setCancelable(false);

                AlertDialog dialog = builder.create();
                viewModel.selectedSpeciesId.clear();
                viewModel.selectedSpeciesName.clear();

                viewModel.speciesMaster.observe(getViewLifecycleOwner(), new Observer<List<SpeciesMaster>>() {
                    @Override

                    public void onChanged(List<SpeciesMaster> speciesMasters) {

                        speciesDialogBinding.speciesCheckRecycler.setLayoutManager(new LinearLayoutManager(requireActivity()));
                        speciesDialogBinding.speciesCheckRecycler.setAdapter(new SpeciesCheckBoxAdapter(speciesMasters, TodayDataFragment.this, false));

                        speciesDialogBinding.selectAllCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                            isSelectAll = isChecked;
                            viewModel.selectedSpeciesId.clear();
                            viewModel.selectedSpeciesName.clear();
                            if (isSelectAll) {

                                for (SpeciesMaster items : speciesMasters) {

                                    viewModel.selectedSpeciesId.add(items.getSpeciesMasterID());
                                    viewModel.selectedSpeciesName.add(items.getSpeciesName());
                                }
                            }

                            speciesDialogBinding.speciesCheckRecycler.setAdapter(new SpeciesCheckBoxAdapter(speciesMasters, TodayDataFragment.this, isChecked));


                        });


                        speciesDialogBinding.okBtn.setOnClickListener(c -> {

                            viewModel.selectedSpecies = "";
                            if (!viewModel.selectedSpeciesName.isEmpty()) {
                                for (int i = 0; i < viewModel.selectedSpeciesName.size(); i++) {
                                    if (i == 0) {
                                        viewModel.selectedSpecies = viewModel.selectedSpeciesName.get(i);
                                    } else {
                                        viewModel.selectedSpecies = viewModel.selectedSpecies + "," + viewModel.selectedSpeciesName.get(i);

                                    }
                                }
                            }


                            exportDialogBinding.speciesTxt.setText(viewModel.selectedSpecies);

                            dialog.dismiss();

                        });

                    }

                });


                speciesDialogBinding.cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override

                    public void onClick(View v) {

                        viewModel.setToDateDialog(null);

                        viewModel.setFromDateDialog(null);

                        dialog.dismiss();

                    }

                });


                // Show the dialog

                dialog.show();


            }

        });


        exportDialog = builder.create();

        exportDialog.show();

    }

    @Override
    public void onItemCheck(SpeciesMaster speciesMaster, boolean isChecked) {
        if (isChecked) {
            viewModel.selectedSpeciesId.add(speciesMaster.getSpeciesMasterID());
            viewModel.selectedSpeciesName.add(speciesMaster.getSpeciesName());

        } else {
            viewModel.selectedSpeciesId.remove(speciesMaster.getSpeciesMasterID());
            if (viewModel.selectedSpeciesName.contains(speciesMaster.getSpeciesName())) {
                viewModel.selectedSpeciesName.remove(speciesMaster.getSpeciesName());
            }
        }
    }

    private String convertDateInUTCFormat(String inputDate) {
        try {
            // Create a SimpleDateFormat object with the input date format
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy", Locale.ENGLISH);

            // Parse the input date string
            Date date = inputDateFormat.parse(inputDate);

            // Create a Calendar instance and set its time and timezone
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+0530"));
            calendar.setTime(date);

            // Reset hour, minute, second, and millisecond to zero
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            // Now the calendar instance represents the date at 00:00:00.000 in the UTC+5:30 timezone

            // Create a SimpleDateFormat object with the desired output format
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z", Locale.ENGLISH);

            // Set the timezone to ensure the formatted string represents the correct timezone offset
            outputDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0530"));

            // Format the date into the desired output format
            String formattedDate = outputDateFormat.format(calendar.getTime());

            System.out.println("Formatted Date: " + formattedDate);

            return formattedDate;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void setRFNameSpinnerItems(List<RFMaster> items) {
        Set<RFMaster2> rfSet = new HashSet<>();
        for (RFMaster item : items) {
            rfSet.add(new RFMaster2(item.reservForestMasterID, item.reservForestMasterName));
        }

        List<RFMaster2> rfList = new ArrayList<>(rfSet);
        ArrayAdapter<RFMaster2> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, rfList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exportDialogBinding.rfNameSpinner.setAdapter(adapter);

    }

    private void setDistrictNameSpinnerItems(List<DistrictMaster> items) {
        ArrayAdapter<DistrictMaster> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exportDialogBinding.districtNameSpinner.setAdapter(adapter);

    }


    private boolean checkValidation() {
        // Check if Date values are not null
        if (viewModel.getFromDateDialog().getValue() == null ||
                viewModel.getToDateDialog().getValue() == null) {
            return false;
        }

        if (viewModel.selectedSpeciesId == null || viewModel.selectedSpeciesId.isEmpty()) {
            for (SpeciesMaster speciesMast : viewModel.speciesMaster1) {
                viewModel.selectedSpeciesId.add(speciesMast.getSpeciesMasterID());

            }

        }

        // Check if String values are not null and not empty
        if ((viewModel.getRfName().getValue() == null || viewModel.getRfName().getValue().isEmpty()) ||
                (viewModel.getDistrictName().getValue() == null || viewModel.getDistrictName().getValue().isEmpty())) {
            return false;
        }

        // If none of the conditions for being "unfilled" are met, return true
        return true;
    }

    @Override
    public void onFilterComplete(int count) {
        if (count == 0) {

            binding.noData.setVisibility(View.VISIBLE);
        } else {
            binding.noData.setVisibility(View.GONE);

        }
    }

}