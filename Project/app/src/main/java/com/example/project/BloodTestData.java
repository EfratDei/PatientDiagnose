package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class BloodTestData extends AppCompatActivity {
    public static EditText[] bloodData;
    private Button importXL, manual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_test_data);

        bloodData = new EditText[11];
        bloodData[0] = findViewById(R.id.Name);
        bloodData[1] = findViewById(R.id.id);
        bloodData[2] = findViewById(R.id.age);
        bloodData[3] = findViewById(R.id.rbc);
        bloodData[4] = findViewById(R.id.hct);
        bloodData[5] = findViewById(R.id.urea);
        bloodData[6] = findViewById(R.id.hb);
        bloodData[7] = findViewById(R.id.crtn);
        bloodData[8] = findViewById(R.id.iron);
        bloodData[9] = findViewById(R.id.hdl);
        bloodData[10] = findViewById(R.id.ap);

        importXL = findViewById(R.id.importXL);
        manual = findViewById(R.id.manual);

        manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isAllFilled()) {
                    Toast.makeText(BloodTestData.this, "Not all data is filled!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent diagnosis = new Intent(BloodTestData.this, DiagnosisResults.class);
                startActivity(diagnosis);
            }
        });
        importXL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(BloodTestData.this.getExternalFilesDir(null), "output.xls");
                FileInputStream fileInputStream;
                if (file.exists()) {
                    try {
                        fileInputStream = new FileInputStream(file);
                        RegisterActivity.workbook = new HSSFWorkbook(fileInputStream);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(BloodTestData.this, "Excel doesn't exist, please enter data manually!", Toast.LENGTH_SHORT).show();
                    return;
                }
                int rowPatient = findRowPatient(RegisterActivity.workbook.getSheet("Patients Diagnosis"),
                        ClientMeeting.id.getText().toString());
                if (rowPatient == -1) {
                    Toast.makeText(BloodTestData.this, "Patient doesn't exist, please enter data manually!", Toast.LENGTH_SHORT).show();
                    return;
                }

                getDataFromPatient(RegisterActivity.workbook.getSheet("Patients Diagnosis"), rowPatient);

                Intent diagnosis = new Intent(BloodTestData.this, DiagnosisResults.class);
                startActivity(diagnosis);
            }
        });

    }

    private boolean isAllFilled() {
        for (EditText editText : bloodData)
            if (editText.getText().toString().equals(""))
                return false;
        return true;
    }

    private int findRowPatient(Sheet sheet, String ID) {
        for (int i = 0; sheet.getRow(i) != null; i++)
            if (sheet.getRow(i).getCell(1).getStringCellValue().equals(ID))
                return i;
        return -1;
    }

    private void getDataFromPatient(Sheet sheet, int patientRow) {
        for (int i = 0; i < DiagnosisActivity.radioButtons.length; i++)
            DiagnosisActivity.radioButtons[i].setChecked(sheet.getRow(patientRow).getCell(i + 2).getStringCellValue().equals("True"));
        for (int i = 0; i < bloodData.length; i++)
            bloodData[i].setText(sheet.getRow(patientRow).getCell(i + 13).getStringCellValue());
    }
}