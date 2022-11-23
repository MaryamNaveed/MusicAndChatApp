package com.ass2.i190426_i190435;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPassword extends AppCompatActivity {

    EditText email, password, phone;
    TextView show, signin;
    Boolean showed=false;
    Button change;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        phone=findViewById(R.id.phone);

        show=findViewById(R.id.show);
        change=findViewById(R.id.change);
        signin=findViewById(R.id.signin);

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

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringRequest request = new StringRequest(Request.Method.POST, Ip.ipAdd + "/forgotPassword.php",
                        new Response.Listener<String>() {
                            @SuppressLint("NotifyDataSetChanged")
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onResponse(String response) {
                                System.out.println(response);


                                try {
                                    JSONObject res = new JSONObject(response);

                                    if (res.getInt("reqcode") == 1) {
                                        Toast.makeText(ForgotPassword.this, res.get("reqmsg").toString(), Toast.LENGTH_LONG).show();

                                    } else {
                                        Toast.makeText(ForgotPassword.this, res.get("reqmsg").toString(), Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(ForgotPassword.this, "Cannot Parse JSON", Toast.LENGTH_LONG).show();
                                    Toast.makeText(ForgotPassword.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }


                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                Toast.makeText(ForgotPassword.this, "Connection Error", Toast.LENGTH_LONG).show();
                                Toast.makeText(ForgotPassword.this, error.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }) {
                    @Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();

                        params.put("email", email.getText().toString());
                        params.put("phone", phone.getText().toString());
                        params.put("password", password.getText().toString());

                        return params;
                    }
                };

                request.setRetryPolicy(new DefaultRetryPolicy(
                        50000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                RequestQueue queue = Volley.newRequestQueue(ForgotPassword.this);
                queue.add(request);
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ForgotPassword.this, SignIn.class);
                startActivity(intent);
            }
        });

    }
}