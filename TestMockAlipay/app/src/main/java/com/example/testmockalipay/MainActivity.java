package com.example.testmockalipay;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.mock.alipay.Callback;
import com.mock.alipay.PasswordKeypad;

public class MainActivity extends AppCompatActivity {
    private PasswordKeypad mKeypad;
    private boolean state;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mKeypad = new PasswordKeypad();
        mKeypad.setPasswordCount(6);
        mKeypad.setCallback(new Callback() {
            @Override
            public void onForgetPassword() {
                Toast.makeText(getApplicationContext(),"忘记密码",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onInputCompleted(CharSequence password) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (state) {
                            mKeypad.setPasswordState(true);
                            state = false;
                        } else {
                            mKeypad.setPasswordState(false, "密码输入错误");
                            state = true;
                        }
                    }
                },1000);
            }

            @Override
            public void onPasswordCorrectly() {
                mKeypad.dismiss();
            }

            @Override
            public void onCancel() {
                //todo:做一些埋点类的需求
            }
        });
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mKeypad.show(getSupportFragmentManager(), "PasswordKeypad");
            }
        });
    }
}
