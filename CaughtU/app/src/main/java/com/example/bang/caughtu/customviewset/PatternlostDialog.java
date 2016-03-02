package com.example.bang.caughtu.customviewset;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.example.bang.caughtu.R;
import com.example.bang.caughtu.activityset.PatternlockscreenActivity;

/**
 * Created by BANG on 2015-07-30.
 */
public class PatternlostDialog extends Dialog implements View.OnClickListener{

    private Context context;
    //취소 전송 버튼
    private Button btnCancle, btnTransfer;

    public PatternlostDialog(Context context) {
        super(context);

        this.context = context;

        //원래 다이얼로그 타이틀 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_patternlost);

        Init();
    }

    public void Init(){
        btnCancle = (Button) findViewById(R.id.btnPatternlostDlgCancle);
        btnTransfer = (Button) findViewById(R.id.btnPatternlostDlgTrans);

        btnCancle.setOnClickListener(PatternlostDialog.this);
        btnTransfer.setOnClickListener(PatternlostDialog.this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == btnCancle.getId()){
            dismiss();
        }
        else if(view.getId() == btnTransfer.getId()){
            dismiss();
            //패턴분실 메일 전송
            ( (PatternlockscreenActivity)context ).executeTaskPatternLost();

            Toast.makeText(context, "패턴정보를 설정하신 메일로 전송하였습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
