package com.example.ali_arslan_project2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import android.widget.Button;
import android.widget.Toast;
import android.util.Log;
import androidx.core.view.WindowInsetsCompat;
import android.database.Cursor;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.example.ali_arslan_project2.AddNewDialogFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StockPageActivity extends AppCompatActivity {

    int userId;
    int stockId;
    String workingMode,sessionToken;
    private DBHelper dbHelper;
    private  restHelper restHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbHelper = new DBHelper(this);
        super.onCreate(savedInstanceState);
        userId = getIntent().getIntExtra("USER_ID", -1);
        stockId = getIntent().getIntExtra("INVENTORY_ID", -1);
        workingMode = getIntent().getStringExtra("workingMode");
        if(workingMode.equals("online"))
        {
            sessionToken = getIntent().getStringExtra("sessionToken");
            restHelper =  new restHelper("http://10.0.2.2/rest/");
        }
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stock_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button addNewButton = findViewById(R.id.addNew);

        addNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create and show the DialogFragment
                AddNewDialogFragment dialog = AddNewDialogFragment.newInstance(userId, stockId,workingMode,sessionToken);
                dialog.show(getSupportFragmentManager(), "AddNewDialogFragment");
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
    // Silme uyarı penceresini göstermek için metod
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
                                boolean myStatus= true;
                                Map<String, Object> data;
                                if(islist)
                                {
                                    try
                                    {
                                        data = restHelper.getItem("",String.valueOf(stockId),sessionToken);
                                        myStatus=(boolean) data.get("status");
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
                                            restHelper.deleteItem(String.valueOf(did),sessionToken);
                                        }
                                    }catch (Exception e){}

                                }
                                else
                                {

                                    data = restHelper.deleteItem(String.valueOf(id),sessionToken);
                                    myStatus=(boolean) data.get("status");
                                }
                                final boolean status = myStatus;
                                runOnUiThread(() -> {
                                    if(status)
                                    {

                                        loadTableDataRest();
                                        Toast.makeText(StockPageActivity.this, "Deleted", Toast.LENGTH_SHORT).show();


                                    }
                                    else
                                    {
                                        Toast.makeText(StockPageActivity.this, "Item not deleted", Toast.LENGTH_SHORT).show();
                                    }

                                });
                            });
                        }
                        else
                        {
                            dbHelper.deleteItemById(islist,id);
                            loadTableData();
                            Toast.makeText(StockPageActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
    public void editItemFragment(View view,int id)
    {
        UpdateInventoryFragment dialog = UpdateInventoryFragment.newInstance(id,userId,workingMode,sessionToken,stockId);
        dialog.show(getSupportFragmentManager(), "UpdateInventoryFragment");
    }
    public void openStockPage(View view) {
        Intent intent = new Intent(StockPageActivity.this, mainPage.class);
        intent.putExtra("USER_ID", userId);
        intent.putExtra("STOCK_ID", stockId);
        intent.putExtra("workingMode", workingMode);
        if(workingMode.equals("online"))
        {
            intent.putExtra("sessionToken", sessionToken);
        }
        startActivity(intent);
    }
    public void opennotificationSetting(View view) {
        Intent intent = new Intent(StockPageActivity.this, NotificationSettings.class);
        intent.putExtra("USER_ID", userId);
        intent.putExtra("STOCK_ID", stockId);
        intent.putExtra("workingMode", workingMode);
        if(workingMode.equals("online"))
        {
            intent.putExtra("sessionToken", sessionToken);
        }
        startActivity(intent);
    }


    public void loadTableDataRest()
    {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.execute(() -> {
            Map<String, Object> data;
            data = restHelper.getItem("",String.valueOf(stockId),sessionToken);
            boolean status =(boolean) data.get("status");
            Object result =  data.get("result");
            runOnUiThread(() -> {
                if(status)
                {
                    JSONArray resultArray;
                    try
                    {
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



                        TableLayout tableLayout = findViewById(R.id.datagrid);
                        tableLayout.removeAllViews(); // Temizle önceki verilers
                        List<Item> items = dbHelper.getAllItems(String.valueOf(stockId));

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
                        headLocation.setText("Location");
                        headLocation.setPadding(8, 8, 8, 8);
                        headLocation.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
                        row1.addView(headLocation);

                        TextView headStock = new TextView(this);
                        headStock.setTextColor(0xFFFFFFFF);
                        headStock.setText("Stock");
                        headStock.setPadding(8, 8, 8, 8);
                        headStock.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
                        row1.addView(headStock);

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
                        for (int i = 0 ; i < resultArray.length(); i++)
                        {
                            TableRow row = new TableRow(this);
                            JSONObject element = resultArray.getJSONObject(i);
                            String id = element.getString("id");
                            String name = element.getString("name");
                            String location = element.getString("location");
                            String stock = element.getString("stock");
                            String noti= element.getString("itemnotification");
                            boolean notification;
                            if(noti.equals("1"))
                            {
                                notification = true;
                            }
                            else
                            {

                                notification = false;
                            }
                            String lessthan = element.getString("lessthan");

                            int backgroundColor;
                            if (notification && Integer.parseInt(stock) < Integer.parseInt(lessthan)) {
                                backgroundColor = 0xFFFFCCCC; // Açık kırmızı
                            } else {
                                backgroundColor = (i % 2 == 0) ? 0xFFFFFFFF : 0xFFF0F0F0; // Beyaz ve gri
                            }
                            row.setBackgroundColor(backgroundColor);

                            // Adı ekle
                            TextView nameView = new TextView(this);
                            nameView.setText(name);
                            nameView.setPadding(8, 8, 8, 8);
                            nameView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
                            row.addView(nameView);

                            // Lokasyonu ekle
                            TextView locationView = new TextView(this);
                            locationView.setText(location);
                            locationView.setPadding(8, 8, 8, 8);
                            locationView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
                            row.addView(locationView);

                            // Stoku ekle
                            TextView stockView = new TextView(this);
                            stockView.setText(String.valueOf(stock));
                            stockView.setPadding(8, 8, 8, 8);
                            stockView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
                            row.addView(stockView);
                            // empty ekle
                            TextView empty = new TextView(this);
                            empty.setText(String.valueOf(""));
                            empty.setPadding(8, 8, 8, 8);
                            empty.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
                            row.addView(empty);

                            // Edit ve Delete butonlarını ekle
                            ImageView editButton = new ImageView(this);
                            editButton.setImageResource(R.drawable.ic_edit);
                            editButton.setPadding(8, 8, 8, 8);
                            editButton.setOnClickListener(v -> editItemFragment(v,Integer.parseInt(id)));
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
                        Log.e("hata",e.toString());
                        Toast.makeText(this, "Data Could not retreived 1.", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(this, "Data Could not retreived 2.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
    public void loadTableData() {
        TableLayout tableLayout = findViewById(R.id.datagrid);
        tableLayout.removeAllViews(); // Temizle önceki verilers
        List<Item> items = dbHelper.getAllItems(String.valueOf(stockId));

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
        headLocation.setText("Location");
        headLocation.setPadding(8, 8, 8, 8);
        headLocation.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
        row1.addView(headLocation);

        TextView headStock = new TextView(this);
        headStock.setTextColor(0xFFFFFFFF);
        headStock.setText("Stock");
        headStock.setPadding(8, 8, 8, 8);
        headStock.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
        row1.addView(headStock);

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



        int counter=0;
        for (Item item : items)
        {
            TableRow row = new TableRow(this);

            int id = item.id;
            String name = item.name;
            String location = item.location;
            int stock = item.stock;
            boolean notification = item.notification;
            int lessthan = item.lessThan;

            // Satır rengi belirleme
            int backgroundColor;
            if (notification && stock < lessthan) {
                backgroundColor = 0xFFFFCCCC; // Açık kırmızı
            } else {
                backgroundColor = (counter % 2 == 0) ? 0xFFFFFFFF : 0xFFF0F0F0; // Beyaz ve gri
            }
            row.setBackgroundColor(backgroundColor);

            // Adı ekle
            TextView nameView = new TextView(this);
            nameView.setText(name);
            nameView.setPadding(8, 8, 8, 8);
            nameView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
            row.addView(nameView);

            // Lokasyonu ekle
            TextView locationView = new TextView(this);
            locationView.setText(location);
            locationView.setPadding(8, 8, 8, 8);
            locationView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
            row.addView(locationView);

            // Stoku ekle
            TextView stockView = new TextView(this);
            stockView.setText(String.valueOf(stock));
            stockView.setPadding(8, 8, 8, 8);
            stockView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
            row.addView(stockView);
            // empty ekle
            TextView empty = new TextView(this);
            empty.setText(String.valueOf(""));
            empty.setPadding(8, 8, 8, 8);
            empty.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
            row.addView(empty);

            // Edit ve Delete butonlarını ekle
            ImageView editButton = new ImageView(this);
            editButton.setImageResource(R.drawable.ic_edit);
            editButton.setPadding(8, 8, 8, 8);
            editButton.setOnClickListener(v -> editItemFragment(v,id));
            editButton.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
            row.addView(editButton);

            ImageView deleteButton = new ImageView(this);
            deleteButton.setImageResource(R.drawable.ic_delete);
            deleteButton.setPadding(8, 8, 8, 8);
            deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog(v,false,id));
            deleteButton.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
            row.addView(deleteButton);

            tableLayout.addView(row);

            counter++;
        }
    }

    public void deleteAllItems(View view)
    {
        showDeleteConfirmationDialog(view, true, stockId);
    }


}