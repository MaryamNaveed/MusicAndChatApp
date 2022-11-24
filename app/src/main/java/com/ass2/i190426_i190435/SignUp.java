package com.ass2.i190426_i190435;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUp extends AppCompatActivity {

    TextView signin, show;
    String gender="";
    ImageView male,female,prefernottosay;
    EditText name,email, password, phone;
    Button signup;
    Boolean showed = false;
    CircleImageView dp;
    Bitmap selectedImage= null;
    SharedPreferences mPref;
    SharedPreferences.Editor editmPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signin=findViewById(R.id.signin);
        signup=findViewById(R.id.signup);
        show=findViewById(R.id.show);
        male=findViewById(R.id.male);
        female=findViewById(R.id.female);
        prefernottosay=findViewById(R.id.prefernottosay);
        name=findViewById(R.id.name);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        dp = findViewById(R.id.dp);
        phone = findViewById(R.id.phone);
        mPref=getSharedPreferences("com.ass2.i190426_i190435", MODE_PRIVATE);
        editmPref=mPref.edit();

        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gender="male";
                male.setImageResource(R.drawable.male);
                female.setImageResource(R.drawable.female1);
                prefernottosay.setImageResource(R.drawable.nottosay1);
            }
        });
        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gender="female";
                male.setImageResource(R.drawable.male1);
                female.setImageResource(R.drawable.female);
                prefernottosay.setImageResource(R.drawable.nottosay1);
            }
        });
        prefernottosay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gender="";
                male.setImageResource(R.drawable.male1);
                female.setImageResource(R.drawable.female1);
                prefernottosay.setImageResource(R.drawable.nottosay);
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(SignUp.this, SignIn.class);
                startActivity(intent);
            }
        });

        dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(
                        Intent.createChooser(i, "Choose your Dp"),
                        200
                );
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if (selectedImage != null) {


                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    String lastSeen = "Last Seen " + dtf.format(now);
                    String deviceId = OneSignal.getDeviceState().getUserId();

                    Bitmap bmp = ((BitmapDrawable) dp.getDrawable()).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                    byte[] byteArray = stream.toByteArray();
                    final String imageData=Base64.getEncoder().encodeToString(byteArray);

                    User u = new User(name.getText().toString(), email.getText().toString(),
                            password.getText().toString(), gender, phone.getText().toString(),
                            "offline", lastSeen, deviceId, imageData);





                    StringRequest request = new StringRequest(Request.Method.POST, Ip.ipAdd + "/signupUser.php",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    System.out.println(response);


                                    try {
                                        JSONObject res = new JSONObject(response);

                                        if (res.getInt("reqcode") == 1) {
                                            Toast.makeText(SignUp.this, "Signed In", Toast.LENGTH_LONG).show();
                                            u.setId(res.getInt("id"));
                                            editmPref.putBoolean("loggedIn", true);
                                            editmPref.putInt("id", u.getId());
                                            editmPref.apply();
                                            editmPref.commit();
                                            Intent intent=new Intent(SignUp.this, TabLayout.class);
                                            startActivity(intent);

                                        } else {
                                            Toast.makeText(SignUp.this, res.get("reqmsg").toString(), Toast.LENGTH_LONG).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(SignUp.this, "Cannot Parse JSON", Toast.LENGTH_LONG).show();
                                        Toast.makeText(SignUp.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }


                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(SignUp.this, "Connection Error", Toast.LENGTH_LONG).show();
                                    Toast.makeText(SignUp.this, error.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }) {
                        @Nullable
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
//                            Toast.makeText(SignUp.this, u.getDeviceId(), Toast.LENGTH_LONG).show();
                            Map<String, String> params = new HashMap<>();
                            params.put("name", u.getName());
                            params.put("email", u.getEmail());
                            params.put("password", u.getPassword());
                            params.put("gender", u.getGender());
                            params.put("dp",u.getDp());
                            params.put("phone", u.getPhone());
                            params.put("status", u.getStatus());
                            params.put("lastSeen", u.getLastSeen());
                            params.put("deviceId", u.getDeviceId());


                            return params;
                        }
                    };

                    request.setRetryPolicy(new DefaultRetryPolicy(
                            10000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                    RequestQueue queue = Volley.newRequestQueue(SignUp.this);
                    queue.add(request);


                } else {
                    Toast.makeText(SignUp.this, "Please select dp", Toast.LENGTH_LONG).show();
                }
            }


            });

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(showed==false){
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    showed=true;
                }
                else{
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    showed=false;

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==200 && resultCode==RESULT_OK){
            try{
                Uri dpp=data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(dpp);
                selectedImage = BitmapFactory.decodeStream(imageStream);
                dp.setImageBitmap(selectedImage);
            }
            catch (Exception e){

            }

        }
    }
}