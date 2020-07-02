package com.example.finalprojectandroid2.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.finalprojectandroid2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class registerScreen extends AppCompatActivity
{
    private FirebaseAuth mAuth;
    private TextInputLayout emailText;
    private TextInputLayout passwordText;
    private TextInputLayout phoneNumberText;
    private TextInputLayout nameText;
    private Button registerButton;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);
        passwordText = (TextInputLayout) findViewById(R.id.passwordText);
        emailText = (TextInputLayout) findViewById(R.id.emailText);
        phoneNumberText = (TextInputLayout) findViewById(R.id.phoneNumberText);
        nameText = (TextInputLayout) findViewById(R.id.nameText);
        registerButton = (Button) findViewById(R.id.registerButton);
        mAuth = FirebaseAuth.getInstance();

    }

    public void registerfunc(View view)
    {
        final String password = Objects.requireNonNull(passwordText.getEditText()).getText().toString().trim();
        final String email = Objects.requireNonNull(emailText.getEditText()).getText().toString().trim();
        final String name = Objects.requireNonNull(nameText.getEditText()).getText().toString().trim();
        final String phoneNumber = Objects.requireNonNull(phoneNumberText.getEditText()).getText().toString().trim();

        if (checkEmail(email) && checkPassword(password) && checkName(name)  && checkPhoneNumber(phoneNumber) )
        {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(registerScreen.this, "Registration Succeed.", Toast.LENGTH_SHORT).show();
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(registerScreen.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            saveToDataBase(email, name, phoneNumber);
            emailText.getEditText().setText("");
            passwordText.getEditText().setText("");
            phoneNumberText.getEditText().setText("");
            nameText.getEditText().setText("");

        }
    }

    private boolean checkPhoneNumber(String phoneNumber)
    {
        boolean returnValue = false;

        if (phoneNumber.length() > 0)
        {
            phoneNumberText.setError(null);
            returnValue = true;
        }
        else
        {
            phoneNumberText.setError("Field can't be empty");
            returnValue = false;
        }

        return returnValue;
    }

    private boolean checkName(String name)
    {
        boolean returnValue = false;

        if (name.length() > 0)
        {
            nameText.setError(null);
            returnValue = true;
        }
        else
        {
            nameText.setError("Field can't be empty");
            returnValue = false;
        }

        return returnValue;
    }

    private boolean checkEmail(String email)
    {
        boolean returnValue = false;
        if (email.length() == 0)
        {
            emailText.setError("Field can't be empty");
            returnValue = false;
        }
        else
        {
            if (email.contains("@"))
            {
                String[] emailParts = email.split("@");
                if (emailParts.length == 2)
                {
                    if (email.split("@")[1].equals("gmail.com") == true)
                    {
                        emailText.setError(null);
                        returnValue = true;
                    }
                    else
                    {
                        emailText.setError("Use only gmail account");
                        returnValue = false;
                    }
                }
                else
                {
                    emailText.setError("Enter Valid email");
                    returnValue = false;
                }
            }
            else
            {
                emailText.setError("Enter Valid email");
                returnValue = false;
            }

        }

        return returnValue;
    }

    private boolean checkPassword(String password)
    {
        boolean returnValue = false;

        if (password.length() > 7)
        {
            passwordText.setError(null);
            returnValue = true;
        }
        else
        {
            passwordText.setError("Weak password");
            returnValue = false;
        }

        return returnValue;
    }

    private void saveToDataBase(String email, String name, String phoneNumber)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(email.split("@")[0]);
        myRef.child("email").setValue(email);
        myRef.child("name").setValue(name);
        myRef.child("phoneNumber").setValue(phoneNumber);
    }
}
