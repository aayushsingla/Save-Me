package com.codefundo.saveme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;
import com.microsoft.windowsazure.mobileservices.MobileServiceActivityResult;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class LoginActivity extends AppCompatActivity {
    public static final String SHAREDPREFFILE = "temp";
    public static final String USERIDPREF = "uid";
    public static final String TOKENPREF = "tkn";
    private static final int GOOGLE_LOGIN_REQUEST_CODE = 1;
    private MobileServiceClient mClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mClient = SaveMe.getAzureClient(this);

        if (loadUserTokenCache(mClient)) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        SignInButton signInButton = findViewById(R.id.btn_sign_in);
        signInButton.setOnClickListener(view -> authenticate());

        TextView skipTv = findViewById(R.id.btn_skip);
        skipTv.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, MainActivity.class)));

    }


    private void authenticate() {
        // Sign in using the Google provider.
        mClient.login(MobileServiceAuthenticationProvider.Google, "saveme", GOOGLE_LOGIN_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // When request completes
        if (resultCode == RESULT_OK) {
            // Check the request code matches the one we send in the login request
            if (requestCode == GOOGLE_LOGIN_REQUEST_CODE) {
                MobileServiceActivityResult result = mClient.onActivityResult(data);
//                if (result.isLoggedIn()) {
//                    // sign-in succeeded
//                    cacheUserToken(mClient.getCurrentUser());
//                    createAndShowDialog(String.format("You are now signed in - %1$2s", mClient.getCurrentUser().getUserId()), "Success",true);
//                } else {
//                    // sign-in failed, check the error message
//                    String errorMessage = result.getErrorMessage();
//                    createAndShowDialog(errorMessage, "Error",false);
//                }
                createAndShowDialog(String.format("You are now signed in - %1$2s", "haan please chalja"), "Success", true);

            }
        }
    }


    private void createAndShowDialog(String format, String success, boolean b) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this)
                .setTitle("Login")
                .setMessage(format + success);

        if (b) {
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("Ok", (d, which) -> {
                startActivity(new Intent(LoginActivity.this, DetailsActivity.class));
                finish();
            });
        } else {
            alertDialog.setCancelable(true);
            alertDialog.setPositiveButton("Retry", (d, which) -> authenticate());
        }
        alertDialog.show();
    }

    private void cacheUserToken(MobileServiceUser user) {
        SharedPreferences prefs = getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(USERIDPREF, user.getUserId());
        editor.putString(TOKENPREF, user.getAuthenticationToken());
        editor.apply();
    }

    private boolean loadUserTokenCache(MobileServiceClient client) {
        SharedPreferences prefs = getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
        String userId = prefs.getString(USERIDPREF, null);
        if (userId == null)
            return false;
        String token = prefs.getString(TOKENPREF, null);
        if (token == null)
            return false;

        MobileServiceUser user = new MobileServiceUser(userId);
        user.setAuthenticationToken(token);
        client.setCurrentUser(user);

        return true;
    }
}


