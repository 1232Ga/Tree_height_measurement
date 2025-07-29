package com.garudauav.forestrysurvey.adapters.recyclerview_adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.garudauav.forestrysurvey.databinding.SpeciesCheckboxBinding;
import com.garudauav.forestrysurvey.databinding.SpeciesRecylerLayoutBinding;
import com.garudauav.forestrysurvey.models.SpeciesMaster;

import java.util.List;

public class SpeciesCheckBoxAdapter extends RecyclerView.Adapter<SpeciesCheckBoxAdapter.SpecicesViewHolder> {

    private List<SpeciesMaster> speciesMasterList;
    private CheckSpecies checkSpecies;
    private boolean isAllSelect;

    public SpeciesCheckBoxAdapter(List<SpeciesMaster> speciesMasterList, CheckSpecies checkSpecies, boolean isAllSelect) {
        this.speciesMasterList = speciesMasterList;
        this.checkSpecies = checkSpecies;
        this.isAllSelect = isAllSelect;
    }

    @NonNull
    @Override
    public SpecicesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SpeciesCheckboxBinding binding = SpeciesCheckboxBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SpecicesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecicesViewHolder holder, int position) {
        holder.bind(speciesMasterList.get(position));
    }

    @Override
    public int getItemCount() {
        return speciesMasterList.size();
    }

    class SpecicesViewHolder extends RecyclerView.ViewHolder {

        private SpeciesCheckboxBinding binding;

        public SpecicesViewHolder(@NonNull SpeciesCheckboxBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(SpeciesMaster master) {

            binding.speciesCheck.setText(master.getSpeciesName());
            if (isAllSelect) {
                binding.speciesCheck.setChecked(true);
            } else {
                binding.speciesCheck.setChecked(false);

            }
            binding.speciesCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    checkSpecies.onItemCheck(master, isChecked);
                }
            });

        }
    }

    public interface CheckSpecies {
        void onItemCheck(SpeciesMaster speciesMaster, boolean isChecked);
    }
}
