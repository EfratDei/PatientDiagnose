package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

public class DiagnosisActivity extends AppCompatActivity {
    public static RadioButton[] radioButtons;
    private Button proceed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnosis);

        radioButtons=new RadioButton[11];
        radioButtons[0]=findViewById(R.id.HadStroke);
        radioButtons[1]=findViewById(R.id.HadHeartAttack);
        radioButtons[2]=findViewById(R.id.HighBloodPressure);
        radioButtons[3]=findViewById(R.id.HadStroke);
        radioButtons[4]=findViewById(R.id.pregnent);
        radioButtons[5]=findViewById(R.id.lowBloodPressure);
        radioButtons[6]=findViewById(R.id.diabetic);
        radioButtons[7]=findViewById(R.id.drags);
        radioButtons[8]=findViewById(R.id.eastern);
        radioButtons[9]=findViewById(R.id.manWoman);
        radioButtons[10]=findViewById(R.id.ethiopian);
        proceed=findViewById(R.id.proceed);

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DiagnosisActivity.this,BloodTestData.class);
                startActivity(intent);
            }
        });

    }
}