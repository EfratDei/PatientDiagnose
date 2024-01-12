package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class DiagnosisResults extends AppCompatActivity {
    private ListView list;
    private listAdapter adapter;
    private ArrayList<item> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnosis_results);

        items = new ArrayList<>();
        adapter = new listAdapter(this, items);
        list = findViewById(R.id.diagnosisList);
        setItems();
        list.setAdapter(adapter);

        if (!writeDiagnosisToXL(createOutputXL()))
            Toast.makeText(this, "Error with creating new data in XL!", Toast.LENGTH_SHORT).show();
    }

    private boolean writeDiagnosisToXL(int freeRow) {
        File file = new File(this.getExternalFilesDir(null), "output.xls");
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        Sheet sheet = RegisterActivity.workbook.getSheet("Patients Diagnosis");
        Row row = sheet.createRow(freeRow);
        row.createCell(0).setCellValue(ClientMeeting.name.getText().toString());
        row.createCell(1).setCellValue(ClientMeeting.id.getText().toString());
        for (int i = 0; i < DiagnosisActivity.radioButtons.length; i++)
            if (DiagnosisActivity.radioButtons[i].isChecked())
                row.createCell(i + 2).setCellValue("True");
            else
                row.createCell(i + 2).setCellValue("False");

        for (int i = 0; i < BloodTestData.bloodData.length; i++)
            row.createCell(i + 13).setCellValue(BloodTestData.bloodData[i].getText().toString());

        String diagnosis = "", recommendation = "";
        for (int i = 0; i < items.size(); i++) {
            diagnosis += i + ". " + items.get(i).getDiagnosis() + "\n";
            recommendation += i + ". " + items.get(i).getTreatment() + "\n";
        }
        row.createCell(24).setCellValue(diagnosis);
        row.createCell(25).setCellValue(recommendation);
        row.createCell(26).setCellValue(ClientMeeting.date.toString());

        try {
            RegisterActivity.workbook.write(fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private int createOutputXL() {
        File file = new File(this.getExternalFilesDir(null), "output.xls");
        FileInputStream fileInputStream;
        if (file.exists()) {
            try {
                fileInputStream = new FileInputStream(file);
                RegisterActivity.workbook = new HSSFWorkbook(fileInputStream);
                return getFirstFreeRow(RegisterActivity.workbook.getSheet("Patients Diagnosis"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            RegisterActivity.workbook = new HSSFWorkbook();
            Sheet sheet = RegisterActivity.workbook.createSheet("Patients Diagnosis");
            Row row = sheet.createRow(0);
            row.createCell(0).setCellValue("Name");
            row.createCell(1).setCellValue("ID");
            for (int i = 0; i < DiagnosisActivity.radioButtons.length; i++)
                row.createCell(i + 2).setCellValue(DiagnosisActivity.radioButtons[i].getText().toString());

            for (int i = 0; i < BloodTestData.bloodData.length; i++)
                row.createCell(i + 13).setCellValue(BloodTestData.bloodData[i].getHint().toString());

            row.createCell(24).setCellValue("Diagnosis");
            row.createCell(25).setCellValue("Recommendation");
            row.createCell(26).setCellValue("Date");

            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(file);
                RegisterActivity.workbook.write(fileOutputStream);
            } catch (IOException e) {
            }
            return 1;
        }
        return -1;
    }

    private void setItems() {
        for (int i = 0; i < BloodTestData.bloodData.length; i++)
            items.add(new item(BloodTestData.bloodData[i].getHint() +
                    "=" + BloodTestData.bloodData[i].getText(),
                    "", ""));
        setByAllCriterias();
    }

    private void setByAllCriterias() {
        boolean isEastern, isMan, isPreganent;
        float value;
        for (int i = 0, age; i < BloodTestData.bloodData.length; i++) {
            value = Float.parseFloat(BloodTestData.bloodData[i].getText().toString());
            age = Integer.valueOf(ClientMeeting.age.getText().toString());
            isEastern = DiagnosisActivity.radioButtons[8].isChecked();
            isMan = DiagnosisActivity.radioButtons[9].isChecked(); // false=Woman
            isPreganent = DiagnosisActivity.radioButtons[4].isChecked();
            if (i == 0)// wbc
            {
                if (age >= 18 && value < 4500 || age >= 4 && age <= 17 && value < 5500 || age >= 0 && age <= 3 && value < 6000) {
                    items.get(i).setDiagnosis("מצביעים על מחלה ויראלית, כשל של מערכת החיסון ובמקרים נדירים ביותר על סרטן.");
                    items.get(i).setTreatment("לנוח בבית ובמקרה נדיר לקחת אנטרקטיניב - Entrectinib");
                } else if (age >= 18 && value > 11000 || age >= 4 && age <= 17 && value > 15500 || age >= 0 && age <= 3 && value < 17500) {
                    items.get(i).setDiagnosis("מצביעים לרוב על קיומו של זיהום, אם קיימת מחלת חום. במקרים אחרים, נדירים ביותר, עלולים\n" +
                            "ערכים גבוהים מאוד להעיד על מחלת דם או סרטן.\n");
                    items.get(i).setTreatment("שילוב של ציקלופוספאמיד וקורטיקוסרואידים\n" +
                            "אנטרקטיניב - Entrectinib\n" +
                            "אנטיביוטיקה ייעודית\n");
                }
            }

            if (i == 1)// wbc
            {
                if (value > 0.54) {
                    items.get(i).setDiagnosis("זיהום.");
                    items.get(i).setTreatment("אנטיביוטיקה יעודית");
                } else if (value < 0.28) {
                    items.get(i).setDiagnosis("מעידים על הפרעה ביצירת הדם, על נטייה לזיהומים מחיידקים ובמקרים נדירים - על תהליך\n" +
                            "סרטני");
                    items.get(i).setTreatment("דור 10 מ\"ג של B12 ביום למשך חודש\n" +
                            "כדור 5 מ\"ג של חומצה פולית ביום למשך חודש");
                }

            }
            if (i == 2)// wbc
            {
                if (value > 0.52) {
                    items.get(i).setDiagnosis("עשויים להצביע על זיהום חיידקי ממושך או על סרטן הלימפומה");
                    items.get(i).setTreatment("אנטיביוטיקה יעודית\n" +
                            "אנטרקטיניב - Entrectinib");
                } else if (value < 0.36) {
                    items.get(i).setDiagnosis("מעיד על בעייה ביצירת תאי הדם");
                    items.get(i).setTreatment("דור 10 מ\"ג של B12 ביום למשך חודש\n" +
                            "כדור 5 מ\"ג של חומצה פולית ביום למשך חודש");


                }
                if (i == 3)// wbc
                {
                    if (value > 6) {
                        items.get(i).setDiagnosis("עלולים להצביע על הפרעה במערכת ייצור הדם. רמות גבוהות נצפו גם אצל מעשנים ואצל חולים\n" +
                                "במחלות ריאות.");
                        items.get(i).setTreatment("כדור 10 מ\"ג של B12 ביום למשך חודש\n" +
                                "כדור 5 מ\"ג של חומצה פולית ביום למשך חודש");

                    } else if (value < 4.5) {
                        items.get(i).setDiagnosis("עשוי להצביע על אנמיה או דימומים קשים");
                        items.get(i).setTreatment("שני כדורי 10 מ\"ג של B12 ביום למשך חודש");
                    }
                }
                if (i == 4)//htc needs to set gender
                {
                    //men
                    if (isMan) {
                        if (value > 0.54) {
                            items.get(i).setDiagnosis("ערכים גבוהים: שכיח בדרך כלל אצל מעשנים.");
                            items.get(i).setTreatment("להפסיק לעשן");
                        } else if (value <= 0.37) {
                            items.get(i).setDiagnosis("ערכים נמוכים: מצביעים לרוב על דימום או על אנמיה.");
                            items.get(i).setTreatment("שני כדורי 10 מ\"ג של B12 ביום למשך חודש ולהתפנות בדחיפות לבית החולים");
                        }
                    }
                    //woman
                    else if (value <= 0.33) {
                        items.get(i).setDiagnosis("ערכים נמוכים: מצביעים לרוב על דימום או על אנמיה.");
                        items.get(i).setTreatment("שני כדורי 10 מ\"ג של B12 ביום למשך חודש ולהתפנות בדחיפות לבית החולים");
                    } else if (value >= 0.47) {
                        items.get(i).setDiagnosis("ערכים גבוהים: שכיח בדרך כלל אצל מעשנים.");
                        items.get(i).setTreatment("להפסיק לעשן");
                    }
                }
                if (i == 5)// Urea לבדוק עדות המזרח ואם האישה בהריון
                {
                    if (!isEastern) {
                        if (value > 43) {
                            items.get(i).setDiagnosis("עלולים להצביע על מחלות כליה, התייבשות או דיאטה עתירת חלבונים");
                            items.get(i).setTreatment("לתאם פגישה עם תזונאי, איזון את רמות הסוכר בדם ומנוחה מוחלטת בשכיבה, החזרת נוזלים בשתייה");

                        } else if (value < 17) {
                            if (isPreganent)
                                items.get(i).setDiagnosis("ערכים נמוכים: תת תזונה, דיאטה דלת חלבון או מחלת כבד. יש לציין שבהריון רמת השתנן יורדת");
                            else
                                items.get(i).setDiagnosis("ערכים נמוכים: תת תזונה, דיאטה דלת חלבון או מחלת כבד.");
                            items.get(i).setTreatment("לתאם פגישה עם תזונאי, הפנייה לאבחנה ספציפית לצורך קביעת טיפול");
                        }
                    } else if (value > 43 * 1.1) {
                        items.get(i).setDiagnosis("עלולים להצביע על מחלות כליה, התייבשות או דיאטה עתירת חלבונים");
                        items.get(i).setTreatment("לתאם פגישה עם תזונאי, איזון את רמות הסוכר בדם ומנוחה מוחלטת בשכיבה, החזרת נוזלים בשתייה");

                    } else if (value < 17 * 1.1) {
                        if (isPreganent)
                            items.get(i).setDiagnosis("ערכים נמוכים: תת תזונה, דיאטה דלת חלבון או מחלת כבד. יש לציין שבהריון רמת השתנן יורדת");
                        else
                            items.get(i).setDiagnosis("ערכים נמוכים: תת תזונה, דיאטה דלת חלבון או מחלת כבד.");
                        items.get(i).setTreatment("לתאם פגישה עם תזונאי, הפנייה לאבחנה ספציפית לצורך קביעת טיפול");
                    }
                }

            }
            if (i == 6)// hb
            {
                if (age <= 17 && value <= 11.5 && !(value >= 12 && value <= 18) && isMan || !(value >= 12 && value <= 16) && !isMan) {
                    items.get(i).setDiagnosis("ערכים נמוכים: מעידים על אנמיה. זו יכולה לנבוע מהפרעה המטולוגית, ממחסור בברזל ומדימומים.");
                    items.get(i).setTreatment("שני כדורי 10 מ\"ג של B12 ביום למשך חודש,שני כדורי 10 מ\"ג של B12 ביום למשך חודש,להתפנות בדחיפות לבית החולים וזריקה של הורמון לעידוד ייצור תאי הדם האדומים");
                }
            }
            if (i == 7)// crtn
            {
                if (age > 0 && age <= 2 && value < 0.2) {
                    items.get(i).setDiagnosis("ערכים נמוכים: נראים לרוב בחולים בעלי מסת שריר ירודה מאוד ואנשים בתת תזונה שאינם צורכים די חלבון");
                    items.get(i).setTreatment("לתאם פגישה עם תזונאי,שני כדורי 5 מ\"ג של כורכום c3 של אלטמן ביום למשך חודש");
                } else if (age > 0 && age <= 2 && value < 0.5) {
                    items.get(i).setDiagnosis("ערכים גבוהים: עלולים להצביע על בעיה כלייתית ובמקרים חמורים על אי ספיקת כליות. ערכים גבוהים ניתן\n" +
                            "למצוא גם בעת שלשולים והקאות )הגורמים לפירוק מוגבר של שריר ולערכים גבוהים של קריאטינין(, מחלות שריר\n" +
                            "וצריכה מוגברת של בשר");
                    items.get(i).setTreatment("לתאם פגישה עם תזונאי,איזון את רמות הסוכר בדם ושני כדורי 5 מ\"ג של כורכום c3 של אלטמן ביום למשך חודש");
                } else if (age >= 3 && age <= 17 && value > 1) {
                    items.get(i).setDiagnosis("ערכים גבוהים: עלולים להצביע על בעיה כלייתית ובמקרים חמורים על אי ספיקת כליות. ערכים גבוהים ניתן\n" +
                            "למצוא גם בעת שלשולים והקאות )הגורמים לפירוק מוגבר של שריר ולערכים גבוהים של קריאטינין(, מחלות שריר\n" +
                            "וצריכה מוגברת של בשר");
                    items.get(i).setTreatment("לתאם פגישה עם תזונאי,איזון את רמות הסוכר בדם ושני כדורי 5 מ\"ג של כורכום c3 של אלטמן ביום למשך חודש");
                } else if (age >= 3 && age <= 17 && value > 0.5) {
                    items.get(i).setDiagnosis("ערכים נמוכים: נראים לרוב בחולים בעלי מסת שריר ירודה מאוד ואנשים בתת תזונה שאינם צורכים די חלבון");
                    items.get(i).setTreatment("לתאם פגישה עם תזונאי,שני כדורי 5 מ\"ג של כורכום c3 של אלטמן ביום למשך חודש");
                } else if (age >= 18 && age <= 59 && value > 0.6) {
                    items.get(i).setDiagnosis("ערכים נמוכים: נראים לרוב בחולים בעלי מסת שריר ירודה מאוד ואנשים בתת תזונה שאינם צורכים די חלבון");
                    items.get(i).setTreatment("לתאם פגישה עם תזונאי,שני כדורי 5 מ\"ג של כורכום c3 של אלטמן ביום למשך חודש");
                } else if (age >= 18 && age <= 59 && value > 1) {
                    items.get(i).setDiagnosis("ערכים גבוהים: עלולים להצביע על בעיה כלייתית ובמקרים חמורים על אי ספיקת כליות. ערכים גבוהים ניתן\n" +
                            "למצוא גם בעת שלשולים והקאות )הגורמים לפירוק מוגבר של שריר ולערכים גבוהים של קריאטינין(, מחלות שריר\n" +
                            "וצריכה מוגברת של בשר");
                    items.get(i).setTreatment("לתאם פגישה עם תזונאי,איזון את רמות הסוכר בדם ושני כדורי 5 מ\"ג של כורכום c3 של אלטמן ביום למשך חודש");
                } else if (age >= 60 && value > 1.2) {
                    items.get(i).setDiagnosis("ערכים גבוהים: עלולים להצביע על בעיה כלייתית ובמקרים חמורים על אי ספיקת כליות. ערכים גבוהים ניתן\n" +
                            "למצוא גם בעת שלשולים והקאות )הגורמים לפירוק מוגבר של שריר ולערכים גבוהים של קריאטינין(, מחלות שריר\n" +
                            "וצריכה מוגברת של בשר");
                    items.get(i).setTreatment("לתאם פגישה עם תזונאי,איזון את רמות הסוכר בדם ושני כדורי 5 מ\"ג של כורכום c3 של אלטמן ביום למשך חודש");
                } else if (age >= 60 && value > 0.6) {
                    items.get(i).setDiagnosis("ערכים נמוכים: נראים לרוב בחולים בעלי מסת שריר ירודה מאוד ואנשים בתת תזונה שאינם צורכים די חלבון");
                    items.get(i).setTreatment("לתאם פגישה עם תזונאי,שני כדורי 5 מ\"ג של כורכום c3 של אלטמן ביום למשך חודש");
                }
            }
            if (i == 8)// iron
            {
                if (isMan) {
                    if (value >= 160) {
                        items.get(i).setDiagnosis("רמות גבוהות: עלולים להצביע על הרעלת ברזל.");
                        items.get(i).setTreatment("להתפנות לבית החולים");

                    } else if (value <= 60) {
                        items.get(i).setDiagnosis("רמות נמוכות: מעידה בדרך כלל על תזונה לא מספקת או על עלייה בצורך בברזל )למשל בהריון( או על איבוד דם\n" +
                                "בעקבות דימום.");
                        items.get(i).setTreatment("תאם פגישה עם תזונאי,להתפנות בדחיפות לבית החולים");
                    }
                } else if (value >= 160 * 0.8) {
                    items.get(i).setDiagnosis("רמות גבוהות: עלולים להצביע על הרעלת ברזל.");
                    items.get(i).setTreatment("להתפנות לבית החולים");

                } else if (value <= 60 * 0.8) {
                    items.get(i).setDiagnosis("רמות נמוכות: מעידה בדרך כלל על תזונה לא מספקת או על עלייה בצורך בברזל )למשל בהריון( או על איבוד דם\n" +
                            "בעקבות דימום.");
                    items.get(i).setTreatment("תאם פגישה עם תזונאי,להתפנות בדחיפות לבית החולים");
                }
            }
            if (i == 9)// hdl add gender
            {
                //men
                if (DiagnosisActivity.radioButtons[10].isChecked()) {
                    if (isMan) {
                        if (value >= 62 * 1.2) {
                            items.get(i).setDiagnosis("רמות גבוהות: לרוב אינן מזיקות. פעילות גופנית מעלה את רמות הכולסטרול ה\"טוב\"");
                            items.get(i).setTreatment("פעילות גופנית");
                        } else if (value <= 29 * 1.2) {
                            items.get(i).setDiagnosis("רמות נמוכות: עשויות להצביע על סיכון למחלות לב, על היפרליפידמיה )יתר שומנים בדם( או על סוכרת מבוגרים.");
                            items.get(i).setTreatment("לתאם פגישה עם תזונאי, כדור 5 מ\"ג של סימוביל ביום למשך שבוע,התאמת אינסולין למטופל ולתאם פגישה עם תזונאי");
                        }
                    }
                    //woman
                    else if (value >= 82 * 1.2) {
                        items.get(i).setDiagnosis("רמות גבוהות: לרוב אינן מזיקות. פעילות גופנית מעלה את רמות הכולסטרול ה\"טוב\"");
                        items.get(i).setTreatment("פעילות גופנית");
                    } else if (value <= 29 * 1.2) {
                        items.get(i).setDiagnosis("רמות נמוכות: עשויות להצביע על סיכון למחלות לב, על היפרליפידמיה )יתר שומנים בדם( או על סוכרת מבוגרים.");
                        items.get(i).setTreatment("לתאם פגישה עם תזונאי, כדור 5 מ\"ג של סימוביל ביום למשך שבוע,התאמת אינסולין למטופל ולתאם פגישה עם תזונאי");
                    }
                } else if (isMan) {
                    if (value >= 62) {
                        items.get(i).setDiagnosis("רמות גבוהות: לרוב אינן מזיקות. פעילות גופנית מעלה את רמות הכולסטרול ה\"טוב\"");
                        items.get(i).setTreatment("פעילות גופנית");
                    } else if (value <= 29) {
                        items.get(i).setDiagnosis("רמות נמוכות: עשויות להצביע על סיכון למחלות לב, על היפרליפידמיה )יתר שומנים בדם( או על סוכרת מבוגרים.");
                        items.get(i).setTreatment("לתאם פגישה עם תזונאי, כדור 5 מ\"ג של סימוביל ביום למשך שבוע,התאמת אינסולין למטופל ולתאם פגישה עם תזונאי");
                    }
                }
                //woman
                else if (value >= 82) {
                    items.get(i).setDiagnosis("רמות גבוהות: לרוב אינן מזיקות. פעילות גופנית מעלה את רמות הכולסטרול ה\"טוב\"");
                    items.get(i).setTreatment("פעילות גופנית");
                } else if (value <= 29) {
                    items.get(i).setDiagnosis("רמות נמוכות: עשויות להצביע על סיכון למחלות לב, על היפרליפידמיה )יתר שומנים בדם( או על סוכרת מבוגרים.");
                    items.get(i).setTreatment("לתאם פגישה עם תזונאי, כדור 5 מ\"ג של סימוביל ביום למשך שבוע,התאמת אינסולין למטופל ולתאם פגישה עם תזונאי");
                }
            }
            if (i == 10)// ap להוסיף עדות
            {
                //עדות המזרח
                if (isEastern) {
                    if (value >= 120) {
                        items.get(i).setDiagnosis("רמות גבוהות: עלולים להצביע על מחלות כבד, מחלות בדרכי המרה, הריון, פעילות יתר של בלוטת התריס או\n" +
                                "שימוש בתרופות שונות.");
                        items.get(i).setTreatment("הפנייה לאבחנה ספציפית לצורך קביעת טיפול,הפנייה לטיפול כירורגי,Propylthiouracil להקטנת פעילות בלוטת התריס והפנייה לרופא המשפחה לצורך בדיקת התאמה בין התרופות");
                    } else if (value <= 60) {
                        items.get(i).setDiagnosis("רמות נמוכות: עשויות להצביע על תזונה לקויה שחסרים בה חלבונים. חוסר בוויטמינים כמו ויטמין ,B12ויטמין ,C\n" +
                                "ויטמין ,B6חומצה פולית.");
                        items.get(i).setTreatment("הפנייה לבדיקת דם לזיהוי הוויטמינים החסרים,לתאם פגישה עם תזונאי");
                    }
                } else if (value <= 30) {
                    items.get(i).setDiagnosis("רמות נמוכות: עשויות להצביע על תזונה לקויה שחסרים בה חלבונים. חוסר בוויטמינים כמו ויטמין ,B12ויטמין ,C\n" +
                            "ויטמין ,B6חומצה פולית.");
                    items.get(i).setTreatment("הפנייה לבדיקת דם לזיהוי הוויטמינים החסרים,לתאם פגישה עם תזונאי");
                } else if (value >= 90) {
                    items.get(i).setDiagnosis("רמות גבוהות: עלולים להצביע על מחלות כבד, מחלות בדרכי המרה, הריון, פעילות יתר של בלוטת התריס או\n" +
                            "שימוש בתרופות שונות.");
                    items.get(i).setTreatment("הפנייה לאבחנה ספציפית לצורך קביעת טיפול,הפנייה לטיפול כירורגי,Propylthiouracil להקטנת פעילות בלוטת התריס והפנייה לרופא המשפחה לצורך בדיקת התאמה בין התרופות");
                }
            }
        }
    }

    private int getFirstFreeRow(Sheet sheet) {
        for (int i = 0; true; i++)
            if (sheet.getRow(i) == null)
                return i;
    }
}

