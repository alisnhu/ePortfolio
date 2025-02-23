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


public class AddInventoryDialogFragment extends DialogFragment {

    private DBHelper dbHelper;
    private int userId;

    @Nullable
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // View oluştur
        if (getArguments() != null) {
            userId = getArguments().getInt("USER_ID", -1);
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
                dbHelper.addInventory(inventoryName,userId);
                Toast.makeText(requireContext(), "Inventory added: " + inventoryName, Toast.LENGTH_SHORT).show();
                dismiss();
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
