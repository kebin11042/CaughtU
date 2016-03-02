package com.example.bang.caughtu.fragmentset;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bang.caughtu.R;
import com.example.bang.caughtu.activityset.FirstActivity;

import java.util.regex.Pattern;

/**
 * Created by BANG on 2015-07-23.
 */
public class FirstemailFragment extends Fragment implements View.OnClickListener{

    private ViewGroup rootVIew;

    private EditText edtxEmail;

    private Button btnEmailOk;

    public static FirstemailFragment create(){
        FirstemailFragment fragment = new FirstemailFragment();

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootVIew = (ViewGroup) inflater.inflate(R.layout.fragment_first_email, container, false);

        edtxEmail = (EditText) rootVIew.findViewById(R.id.edtxFirstEmail);

        btnEmailOk = (Button) rootVIew.findViewById(R.id.btnFirstEmailOk);

        btnEmailOk.setOnClickListener(FirstemailFragment.this);

        return rootVIew;
    }

    //이메일 유효성 검사
    public boolean isEmail(String email) {
        if (email==null) return false;
        boolean b = Pattern.matches("[\\w\\~\\-\\.]+@[\\w\\~\\-]+(\\.[\\w\\~\\-]+)+", email.trim());
        return b;
    }

    //이메일 비어있는지 검사
    public boolean IsEmpty(String str){
        return str.equals("") || ( str.length() == 0 ) ;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == btnEmailOk.getId()){
            String strEmail = edtxEmail.getText().toString();

            if(!IsEmpty(strEmail)){
                if(isEmail(strEmail)){
                    ((FirstActivity) getActivity()).setUserEmail(strEmail);
                    //Pattern 설정 화면으로 넘어가기
                    ((FirstActivity) getActivity()).setViewPagerPage(1);
                }
                else{
                    Toast.makeText(getActivity(), "이메일이 유효하지 않습니다. 다시 확인해 주세요", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(getActivity(), "이메일 입력란이 비어있습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
