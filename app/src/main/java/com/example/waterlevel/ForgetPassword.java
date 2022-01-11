package com.example.waterlevel;




import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ForgetPassword extends AppCompatActivity {

    DatabaseReference reference;
    TextInputLayout Username,Password,ConformPassword,Email;
    Button ChangePassword;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_forget_password);
        reference = FirebaseDatabase.getInstance().getReference("users");
        //hooks
        Username = findViewById(R.id.f_username);
        Password = findViewById(R.id.f_password);
        ConformPassword = findViewById(R.id.f_conform_password);
        ChangePassword = findViewById(R.id.f_change_password_btn);
        Email = findViewById(R.id.f_email);
        progressBar = findViewById(R.id.f_progressBar1);
        progressBar.setVisibility(View.GONE);




    }

    private Boolean validate_Username()
    {



        if(Username.getEditText().getText().toString().isEmpty())
        {
            Username.setError("Username cannot be empty");
            return false;
        }
        else {
            Username.setError(null);
            Username.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean validatePassword()
    {
        String passwordVal = "^" +
                //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{4,}" +               //at least 4 characters
                "$";
        String pas = Password.getEditText().getText().toString();
        if (!pas.matches(passwordVal))
        {
            Password.setError("Password is too weak");
            return false;
        }
        else if (pas.isEmpty())
        {
            Password.setError("Filed cannot empty!");
            return false;
        }
        else
        {
            Password.setError(null);
            Password.setErrorEnabled(false);
            return true;

        }
    }
    private Boolean confromPassword()
    {
        String cf = ConformPassword.getEditText().getText().toString();
        String pas = Password.getEditText().getText().toString();
        if (cf.isEmpty())
        {
            ConformPassword.setError("Filed cannot empty!");
            return false;
        }
        else if(cf.equals(pas))
        {
            ConformPassword.setError(null);
            ConformPassword.setErrorEnabled(false);
            return true;
        }
        else
        {
            ConformPassword.setError("Password Not Match");
            return false;

        }
    }
    private Boolean validateEmail()
    {
        if(Email.getEditText().getText().toString().isEmpty())
        {
            Email.setError("Email cannot be empty");
            return false;
        }
        else {
            Email.setError(null);
            Email.setErrorEnabled(false);
            return true;
        }
    }

    public void ChangePassword(View view)
    {
        if (!validate_Username() | !confromPassword() | !validatePassword() | !validateEmail())
        {
            return;
        }

        //checking the user is exist or not!
        String isuser = Username.getEditText().getText().toString();
        String isemail = Email.getEditText().getText().toString();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        Query chekUser = reference.orderByChild("username").equalTo(isuser);
        chekUser.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    Username.setError(null);
                    Username.setErrorEnabled(false);

                    String emailFromDB = dataSnapshot.child(isuser).child("email").getValue(String.class);
                    if (emailFromDB.equals(isemail))
                    {
                        Email.setError(null);
                        Email.setErrorEnabled(false);
                        progressBar.setVisibility(View.VISIBLE);
                        reference.child(Username.getEditText().getText().toString()).child("pas").setValue(ConformPassword.getEditText().getText().toString());
                        Toast.makeText(getApplicationContext(), "Your Password is changed", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),Login.class);
                        startActivity(intent);
                        finish();

                    }
                    else
                    {
                        Email.setError("No email found to user");
                        Email.requestFocus();
                    }

                    //main work



                }
                else
                {
                    Username.setError("No Such User Exist");
                    Username.requestFocus();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });//checking done


    }
}