package com.example.front_end;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


interface call{
    public void onSuccess(boolean result);
}
class fetch implements call{
    public static boolean myObjAsString;
    @Override
    public void onSuccess(boolean result) {
        myObjAsString = result;
    }
}

public class MainActivity extends AppCompatActivity {
    public static EditText user;
    public static EditText pass;
    public static String username;
    public static  String password;
    public static Context context;
    public static String myObjAsString;
    public static RequestQueue requestQueue;
    public static String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
    }
    public void beforebuttons(){
        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());
        // Instantiate the RequestQueue with the cache and network.
        requestQueue = new RequestQueue(cache, network);

        // Start the queue
        requestQueue.start();

        // Log in button function
        user = (EditText) findViewById(R.id.username);
        pass = (EditText) findViewById(R.id.password);

        username = user.getText().toString();
        password = pass.getText().toString();

    }
    public void signing(View v) throws JSONException, NoSuchAlgorithmException {
        beforebuttons();

        // Setting path for sign up - Rest API
        url = "http://10.0.2.2:8000/adding";
        login();

    }
    public void display(View v) throws JSONException, NoSuchAlgorithmException {
        beforebuttons();

        // Setting path for log in - Rest API
        url = "http://192.168.1.43:8000/authentication";


        login();
         /*
        es.submit(() -> {
            try {
                auth.login(requestQueue,hold);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }; auth.url = "http://10.0.2.2:8000/";});

             */

    }

    public static byte[] getSHA(String input) throws NoSuchAlgorithmException
    {
        // Static getInstance method is called with hashing SHA
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // digest() method called
        // to calculate message digest of an input
        // and return array of byte
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }
    public static String toHexString(byte[] hash)
    {
        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 64)
        {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }

    public void login() throws JSONException, NoSuchAlgorithmException {
        myObjAsString="Lacking";
        // Make new json object and put params in it
        JSONObject jsonParams = new JSONObject();
        jsonParams.put("password",toHexString(getSHA(password)));
        jsonParams.put("username", username);
        // Building a request
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                // Using a variable for the domain is great for testing
                url,
                jsonParams,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            myObjAsString = response.getString("Status");
                            Toast.makeText(MainActivity.this, myObjAsString, Toast.LENGTH_LONG).show();

                            System.out.println(myObjAsString.trim());
                            if (myObjAsString.trim().equals("Successful")){
                                Intent intent = new Intent(context,imageselect.class);
                                context.startActivity(intent);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();

                    }
                });
        requestQueue.add(request);

    }



}
