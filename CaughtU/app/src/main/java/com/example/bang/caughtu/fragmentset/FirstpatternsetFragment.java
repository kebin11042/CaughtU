package com.example.bang.caughtu.fragmentset;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.eftimoff.patternview.PatternView;
import com.eftimoff.patternview.cells.Cell;
import com.example.bang.caughtu.R;
import com.example.bang.caughtu.activityset.FirstActivity;

import java.util.List;

/**
 * Created by BANG on 2015-07-23.
 */
public class FirstpatternsetFragment extends Fragment implements View.OnClickListener{

    private static String[] strPattern;

    //0이면 처음 입력, 1이면 확인 입력
    private int patternState;

    private String strPatternData;

    private ViewGroup rootVIew;

    private ImageButton ibtnBack;

    private PatternView patternView;

    private TextView txtvPattern;
    private Button btnPatternOk;

    public static FirstpatternsetFragment create(){
        FirstpatternsetFragment fragment = new FirstpatternsetFragment();

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        strPattern = new String[2];
        strPattern[0] = "패턴을 입력해주세요";
        strPattern[1] = "확인을 위해 패턴을 한번 더 입력해주세요";

        patternState = 0;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootVIew = (ViewGroup) inflater.inflate(R.layout.fragment_first_patternset, container, false);

        ibtnBack = ((FirstActivity) getActivity()).getIbtnBack();

        patternView = (PatternView) rootVIew.findViewById(R.id.ptvFirstSet);

        txtvPattern = (TextView) rootVIew.findViewById(R.id.txtvFirstPatternset);

        btnPatternOk = (Button) rootVIew.findViewById(R.id.btnFirstPatternOk);

        ibtnBack.setOnClickListener(FirstpatternsetFragment.this);
        btnPatternOk.setOnClickListener(FirstpatternsetFragment.this);

        return rootVIew;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == btnPatternOk.getId()){
            //사용자가 입력한 패턴 정보를 List로 받아온다
            List<Cell> listCell = patternView.getPattern();
            //Log.w("listCell", "length = " + listCell.size());
            //첫번째 입력
            if(patternState == 0){
                if(listCell.size() < 4){
                    Toast.makeText(getActivity(), "패턴은 최소 4개 이상이여야 합니다", Toast.LENGTH_SHORT).show();
                }
                else{
                    //패턴정보 저장
                    strPatternData = patternView.getPatternString();
                    //다시 입력 상태로 바꾸기
                    patternState = 1;
                    patternView.clearPattern();
                    //글 바꾸기
                    txtvPattern.setText(strPattern[1]);
                }
            }
            else{
                if(strPatternData.equals(patternView.getPatternString())){
                    Toast.makeText(getActivity(), "메일, 패턴 정보가 설정되었습니다.", Toast.LENGTH_SHORT).show();
                    ((FirstActivity) getActivity()).setUserPattern(strPatternData);
                    ((FirstActivity) getActivity()).setUserDataToDB();
                }
                else{
                    Toast.makeText(getActivity(), "입력하신 패턴이 다릅니다. 다시 입력해주세요", Toast.LENGTH_SHORT).show();
                    patternView.clearPattern();
                }
            }
        }

        if(view.getId() == ibtnBack.getId()){
            if(patternState == 0){
                //메일 입력 화면으로 돌아가기
                ((FirstActivity) getActivity()).setViewPagerPage(0);
            }
            else{
                patternView.clearPattern();

                txtvPattern.setText(strPattern[0]);

                patternState = 0;
            }
        }
    }
}
