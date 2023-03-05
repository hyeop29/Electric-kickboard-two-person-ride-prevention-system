package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.wifi.hotspot2.pps.HomeSp;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

public class temp3Activity extends AppCompatActivity {

    private TextView tv_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp3);

        tv_id = findViewById(R.id.tv_id);

        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");


        tv_id.setText(userID);

        Button button=findViewById(R.id.finish);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Intent intent=new Intent(temp3Activity.this, HomeActivity.class);
                //startActivity(intent);



                //본식이가 만든부분
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //JSONObject jsonObject = new JSONObject(response);
                            //boolean success = jsonObject.getBoolean("success");
                            Intent intent=new Intent(temp3Activity.this, HomeActivity.class);
                            startActivity(intent);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                };

                // 서버로 Volley를 이용해서 요청한다
                StopRequest stopRequest = new  StopRequest(responseListener);
                RequestQueue queue = Volley.newRequestQueue(temp3Activity.this);
                queue.add(stopRequest);

            }
        });
    }

}