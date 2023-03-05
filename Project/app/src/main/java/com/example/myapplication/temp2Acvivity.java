package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class temp2Acvivity extends AppCompatActivity {

    private TextView tv_id, tv_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp2_acvivity);

        tv_id = findViewById(R.id.tv_id);
        tv_password = findViewById(R.id.tv_password);

        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");
        String userPassword = intent.getStringExtra("userPassword");


        tv_id.setText(userID);
        tv_password.setText(userPassword);

        Button button=findViewById(R.id.start);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Intent intent=new Intent(temp2Acvivity.this,temp3Activity.class);
                //intent.putExtra("userID",userID);
                //startActivity(intent);




                //여기서 내가 만드는거(본식)
                //String userID = tv_id.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //JSONObject jsonObject = new JSONObject(response);
                            //boolean success = jsonObject.getBoolean("success");
                            Intent intent=new Intent(temp2Acvivity.this,temp3Activity.class);
                            intent.putExtra("userID",userID);
                            startActivity(intent);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                };

                // 서버로 Volley를 이용해서 요청한다
                GoRequest goRequest = new  GoRequest(userID, responseListener);
                RequestQueue queue = Volley.newRequestQueue(temp2Acvivity.this);
                queue.add(goRequest);


            }
        });

    }
}