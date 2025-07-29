package com.garudauav.forestrysurvey.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.garudauav.forestrysurvey.R;
import com.garudauav.forestrysurvey.utils.PrefManager;

public class OnBoardActivity extends AppCompatActivity {

    private ViewPager viewPager;

    private ViewPagerAdapter viewPagerAdapter;

    private LinearLayout dotsLayout;

    private TextView[] dots;

    private int[] layouts;

    private TextView btnSkip, btnNext, btn_start;

    private LinearLayout layout_btn;
    private PrefManager prefManager;
    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature ( Window.FEATURE_NO_TITLE );

        getWindow ().setFlags ( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );

        setContentView ( R.layout.activity_on_board );

        SharedPreferences share = getSharedPreferences ( "PREFS",MODE_PRIVATE );
        prefManager=new PrefManager(this);

        if (share.getInt ( "INTRO",0 )==1){
            prefManager.setFirstTimeLaunch(false);
            if(prefManager.isTermsAndConditionsAgreed()){
                startActivity (new Intent( OnBoardActivity.this, PreLoginActivity.class ));

            }else{
                startActivity (new Intent( OnBoardActivity.this, PermissionActivity.class ));

            }

            finish ();

        }

        viewPager = (ViewPager) findViewById ( R.id.view_pager );

        dotsLayout = (LinearLayout) findViewById ( R.id.layoutDots );

        btnSkip = (TextView) findViewById ( R.id.btn_skip );

        btnNext = (TextView) findViewById ( R.id.btn_next );

        btn_start = (TextView) findViewById ( R.id.btn_start );

        layout_btn = (LinearLayout) findViewById ( R.id.layout_btn );

        layouts = new int[]{

                R.layout.welcome_slider1,

                R.layout.welcome_slider2,

                R.layout.welcome_slider3};

// adding bottom dots

        addBottomDots ( 0 );

        viewPagerAdapter = new ViewPagerAdapter ();

        viewPager.setAdapter ( viewPagerAdapter );

        viewPager.addOnPageChangeListener ( viewPagerPageChangeListener );


        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {

// checking for last page

// if last page home screen will be launched

                int current = getItem(1);

                if (current < layouts.length) {

// move to next screen

                    viewPager.setCurrentItem(current);

                } else {

                    launchHomeScreen();

                }

            }

        });

    }

    public void btnSkipClick(View v)

    {

        launchHomeScreen();

    }


    public void btnNextClick(View v)

    {

// checking for last page

// if last page home screen will be launched

        int current = getItem(1);

        if (current < layouts.length) {

// move to next screen

            viewPager.setCurrentItem(current);

        } else {

            launchHomeScreen();

        }

    }


    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener () {

        @Override

        public void onPageSelected(int position) {

            addBottomDots ( position );

// changing the next button text 'NEXT' / 'GOT IT'

            if (position == layouts.length - 1) {

// last page. make button text to GOT IT

                btnNext.setText ( getString ( R.string.start ) );

                btnSkip.setVisibility ( View.GONE );

                layout_btn.setVisibility(View.GONE);

                btn_start.setVisibility(View.VISIBLE);

            } else {

// still pages are left

                btnNext.setText ( getString ( R.string.next ) );

                btnSkip.setVisibility ( View.VISIBLE );

                layout_btn.setVisibility(View.VISIBLE);

                btn_start.setVisibility(View.GONE);

            }

        }
        @Override

        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }
        @Override

        public void onPageScrollStateChanged(int arg0) {

        }

    };

    private void addBottomDots(int currentPage) {

        dots = new TextView[layouts.length];

        dotsLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {

            dots[i] = new TextView(this);

            dots[i].setText( Html.fromHtml("&#8226;"));

            dots[i].setTextSize(35);

            dots[i].setTextColor(getResources().getColor(R.color.dot_inactive));

            dotsLayout.addView(dots[i]);

        }

        if (dots.length > 0)

            dots[currentPage].setTextColor(getResources().getColor(R.color.primary));

    }

    private int getItem(int i) {

        return viewPager.getCurrentItem() + i;

    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(true);
        SharedPreferences share = getSharedPreferences ( "PREFS",MODE_PRIVATE );

        SharedPreferences.Editor editor;

        editor = share.edit ();

        editor.putInt( "INTRO",1 );

        editor.apply();

        if(prefManager.isTermsAndConditionsAgreed()){
            startActivity (new Intent( OnBoardActivity.this, PreLoginActivity.class ));

        }else{
            startActivity (new Intent( OnBoardActivity.this, PermissionActivity.class ));
        }
        finish();

    }

    public class ViewPagerAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;

        public ViewPagerAdapter() {

        }
        @Override

        public Object instantiateItem(ViewGroup container, int position) {

            layoutInflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);

            container.addView(view);

            return view;

        }
        @Override

        public int getCount() {

            return layouts.length;

        }
        @Override

        public boolean isViewFromObject(View view, Object obj) {

            return view == obj;

        }
        @Override

        public void destroyItem(ViewGroup container, int position, Object object) {

            View view = (View) object;

            container.removeView(view);

        }

    }

}