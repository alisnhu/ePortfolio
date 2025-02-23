package com.example.ali_arslan_project2;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import android.content.DialogInterface;
import androidx.fragment.app.FragmentManager;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class AddInventoryDialogFragment extends DialogFragment {

    private DBHelper dbHelper;
    private int userId;
    private restHelper myRestHelper;
    private String workingmode;
    private String sessionToken;
    private Map<String, Object> data;

    @Nullable
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // View oluştur
        if (getArguments() != null) {
            userId = getArguments().getInt("USER_ID", -1);
            workingmode = getArguments().getString("workingmode");
            if(workingmode.equals("online"))
            {
                sessionToken = getArguments().getString("sessionToken");
            }
        }
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_inventory, null);
        dbHelper = new DBHelper(requireContext());

        // EditText ve Button'ları tanımla
        EditText inventoryNameEditText = view.findViewById(R.id.inventoryNameEditText);
        Button addButton = view.findViewById(R.id.addButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(view)
                .setTitle("Add New Inventory")
                .setCancelable(true);

        addButton.setOnClickListener(v -> {
            String inventoryName = inventoryNameEditText.getText().toString().trim();
            if (!inventoryName.isEmpty()) {
                if(!workingmode.equals("online"))
                {
                    dbHelper.addInventory(inventoryName,userId);
                    Toast.makeText(requireContext(), "Inventory added: " + inventoryName, Toast.LENGTH_SHORT).show();
                    dismiss();
                }
                else
                {

                    ExecutorService executor = Executors.newFixedThreadPool(1);
                    executor.execute(() -> {
                        myRestHelper =  new restHelper("http://10.0.2.2/rest/");
                        data = myRestHelper.addInventory(inventoryName,sessionToken);
                        boolean status =(boolean) data.get("status");
                        requireActivity().runOnUiThread(() -> {
                            if(status)
                            {
                                Toast.makeText(requireContext(), "Inventory added: " + inventoryName, Toast.LENGTH_SHORT).show();
                                dismiss();
                            }
                            else
                            {
                                Toast.makeText(requireContext(), "Inventory can not added: " + inventoryName, Toast.LENGTH_SHORT).show();
                                dismiss();

                            }
                        });

                    });
                }
            } else {
                Toast.makeText(requireContext(), "Please enter an inventory name", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> dismiss());



        return builder.create();
    }
    @Override
    public void onDismiss(@Nullable DialogInterface dialog) {
        super.onDismiss(dialog);
        if (getActivity() instanceof mainPage) {
            ((mainPage) getActivity()).updateInventoryList();
        }
    }

    public void show(FragmentManager fragmentManager) {
        super.show(fragmentManager, "AddInventoryDialog");
    }

}
