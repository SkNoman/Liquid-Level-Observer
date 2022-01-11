package com.example.waterlevel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class Login extends AppCompatActivity {

    Button callsignup,ForgetPassword;
    TextInputLayout  username,password;
    String token;
    DatabaseReference reference;
    SharedPreferences sharedPreferences;
    public ProgressBar progressBar;
    CheckBox Rememberme;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        //hooks

        ForgetPassword = findViewById(R.id.forget_password_btn);
        Rememberme = findViewById(R.id.rememberme);
        progressBar = (ProgressBar)findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.GONE);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        callsignup = findViewById(R.id.callsignup);

        ForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ForgetPassword.class);
                startActivity(intent);
                finish();
            }
        });
        callsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SignUp.class);
                startActivity(intent);
                finish();
            }
        });

        SessionManager sessionManager = new SessionManager(Login.this,SessionManager.SESSION_REMEMBERME);
        if (sessionManager.checkRememberMe())
        {
            HashMap<String,String> rememberMeDetails = sessionManager.getrRembermeDetailFromSession();
            username.getEditText().setText(rememberMeDetails.get(SessionManager.KEY_SESSIONUSERNAME));
            password.getEditText().setText(rememberMeDetails.get(SessionManager.KEY_SESSIONPASSWORD));
        }
    }

    private Boolean validateUsername()
    {
        String val = username.getEditText().getText().toString();
        if(val.isEmpty())
        {
            username.setError("Field cannot be empty");
            return false;
        }
        else {
            username.setError(null);
            username.setErrorEnabled(false);//to remove error sign
            return true;
        }
    }

    private Boolean validatePassword()
    {
        String val = password.getEditText().getText().toString();

        if (val.isEmpty()) {
            password.setError("Field cannot be empty");
            return false;
        }
        else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }
    public  void loginUser(View view)
    {
        progressBar.setVisibility(View.VISIBLE);
        if (!validateUsername() | !validatePassword())
        {
            return;
        }
        else
        {
            isUser();
        }
        String loginusername = username.getEditText().getText().toString().trim();
        String loginpassword = password.getEditText().getText().toString().trim();

    }

    private void isUser()
    {
        String userEnteredUsername = username.getEditText().getText().toString().trim();
        String userEnteredPassword = password.getEditText().getText().toString().trim();

        if(Rememberme.isChecked())
        {
            SessionManager sessionManager = new SessionManager(Login.this,SessionManager.SESSION_REMEMBERME);
            sessionManager.createRemembermeSession(userEnteredUsername,userEnteredPassword);
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        Query chekUser = reference.orderByChild("username").equalTo(userEnteredUsername);
        chekUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    username.setError(null);
                    username.setErrorEnabled(false);
                    String passwordFromDB = dataSnapshot.child(userEnteredUsername).child("pas").getValue(String.class);
                    if (passwordFromDB.equals(userEnteredPassword))
                    {

                        password.setError(null);
                        password.setErrorEnabled(false);

                        String nameFromDB = dataSnapshot.child(userEnteredUsername).child("name").getValue(String.class);
                        String usernameFromDB = dataSnapshot.child(userEnteredUsername).child("username").getValue(String.class);
                        String emailFromDB = dataSnapshot.child(userEnteredUsername).child("email").getValue(String.class);
                        String cityFromDB = dataSnapshot.child(userEnteredUsername).child("city").getValue(String.class);
                        String phoneFromDB = dataSnapshot.child(userEnteredUsername).child("phone").getValue(String.class);

                        FirebaseMessaging.getInstance().getToken()
                                .addOnCompleteListener(new OnCompleteListener<String>() {
                                    @Override
                                    public void onComplete(@NonNull Task<String> task) {
                                        if (task.isSuccessful())
                                        {
                                            token = task.getResult();
                                            FirebaseDatabase.getInstance()
                                                    .getReference("users")
                                                    .child(userEnteredUsername)
                                                    .child("device_token").setValue(token);

                                        }
                                        else
                                        {
                                            Toast.makeText(getApplicationContext(),"Please logout and login again",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });


                        //create session here
                        SessionManager sessionManager = new SessionManager(Login.this,SessionManager.SESSION_USERSESSION);
                        sessionManager.createLoginSession(nameFromDB,usernameFromDB,emailFromDB,phoneFromDB,cityFromDB,passwordFromDB);
                        Intent intent = new Intent(getApplicationContext(),Dashboard.class);
                        startActivity(intent);
                        finish();

                    }
                    else
                    {
                        password.setError("Wrong Password");
                        password.requestFocus();
                    }

                }
                else
                {
                    username.setError("No Such User Exist");
                    username.requestFocus();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}