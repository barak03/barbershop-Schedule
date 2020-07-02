package com.example.finalprojectandroid2.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.finalprojectandroid2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private final String barbershopEmail = "barbershopprojectp25@gmail.com";
    private final String MY_PREFS_NAME = "finalProjectFile";
    private final String EMAIL_KEY = "email";
    private String loggedInUserEmail;
    private TextInputLayout emailText;
    private  TextInputLayout passwordText;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        loggedInUserEmail = prefs.getString(EMAIL_KEY, "No email defined");
        SharedPreferences.Editor editor = prefs.edit();
        editor.apply();
        if (loggedInUserEmail.equals("No email defined") == false)
        {
            emailText.getEditText().setText(loggedInUserEmail);
        }
    }

    public void LoginFunc(View view)
    {
        final Intent managementScreenIntent = new Intent(this, managementScreen.class);
        final Intent customerScreenIntent = new Intent(this, customerScreen.class);
        final String email = Objects.requireNonNull(emailText.getEditText()).getText().toString().trim();
        String password = Objects.requireNonNull(passwordText.getEditText()).getText().toString().trim();
        if (checkEmail(email) && checkPassword(password))
        {
            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(EMAIL_KEY, email);
            editor.commit();
            editor.apply();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(MainActivity.this, "Log in Succeed", Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (email.equals(barbershopEmail))
                                {
                                    startActivity(managementScreenIntent);
                                }
                                else
                                {
                                    startActivity(customerScreenIntent);
                                }
                            }
                            else
                            {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    private boolean checkEmail(String email)
    {
        boolean returnValue = false;

        if (email.length() > 0)
        {
            emailText.setError(null);
            returnValue = true;
        }
        else
        {
            emailText.setError("Field can't be empty");
            returnValue = false;
        }

        return returnValue;
    }

    private boolean checkPassword(String password )
    {
        boolean returnValue = false;

        if (password.length() > 0)
        {
            passwordText.setError(null);
            returnValue = true;
        }
        else
        {
            passwordText.setError("Field can't be empty");
            returnValue = false;
        }

        return returnValue;
    }


    public void registerFunc(View view)
    {
        final Intent intent = new Intent(this, registerScreen.class);
        startActivity(intent);
    }
}


