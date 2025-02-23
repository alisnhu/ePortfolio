package com.example.ali_arslan_project2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;

import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class addNotification extends DialogFragment {


    public static addNotification newInstance(int userId, int stockId,String workingMode, String sessionToken) {
        Log.d("Ozel","instanceda gelen veriler"  + userId +" - inventory id"+stockId);
        addNotification fragment = new addNotification();
        Bundle args = new Bundle();
        args.putInt("USER_ID", userId); // Anahtar adı burada "USER_ID"
        args.putInt("STOCK_ID", stockId); // Anahtar adı burada "USER_ID"
        args.putString("workingMode", workingMode);
        args.putString("sessionToken", sessionToken);
        fragment.setArguments(args);
        return fragment;
    }
    int userId;
    int inventoryId;
    String workingMode, sessionToken;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create the dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        Bundle arguments = getArguments();
        if (arguments != null) {
            userId = arguments.getInt("USER_ID",-1);
            inventoryId = arguments.getInt("STOCK_ID",-1);
            workingMode = arguments.getString("workingMode");
            sessionToken = arguments.getString("sessionToken");
        }



        // Inflate the dialog layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_add_notification, null);
        // Set the custom layout for the dialog
        builder.setView(dialogView);

        // Find and set up the Add button separately
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button addButton = dialog.findViewById(R.id.addButton);
            Button cancelButton = dialog.findViewById(R.id.cancelButton);

            if (addButton != null) {
                addButton.setOnClickListener(v -> {
                    // Handle the Add button click
                    if(workingMode.equals("online"))
                    {
                        handleAddButtonClickRest(dialogView);
                    }
                    else
                    {
                        handleAddButtonClick(dialogView);
                    }
                    dialog.dismiss(); // Dismiss the dialog after adding
                });
            }

            if (cancelButton != null) {
                cancelButton.setOnClickListener(v -> dialog.dismiss());
            }
        });

        return dialog;
    }

    private void handleAddButtonClick(View dialogView) {
        // Extract data from form fields and handle accordingly
        DBHelper dbHelper = new DBHelper(getActivity());
        EditText inventoryNameEditText = dialogView.findViewById(R.id.inventoryNameEditText);
        EditText numberTextBox = dialogView.findViewById(R.id.numberTextBox);

        String name = inventoryNameEditText.getText().toString();
        String less = numberTextBox.getText().toString();
        Log.d("Ozel","burada gelen veriler usersid"  + userId +" - inventory id"+inventoryId+" - less "+less + "name" + name);
        dbHelper.addNotification(name, less, userId, inventoryId);


    }

    private void handleAddButtonClickRest(View dialogView) {
        // Extract data from form fields and handle accordingly
        restHelper RestHelper = new restHelper("http://10.0.2.2/rest/");
        EditText inventoryNameEditText = dialogView.findViewById(R.id.inventoryNameEditText);
        EditText numberTextBox = dialogView.findViewById(R.id.numberTextBox);

        String name = inventoryNameEditText.getText().toString();
        String less = numberTextBox.getText().toString();
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.execute(() -> {

            Map<String, Object> data = RestHelper.addNotification(name,Integer.parseInt(less),inventoryId,sessionToken);
            boolean status = (boolean) data.get("status");
            requireActivity().runOnUiThread(() -> {
                if(status)
                {
                    Toast.makeText(requireContext(), "item added: ", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    Toast.makeText(requireContext(), "item can not added: ", Toast.LENGTH_SHORT).show();
                }
            });

        });



    }




    @Override
    public void onDismiss(@Nullable DialogInterface dialog) {
        super.onDismiss(dialog);
        if (getActivity() instanceof NotificationSettings) {
            if(workingMode.equals("online"))
            {
                ((NotificationSettings) getActivity()).loadTableDataRest();
            }
            else
            {

                ((NotificationSettings) getActivity()).loadTableData();
            }
        }
    }

}

