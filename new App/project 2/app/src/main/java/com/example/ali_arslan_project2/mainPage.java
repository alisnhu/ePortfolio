package com.example.ali_arslan_project2;

import android.content.Intent;
import android.os.Bundle;
import com.example.ali_arslan_project2.AddInventoryDialogFragment;
import java.util.List;
import java.util.ArrayList;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import java.util.List;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class mainPage extends AppCompatActivity {

    private DBHelper dbHelper;
    private  restHelper restHelper;
    private LinearLayout whiteBackground;
    int userId;
    String workingMode;
    String sessionToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = getIntent().getIntExtra("USER_ID", -1);
        workingMode = getIntent().getStringExtra("workingMode");
        Log.e("Working mode" , workingMode);
        if(workingMode.equals("online"))
        {
            sessionToken =getIntent().getStringExtra("sessionToken");
        }


        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DBHelper(this);
        if(workingMode.equals("online"))
        {
            restHelper =  new restHelper("http://10.0.2.2/rest/");
        }
        whiteBackground = findViewById(R.id.whiteBackground);

        if(workingMode.equals("online"))
        {
            updateInventoryListRest();
        }
        else
        {
            updateInventoryList();
        }
        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> {
            AddInventoryDialogFragment dialog = new AddInventoryDialogFragment();
            Bundle args = new Bundle();
            args.putInt("USER_ID", userId);
            args.putString("workingmode", workingMode);
            if(workingMode.equals("online"))
            {
                args.putString("sessionToken", sessionToken);
            }
            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(), "AddInventoryDialog");
        });
    }

    private void addCardView(String inventoryName,String inventoryId) {

        LinearLayout whiteBackground = findViewById(R.id.whiteBackground); // Find the layout only once
        // Yeni bir CardView oluşturuluyor
        CardView cardView = (CardView) LayoutInflater.from(this).inflate(R.layout.inventory_card, whiteBackground, false);
        TextView textView = cardView.findViewById(R.id.inventoryItem);
        textView.setText(inventoryName);
        cardView.setTag(inventoryId);

        ImageView deleteIcon = cardView.findViewById(R.id.delete);  // Silme ikonunu bul
        deleteIcon.setTag(inventoryId);  // Silme ikonuna da tag ekle

        // CardView'i whiteBackground LinearLayout'una ekle
        whiteBackground.addView(cardView);
    }

    public void openStockPage(View view) {
        Intent intent = new Intent(mainPage.this, StockPageActivity.class);
        CardView clickedCardView = (CardView) view;
        int inventoryId = Integer.parseInt((String)clickedCardView.getTag());
        intent.putExtra("USER_ID", userId);
        intent.putExtra("workingMode", workingMode);
        if(workingMode.equals("online"))
        {
            intent.putExtra("sessionToken", sessionToken);
        }
        intent.putExtra("INVENTORY_ID", inventoryId);
        startActivity(intent);
    }

    public void updateInventoryList() {
        whiteBackground.removeAllViews(); // Mevcut görünümü temizle
        List<List> inventories = dbHelper.getAllInventories(String.valueOf(userId));
        for (List<String> inventory : inventories) {
            String id = inventory.get(0);
            String name = inventory.get(1);
            addCardView(name,id);
        }
    }

    public void updateInventoryListRest() {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.execute(() -> {

            // bu kism asenkron yapinicin
            Map<String, Object> data;
            data = restHelper.getInventory("",sessionToken);
            boolean status =(boolean) data.get("status");
            //String result = (String) data.get("result").toString();
            Object result =  data.get("result");
            JSONObject jsonObject;

            runOnUiThread(() -> {
                if(status)
                {
                    whiteBackground.removeAllViews(); // Mevcut görünümü temizle
                    JSONArray resultArray;
                    try{
                        if (result instanceof JSONArray)
                        {
                            resultArray = (JSONArray) result;  // Direkt JSON dizisi olarak al
                        }
                        else
                        {
                            resultArray = new JSONArray(result.toString());  // String ise JSON dizisine çevir
                        }
                        Log.e("Buara kadar geldi","vallahi geldi");

                        for (int i = 0 ; i < resultArray.length(); i++) {
                            JSONObject element = resultArray.getJSONObject(i);
                            String id = element.getString("id");
                            String name = element.getString("inventory_name");
                            addCardView(name,id);
                        }
                    }catch(Exception e)
                    {
                        Toast.makeText(this, "Data Could not retreived.", Toast.LENGTH_SHORT).show();
                    }
                }

                else
                {
                    Toast.makeText(this, "Data Could not retreived.", Toast.LENGTH_SHORT).show();
                }

            });

        });

    }

    public void deleteInventory(View view)
    {

        String inventoryId = (String) view.getTag(); // ID'yi al

        if(workingMode.equals("online"))
        {

            ExecutorService executor = Executors.newFixedThreadPool(1);
            executor.execute(() -> {
                Map<String, Object> data;
                data = restHelper.deleteInventory(inventoryId,sessionToken);
                boolean status =(boolean) data.get("status");
                runOnUiThread(() -> {
                    if(status)
                    {
                        updateInventoryListRest();
                        Toast.makeText(this, "Inventoryu Deleted.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(this, "Inventoryu couldnot Deleted.", Toast.LENGTH_SHORT).show();
                    }
                });

            });
        }
        else
        {

        }


    }

}
