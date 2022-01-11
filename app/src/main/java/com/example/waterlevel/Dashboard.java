package com.example.waterlevel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.transition.Transition;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class Dashboard extends AppCompatActivity {

    String value = "1";
    TextView Ultra,Water,Motor,Name;
    TextView show;
    Button Signout,mode;
    String session_username;
    private static final String url="https://sknoman.000webhostapp.com/WaterLevel/insertData.php";
    // private static final String url="https://smartwaterlevel.000webhostapp.com/WaterLevel/insertData.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dashboard);

        // user session
        SessionManager sessionManager = new SessionManager(this,SessionManager.SESSION_USERSESSION);
        HashMap<String,String> userDetails =  sessionManager.getUserDetailFromSession();
        String name = userDetails.get(SessionManager.KEY_NAME);
        session_username = userDetails.get(SessionManager.KEY_USERNAME);

        //hooks
        Name = findViewById(R.id.name);
        Ultra = findViewById(R.id.usdata);
        Water = findViewById(R.id.wsdata);
        Motor = findViewById(R.id.motorsatus);
        Signout = findViewById(R.id.signout);
        show = findViewById(R.id.textshow);
        mode = findViewById(R.id.gotomode);
        mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Press Auto/Manual To Enable Mode" ,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),Mode.class);
                startActivity(intent);
            }
        });

        Name.setText("Welcome "+name);
        Signout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                logout();
            }
        });
        getData();


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

    public void getData()
    {


        String id = value.trim();
        if (id.equals("")) {

            Toast.makeText(this, "Check Detail!", Toast.LENGTH_LONG).show();
            return;
        }
        String url = Config.DATA_URL + value.trim();
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                showJSONS(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Dashboard.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        refresh(3000);

    }

    private void showJSONS(String response)
    {
        int watersensor =0;
        int ultrasonicsensor = 0;
        int  mode  = 0;
        int status = 0;
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY);
            JSONObject collegeData = result.getJSONObject(0);
            ultrasonicsensor = collegeData.getInt(Config.KEY_US);
            watersensor = collegeData.getInt(Config.KEY_WS);
            mode = collegeData.getInt(Config.KEY_M);
            status = collegeData.getInt(Config.KEY_STATUS);
        } catch (JSONException e) {
            e.printStackTrace();
        }

       // Toast.makeText(getApplicationContext(), "Mode"+mode, Toast.LENGTH_SHORT).show();
        if(mode >0)
        {
            show.setText("Auto Mode");
        }
        if (mode <1)
        {
            show.setText("Manual Mode");
        }
        Ultra.setText("Liquid Distance:"+ultrasonicsensor);

        if(watersensor < 700 && status >0)
        {
            Motor.setText("Motor Is On");
        }
        if(watersensor > 700)
        {
            Motor.setText("Motor Is Off");
            Notify();

        }
        if (status < 1)
        {
            Motor.setText("Motor Is Off");
        }
        if(mode > 0 && watersensor < 700)
        {
            Motor.setText("Motor Is On");
        }

        if(ultrasonicsensor<=5 && ultrasonicsensor >=0)
        {
            Water.setText("Liquid Level: High");
        }
        else if (ultrasonicsensor<=10 && ultrasonicsensor >=5)
        {
            Water.setText("Liquid Level: Medium");
        }

        else if(ultrasonicsensor > 10)
        {
            Water.setText("Liquid Level: Low");
        }
    }
    private void refresh(int milliseconds)
    {

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run()
            {
                getData();
            }
        };
        handler.postDelayed(runnable,milliseconds);
    }
    private void Notify()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel("n","n", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"n")
                .setContentTitle("Your Tank Is Full")
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setAutoCancel(true)
                .setContentText("Motor Turned Of");
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(999,builder.build());
    }
}