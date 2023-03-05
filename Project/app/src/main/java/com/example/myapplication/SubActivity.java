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

public class SubActivity extends AppCompatActivity {


    private EditText et_ID, et_Password, et_Name, et_Age;
    private Button button_sub;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        //아이디 값 찾아주기
        et_ID = findViewById(R.id.et_ID);
        et_Password = findViewById(R.id.et_Password);
        et_Name = findViewById(R.id.et_Name);
        et_Age = findViewById(R.id.et_Age);

        //회원가입 버튼 클릭시 수행
        button_sub=findViewById(R.id.button_sub);
        button_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //EditText에 현재 입력된 값을 가져오는 코드
                String userID = et_ID.getText().toString();
                String userPassword = et_Password.getText().toString();
                String userName = et_Name.getText().toString();
                int userAge = Integer.parseInt(et_Age.getText().toString());
                Log.d("test", "registertest");

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) { //회원가입이 성공할 경우
                                Toast.makeText(getApplicationContext(), "회원 등록에 성공하였습니다", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SubActivity.this, MainActivity.class);
                                startActivity(intent);
                            } else { //회원가입이 실패할 경우
                                Toast.makeText(getApplicationContext(), "회원 등록에 실패하였습니다", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                };

                // 서버로 Volley를 이용해서 요청한다
                RegisterRequest registerRequest = new RegisterRequest(userID, userPassword, userName, userAge, responseListener);
                RequestQueue queue = Volley.newRequestQueue(SubActivity.this);
                queue.add(registerRequest);
            }

        });


    }
}