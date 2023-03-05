package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private EditText et_id,et_password;
    private Button bt_join, button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        et_id = findViewById(R.id.ed_id);
        et_password = findViewById(R.id.ed_password);
        button = findViewById(R.id.button);
        bt_join = findViewById(R.id.bt_join);


        //회원버튼 클릭시 수행
        bt_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this , SubActivity.class);
                startActivity(intent); //액티비티 이동
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //EditText에 현재 입력된 값을 가져오는 코드
                String userID = et_id.getText().toString();
                String userPassword = et_password.getText().toString();
                Log.d("test", "logintest");

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            Log.d("test", "jsontest");
                            if (success) { //회원가입이 성공할 경우
                                Log.d("test", "test login");
                                String userID=jsonObject.getString("userID");
                                String userPassword=jsonObject.getString("userPassword");

                                Toast.makeText(getApplicationContext(), "로그인에 성공하였습니다", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, temp2Acvivity.class);

                                intent.putExtra("userID",userID);
                                intent.putExtra("userPassword",userPassword);
                                startActivity((intent));
                            } else { //회원가입이 실패할 경우
                                Log.d("test", "fail");
                                Toast.makeText(getApplicationContext(), "로그인 실패하였습니다", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                LoginRequest loginRequest = new LoginRequest(userID,userPassword, responseListener);
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                queue.add(loginRequest);

            }
        });



    }
}