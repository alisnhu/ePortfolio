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
public class AddNewDialogFragment extends DialogFragment {


    public static AddNewDialogFragment newInstance(int userId, int stockId,String workingMode,String sessionToken) {
        AddNewDialogFragment fragment = new AddNewDialogFragment();
        Bundle args = new Bundle();
        args.putInt("userId", userId);
        args.putInt("stockId", stockId);
        args.putString("workingMode", workingMode);
        args.putString("sessionToken", sessionToken);
        fragment.setArguments(args);
        return fragment;
    }
    int userId;
    int inventoryId;
    String workingMode;
    String sessionToken;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create the dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        Bundle arguments = getArguments();
        if (arguments != null) {
            userId = arguments.getInt("userId");
            inventoryId = arguments.getInt("stockId");
            workingMode = arguments.getString("workingMode");
            sessionToken = arguments.getString("sessionToken");
        }



        // Inflate the dialog layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_new, null);
        Button stockIncrement = dialogView.findViewById(R.id.stockIncrement);
        Button stockDecrement = dialogView.findViewById(R.id.stockDecrement);
        EditText formStock = dialogView.findViewById(R.id.formStock);
        stockIncrement.setOnClickListener(v -> adjustValue(formStock, true));
        stockDecrement.setOnClickListener(v -> adjustValue(formStock, false));

        Button notificationIncrement = dialogView.findViewById(R.id.notificationIncrement);
        Button notificationDecrement = dialogView.findViewById(R.id.notificationDecrement);
        EditText formNotification = dialogView.findViewById(R.id.formNotification);
        notificationIncrement.setOnClickListener(v -> adjustValue(formNotification, true));
        notificationDecrement.setOnClickListener(v -> adjustValue(formNotification, false));
        // Set the custom layout for the dialog
        builder.setView(dialogView);

        // Find and set up the Add button separately
        AlertDialog dialog = builder.create();
         dialog.setOnShowListener(dialogInterface -> {
            Button addButton = dialog.findViewById(R.id.buttonAdd);
            Button cancelButton = dialog.findViewById(R.id.buttonCancel);

            if (addButton != null) {
                addButton.setOnClickListener(v -> {
                    // Handle the Add button click
                    handleAddButtonClick(dialogView);
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
        restHelper restHelper = new restHelper("http://10.0.2.2/rest/");
        EditText nameEditText = dialogView.findViewById(R.id.formName);
        EditText locationEditText = dialogView.findViewById(R.id.formLocation);
        EditText stockNumberPicker = dialogView.findViewById(R.id.formStock);
        EditText notificationNumberPicker = dialogView.findViewById(R.id.formNotification);
        CheckBox notificationCheckbox = dialogView.findViewById(R.id.notificationCheckbox);

        String name = nameEditText.getText().toString();
        String location = locationEditText.getText().toString();
        int stock = Integer.parseInt(stockNumberPicker.getText().toString());
        int notification = Integer.parseInt(notificationNumberPicker.getText().toString());
        boolean isNotificationEnabled = notificationCheckbox.isChecked();
        Log.e("notificatio", String.valueOf(isNotificationEnabled));
        if(!workingMode.equals("online"))
        {
            dbHelper.addItem(name, location, stock, isNotificationEnabled, notification, userId, inventoryId);
        }
        else
        {
            ExecutorService executor = Executors.newFixedThreadPool(1);
            executor.execute(() -> {
                    Map<String, Object> data;
                    data = restHelper.addItem(name, location, String.valueOf(stock), String.valueOf(inventoryId), isNotificationEnabled, notification, sessionToken);
                    boolean status =(boolean) data.get("status");
                    String message = (String) data.get("message");
                    requireActivity().runOnUiThread(() -> {
                        Log.e("Status" , String.valueOf(status));
                        Log.e("message" ,message);
                        if(status)
                        {
                            Toast.makeText(requireContext(), "item added: ", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(requireContext(), "item could not added: " + (String)data.get("message"), Toast.LENGTH_SHORT).show();
                        }
                });
            });
        }


    }

    public void adjustValue(EditText editText, boolean increase)
    {
        View view = getView();
        try
        {
            int currentValue = Integer.parseInt(editText.getText().toString());
            if (increase)
            {
                currentValue++;
            }
            else
            {
                if (currentValue > 0)
                {
                    currentValue--;
                }
                else
                {
                    Toast.makeText(getContext(), "Stock value cannot be less than 0", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            editText.setText(String.valueOf(currentValue));
        }
        catch (NumberFormatException e)
        {
            editText.setText("0");
        }
    }


    @Override
    public void onDismiss(@Nullable DialogInterface dialog) {
        super.onDismiss(dialog);
        if (getActivity() instanceof StockPageActivity) {
            if(workingMode.equals("online"))
            {

                ((StockPageActivity) getActivity()).loadTableDataRest();
            }
            else
            {

                ((StockPageActivity) getActivity()).loadTableData();
            }
            ((StockPageActivity) getActivity()).loadTableData();
        }
    }

}

