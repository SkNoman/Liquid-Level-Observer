package com.example.waterlevel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class Mode extends AppCompatActivity {

    Button Signout,ON,OF,auto,manual;
    TextView motortext;
    String  session_username;
    String mvalue = "";
    String moodvalue = "";
    private static final String url="https://sknoman.000webhostapp.com/WaterLevel/insertData.php";
    private static final String url2="https://sknoman.000webhostapp.com/WaterLevel/updataMode.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode);


        SessionManager sessionManager = new SessionManager(this,SessionManager.SESSION_USERSESSION); // user session
        HashMap<String,String> userDetails =  sessionManager.getUserDetailFromSession();
        session_username = userDetails.get(SessionManager.KEY_USERNAME);


        ON = findViewById(R.id.on);
        OF = findViewById(R.id.off);
        auto = findViewById(R.id.auto);
        manual = findViewById(R.id.manual);
        Signout = findViewById(R.id.signout);
        motortext = findViewById(R.id.motorsatus2);
        Signout = findViewById(R.id.signout);
        Signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Back To Auto Mode", Toast.LENGTH_SHORT).show();
                moodvalue = "1";
                updateMood();
                Intent intent = new Intent(getApplicationContext(),Dashboard.class);
                startActivity(intent);
            }
        });

        manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Manual Mode", Toast.LENGTH_SHORT).show();
                moodvalue ="0";
                updateMood();
            }
        });

        ON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mvalue = "1";
                motortext.setText("Motor Is On");
                sendData();
            }
        });

        OF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mvalue = "0";
                motortext.setText("Motor Is Off");
                sendData();
            }
        });
    }

    private void updateMood()
    {
        final  String mood = moodvalue.trim();



        StringRequest request=new StringRequest(Request.Method.POST, url2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                moodvalue = "";
                Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String,String> param=new HashMap<String,String>();
                param.put("moodvalue",mood);
                return param;
            }
        };


        RequestQueue queue= Volley.newRequestQueue(getApplicationContext());
        queue.add(request);

    }

    private void sendData()
    {

        final  String m = mvalue.trim();



        StringRequest request=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                mvalue = "";
                Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String,String> param=new HashMap<String,String>();
                param.put("mvalue",m);
                return param;
            }
        };


        RequestQueue queue= Volley.newRequestQueue(getApplicationContext());
        queue.add(request);


    }
    private void logout()
    {
        FirebaseDatabase.getInstance().getReference("users").child(session_username).child("device_token").removeValue();
        SessionManager sessionManager = new SessionManager(this,SessionManager.SESSION_REMEMBERME);
        sessionManager.logoutUserSession();
        Intent intent = new Intent(getApplicationContext(),Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}