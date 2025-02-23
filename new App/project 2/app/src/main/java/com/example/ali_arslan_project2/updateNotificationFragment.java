package com.example.ali_arslan_project2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class updateNotificationFragment extends DialogFragment {




    public static updateNotificationFragment newInstance(int notificationId,int userID,String workingMode,String sessionToken ,int inventoryId) {
        updateNotificationFragment fragment = new updateNotificationFragment();
        Bundle args = new Bundle();
        args.putInt("notificationId", notificationId);
        args.putInt("userID", userID);
        args.putInt("inventoryId", inventoryId);
        args.putString("workingMode", workingMode);
        args.putString("sessionToken", sessionToken);
        fragment.setArguments(args);
        return fragment;
    }
    int notificationId;
    int userID;
    int inventoryId;
    String workingMode , sessionToken;
    DBHelper dbHelper;
    restHelper RestHelper;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Bundle arguments = getArguments();
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            notificationId = arguments.getInt("notificationId");
            userID = arguments.getInt("userID");
            workingMode = arguments.getString("workingMode");
            if(workingMode.equals("online"))
            {
                inventoryId = arguments.getInt("inventoryId");
                sessionToken = arguments.getString("sessionToken");
                RestHelper = new restHelper("http://10.0.2.2/rest/");
            }
            else
            {
                dbHelper = new DBHelper(getActivity());
            }
        }
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_update_notification, null);
        // Set the custom layout for the dialog
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button addButton = dialog.findViewById(R.id.addButton);
            Button cancelButton = dialog.findViewById(R.id.cancelButton);
            EditText name = dialog.findViewById(R.id.inventoryNameEditText);
            EditText less = dialog.findViewById(R.id.numberTextBox);
            if(workingMode.equals("online"))
            {
                ExecutorService executor = Executors.newFixedThreadPool(1);
                executor.execute(() -> {
                    Log.e("notification id degeri",String.valueOf(notificationId));
                    Log.e("inventiry id degeri",String.valueOf(inventoryId));
                    Map<String,Object> data = RestHelper.getNotification(String.valueOf(notificationId),inventoryId,sessionToken);
                    boolean status = (boolean) data.get("status");
                    requireActivity().runOnUiThread(() -> {

                        if(status)
                        {
                            try
                            {
                                JSONArray resultArrray = new JSONArray(data.get("result").toString());
                                JSONObject resultObject = resultArrray.getJSONObject(0);
                                name.setText(resultObject.getString("name"));
                                less.setText(resultObject.getString("lessthan"));
                            }
                            catch(Exception e)
                            {
                                Toast.makeText(getContext(), "data cannot added"+e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                });
            }
            else
            {
                Item2 data = dbHelper.getNotificationById(notificationId,inventoryId);
                name.setText(data.name);
                less.setText(data.less);
            }
            if (addButton != null) {
                addButton.setOnClickListener(v -> {
                    // Handle the Add button click
                    if(workingMode.equals("online"))
                    {
                        ExecutorService executor = Executors.newFixedThreadPool(1);
                        executor.execute(() -> {
                            String uname = name.getText().toString();
                            String uless = less.getText().toString();
                            Map<String,Object> data = RestHelper.updateNotification(String.valueOf(notificationId),uname,Integer.valueOf(uless),inventoryId,sessionToken);
                            boolean status = (boolean) data.get("status");
                            requireActivity().runOnUiThread(() -> {
                                if(status)
                                {
                                    Toast.makeText(getContext(), "data updated", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(getContext(), "data can not updated", Toast.LENGTH_SHORT).show();
                                }
                            });

                        });
                    }
                    else
                    {
                        String uname = name.getText().toString();
                        String uless = less.getText().toString();
                        dbHelper.updateNotification(inventoryId,uname,uless);
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