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
import android.view.View;
import android.widget.Button;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import java.util.List;

public class mainPage extends AppCompatActivity {

    private DBHelper dbHelper;
    private LinearLayout whiteBackground;
    int userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = getIntent().getIntExtra("USER_ID", -1);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DBHelper(this);
        whiteBackground = findViewById(R.id.whiteBackground);

        List<List> inventories = dbHelper.getAllInventories(String.valueOf(userId));
        for (List<String> inventory : inventories) {
            String id = inventory.get(0);
            String name = inventory.get(1);
            addCardView(name,id);
        }

        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> {
            AddInventoryDialogFragment dialog = new AddInventoryDialogFragment();
            Bundle args = new Bundle();
            args.putInt("USER_ID", userId);
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

        // CardView'i whiteBackground LinearLayout'una ekle
        whiteBackground.addView(cardView);
    }

    public void openStockPage(View view) {
        Intent intent = new Intent(mainPage.this, StockPageActivity.class);
        CardView clickedCardView = (CardView) view;
        int inventoryId = Integer.parseInt((String)clickedCardView.getTag());
        intent.putExtra("USER_ID", userId);
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

}
