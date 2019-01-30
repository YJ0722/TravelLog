package com.example.admin.travellog_ver30._notUse;

/**
 * Created by eee30 on 2018-09-30.
 */
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.admin.travellog_ver30.IntroActivity;
import com.example.admin.travellog_ver30.R;

public class LoginActivity extends Activity {

    public static final String PREFS_NAME = "prefs";
    SharedPreferences prefs;
    EditText user_password = null;
    Button login_but;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        final String password = prefs.getString("password", null);


        if (password != null){

            setContentView(R.layout.activity_login);

            user_password = (EditText) findViewById(R.id.password);
            login_but = (Button) findViewById(R.id.loginbutton);

            login_but.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    try {
                        if(password.equals(user_password.getText().toString())) {
                            Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                            // 동작 위해 잠시 주석. 나중에 필요하면 주석된 코드로 변경 필요
                            //Intent i = new Intent(LoginActivity.this, MenuActivity.class);
                            Intent i = new Intent(LoginActivity.this, IntroActivity.class);
                            startActivity(i);
                        } else {
                            Toast.makeText(LoginActivity.this,"로그인 실패", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
//            Intent i = new Intent(LoginActivity.this, MenuActivity.class);
            Intent i = new Intent(LoginActivity.this, IntroActivity.class);
            startActivity(i);
        }
    }
}