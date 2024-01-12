package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class LoginActivity extends AppCompatActivity {
    public static final String fileName = "DB.xls", sheetName = "Doctors DB";
    private EditText username, password;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;
        username = findViewById(R.id.LoginUserName);
        password = findViewById(R.id.LoginPassword);

//check if excel file
        File file = new File(this.getExternalFilesDir(null), fileName);
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
        }

        //if excel exists or the username exists
        findViewById(R.id.LoginButton2).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (RegisterActivity.workbook == null
                        || !isUserExist(RegisterActivity.workbook.getSheet(sheetName),
                        username.getText().toString(), password.getText().toString()))
                {
                    Toast.makeText(context, "This User Doesn't exist!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent login = new Intent(LoginActivity.this, ClientMeeting.class);
                startActivity(login);
            }
        });
    }
//checks username and password is exists in the db
    public static boolean isUserExist(Sheet sheet, String username, String password) {
        for (int i = 0; sheet.getRow(i) != null; i++)
            if (sheet.getRow(i).getCell(0).getStringCellValue().equals(username)
                    && sheet.getRow(i).getCell(2).getStringCellValue().equals(password))
                return true;
        return false;
    }
}