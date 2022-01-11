package com.example.waterlevel;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {
    //Variables
    SharedPreferences userSession;
    SharedPreferences.Editor editor;
    Context context;

    //session names
    public static final String SESSION_USERSESSION = "userLoginSession";
    public static final String SESSION_REMEMBERME = "rememberMe";

    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_NAME = "name";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_CITY = "city";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_PASSWORD = "pas";

    //remeber me variables
    private static final String IS_REMEMBERME= "IsRememberMe";
    public static final String KEY_SESSIONUSERNAME = "username";
    public static final String KEY_SESSIONPASSWORD = "pas";

    public SessionManager(Context _context, String sessionName)
    {
        context = _context;
        userSession = context.getSharedPreferences(sessionName,Context.MODE_PRIVATE);
        editor = userSession.edit();

    }


    //Login session
    public void createLoginSession(String name,String username,String eamil, String city, String phone, String pas)
    {
        editor.putBoolean(IS_LOGIN,true);
        editor.putString(KEY_NAME,name);
        editor.putString(KEY_USERNAME,username);
        editor.putString(KEY_EMAIL,eamil);
        editor.putString(KEY_CITY,city);
        editor.putString(KEY_PHONE,phone);
        editor.putString(KEY_PASSWORD,pas);

        editor.commit();
    }
    public HashMap<String , String > getUserDetailFromSession()
    {
        HashMap<String,String> userData =new  HashMap<String,String>();
        userData.put(KEY_NAME,userSession.getString(KEY_NAME,null));
        userData.put(KEY_USERNAME,userSession.getString(KEY_USERNAME,null));
        userData.put(KEY_EMAIL,userSession.getString(KEY_EMAIL,null));
        userData.put(KEY_CITY,userSession.getString(KEY_CITY,null));
        userData.put(KEY_PHONE,userSession.getString(KEY_PHONE,null));
        userData.put(KEY_PASSWORD,userSession.getString(KEY_PASSWORD,null));

        return  userData;
    }
    public boolean checkLogin()
    {
        if(userSession.getBoolean(IS_LOGIN,false))
        {
            return true;
        }
        else
        {
            return false;
        }


    }


    //remember me session
    public void createRemembermeSession(String username, String pas)
    {
        editor.putBoolean(IS_REMEMBERME,true);
        editor.putString(KEY_SESSIONUSERNAME,username);
        editor.putString(KEY_SESSIONPASSWORD,pas);
        editor.commit();
    }
    public  void logoutUserSession()
    {

        editor.clear();
        editor.commit();
    }
    public HashMap<String , String > getrRembermeDetailFromSession()
    {
        HashMap<String,String> userData =new  HashMap<String,String>();
        userData.put(KEY_SESSIONUSERNAME,userSession.getString(KEY_SESSIONUSERNAME,null));
        userData.put(KEY_SESSIONPASSWORD,userSession.getString(KEY_SESSIONPASSWORD,null));

        return  userData;
    }
    public boolean checkRememberMe()
    {
        if(userSession.getBoolean(IS_REMEMBERME,false))
        {
            return true;
        }
        else
        {
            return false;
        }

    }



}
