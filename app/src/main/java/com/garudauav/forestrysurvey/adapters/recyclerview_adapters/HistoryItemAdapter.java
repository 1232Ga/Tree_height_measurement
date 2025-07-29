package com.garudauav.forestrysurvey.adapters.recyclerview_adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.garudauav.forestrysurvey.R;
import com.garudauav.forestrysurvey.databinding.HistoryItemBinding;
import com.garudauav.forestrysurvey.models.SyncHistory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class HistoryItemAdapter extends RecyclerView.Adapter<HistoryItemAdapter.HistoryViewHolder> {
    private List<SyncHistory> syncHistoryList;
    private RetryData retryData;

    public HistoryItemAdapter(List<SyncHistory> syncDataList, RetryData retryData) {
        this.syncHistoryList = syncDataList;
        this.retryData = retryData;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        HistoryItemBinding binding = HistoryItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new HistoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        holder.bind(syncHistoryList.get(position));
//        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return syncHistoryList.size();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {

        private HistoryItemBinding binding;

        public HistoryViewHolder(HistoryItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;


        }

        public void bind(SyncHistory data) {

            binding.date.setText(convertDate(/*"Fri Feb 16 00:00:00 GMT+05:30 2024"*/data.getDateCreated().toString()));
            binding.imgCount.setText(data.getNumberOfTree() + "");
            if (data.isFailed()) {
                binding.uploadedLay.setVisibility(View.GONE);
                binding.retryLay.setVisibility(View.VISIBLE);
            }

            binding.retryLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (data.isFailed()) {
                        retryData.onItemClick(data);
                    } else {
                        Toast.makeText(v.getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            binding.uploadedLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (data.isFailed()) {
                        retryData.onItemClick(data);
                    } else {
                        //  Toast.makeText(v.getContext(), "Already Uploaded", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            if (data.getQcApproval() == 0) {
                binding.approvalStatus.setText("Pending");
                binding.statusImg.setImageDrawable(AppCompatResources.getDrawable(binding.getRoot().getContext(), R.drawable.ic_pending));

                binding.approvalStatus.setTextColor(binding.getRoot().getContext().getResources().getColor(R.color.pending, binding.getRoot().getContext().getTheme()));
            } else if (data.getQcApproval() == 1) {
                binding.approvalStatus.setText("Approved");
                binding.approvalStatus.setTextColor(binding.getRoot().getContext().getResources().getColor(R.color.approved, binding.getRoot().getContext().getTheme()));

                binding.statusImg.setImageDrawable(AppCompatResources.getDrawable(binding.getRoot().getContext(), R.drawable.ic_approve));

            } else {
                binding.approvalStatus.setText("Rejected");
                binding.approvalStatus.setTextColor(binding.getRoot().getContext().getResources().getColor(R.color.reject, binding.getRoot().getContext().getTheme()));

                binding.statusImg.setImageDrawable(AppCompatResources.getDrawable(binding.getRoot().getContext(), R.drawable.ic_reject));

            }


          /*  binding.checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    data.setChecked(true);
                }
            });*/

        }
    }

    public static String convertDate2(String inputDate) {
        // Define the input and output date formats
        SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy", Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yy", Locale.ENGLISH);

        // Set the time zone for parsing the input date
        inputFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        try {
            // Parse the input date string
            Date date = inputFormat.parse(inputDate);

            // Format the date into the new format
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    public String convertDate(String inputDate) {
        // Define the input and output date formats
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yy");

        // Set the input date format to parse the UTC timezone
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            // Parse the input date string into a Date object
            Date date = inputFormat.parse(inputDate);

            // Format the Date object into the desired output date string format
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
/*
    public static String convertDate(String inputDate) {
        LocalDateTime dateTime = LocalDateTime.parse(inputDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        // Define the new format
        DateTimeFormatter newFormat = DateTimeFormatter.ofPattern("dd/MM/yy");

        // Format the date
        String formattedDate = dateTime.format(newFormat);
        return formattedDate;
    }
*/

    public interface RetryData {
        void onItemClick(SyncHistory syncHistory);
    }
}
