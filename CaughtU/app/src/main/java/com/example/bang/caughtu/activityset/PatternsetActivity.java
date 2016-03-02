package com.example.bang.caughtu.activityset;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.eftimoff.patternview.PatternView;
import com.eftimoff.patternview.cells.Cell;
import com.example.bang.caughtu.R;

import java.util.List;

/**
 * Created by BANG on 2015-07-24.
 */
public class PatternsetActivity extends Activity implements View.OnClickListener{

    private static String[] strPattern;

    //0 : 기존패턴, 1 : 새로운 패턴, 2 : 패턴 확인
    public int patternState;

    //로컬 DB
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    //기존 패턴과 새로운 패턴
    private String oldPattern, newPattern;

    private PatternView patternView;

    private ImageButton ibtnBack, ibtnMenu;
    private TextView txtvPattern;
    private Button btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //로컬 DB 초기화
        preferences = getSharedPreferences("pref", MODE_PRIVATE);
        editor = preferences.edit();

        //기존 저장 되어있던 패턴 값 불러오기
        oldPattern = preferences.getString("PATTERN", "NOT");

        patternState = 0;

        strPattern = new String[3];
        strPattern[0] = "기존의 패턴을 입력해주세요";
        strPattern[1] = "새로 설정하실 패턴을 입력해주세요";
        strPattern[2] = "확인을 위해 패턴을 한번 더 입력해주세요";

        setContentView(R.layout.activity_patternset);

        Init();
    }

    public void Init(){

        patternView = (PatternView) findViewById(R.id.ptvPatternsetPattern);

        txtvPattern = (TextView) findViewById(R.id.txtvPatternsetPattern);

        ibtnBack = (ImageButton) findViewById(R.id.ibtnPatternsetBack);
        ibtnMenu = (ImageButton) findViewById(R.id.ibtnPatternsetMenu);

        ibtnBack.setOnClickListener(PatternsetActivity.this);
        ibtnMenu.setOnClickListener(PatternsetActivity.this);

        btnOk = (Button) findViewById(R.id.btnPatternsetOk);
        btnOk.setOnClickListener(PatternsetActivity.this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == btnOk.getId()){
            //사용자가 입력한 패턴 정보를 List로 받아온다
            List<Cell> listCell = patternView.getPattern();
            //Log.w("listCell", "length = " + listCell.size());
            //첫번째 입력
            if(patternState == 0){
                String inputPattern = patternView.getPatternString();
                //기존의 패턴과 입력받은 패턴이 일치하다면
                patternView.clearPattern();
                if(oldPattern.equals(inputPattern)){
                    txtvPattern.setText(strPattern[1]);
                    patternState = 1;
                }
                else{
                    Toast.makeText(PatternsetActivity.this, "패턴이 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }
            else if(patternState == 1){
                if(listCell.size() < 4){
                    Toast.makeText(PatternsetActivity.this, "패턴은 최소 4개 이상이여야 합니다", Toast.LENGTH_SHORT).show();
                }
                else{
                    //패턴정보 저장
                    newPattern = patternView.getPatternString();
                    //다시 입력 상태로 바꾸기
                    patternState = 2;
                    patternView.clearPattern();
                    //글 바꾸기
                    txtvPattern.setText(strPattern[2]);
                }
            }
            else{
                if(newPattern.equals(patternView.getPatternString())){
                    Toast.makeText(PatternsetActivity.this, "패턴 정보가 설정되었습니다.", Toast.LENGTH_SHORT).show();
                    editor.putString("PATTERN", newPattern);
                    editor.apply();

                    finish();
                }
                else{
                    Toast.makeText(PatternsetActivity.this, "입력하신 패턴이 다릅니다. 다시 입력해주세요", Toast.LENGTH_SHORT).show();
                    patternView.clearPattern();
                }
            }
        }
        //뒤로 가기 버튼
        else if(view.getId() == ibtnBack.getId()){
            if(patternState == 0){
                finish();
            }
            else if(patternState == 1){
                patternView.clearPattern();

                txtvPattern.setText(strPattern[0]);

                patternState = 0;
            }
            else{
                patternView.clearPattern();

                txtvPattern.setText(strPattern[1]);

                patternState = 1;
            }
        }
        else if(view.getId() == ibtnMenu.getId()){
            finish();
        }
    }
}
