package com.example.ali_arslan_project2;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import android.telephony.SmsManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UpdateInventoryFragment extends DialogFragment {


    public static UpdateInventoryFragment newInstance(int itemId,int userID,String workingMode,String sessionToken ,int inventoryId) {
        UpdateInventoryFragment fragment = new UpdateInventoryFragment();
        Bundle args = new Bundle();
        args.putInt("itemId", itemId);
        args.putInt("userID", userID);
        args.putInt("inventoryId", inventoryId);
        args.putString("workingMode", workingMode);
        args.putString("sessionToken", sessionToken);
        fragment.setArguments(args);
        return fragment;
    }
    int itemId;
    int userID;
    int inventoryId;
    String workingMode , sessionToken;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create the dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        Bundle arguments = getArguments();
        if (arguments != null) {
            itemId = arguments.getInt("itemId");
            userID = arguments.getInt("userID");
            workingMode = arguments.getString("workingMode");
            if(workingMode.equals("online"))
            {
                inventoryId = arguments.getInt("inventoryId");
                sessionToken = arguments.getString("sessionToken");
            }
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


        EditText nameEditText = dialogView.findViewById(R.id.formName);
        EditText locationEditText = dialogView.findViewById(R.id.formLocation);
        EditText stockNumberPicker = dialogView.findViewById(R.id.formStock);
        EditText notificationNumberPicker = dialogView.findViewById(R.id.formNotification);
        CheckBox notificationCheckbox = dialogView.findViewById(R.id.notificationCheckbox);
        if(workingMode.equals("online"))
        {

            ExecutorService executor = Executors.newFixedThreadPool(1);
            executor.execute(() -> {

                Map<String , Object> data;
                restHelper RestHelper = new restHelper("http://10.0.2.2/rest/");
                data = RestHelper.getItem(String.valueOf(itemId),String.valueOf(inventoryId),sessionToken);

                boolean status = (boolean) data.get("status");
                requireActivity().runOnUiThread(() -> {
                    if(status)
                    {
                        try
                        {
                            JSONArray resultArrray = new JSONArray(data.get("result").toString());
                            JSONObject resultObject = resultArrray.getJSONObject(0);
                            nameEditText.setText(resultObject.getString("name"));
                            locationEditText.setText(resultObject.getString("location"));
                            stockNumberPicker.setText(resultObject.getString("stock"));
                            notificationNumberPicker.setText(resultObject.getString("lessthan"));
                            if(resultObject.getString("itemnotification").equals("1"))
                            {
                                notificationCheckbox.setChecked(true);
                            }
                            else
                            {
                                notificationCheckbox.setChecked(false);
                            }
                        }catch(Exception e)
                        {
                            Log.e("hatalogu" , e.toString());
                            Toast.makeText(getContext(), "data cannot added"+e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(getContext(), "data cannot added", Toast.LENGTH_SHORT).show();
                    }
                    // nbu kisim senkron yapilacak islemler icin
                });

            });

        }
        else
        {

            DBHelper dbHelper = new DBHelper(getActivity());
            Item item = dbHelper.getItemById(itemId);
            if (item != null) {
                nameEditText.setText(item.name);
                locationEditText.setText(item.location);
                stockNumberPicker.setText(String.valueOf(item.stock));
                notificationNumberPicker.setText(String.valueOf(item.lessThan));
                notificationCheckbox.setChecked(item.notification);
            }
        }






        // Find and set up the Add button separately
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button addButton = dialog.findViewById(R.id.buttonAdd);
            Button cancelButton = dialog.findViewById(R.id.buttonCancel);

            if (addButton != null) {
                addButton.setOnClickListener(v -> {
                    // Handle the Add button click
                    handleUpdateButtonClick(dialogView);
                    dismiss(); // Dismiss the dialog after adding
                });
            }

            if (cancelButton != null) {
                cancelButton.setOnClickListener(v -> dismiss());
            }
        });

        return dialog;
    }

    private void handleUpdateButtonClick(View dialogView) {
        // Extract data from form fields and handle accordingly
        DBHelper dbHelper = new DBHelper(getActivity());
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
        if(workingMode.equals("online"))
        {

            restHelper RestHelper = new restHelper("http://10.0.2.2/rest/");
            ExecutorService executor = Executors.newFixedThreadPool(1);
            executor.execute(() -> {

                Map <String, Object> data;
                // bu kism asenkron yapinicin
                data = RestHelper.updateItem(String.valueOf(itemId),name,location,String.valueOf(stock),String.valueOf(inventoryId),isNotificationEnabled,notification,sessionToken);
                boolean status = (boolean) data.get("status");
                requireActivity().runOnUiThread(() -> {
                    if(status)
                    {
                        Toast.makeText(getContext(), "data updated", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getContext(), "data cannot added", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                });

            });

        }
        else
        {
            dbHelper.updateItem(name, location, stock, isNotificationEnabled, notification, itemId);
            dismiss();
        }

        /**
         * SMS sending codes will be here ---------------------------------------------
         */
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            String phoneNumber =dbHelper.getPHoneNumber(userID);
            if (isNotificationEnabled && stock<notification)
            {
                SmsManager smsManager =SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber,null,"Your inventory "+name+" is lover than "+notification,null,null);
            }
            boolean checknotification = dbHelper.checkNotificationLessThan(stock);
            if(checknotification)
            {
                SmsManager smsManager =SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber,null,"Your inventory "+name+" is lover than ",null,null);
            }
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

