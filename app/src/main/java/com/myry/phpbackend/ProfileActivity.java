package com.myry.phpbackend;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    TextView editTextUserId;
    EditText  editTextUsername, editTextEmail, editTextFullname, editTextContact;
    Button idBtnEditProf, idBtnChgPword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //if the user is not logged in
        //starting the login activity
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        //getting the current user
        User user = SharedPrefManager.getInstance(this).getUser();




        editTextUserId = (TextView) findViewById(R.id.editTextUserID);
        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextFullname = (EditText) findViewById(R.id.editTextFullname);
        editTextContact = (EditText) findViewById(R.id.editTextContact);

        idBtnEditProf = (Button) findViewById(R.id.idBtnEditProf);
        idBtnChgPword = (Button) findViewById(R.id.idBtnChgPword);


        editTextUserId.setText(String.valueOf(user.getId()));
        editTextUsername.setText(String.valueOf(user.getUsername()));
        editTextEmail.setText(String.valueOf(user.getEmail()));
        editTextFullname.setText(String.valueOf(user.getFullname()));
        editTextContact.setText(String.valueOf(user.getContact()));

        idBtnEditProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

        idBtnChgPword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, ChgPwordActivity.class));
            }
        });
    }

    private void updateProfile(){
        String userID = editTextUserId.getText().toString();
        String fullName = editTextFullname.getText().toString();
        String email = editTextEmail.getText().toString();
        String contact = editTextContact.getText().toString();

        if(TextUtils.isEmpty(fullName)){
            editTextFullname.setError("Fullname is required!");
            editTextFullname.requestFocus();
        }

        if(TextUtils.isEmpty(contact)){
            editTextContact.setError("Contact is required!");
            editTextContact.requestFocus();
        }


        class UpdateProfile extends AsyncTask<Void, Void, String> {

            private ProgressBar progressBar;


            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();

                HashMap<String, String> params = new HashMap<>();
                params.put("id", userID);
                params.put("fullname", fullName);
                params.put("contact", contact);
                params.put("email", email);

                return requestHandler.sendPostRequest(URLs.URL_UPDATE, params); //HashMap<String, String>
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
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), "Some error occurred", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        UpdateProfile up = new UpdateProfile();
        up.execute();
    }





    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent backHome = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(backHome);
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
