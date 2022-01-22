package com.myry.phpbackend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ChgPwordActivity extends AppCompatActivity {

    EditText editTextNewPassword, editTextVerifyPassword;
    Button idBtnPword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chgpword);

        //if the user is not logged in
        //starting the login activity
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);



        editTextNewPassword = (EditText) findViewById(R.id.editTextNewPassword);
        editTextVerifyPassword = (EditText) findViewById(R.id.editTextVerifyPassword);
        idBtnPword = (Button) findViewById(R.id.idBtnPword);

        idBtnPword.setOnClickListener(v -> chgPword());
    }

    private void chgPword() {
        //getting the current user
        User user = SharedPrefManager.getInstance(this).getUser();


        final String userID = (String) getText(user.getId());
        final String pwd = editTextNewPassword.getText().toString().trim();
        final String vPwd = editTextVerifyPassword.getText().toString().trim();


        if (!TextUtils.isEmpty(pwd) && TextUtils.isEmpty(vPwd)) {
            Toast.makeText(ChgPwordActivity.this, "Please verify your password!", Toast.LENGTH_SHORT).show();
            editTextVerifyPassword.requestFocus();
        }

        if (!pwd.equals(vPwd)) {
            Toast.makeText(ChgPwordActivity.this, "Please check both having same password..", Toast.LENGTH_SHORT).show();
            editTextNewPassword.requestFocus();
            editTextVerifyPassword.requestFocus();
        }

        class ChgPassword extends AsyncTask<Void, Void, String> {

            private ProgressBar progressBar;


            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();

                HashMap<String, String> params = new HashMap<>();
                params.put("id", userID);
                params.put("pwd", pwd);

                return requestHandler.sendPostRequest(URLs.URL_CHGPWORD, params); //HashMap<String, String>
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar = (ProgressBar) findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressBar.setVisibility(View.GONE);
                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        //getting the user from the response
                        JSONObject userJson = obj.getJSONObject("user");

                        //creating a new user object
                        User user = new User(
                                userJson.getInt("id"),
                                userJson.getString("username"),
                                userJson.getString("email"),
                                userJson.getString("fname"),
                                userJson.getString("contact")
                        );

                        //storing the user in shared preferences
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                        //starting the profile activity
                        finish();
                        startActivity(new Intent(getApplicationContext(), ChgPwordActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), "Some error occurred", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        ChgPassword pw = new ChgPassword();
        pw.execute();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent backHome2 = new Intent(ChgPwordActivity.this, ProfileActivity.class);
            startActivity(backHome2);
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}