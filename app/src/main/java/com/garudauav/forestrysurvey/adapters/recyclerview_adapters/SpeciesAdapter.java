package com.garudauav.forestrysurvey.adapters.recyclerview_adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.garudauav.forestrysurvey.databinding.SpeciesRecylerLayoutBinding;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpeciesAdapter extends RecyclerView.Adapter<SpeciesAdapter.SpeciesViewHolder> implements Filterable {

    private List<PieEntry> pieEntries;
    private List<PieEntry> fullList;
    private FilterResultsListener filterResultsListener;
    private Context context;
    private boolean showAllItems;
    private  boolean Dataset;

    public SpeciesAdapter(Context context, List<PieEntry> pieEntries, FilterResultsListener filterResultsListener, boolean b) {
        this.filterResultsListener = filterResultsListener;
        this.pieEntries = pieEntries;
        this.context = context;
        fullList = new ArrayList<>(pieEntries);
        Dataset=b;
    }
    public void addData(List<PieEntry> newSpeciesData) {
        int startPos = this.pieEntries.size();
        this.pieEntries=newSpeciesData;
        fullList=new ArrayList<>(newSpeciesData);
        notifyItemRangeInserted(startPos, newSpeciesData.size());
    }
    public void setShowAllItems(boolean showAllItems) {
        this.showAllItems = showAllItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SpeciesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        SpeciesRecylerLayoutBinding binding = SpeciesRecylerLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new SpeciesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SpeciesViewHolder holder, int position) {
        holder.bind(pieEntries.get(position));
    }

    @Override
    public int getItemCount() {

        return pieEntries.size();/*showAllItems ? pieEntries.size() : Math.min(4, pieEntries.size());*/
    }

    @Override
    public Filter getFilter() {
        return Searched_Filter;
    }

    private Filter Searched_Filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<PieEntry> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(fullList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (PieEntry item : fullList) {
                    //have to check
                    if (item.getLabel().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }




        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            pieEntries.clear();
            pieEntries.addAll((ArrayList) results.values);
            notifyDataSetChanged();
            if (filterResultsListener != null) {
                filterResultsListener.onFilterComplete(pieEntries.size());
            }
        }
    };

    public void clearData(){
        if(pieEntries!=null){
            pieEntries.clear();
            notifyDataSetChanged();
        }
    }

    class SpeciesViewHolder extends RecyclerView.ViewHolder {
        private SpeciesRecylerLayoutBinding binding;

        public SpeciesViewHolder(@NonNull SpeciesRecylerLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

        // Bind data to the ViewBinding
        public void bind(PieEntry data) {
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)); // Alpha set to 255 (opaque)

            //  binding.speciesCard.setCardBackgroundColor(color);
            binding.icTree.setColorFilter(color/*Integer.parseInt(data.getData().toString())*/);

            binding.treeSpeciesName.setText(data.getLabel());
            //binding.treeSpeciesName.setTextColor(textColor);
            binding.treeSpeciesCount.setText(Math.round(data.getValue()) + "");
            binding.treeSpeciesCount.setTextColor(color);

            // Add more bindings as needed
        }
    }

    public interface FilterResultsListener {
        void onFilterComplete(int count);
    }
}
