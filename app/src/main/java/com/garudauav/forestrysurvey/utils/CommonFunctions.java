package com.garudauav.forestrysurvey.utils;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.garudauav.forestrysurvey.R;
import com.garudauav.forestrysurvey.databinding.CodeNotFoundDialogBinding;
import com.garudauav.forestrysurvey.databinding.SheetGeneratedDialogBinding;
import com.garudauav.forestrysurvey.models.TreeData;
import com.garudauav.forestrysurvey.models.response_models.ExportData;
import com.garudauav.forestrysurvey.ui.activities.SyncActivity;
import com.garudauav.forestrysurvey.utils.callback_interface.DatePickerCallback;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import okhttp3.ResponseBody;

public class CommonFunctions {

    public static String convertDateFormat2(String inputDate, String inputFormat, String outputFormat) {
        try {
            SimpleDateFormat inputDateFormat = new SimpleDateFormat(inputFormat);
            // Setting the time zone to the input's time zone if you want to preserve the original date
            // Or you could explicitly set it to the timezone of the input date, e.g., TimeZone.getTimeZone("GMT+5:30")

            SimpleDateFormat outputDateFormat = new SimpleDateFormat(outputFormat);
            outputDateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Assuming you want the output in UTC

            // Parse input date using input format
            Date date = inputDateFormat.parse(inputDate);

            // Subtract one day
            Calendar calendar = Calendar.getInstance();
            assert date != null;
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, +1); // adding one day

            // Format the date using the output format
            return outputDateFormat.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return ""; // Return an empty string or handle the error as needed
        }
    }


    //random color code generate
    public static String getRandomColorCode() {
        Random random = new Random();
        // Generate the next random integer value and ensure it is in 0x00 to 0xFF range for R, G, B components
        int red = random.nextInt(256); // 0-255
        int green = random.nextInt(256); // 0-255
        int blue = random.nextInt(256); // 0-255

        // Format it as hexadecimal and concatenate
        String colorCode = String.format("#%02X%02X%02X", red, green, blue);

        return colorCode;
    }
    // Convert date from one format to another
    public static String convertDateFormat(String inputDate, String outputFormat) {
        try {
            // Use 'EEE' for the short form of the day and correct quotes for "GMT"
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);

            // Parse the input date string
            Date date = inputDateFormat.parse(inputDate);

            // Format the date using the output format
            SimpleDateFormat outputDateFormat = new SimpleDateFormat(outputFormat, Locale.ENGLISH);

            assert date != null;
            return outputDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return ""; // Return an empty string or handle the error as needed
        }
    }

    public static void showDatePickerDialog(Context context, long date, int flag, DatePickerCallback callback) {
        Calendar calendar = Calendar.getInstance();

        // Create a date picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    Date selectedDateInUTC = calendar.getTime();

                    // Set the selected date in a TextView or any other UI component
                    // For example, assuming you have a TextView with the id "textViewSelectedDate":
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

                    // Create a Date object representing the current date and time
                    Date currentDate = new Date();

                    // Format the Date object in UTC format
                    String utcDateString = sdf.format(currentDate);

                    if (callback != null) {
                        callback.onDateSelected(selectedDateInUTC);
                    }

                },
                calendar.get(Calendar.YEAR), // Initial year
                calendar.get(Calendar.MONTH), // Initial month
                calendar.get(Calendar.DAY_OF_MONTH) // Initial day
        );

        // Show the date picker dialog
        if (flag == 0) {
            datePickerDialog.getDatePicker().setMaxDate(date);

        } else if (flag == 1) {
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.getDatePicker().setMinDate(date); // Disallow past dates
        } else {
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        }
        datePickerDialog.show();
    }

    static public boolean isNetworkConnectionAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
        if (isConnected) {
            Log.d("Network", "Connected");
            return true;
        } else {

            Log.d("Network", "Not Connected");
            return false;
        }
    }

    public static void generateExcelSheet(List<ExportData> treeDataList, Context context, String fromDate, String toDate, String name) {

        fromDate=convertDateFormat2(fromDate,"yyyy-MM-dd HH:mm:ss.SSS Z","dd-MM-yy");
        toDate=convertDateFormat2(toDate,"yyyy-MM-dd HH:mm:ss.SSS Z","dd-MM-yy");

        File documentsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String fileName = name + "_" + fromDate + "_" + toDate;
        File filePath = new File(documentsDirectory, fileName + ".csv");

        try {
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
            HSSFSheet hssfSheet = hssfWorkbook.createSheet("MySpecies");

            String[] headers = {"S.NO", "Name of Species", "Location", "Height", "Girth Of Tree",
                    "Total Tree", "Name of RF","Name of Plot" ,"Name of District", "Capture on", "Capture by"};
            HSSFRow headerRow = hssfSheet.createRow(0);


            for (int i = 0; i < headers.length; i++) {
                HSSFCell headerCell = headerRow.createCell(i);
                headerCell.setCellValue(headers[i]);
                //headerCell.setCellStyle(cellStyle);


                // Adjust the column width based on the length of the text
                int textLength = headers[i].length();
                int defaultColumnWidth = hssfSheet.getColumnWidth(i);
                int newColumnWidth = Math.max(defaultColumnWidth, (textLength + 2) * 256); // Adjusting width based on text length
                hssfSheet.setColumnWidth(i, newColumnWidth);
            }

            for (int i = 0; i < treeDataList.size(); i++) {
                HSSFRow dataRow = hssfSheet.createRow(i + 1);
                ExportData treeData = treeDataList.get(i);

                dataRow.createCell(0).setCellValue(i + 1);
                dataRow.createCell(1).setCellValue(treeData.getSpeciesName());


                String coordinates = treeData.getCoordinates().replace("[", "").replace("]", "");

                dataRow.createCell(2).setCellValue(coordinates);
                String height = "";
                if (treeData.getUnitHeight() == 0) {
                    height = treeData.getHeight() + " cm";

                } else if (treeData.getUnitHeight() == 1) {
                    height = treeData.getHeight() + " inch";


                } else if (treeData.getUnitHeight() == 2){
                    height = treeData.getHeight() + " feet";

                }else{
                    height = treeData.getHeight() + " meter";
                }
                dataRow.createCell(3).setCellValue(height);
                String girth = "";
                if (treeData.getUnitGirth() == 0) {
                    girth = treeData.getGirth() + " cm";

                } else if (treeData.getUnitGirth() == 1) {
                    girth = treeData.getGirth() + " inch";


                } else {
                    girth = treeData.getGirth() + " feet";

                }
                dataRow.createCell(4).setCellValue(girth);
                dataRow.createCell(5).setCellValue(treeData.getNumberOfTree());
                dataRow.createCell(6).setCellValue(treeData.getReservForestMasterName());
                dataRow.createCell(7).setCellValue(treeData.getBlockName());
                dataRow.createCell(8).setCellValue(treeData.getDistrict());
                String dateString = treeData.getCaptureDate(); // Example date string



                // Parse ISO string date into ZonedDateTime
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateString);

                // Convert to local timezone
                ZonedDateTime localDateTime = zonedDateTime.withZoneSameInstant(ZoneId.systemDefault());

                // Format local datetime into string representation
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String localDateTimeString = localDateTime.format(formatter);

                // Print the local datetime string
                System.out.println("Local DateTime: " + localDateTimeString);


                dataRow.createCell(9).setCellValue(localDateTimeString);
                dataRow.createCell(10).setCellValue(treeData.getCapturedBy());
            }

            if (filePath.exists()) {
                // If the file exists, modify the name
                int count = 1;
                while (filePath.exists()) {
                    count++;

                    filePath = new File(documentsDirectory, fileName + "(" + count + ")" + ".csv");

                    //   filePath = new File(Environment.getExternalStorageDirectory() + "/" + "eFileName" + "(" + count + ")" + ".xlsx");
                }
            }

            filePath.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            hssfWorkbook.write(fileOutputStream);

            SheetGeneratedDialogBinding dialogBinding = SheetGeneratedDialogBinding.inflate(LayoutInflater.from(context));
            AlertDialog alertDialog;

            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyDialogStyle);
            builder.setView(dialogBinding.getRoot());
            alertDialog = builder.create();

            dialogBinding.doneBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });


            alertDialog.show();



            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }


        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public void showError(int code, ResponseBody errorBody) {
        if (code == 400) {
            try {
                JSONObject jObjError = new JSONObject(errorBody.string());
                JSONArray jsonArray = jObjError.getJSONArray("Errors");
                String errorMessage = jsonArray.getString(0);
               /* DialogManager.responseMessageDialog = new ResponseMessageDialog(BaseClass.this, errorMessage);
                DialogManager.responseMessageDialog.show();*/
            } catch (IOException | JSONException e) {
               /* DialogManager.responseMessageDialog = new ResponseMessageDialog(BaseClass.this, e.getMessage());
                DialogManager.responseMessageDialog.show();*/
            }
        } else if (code == 401) {
           /* DialogManager.responseMessageDialog1 = new ResponseMessageDialognew(BaseClass.this, "The token has been expired. Please login again.");
            DialogManager.responseMessageDialog1.show();*/
        } else if (code == 403) {
           /* DialogManager.responseMessageDialog = new ResponseMessageDialog(BaseClass.this, "The access token was decoded successfully but did not include a scope appropriate to this endpoint");
            DialogManager.responseMessageDialog.show();*/
        } else if (code == 404) {
          /*  DialogManager.responseMessageDialog = new ResponseMessageDialog(BaseClass.this, "Resource not found");
            DialogManager.responseMessageDialog.show();*/

        } else if (code == 429) {
           /* DialogManager.responseMessageDialog = new ResponseMessageDialog(BaseClass.this, "Too many recent requests from you. Wait to make further submissions");
            DialogManager.responseMessageDialog.show();*/

        } else if (code == 500) {
           /* DialogManager.responseMessageDialog = new ResponseMessageDialog(BaseClass.this, "Internal server error. The request was not completed due to an internal error on the server side");
            DialogManager.responseMessageDialog.show();*/

        } else if (code == 503) {
          /*  DialogManager.responseMessageDialog = new ResponseMessageDialog(BaseClass.this, "Service unavailable. The server was unavailable");
            DialogManager.responseMessageDialog.show();*/

        } else {
           /* DialogManager.responseMessageDialog = new ResponseMessageDialog(BaseClass.this, "Something went wrong! Please contact administration!");
            DialogManager.responseMessageDialog.show();*/
        }
    }

}
