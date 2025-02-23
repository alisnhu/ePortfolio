package com.example.ali_arslan_project2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.EditText;
import android.widget.Toast;


public class signUp extends AppCompatActivity {


    private EditText usernameField, passwordField, passwordAgainField, emailField, phoneField;
    private DBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        dbHelper = new DBHelper(this);

        usernameField = findViewById(R.id.username);
        passwordField = findViewById(R.id.password);
        passwordAgainField = findViewById(R.id.passwordAgain);
        emailField = findViewById(R.id.email);
        phoneField = findViewById(R.id.phone);

        Button signUpButton = findViewById(R.id.signUp_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameField.getText().toString();
                String password = passwordField.getText().toString();
                String passwordAgain = passwordAgainField.getText().toString();
                String email = emailField.getText().toString();
                String phone = phoneField.getText().toString();

                if (password.equals(passwordAgain)) {
                    dbHelper.addUser(username, password, email, phone);
                    Toast.makeText(signUp.this, "User Registered Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(signUp.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
            }
        });


        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

    }





}