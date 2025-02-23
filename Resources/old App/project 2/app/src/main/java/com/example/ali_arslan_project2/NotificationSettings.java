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

import java.util.List;

public class NotificationSettings extends AppCompatActivity {
    int userId;
    int inventoryId;
    DBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbHelper = new DBHelper(this);

        super.onCreate(savedInstanceState);
        userId = getIntent().getIntExtra("USER_ID", -1);
        inventoryId = getIntent().getIntExtra("STOCK_ID", -1);
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
        loadTableData();
    }

    public void showDeleteConfirmationDialog(View view,boolean islist, int id ) {
        new AlertDialog.Builder(this)
                .setTitle("Deletion Confirmation")
                .setMessage("Are you sure to delete ??")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper.deletenotification(islist,id);
                        loadTableData();
                        Toast.makeText(NotificationSettings.this, "Deleted", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
    public void openStockPage(View view) {
        Intent intent = new Intent(NotificationSettings.this, mainPage.class);
        intent.putExtra("USER_ID", userId);
        intent.putExtra("STOCK_ID", inventoryId);
        startActivity(intent);
    }
    public void opennotificationSetting(View view) {
        Intent intent = new Intent(NotificationSettings.this, NotificationSettings.class);
        intent.putExtra("USER_ID", userId);
        intent.putExtra("STOCK_ID", inventoryId);
        startActivity(intent);
    }
    public void addNewNotification(View view)
    {
        addNotification mydialog = new addNotification().newInstance(userId,inventoryId);
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
    public void deleteAllNotification(View view)
    {
        showDeleteConfirmationDialog(view, true, inventoryId);
    }
}