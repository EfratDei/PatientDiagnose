package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.Date;

public class ClientMeeting extends AppCompatActivity {
    public static EditText name, age, id;
    public static Date date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_meeting);

        name=findViewById(R.id.Name);
        age=findViewById(R.id.age);
        id=findViewById(R.id.id);

        findViewById(R.id.importXL).setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                date=new Date();
                Intent diagnosis = new Intent(ClientMeeting.this,DiagnosisActivity.class);
                startActivity(diagnosis);
            }
        });

    }

}