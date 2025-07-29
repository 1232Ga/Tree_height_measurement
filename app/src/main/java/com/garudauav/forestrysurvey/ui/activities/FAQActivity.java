package com.garudauav.forestrysurvey.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;

import com.garudauav.forestrysurvey.R;
import com.garudauav.forestrysurvey.databinding.ActivityFaqactivityBinding;

public class FAQActivity extends AppCompatActivity {
  private   ActivityFaqactivityBinding binding;
  private   boolean expand1=false;
    private boolean expand2=false;
    private boolean expand3=false;
    private boolean expand4=false;
    private   boolean expand5=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityFaqactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.backArrowImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
binding.queryLay1.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if(expand1){
            expand1=false;
            binding.upImg1.setVisibility(View.GONE);
            binding.downImg1.setVisibility(View.VISIBLE);
            binding.answer1.setVisibility(View.GONE);
            //   binding.divider1.setVisibility(View.VISIBLE);
            binding.query1.setBackgroundColor(ContextCompat.getColor(FAQActivity.this, R.color.white));

        }else{
            expand1=true;
            binding.upImg1.setVisibility(View.VISIBLE);
            binding.downImg1.setVisibility(View.GONE);
            binding.answer1.setVisibility(View.VISIBLE);
            // binding.divider1.setVisibility(View.GONE);
            binding.query1.setBackgroundColor(ContextCompat.getColor(FAQActivity.this, R.color.faq_background));



        }
    }
});

        binding.regulate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(expand1){
                   expand1=false;
                   binding.upImg1.setVisibility(View.GONE);
                   binding.downImg1.setVisibility(View.VISIBLE);
                   binding.answer1.setVisibility(View.GONE);
                //   binding.divider1.setVisibility(View.VISIBLE);
                   binding.query1.setBackgroundColor(ContextCompat.getColor(FAQActivity.this, R.color.white));

               }else{
                   expand1=true;
                   binding.upImg1.setVisibility(View.VISIBLE);
                   binding.downImg1.setVisibility(View.GONE);
                   binding.answer1.setVisibility(View.VISIBLE);
                  // binding.divider1.setVisibility(View.GONE);
                   binding.query1.setBackgroundColor(ContextCompat.getColor(FAQActivity.this, R.color.faq_background));



               }
            }
        });

        binding.queryLay2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(expand2){
                    expand2=false;
                    binding.upImg2.setVisibility(View.GONE);
                    binding.downImg2.setVisibility(View.VISIBLE);
                    binding.answer2.setVisibility(View.GONE);
                    //   binding.divider2.setVisibility(View.VISIBLE);
                    binding.query2.setBackgroundColor(ContextCompat.getColor(FAQActivity.this, R.color.white));

                }else{
                    expand2=true;
                    binding.upImg2.setVisibility(View.VISIBLE);
                    binding.downImg2.setVisibility(View.GONE);
                    binding.answer2.setVisibility(View.VISIBLE);
                    //    binding.divider2.setVisibility(View.GONE);
                    binding.query2.setBackgroundColor(ContextCompat.getColor(FAQActivity.this, R.color.faq_background));



                }
            }
        });

        binding.regulate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(expand2){
                    expand2=false;
                    binding.upImg2.setVisibility(View.GONE);
                    binding.downImg2.setVisibility(View.VISIBLE);
                    binding.answer2.setVisibility(View.GONE);
                 //   binding.divider2.setVisibility(View.VISIBLE);
                    binding.query2.setBackgroundColor(ContextCompat.getColor(FAQActivity.this, R.color.white));

                }else{
                    expand2=true;
                    binding.upImg2.setVisibility(View.VISIBLE);
                    binding.downImg2.setVisibility(View.GONE);
                    binding.answer2.setVisibility(View.VISIBLE);
                //    binding.divider2.setVisibility(View.GONE);
                    binding.query2.setBackgroundColor(ContextCompat.getColor(FAQActivity.this, R.color.faq_background));



                }
            }
        });

        binding.queryLay3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(expand3){
                    expand3=false;
                    binding.upImg3.setVisibility(View.GONE);
                    binding.downImg3.setVisibility(View.VISIBLE);
                    binding.answer3.setVisibility(View.GONE);
                    //    binding.divider3.setVisibility(View.VISIBLE);
                    binding.query3.setBackgroundColor(ContextCompat.getColor(FAQActivity.this, R.color.white));

                }else{
                    expand3=true;
                    binding.upImg3.setVisibility(View.VISIBLE);
                    binding.downImg3.setVisibility(View.GONE);
                    binding.answer3.setVisibility(View.VISIBLE);
                    //    binding.divider3.setVisibility(View.GONE);
                    binding.query3.setBackgroundColor(ContextCompat.getColor(FAQActivity.this, R.color.faq_background));



                }
            }
        });

        binding.regulate3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(expand3){
                    expand3=false;
                    binding.upImg3.setVisibility(View.GONE);
                    binding.downImg3.setVisibility(View.VISIBLE);
                    binding.answer3.setVisibility(View.GONE);
                //    binding.divider3.setVisibility(View.VISIBLE);
                    binding.query3.setBackgroundColor(ContextCompat.getColor(FAQActivity.this, R.color.white));

                }else{
                    expand3=true;
                    binding.upImg3.setVisibility(View.VISIBLE);
                    binding.downImg3.setVisibility(View.GONE);
                    binding.answer3.setVisibility(View.VISIBLE);
                //    binding.divider3.setVisibility(View.GONE);
                    binding.query3.setBackgroundColor(ContextCompat.getColor(FAQActivity.this, R.color.faq_background));



                }
            }
        });

        binding.queryLay4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(expand4){
                    expand4=false;
                    binding.upImg4.setVisibility(View.GONE);
                    binding.downImg4.setVisibility(View.VISIBLE);
                    binding.answer4.setVisibility(View.GONE);
                    //   binding.divider4.setVisibility(View.VISIBLE);
                    binding.query4.setBackgroundColor(ContextCompat.getColor(FAQActivity.this, R.color.white));

                }else{
                    expand4=true;
                    binding.upImg4.setVisibility(View.VISIBLE);
                    binding.downImg4.setVisibility(View.GONE);
                    binding.answer4.setVisibility(View.VISIBLE);
                    //  binding.divider4.setVisibility(View.GONE);
                    binding.query4.setBackgroundColor(ContextCompat.getColor(FAQActivity.this, R.color.faq_background));



                }
            }
        });

        binding.regulate4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(expand4){
                    expand4=false;
                    binding.upImg4.setVisibility(View.GONE);
                    binding.downImg4.setVisibility(View.VISIBLE);
                    binding.answer4.setVisibility(View.GONE);
                 //   binding.divider4.setVisibility(View.VISIBLE);
                    binding.query4.setBackgroundColor(ContextCompat.getColor(FAQActivity.this, R.color.white));

                }else{
                    expand4=true;
                    binding.upImg4.setVisibility(View.VISIBLE);
                    binding.downImg4.setVisibility(View.GONE);
                    binding.answer4.setVisibility(View.VISIBLE);
                  //  binding.divider4.setVisibility(View.GONE);
                    binding.query4.setBackgroundColor(ContextCompat.getColor(FAQActivity.this, R.color.faq_background));



                }
            }
        });

        binding.queryLay5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(expand5){
                    expand5=false;
                    binding.upImg5.setVisibility(View.GONE);
                    binding.downImg5.setVisibility(View.VISIBLE);
                    binding.answer5.setVisibility(View.GONE);
                    //  binding.divider5.setVisibility(View.VISIBLE);
                    binding.query5.setBackgroundColor(ContextCompat.getColor(FAQActivity.this, R.color.white));

                }else{
                    expand5=true;
                    binding.upImg5.setVisibility(View.VISIBLE);
                    binding.downImg5.setVisibility(View.GONE);
                    binding.answer5.setVisibility(View.VISIBLE);
                    //   binding.divider5.setVisibility(View.GONE);
                    binding.query5.setBackgroundColor(ContextCompat.getColor(FAQActivity.this, R.color.faq_background));



                }
            }
        });
        binding.regulate5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(expand5){
                    expand5=false;
                    binding.upImg5.setVisibility(View.GONE);
                    binding.downImg5.setVisibility(View.VISIBLE);
                    binding.answer5.setVisibility(View.GONE);
                  //  binding.divider5.setVisibility(View.VISIBLE);
                    binding.query5.setBackgroundColor(ContextCompat.getColor(FAQActivity.this, R.color.white));

                }else{
                    expand5=true;
                    binding.upImg5.setVisibility(View.VISIBLE);
                    binding.downImg5.setVisibility(View.GONE);
                    binding.answer5.setVisibility(View.VISIBLE);
                 //   binding.divider5.setVisibility(View.GONE);
                    binding.query5.setBackgroundColor(ContextCompat.getColor(FAQActivity.this, R.color.faq_background));



                }
            }
        });

    }
}