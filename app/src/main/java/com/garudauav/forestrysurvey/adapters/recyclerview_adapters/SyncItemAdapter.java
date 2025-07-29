package com.garudauav.forestrysurvey.adapters.recyclerview_adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.garudauav.forestrysurvey.databinding.SyncItemBinding;
import com.garudauav.forestrysurvey.models.BatchData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class SyncItemAdapter extends RecyclerView.Adapter<SyncItemAdapter.SyncViewHolder> {

    private final List<BatchData> batchDataList;
    private final CheckTreeData checkTreeData;

    public SyncItemAdapter(List<BatchData> batchDataList, CheckTreeData checkTreeData) {
        this.batchDataList = batchDataList;
        this.checkTreeData = checkTreeData;
    }

    @NonNull
    @Override
    public SyncViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        SyncItemBinding binding = SyncItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);


        return new SyncViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SyncViewHolder holder, int position) {
        holder.bind(batchDataList.get(position));


    }

    @Override
    public int getItemCount() {
        return batchDataList.size();
    }

    class SyncViewHolder extends RecyclerView.ViewHolder {

        private SyncItemBinding binding;

        public SyncViewHolder(SyncItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;


        }

        public void bind(BatchData data) {
            binding.date.setText(convertDate(data.getDate().toString()));
            binding.imgCount.setText(data.getTreeCount()+"");
            binding.uploadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkTreeData.onItemChecked(data);
                }
            });


        }
    }

    public static String convertDate(String inputDate) {
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

   public interface CheckTreeData{
        void onItemChecked(BatchData batchData);
    }
}
