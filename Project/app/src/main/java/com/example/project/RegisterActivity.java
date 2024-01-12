package com.example.project;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {
    private static EditText username, password, ID;
    private Context context;
    public static Workbook workbook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = findViewById(R.id.name);
        password = findViewById(R.id.newAccountPassword);
        ID = findViewById(R.id.id);
        context = this;
        findViewById(R.id.CreatNewAccountButton).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 101); // storage
                checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, 101); // storage

                String userName = username.getText().toString(), passWord = password.getText().toString();
                if (userName.length() > 8 || userName.length() < 6) {
                    Toast.makeText(context, "The username length isn't between 6-8", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!checkInput.checkUsername(userName)) {
                    Toast.makeText(context, "The username has more than two digits or have not an english character!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (passWord.length() > 10 || passWord.length() < 8) {
                    Toast.makeText(context, "The password length isn't between 8-10", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!checkInput.checkPassword(passWord)) {
                    Toast.makeText(context, "The password doesn't have special letter/digit/letter!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!createExcelDB())// exist or created successfully
                {
                    Toast.makeText(context, "Error creating DB!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int freeRow=getFirstFreeRow(workbook.getSheet("Doctors DB"));
                if (freeRow==-1)// exist or created successfully
                {
                    Toast.makeText(context, "Username already taken!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!addNewUser(freeRow)) {
                    Toast.makeText(context, "Error Adding user!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent diagnosis = new Intent(RegisterActivity.this, ClientMeeting.class);
                startActivity(diagnosis);
            }
        });

    }


    private boolean createExcelDB() { // username   password    ID
        if (!storeExcelInStorage(this, "DB.xls")) {
            Toast.makeText(this, "Registration failed!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private static boolean storeExcelInStorage(Context context, String fileName) {
        boolean isSuccess;
        File file = new File(context.getExternalFilesDir(null), fileName);
        FileInputStream fileInputStream;
        if (file.exists()) {
            try {
                fileInputStream = new FileInputStream(file);
                workbook = new HSSFWorkbook(fileInputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        // if xl doesn't exist
        workbook = new HSSFWorkbook();
        // create new sheet
        Sheet sheet = workbook.createSheet("Doctors DB");

        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("Username");
        row.createCell(1).setCellValue("ID");
        row.createCell(2).setCellValue("Password");

        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(file);
            workbook.write(fileOutputStream);
            Log.e(TAG, "Writing file" + file);
            isSuccess = true;
        } catch (IOException e) {
            Log.e(TAG, "Error writing Exception: ", e);
            isSuccess = false;
        } catch (Exception e) {
            Log.e(TAG, "Failed to save file due to Exception: ", e);
            isSuccess = false;
        } finally {
            try {
                if (null != fileOutputStream) {
                    fileOutputStream.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return isSuccess;
    }

    // Function to check and request permission
    public void checkPermission(String permission, int requestCode) {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }

    public static int getFirstFreeRow(Sheet sheet) {
        for (int i = 0; true; i++)
            if (sheet.getRow(i) == null)
                return i;
            else if (ID!=null && sheet.getRow(i).getCell(1).getStringCellValue().equals(ID.getText().toString()))
                return -1;
    }

    private boolean addNewUser(int freeRow) {
        try {
            File file = new File(context.getExternalFilesDir(null), "DB.xls");
            FileOutputStream fileOutputStream = new FileOutputStream(file);

            String pass = password.getText().toString(), user = username.getText().toString(), id = ID.getText().toString();
            workbook.getSheet("Doctors DB").createRow(freeRow);
            workbook.getSheet("Doctors DB").getRow(freeRow).createCell(0).setCellValue(user);
            workbook.getSheet("Doctors DB").getRow(freeRow).createCell(1).setCellValue(id);
            workbook.getSheet("Doctors DB").getRow(freeRow).createCell(2).setCellValue(pass);

            workbook.write(fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}