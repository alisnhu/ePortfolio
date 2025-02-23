package com.example.ali_arslan_project2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotificationSettings extends AppCompatActivity {
    int userId;
    int inventoryId;
    String workingMode,sessionToken;
    DBHelper dbHelper;
    restHelper RestHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbHelper = new DBHelper(this);

        super.onCreate(savedInstanceState);
        userId = getIntent().getIntExtra("USER_ID", -1);
        inventoryId = getIntent().getIntExtra("STOCK_ID", -1);
        workingMode = getIntent().getStringExtra("workingMode");
        if(workingMode.equals("online"))
        {
            sessionToken = getIntent().getStringExtra("sessionToken");
            RestHelper =  new restHelper("http://10.0.2.2/rest/");
        }
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        if(workingMode.equals("online"))
        {
            loadTableDataRest();
        }
        else
        {
            loadTableData();
        }
    }

    public void showDeleteConfirmationDialog(View view,boolean islist, int id ) {
        new AlertDialog.Builder(this)
                .setTitle("Deletion Confirmation")
                .setMessage("Are you sure to delete ??")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if(workingMode.equals("online"))
                        {
                            ExecutorService executor = Executors.newFixedThreadPool(1);
                            executor.execute(() -> {
                                Map<String,Object> data;
                                boolean myStatus= true;
                                if(islist)
                                {
                                    try
                                    {
                                        data = RestHelper.getNotification("",inventoryId,sessionToken);
                                        Object result =  data.get("result");
                                        JSONArray resultArray;
                                        if (result instanceof JSONArray)
                                        {
                                            resultArray = (JSONArray) result;  // Direkt JSON dizisi olarak al
                                        }
                                        else
                                        {
                                            resultArray = new JSONArray(result.toString());  // String ise JSON dizisine çevir
                                        }

                                        for (int i = 0 ; i < resultArray.length(); i++)
                                        {
                                            JSONObject element = resultArray.getJSONObject(i);
                                            String did = element.getString("id");
                                            RestHelper.deleteNotification(Integer.parseInt(did),sessionToken);
                                        }
                                    }
                                    catch(Exception e)
                                    {
                                        myStatus = false;
                                    }


                                }
                                else
                                {
                                    data = RestHelper.deleteNotification(id,sessionToken);
                                    myStatus =(boolean) data.get("status");

                                }

                                final boolean status = myStatus;
                                runOnUiThread(() -> {
                                    if(status)
                                    {
                                        loadTableDataRest();
                                        Toast.makeText(NotificationSettings.this, "Deleted", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(NotificationSettings.this, "Problem occured status false", Toast.LENGTH_SHORT).show();
                                    }
                                    // nbu kisim senkron yapilacak islemler icin
                                });

                            });

                        }
                        else
                        {
                            dbHelper.deletenotification(islist,id);
                            loadTableData();
                            Toast.makeText(NotificationSettings.this, "Deleted", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void openStockPage(View view) {
        Intent intent = new Intent(NotificationSettings.this, mainPage.class);
        intent.putExtra("USER_ID", userId);
        intent.putExtra("STOCK_ID", inventoryId);
        intent.putExtra("workingMode", workingMode);
        if(workingMode.equals("online"))
        {
            intent.putExtra("sessionToken", sessionToken);
        }
        startActivity(intent);
    }
    public void opennotificationSetting(View view) {
        Intent intent = new Intent(NotificationSettings.this, NotificationSettings.class);
        intent.putExtra("USER_ID", userId);
        intent.putExtra("STOCK_ID", inventoryId);
        intent.putExtra("workingMode", workingMode);
        if(workingMode.equals("online"))
        {
            intent.putExtra("sessionToken", sessionToken);
        }
        startActivity(intent);
    }
    public void addNewNotification(View view)
    {
        addNotification mydialog = new addNotification().newInstance(userId,inventoryId,workingMode,sessionToken);
        mydialog.show(getSupportFragmentManager(), "addNotification");
    }

    public void loadTableData() {
        TableLayout tableLayout = findViewById(R.id.datagrid);
        tableLayout.removeAllViews(); // Temizle önceki verilers
        List<Item2> items = dbHelper.getAllNotification(String.valueOf(userId));

        TableRow row1 = new TableRow(this);
        row1.setBackgroundColor(0xFF4962C6);


        TextView headName = new TextView(this);
        headName.setTextColor(0xFFFFFFFF);
        headName.setText("Name");
        headName.setPadding(8, 8, 8, 8);
        headName.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
        row1.addView(headName);

        TextView headLocation = new TextView(this);
        headLocation.setTextColor(0xFFFFFFFF);
        headLocation.setText("Less");
        headLocation.setPadding(8, 8, 8, 8);
        headLocation.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
        row1.addView(headLocation);


        TextView headD = new TextView(this);
        headD.setTextColor(0xFFFFFFFF);
        headD.setText("Delete");
        headD.setPadding(8, 8, 8, 8);
        headD.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
        row1.addView(headD);

        tableLayout.addView(row1);



        int counter=0;
        for (Item2 item : items)
        {
            TableRow row = new TableRow(this);

            String id = item.id;
            String name = item.name;
            String less = item.less;


            // Satır rengi belirleme
            int backgroundColor = (counter % 2 == 0) ? 0xFFFFFFFF : 0xFFF0F0F0; // Beyaz ve gri
            row.setBackgroundColor(backgroundColor);

            // Adı ekle
            TextView nameView = new TextView(this);
            nameView.setText(name);
            nameView.setPadding(8, 8, 8, 8);
            nameView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
            row.addView(nameView);

            // Less ekle
            TextView locationView = new TextView(this);
            locationView.setText(less);
            locationView.setPadding(8, 8, 8, 8);
            locationView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
            row.addView(locationView);

            ImageView deleteButton = new ImageView(this);
            deleteButton.setImageResource(R.drawable.ic_delete);
            deleteButton.setPadding(8, 8, 8, 8);
            deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog(v,false,Integer.parseInt(id)));
            deleteButton.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
            row.addView(deleteButton);

            tableLayout.addView(row);

            counter++;
        }
    }
    public void  loadTableDataRest()
    {

        TableLayout tableLayout = findViewById(R.id.datagrid);
        tableLayout.removeAllViews(); // Temizle önceki verilers



        TableRow row1 = new TableRow(this);
        row1.setBackgroundColor(0xFF4962C6);


        TextView headName = new TextView(this);
        headName.setTextColor(0xFFFFFFFF);
        headName.setText("Name");
        headName.setPadding(8, 8, 8, 8);
        headName.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
        row1.addView(headName);

        TextView headLocation = new TextView(this);
        headLocation.setTextColor(0xFFFFFFFF);
        headLocation.setText("Less");
        headLocation.setPadding(8, 8, 8, 8);
        headLocation.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
        row1.addView(headLocation);

        TextView headE = new TextView(this);
        headE.setTextColor(0xFFFFFFFF);
        headE.setText("Edit");
        headE.setPadding(8, 8, 8, 8);
        headE.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
        row1.addView(headE);

        TextView headD = new TextView(this);
        headD.setTextColor(0xFFFFFFFF);
        headD.setText("Delete");
        headD.setPadding(8, 8, 8, 8);
        headD.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
        row1.addView(headD);

        tableLayout.addView(row1);

        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.execute(() -> {
            Map<String, Object> data;
            data = RestHelper.getNotification("",inventoryId,sessionToken);
            boolean status = (boolean) data.get("status");
            Object result =  data.get("result");
            runOnUiThread(() -> {
                if(status)
                {
                    try
                    {

                        JSONArray resultArray;
                        if (result instanceof JSONArray)
                        {
                            resultArray = (JSONArray) result;  // Direkt JSON dizisi olarak al
                        }
                        else if (result instanceof JSONObject)
                        {
                            resultArray = new JSONArray();  // Boş bir JSONArray
                        }
                        else
                        {
                            resultArray = new JSONArray();  // Boş bir JSONArray
                        }

                        for (int i = 0 ; i < resultArray.length(); i++)
                        {
                            TableRow row = new TableRow(this);
                            JSONObject element = resultArray.getJSONObject(i);
                            String id = element.getString("id");
                            String name = element.getString("name");
                            String less = element.getString("lessthan");


                            // Satır rengi belirleme
                            int backgroundColor = (i % 2 == 0) ? 0xFFFFFFFF : 0xFFF0F0F0; // Beyaz ve gri
                            row.setBackgroundColor(backgroundColor);

                            // Adı ekle
                            TextView nameView = new TextView(this);
                            nameView.setText(name);
                            nameView.setPadding(8, 8, 8, 8);
                            nameView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
                            row.addView(nameView);

                            // Less ekle
                            TextView locationView = new TextView(this);
                            locationView.setText(less);
                            locationView.setPadding(8, 8, 8, 8);
                            locationView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
                            row.addView(locationView);

                            ImageView editButton = new ImageView(this);
                            editButton.setImageResource(R.drawable.ic_edit);
                            editButton.setPadding(8, 8, 8, 8);
                            editButton.setOnClickListener(v -> editnotificationFragment(v,Integer.parseInt(id)));
                            editButton.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
                            row.addView(editButton);

                            ImageView deleteButton = new ImageView(this);
                            deleteButton.setImageResource(R.drawable.ic_delete);
                            deleteButton.setPadding(8, 8, 8, 8);
                            deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog(v,false,Integer.parseInt(id)));
                            deleteButton.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
                            row.addView(deleteButton);

                            tableLayout.addView(row);

                        }
                    }
                    catch(Exception e)
                    {
                        Toast.makeText(NotificationSettings.this, "Error Occured 1 "+e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    String message = (String) data.get("message");
                    Toast.makeText(NotificationSettings.this, "Error Occured "+message, Toast.LENGTH_SHORT).show();
                }
                // nbu kisim senkron yapilacak islemler icin
            });

        });


    }

    public void editnotificationFragment(View view,int id)
    {
       updateNotificationFragment dialog = updateNotificationFragment.newInstance(id,userId,workingMode,sessionToken,inventoryId);
        dialog.show(getSupportFragmentManager(), "UpdateInventoryFragment");
    }

    public void deleteAllNotification(View view)
    {
        showDeleteConfirmationDialog(view, true, inventoryId);

    }
}