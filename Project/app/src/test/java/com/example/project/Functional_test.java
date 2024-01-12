package com.example.project;

import static com.example.project.RegisterActivity.workbook;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.Before;
import org.junit.Test;
import org.junit.Test;

import static org.junit.Assert.*;

public class Functional_test {
    private HSSFWorkbook workbook;
    private final String sheetName = "DB_test";

    @Before
    public void createUsers() {
        workbook = new HSSFWorkbook();
        // create new sheet
        Sheet sheet = workbook.createSheet(sheetName);
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("Username");
        row.createCell(1).setCellValue("ID");
        row.createCell(2).setCellValue("Password");

        Row row2 = sheet.createRow(1);
        row2.createCell(0).setCellValue("Amit");
        row2.createCell(1).setCellValue("123");
        row2.createCell(2).setCellValue("Amit100!");

        Row row3 = sheet.createRow(2);
        row3.createCell(0).setCellValue("Daniel");
        row3.createCell(1).setCellValue("100");
        row3.createCell(2).setCellValue("Daniel1!");
    }

    @Test
    public void testLogin() {
        assertEquals(false,
                LoginActivity.isUserExist(
                        workbook.getSheet(sheetName),"Moshe","123"));

        assertEquals(false,
                LoginActivity.isUserExist(
                        workbook.getSheet(sheetName),"Yael","100"));

        assertEquals(true,
                LoginActivity.isUserExist(
                        workbook.getSheet(sheetName),"Amit","Amit100!"));

        assertEquals(true,
                LoginActivity.isUserExist(
                        workbook.getSheet(sheetName),"Daniel","Daniel1!"));
    }
    @Test
    public void testFreeRow(){
        assertNotEquals(-1, RegisterActivity.getFirstFreeRow(workbook.getSheet(sheetName)) );
        assertNotEquals(0, RegisterActivity.getFirstFreeRow(workbook.getSheet(sheetName)) );
        assertNotEquals(5, RegisterActivity.getFirstFreeRow(workbook.getSheet(sheetName)) );
        assertEquals(3, RegisterActivity.getFirstFreeRow(workbook.getSheet(sheetName)) );
    }
}
