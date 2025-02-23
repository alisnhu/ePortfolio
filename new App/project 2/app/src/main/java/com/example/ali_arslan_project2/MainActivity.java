package com.example.ali_arslan_project2;
import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.EditText;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_SMS_PERMISSION = 1;

    private restHelper myRestHelper;
    private Map<String, Object> data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // SMS gönderme iznini kontrol edin ve isteyin
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_SMS_PERMISSION);
        } else {
            // İzin zaten verilmiş
            sendSMS();
        }


    }
    private void sendSMS() {
        // SMS gönderme işlemini burada gerçekleştirin
        Toast.makeText(this, "SMS gönderme izni verildi.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_SMS_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSMS();
            } else {
                Toast.makeText(this, "Sending SMS Granted", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void login(View view) {
        EditText usernameEditText = findViewById(R.id.username);
        EditText passwordEditText = findViewById(R.id.password);

        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        DBHelper dbHelper = new DBHelper(this);
        myRestHelper = new restHelper("http://10.0.2.2/rest/");
        int userId = dbHelper.validateUser(username, password);

        if (userId != -1) {
            String workingMode = dbHelper.getWorkingMode(String.valueOf(userId));
            Intent intent = new Intent(MainActivity.this, mainPage.class);
            if(workingMode.equals("online"))
            {
                ExecutorService executor = Executors.newFixedThreadPool(1);
                executor.execute(() -> {
                    data = myRestHelper.Login(username,password);
                    boolean status = (boolean) data.get("status");
                    runOnUiThread(() -> {
                        if(status)
                        {
                            String sessionToken="";
                            try {
                                JSONObject resultObject = new JSONObject(data.get("result").toString());
                                sessionToken = resultObject.getString("sessionToken");
                            }
                            catch (JSONException e)
                            {
                                Toast.makeText(this, "Invalid login credentials", Toast.LENGTH_SHORT).show();
                            }
                            intent.putExtra("USER_ID", userId);
                            intent.putExtra("workingMode", "online");
                            intent.putExtra("sessionToken", sessionToken);
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(this, "Invalid login credentials", Toast.LENGTH_SHORT).show();
                        }

                    });
                });
            }
            else
            {

                intent.putExtra("USER_ID", userId);
                intent.putExtra("workingMode", "offline");
                startActivity(intent);
            }
        } else {
            ExecutorService executor = Executors.newFixedThreadPool(1);
            executor.execute(() -> {
                data = myRestHelper.Login(username,password);
                boolean status = (boolean) data.get("status");
                runOnUiThread(() -> {
                    if(status)
                    {
                        dbHelper.addUser(username,password,"","","online","http://10.0.2.2/rest/");
                        this.login(view);
                    }
                    else
                    {
                        Toast.makeText(this, "Invalid login credentials", Toast.LENGTH_SHORT).show();
                    }

                });

            });
        }
    }

    public void openSignUp(View view)
    {
        startActivity(new Intent(MainActivity.this, signUp.class));
    }
}