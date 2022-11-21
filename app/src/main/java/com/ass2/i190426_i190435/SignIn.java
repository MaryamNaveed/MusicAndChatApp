package com.ass2.i190426_i190435;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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


public class SignIn extends AppCompatActivity {

    TextView signup, show;
    Button signin;
    EditText email, password;
    Boolean showed=false;
    TextView forgetpass;
    SharedPreferences mPref;
    SharedPreferences.Editor editmPref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        signup= findViewById(R.id.signup);
        signin = findViewById(R.id.signin);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        show = findViewById(R.id.show);
        forgetpass = findViewById(R.id.forgetpass);
        mPref=getSharedPreferences("com.ass2.i190426_i190435", MODE_PRIVATE);
        editmPref=mPref.edit();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(SignIn.this, SignUp.class);
                startActivity(intent);
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringRequest request = new StringRequest(Request.Method.POST, Ip.ipAdd + "/signinUser.php",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                System.out.println(response);


                                try {
                                    JSONObject res = new JSONObject(response);

                                    if (res.getInt("reqcode") == 1) {
                                        Toast.makeText(SignIn.this, "Signed In", Toast.LENGTH_LONG).show();
                                        editmPref.putBoolean("loggedIn", true);
                                        editmPref.putInt("id", res.getInt("id"));
                                        editmPref.apply();
                                        editmPref.commit();
                                        Intent intent=new Intent(SignIn.this, TabLayout.class);
                                        startActivity(intent);

                                    } else {
                                        Toast.makeText(SignIn.this, res.get("reqmsg").toString(), Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(SignIn.this, "Cannot Parse JSON", Toast.LENGTH_LONG).show();
                                    Toast.makeText(SignIn.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }


                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(SignIn.this, "Connection Error", Toast.LENGTH_LONG).show();
                                Toast.makeText(SignIn.this, error.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }) {
                    @Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();

                        params.put("email", email.getText().toString());
                        params.put("password", password.getText().toString());

                        return params;
                    }
                };

                request.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                RequestQueue queue = Volley.newRequestQueue(SignIn.this);
                queue.add(request);

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

        forgetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(SignIn.this);
                builder.setTitle("Recover Password");
                LinearLayout linearLayout=new LinearLayout(SignIn.this);
                final EditText emailpass= new EditText(SignIn.this);


                emailpass.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                linearLayout.addView(emailpass);
                linearLayout.setPadding(10,10,10,10);
                builder.setView(linearLayout);

                builder.setPositiveButton("Recover Password", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String mail = emailpass.getText().toString();
//                            Toast.makeText(SignIn.this, mail, Toast.LENGTH_SHORT).show();


                    }
                });


                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });

    }
}