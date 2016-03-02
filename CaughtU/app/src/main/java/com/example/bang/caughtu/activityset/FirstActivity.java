package com.example.bang.caughtu.activityset;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;

import com.example.bang.caughtu.R;
import com.example.bang.caughtu.customviewset.noTouchViewPager;
import com.example.bang.caughtu.fragmentset.FirstemailFragment;
import com.example.bang.caughtu.fragmentset.FirstpatternsetFragment;

/**
 * Created by BANG on 2015-07-23.
 */
public class FirstActivity extends FragmentActivity implements ViewPager.OnPageChangeListener{

    //로컬 DB
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    //사용자 정보
    private String userEmail;
    private String userPattern;

    private noTouchViewPager viewPager;
    private FirstAdapter adapter;

    //뒤로가기 버튼
    private ImageButton ibtnBack;

    private Fragment[] fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_firstsettings);

        Init();


    }

    public void Init(){

        //로컬 DB 초기화
        preferences = getSharedPreferences("pref", MODE_PRIVATE);
        editor = preferences.edit();

        fragments = new Fragment[2];
        //순서대로 화면 설정
        fragments[0] = FirstemailFragment.create();
        fragments[1] = FirstpatternsetFragment.create();

        ibtnBack = (ImageButton) findViewById(R.id.ibtnFirstTobBack);

        viewPager = (noTouchViewPager) findViewById(R.id.vpagerFirst);
        adapter = new FirstAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        //페이져 스크롤 못하게 하기
        viewPager.setPagingDisabled();

        viewPager.setOnPageChangeListener(FirstActivity.this);
    }

    public void setUserEmail(String userEmail){
        this.userEmail = userEmail;
    }

    public void setUserPattern(String userPattern){
        this.userPattern = userPattern;
    }

    //사용자 정보 로컬 DB로 저장 후 메인 페이지로 이동
    public void setUserDataToDB(){
        editor.putString("EMAIL", userEmail);
        editor.putString("PATTERN", userPattern);
        editor.apply();

        Intent intent = new Intent(FirstActivity.this, MainActivity.class);
        startActivity(intent);

        finish();
    }

    public ImageButton getIbtnBack(){
        return ibtnBack;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if(position == 0){
            ibtnBack.setVisibility(View.GONE);
        }
        else if(position == 1){
            ibtnBack.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    //화면 이동
    public void setViewPagerPage(int position){
        viewPager.setCurrentItem(position, true);
    }

    public class FirstAdapter extends FragmentPagerAdapter {

        public FirstAdapter(android.support.v4.app.FragmentManager fm){
            super(fm);
        }
        
        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
