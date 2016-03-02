package com.example.bang.caughtu.activityset;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.bang.caughtu.R;

import java.util.regex.Pattern;

/**
 * Created by BANG on 2015-07-24.
 */
public class MailsetActivity extends Activity implements View.OnClickListener{

    //로컬 DB
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private ImageButton ibtnBack, ibtnMenu;

    private EditText edtxEmail;
    private Button btnMailOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mailset);

        //로컬 DB 초기화
        preferences = getSharedPreferences("pref", MODE_PRIVATE);
        editor = preferences.edit();

        ibtnBack = (ImageButton) findViewById(R.id.ibtnMailsetBack);
        ibtnMenu = (ImageButton) findViewById(R.id.ibtnMailsetMenu);
        edtxEmail = (EditText) findViewById(R.id.edtxMailsetMail);
        btnMailOk = (Button) findViewById(R.id.btnMailsetOk);

        ibtnBack.setOnClickListener(MailsetActivity.this);
        ibtnMenu.setOnClickListener(MailsetActivity.this);
        btnMailOk.setOnClickListener(MailsetActivity.this);
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
        if(view.getId() == btnMailOk.getId()){
            String strEmail = edtxEmail.getText().toString();

            if(!IsEmpty(strEmail)){
                if(isEmail(strEmail)){
                    editor.putString("EMAIL", strEmail);
                    editor.apply();

                    Toast.makeText(MailsetActivity.this, "이메일 설정을 완료하였습니다.", Toast.LENGTH_SHORT).show();

                    finish();
                }
                else{
                    Toast.makeText(MailsetActivity.this, "이메일이 유효하지 않습니다. 다시 확인해 주세요", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(MailsetActivity.this, "이메일 입력란이 비어있습니다.", Toast.LENGTH_SHORT).show();
            }
        }

        else if(view.getId() == ibtnBack.getId() || view.getId() == ibtnMenu.getId()){
            finish();
        }
    }
}
