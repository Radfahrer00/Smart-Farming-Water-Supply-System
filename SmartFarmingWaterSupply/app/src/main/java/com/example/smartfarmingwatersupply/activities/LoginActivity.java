package com.example.smartfarmingwatersupply.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.example.smartfarmingwatersupply.R;
import com.example.smartfarmingwatersupply.ServiceGenerator;
import com.example.smartfarmingwatersupply.ThingsboardService;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This Activity is called when starting the application.
 * LoginActivity is an activity that handles the login functionality
 * for the Smart Farming Water Supply application. It provides a user interface
 * for entering a username and password, validates these credentials with the Thingsboard server,
 * found at: <a href="https://thingsboard.io/">...</a> ,
 * and attempts to log the user in by fetching a token from the server. On successful login,
 * the user is redirected to the MainActivity.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;

    /**
     * Called when the activity is starting. This method performs the initial
     * setup of the activity's general user interface – it sets the content view
     * and initializes the UI components involved in the login process.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in onSaveInstanceState(Bundle). Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);

        loginButton.setOnClickListener(v -> attemptLogin());
    }


    /**
     * Attempts to log in the user by first validating the username and password
     * inputs. If either field is empty, it prompts the user to fill in the missing
     * information. If both fields are filled, it proceeds to attempt logging in
     * through the {@link #login(String, String)} method.
     */
    private void attemptLogin() {
        String userName = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        // Validate input
        if (TextUtils.isEmpty(userName)) {
            usernameEditText.setText(R.string.enter_username);
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setText(R.string.enter_password);
            return;
        }

        login(userName, password);
    }


    /**
     * Performs the login operation by sending the username and password to the
     * server to obtain a user token. On successful login, the user's token is saved
     * in SharedPreferences, and the LoginActivity navigates to the MainActivity.
     *
     * @param userName The username entered by the user.
     * @param password The password entered by the user.
     */
    public void login(String userName, String password) {
        ThingsboardService tbs = ServiceGenerator.createService (ThingsboardService.class);
        JsonObject usr = new JsonObject();
        usr.addProperty( "username", userName);
        usr.addProperty( "password", password);
        Call<JsonObject> resp = tbs.getUserToken(usr);
        resp.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse (@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.code() == 200) {
                    try {
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        String token = jsonObject.getString("token");
                        Log.d("RESPONSE::", "Starting activity... with token: " + jsonObject.getString("token"));

                        // Save the token to SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("UserToken", token);
                        editor.apply();
                        Log.d("RESPONSE::", "Token saved successfully");

                        // Navigate to the MainActivity (NotificationsFragment)
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    catch (Exception ex) {ex.printStackTrace();}
                } else Log.d("RESPONSE::ERROR", String.valueOf(response.code()));
        }

            @Override
            public void onFailure (@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Log.d( "RESPONSE::ERROR", "NOT WORKING");
            }
        });
    }
}
