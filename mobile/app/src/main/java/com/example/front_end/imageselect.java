package com.example.front_end;

import static com.example.front_end.MainActivity.context;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
@RequiresApi(api = Build.VERSION_CODES.O)
public class imageselect extends AppCompatActivity {
    // One Button
    Button BSelectImage;
    public Bitmap bitmap;
    // One Preview Image
    ImageView IVPreviewImage;
    TextView mTextView;
    public Uri selectedImageUri;
    public String encoded;
    public  RequestQueue requestQueue;
    public String compressed;
    public byte[] toturn;
    public Bitmap decodedByte;
    byte[] byteArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageselect);
        // register the UI widgets with their appropriate IDs
        BSelectImage = findViewById(R.id.BSelectImage);
        IVPreviewImage = findViewById(R.id.IVPreviewImage);

        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());
        // Instantiate the RequestQueue with the cache and network.
        requestQueue = new RequestQueue(cache, network);
        // Start the queue
        requestQueue.start();


    }
    public String readfile(){
            String filename= "imageschanged.txt";

            StringBuffer stringBuffer = new StringBuffer();
            try {
                //Attaching BufferedReader to the FileInputStream by the help of
                 BufferedReader inputReader = new BufferedReader(new InputStreamReader(imageselect.this.openFileInput(filename)));
                 String inputString;

                //Reading data line by line and storing it into the stringbuffer

                while ((inputString = inputReader.readLine()) != null) {
                    stringBuffer.append(inputString + "\n");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            mTextView.setText(stringBuffer.toString());
            return stringBuffer.toString();
    }
    public void upload(View v) {

        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        launchSomeActivity.launch(i);
    }
    ActivityResultLauncher<Intent> launchSomeActivity
            = registerForActivityResult(
            new ActivityResultContracts
                    .StartActivityForResult(),
            result -> {
                if (result.getResultCode()
                        == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    // run operation
                    if (data != null
                            && data.getData() != null) {
                        selectedImageUri = data.getData();
                        bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        IVPreviewImage.setImageBitmap(bitmap);
                        }


                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.RGB_565;

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byteArray = stream.toByteArray();
                    encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    // Optimise image
                    try {
                        volsend();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

    void volsend() throws JSONException{
        Apinterface apinterface = RetrofitClient.getRetrofitInstance().create(Apinterface.class);
        Call<pic> call = apinterface.getUserInformation(encoded);
        call.enqueue(new Callback<pic>() {
            @Override
            public void onResponse(Call<pic> call, Response<pic> response) {
                System.out.println("\n"+"Functioning"+"\n");
                byte[] decodedString = Base64.decode(response.body().getBytes(), Base64.DEFAULT);
                decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                try {
                    store();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<pic> call, Throwable t) {
                Toast.makeText(imageselect.this, "Failed to get the data..", Toast.LENGTH_SHORT).show();
            }
        });
    }
    // Generate file name - only alphabetic
    public String title() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }
    // Store image
    @RequiresApi(api = Build.VERSION_CODES.O)
    void store() throws IOException {
        String title = title();
        MediaStore.Images.Media.insertImage(getContentResolver(), decodedByte, title , "");
        Toast.makeText(imageselect.this, "Image saved as: "+title, Toast.LENGTH_LONG).show();
        /*
        OutputStream fileOutputStream = openFileOutput("imageschanged.txt", Context.MODE_PRIVATE);
        OutputStreamWriter outputWriter = new OutputStreamWriter(fileOutputStream);

        outputWriter.write(readfile()+"Image saved as: " + title+"\n");
        // outputWriter.flush(); Resets file
        outputWriter.close();

         */


    }
}




