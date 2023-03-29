package com.rih.easymoney.activity;

import static com.rih.easymoney.Config.adminPanelUrl;
import static com.rih.easymoney.Config.userForgotPassword;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rih.easymoney.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordActivity extends AppCompatActivity {

    TextView tvSendStatus;
    TextInputEditText etEmail;
    Button btnSend;
    String email;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initViews();

        btnSend.setOnClickListener(v->
        {
            email = etEmail.getText().toString();

            if (email.isEmpty())
            {
                etEmail.setError(getString(R.string.enter_your_email));
                return;
            }
            else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
            {
                etEmail.setError(getString(R.string.invalid_email_address));
                return;
            }

            openProgressBar();
            sendRequest();
        });
    }

    private void initViews()
    {
        etEmail = findViewById(R.id.et_email);
        tvSendStatus = findViewById(R.id.tv_send_status);
        btnSend = findViewById(R.id.btn_send);
    }

    public void sendRequest()
    {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, userForgotPassword, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                if (response.contains("Success"))
                {
                    showDialogSuccessSent();
                }
                else if (response.equals("Email is not Exist!")) {
                    tvSendStatus.setText(getString(R.string.status_not_exists));
                    tvSendStatus.setVisibility(View.VISIBLE);
                    tvSendStatus.setTextColor(getResources().getColor(R.color.red));
                }
                else if (response.equals("Error")) {
                    tvSendStatus.setText(getString(R.string.forgot_pass_Error));
                }
                else
                {
                    Toast.makeText(ForgotPasswordActivity.this, response, Toast.LENGTH_LONG).show();
                }

                closeProgressBar();
                Log.e("ForgotResponse", response);
            }
        }, error -> {
            //This code is executed if there is an error.
            Toast.makeText(getApplicationContext(), "error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            closeProgressBar();
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<>();
                MyData.put("toEmail", email);
                MyData.put("websiteURL", adminPanelUrl);
                return MyData;
            }
        };

        MyRequestQueue.add(MyStringRequest);
    }

    private void showDialogSuccessSent()
    {
        AlertDialog.Builder build = new AlertDialog.Builder(this);
        build.setTitle(getString(R.string.forgot_password));
        build.setMessage(getString(R.string.status_success));
        build.setCancelable(false);

        build.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                dialog.cancel();
                finish();
            }
        });
        AlertDialog olustur = build.create();
        olustur.show();
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