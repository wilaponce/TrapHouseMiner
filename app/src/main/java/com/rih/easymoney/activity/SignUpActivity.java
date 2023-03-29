package com.rih.easymoney.activity;

import static com.rih.easymoney.Config.userRegister;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

import com.rih.easymoney.MainActivity;
import com.rih.easymoney.R;
import es.dmoral.toasty.Toasty;

public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText nameTxt, emailTxt, passwordTxt, passwordConfirmTxt;
    private Button signUpBtn;
    private TextView signIn;
    private String name, email, password, deviceInfo, passConfirm;
    private SharedPreferences prefs;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setToolbar();
        initViews();

        // Android ID
        @SuppressLint("HardwareIds")
        String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        deviceInfo = "Android ID: " + android_id;

        signUpBtn.setOnClickListener(view ->
        {
            name = nameTxt.getText().toString().trim();
            email = emailTxt.getText().toString().trim();
            password = passwordTxt.getText().toString();
            passConfirm = passwordConfirmTxt.getText().toString().trim();

            if (name.isEmpty())
            {
                nameTxt.setError(getString(R.string.enter_your_name));
                return;
            }
            else if (email.isEmpty())
            {
                emailTxt.setError(getString(R.string.enter_your_email));
                return;
            }
            else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
            {
                emailTxt.setError(getString(R.string.invalid_email_address));
                return;
            }
            else if (password.isEmpty())
            {
                passwordTxt.setError(getString(R.string.enter_your_password));
                return;
            }
            else if (passConfirm.isEmpty())
            {
                passwordConfirmTxt.setError(getString(R.string.enter_your_confirm_pass));
                return;
            }
            else if (!passConfirm.equals(password))
            {
                passwordConfirmTxt.setError(getString(R.string.password_is_not_match));
                return;
            }

            openProgressBar();
            new SendRequest().execute();
        });

        signIn.setOnClickListener(view -> {
            Intent GoSignIn = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(GoSignIn);
        });

    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.toolbar_signup);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    private void initViews()
    {
        nameTxt = findViewById(R.id.Name_EdtTxt);
        emailTxt = findViewById(R.id.Email_EdtTxt);
        passwordTxt = findViewById(R.id.Password_EdtTxt);
        passwordConfirmTxt = findViewById(R.id.PasswordConfirm_EdtTxt);
        signUpBtn = findViewById(R.id.Signup_Btn);
        signIn = findViewById(R.id.sign_in);
    }

    public class SendRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0)
        {
            try{
                URL url = new URL(userRegister);

                JSONObject postDataParams = new JSONObject();

                postDataParams.put("name", name);
                postDataParams.put("email", email);
                postDataParams.put("password", password);
                postDataParams.put("mocions", 0);
                postDataParams.put("deviceinfo", deviceInfo);

                Log.e("params",postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                }
                else {
                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {

            if (result.contains("Registration Successful!"))
            {
                Toasty.success(getApplicationContext(), "Registration Successful", Toast.LENGTH_SHORT, true).show();

                prefs = getSharedPreferences("User", Context.MODE_PRIVATE);
                prefs.edit().putString("userEmail", email).apply();
                prefs.edit().putString("userPassword", password).apply();

                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
            else if (result.contains("This email is already used!"))
            {
                Toasty.error(getApplicationContext(), "This email is already used !", Toast.LENGTH_LONG, true).show();
            }
            else if (result.contains("Something went wrong. Please try again"))
            {
                Toasty.error(getApplicationContext(), "Something went wrong. Please try again", Toast.LENGTH_LONG, true).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "Something went wrong" + result,
                        Toast.LENGTH_LONG).show();
            }

            closeProgressBar();
        }
    }

    public String getPostDataString(JSONObject params) throws Exception {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }


    protected void openProgressBar(){
        pDialog = new ProgressDialog(this);
        pDialog.setMessage(getString(R.string.loading));
        pDialog.show();
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
    }

    protected void closeProgressBar(){
        pDialog.dismiss();
    }

}