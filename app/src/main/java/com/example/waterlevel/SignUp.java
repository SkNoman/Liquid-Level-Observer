package com.example.waterlevel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessaging;


public class SignUp extends AppCompatActivity {


    TextInputLayout regfullname,regusername,regemail,regphone,regcity,regpassword;
    Button singup_btn,Signin_btn;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    ProgressBar progressBar;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);



        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("users");//database reference

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful())
                        {
                            token = task.getResult();

                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Token Generation Faild",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        //hooks
        regfullname = findViewById(R.id.fullname);
        regusername = findViewById(R.id.username);
        regemail = findViewById(R.id.email);
        regphone = findViewById(R.id.phone);
        regcity = findViewById(R.id.city);
        regpassword = findViewById(R.id.password);
        singup_btn = findViewById(R.id.signup_btn);
        Signin_btn = findViewById(R.id.sign_btn2);
        progressBar = findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.GONE);

        Signin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Login.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private Boolean validateName()
    {
        String val = regfullname.getEditText().getText().toString();
        if(val.isEmpty())
        {
            regfullname.setError("Field cannot be empty");
            progressBar.setVisibility(View.GONE);
            return false;
        }
        else {
            regfullname.setError(null);
            regfullname.setErrorEnabled(false);//to remove error sign
            return true;
        }
    }

    private Boolean validateUsername()
    {
        String val = regusername.getEditText().getText().toString();
        String noWhiteSpace= "\\A\\w{4,20}\\z";
        if(val.isEmpty())
        {
            regusername.setError("Field cannot be empty");
            progressBar.setVisibility(View.GONE);
            return false;
        }
        else if (val.length()>=15)
        {
            regusername.setError("Username is too long");
            progressBar.setVisibility(View.GONE);
            return false;
        }
        else if(!val.matches(noWhiteSpace))
        {
            regusername.setError("White spaces are not allowed");
            progressBar.setVisibility(View.GONE);
            return false;
        }
        else {
            regusername.setError(null);
            regfullname.setErrorEnabled(false);//to remove error sign
            return true;
        }
    }

    private Boolean validateEmail()
    {
        String val = regemail.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (val.isEmpty()) {
            regemail.setError("Field cannot be empty");
            progressBar.setVisibility(View.GONE);
            return false;
        } else if (!val.matches(emailPattern)) {
            regemail.setError("Invalid email address");
            progressBar.setVisibility(View.GONE);
            return false;
        } else {
            regemail.setError(null);
            regemail.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePhone()
    {
        String val = regphone.getEditText().getText().toString();
        if(val.isEmpty())
        {
            regphone.setError("Field cannot be empty");
            progressBar.setVisibility(View.GONE);
            return false;
        }
        else {
            regphone.setError(null);
            regphone.setErrorEnabled(false);//to remove error sign
            return true;
        }
    }

    private Boolean validateCity()
    {
        String val = regcity.getEditText().getText().toString();
        if(val.isEmpty())
        {
            regcity.setError("Field cannot be empty");
            progressBar.setVisibility(View.GONE);
            return false;
        }
        else {
            regcity.setError(null);
            regcity.setErrorEnabled(false);//to remove error sign
            return true;
        }
    }

    private Boolean validatePassword()
    {
        String val = regpassword.getEditText().getText().toString();

        String passwordVal = "^" +
                //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{4,}" +               //at least 4 characters
                "$";

        if(val.length()>8)
        {
            regpassword.setError("Not more than 8 character");
            return false;
        }
        if (val.isEmpty()) {
            regpassword.setError("Field cannot be empty");

            return false;
        } else if (!val.matches(passwordVal)) {
            regpassword.setError("Password is too weak");

            return false;
        } else {
            regpassword.setError(null);
            regpassword.setErrorEnabled(false);
            return true;
        }
    }

    public void registerUser(View view) {

        progressBar.setVisibility(View.VISIBLE);

        if(!validateName() | !validatePassword() | !validatePhone() | !validateEmail() | !validateUsername() | !validateCity()){
            return;
        }

        //all items
        String name = regfullname.getEditText().getText().toString().trim();
        String username = regusername.getEditText().getText().toString().trim();
        String email = regemail.getEditText().getText().toString().trim();
        String phone = regphone.getEditText().getText().toString().trim();
        String city = regcity.getEditText().getText().toString().trim();
        String pas = regpassword.getEditText().getText().toString().trim();
        String device_token = token;


        UserHelperClass userHelperClass = new UserHelperClass(name,username,email,phone,city,pas,device_token);
        reference.child(username).setValue(userHelperClass);
        Dashboardactivity();
    }

    private void Dashboardactivity()
    {
        Intent intent = new Intent(getApplicationContext(),Dashboard.class);
        startActivity(intent);
        progressBar.setVisibility(View.GONE);
        finish();
    }
}