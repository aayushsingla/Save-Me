package com.codefundo.saveme.auth;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codefundo.saveme.MainActivity;
import com.codefundo.saveme.R;
import com.codefundo.saveme.SaveMe;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class LoginActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_IMEI_PERMISSION = 2336;
    private static final int RC_SIGN_IN = 123;
    public static final String SHAREDPREFFILE = "temp";
    public static final String USERIDPREF = "uid";
    public static final String TOKENPREF = "tkn";
    private static final String CLIENT_ID_WEB_APPS = "434305193965-h324ch2gd4d6jvfkn8rgdf4ih2os61cr.apps.googleusercontent.com";
    private MobileServiceClient mClient;
    private ProgressBar progressBar;

    public static String getCurrentUserUniqueId(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
        return prefs.getString(USERIDPREF, null);
    }

    public static String getCurrentUserToken(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
        return prefs.getString(TOKENPREF, null);
    }

    public static String getDeviceIMEI(Context mContext) {
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        return telephonyManager.getDeviceId();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressBar = findViewById(R.id.indeterminateBar);

        SignInButton signInButton = findViewById(R.id.btn_sign_in);
        signInButton.setOnClickListener(view -> authenticate());

        TextView skipTv = findViewById(R.id.btn_skip);
        skipTv.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, MainActivity.class)));

    }

    private void authenticate() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(CLIENT_ID_WEB_APPS)
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        final Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // When request completes
        if (resultCode == RESULT_OK && requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            hideShowProgressBar();
            final Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else {
            // sign-in failed, check the error message
            hideShowProgressBar();
            createAndShowDialog("signInFailed: ", "Error", false);
        }

    }

    private void handleSignInResult(@NonNull Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String idToken = account.getIdToken();
            onUserSignedIn(idToken);
        } catch (ApiException e) {
            Log.e("Tag", "handleSignInResult:error", e);
        }
    }

    private boolean isDeviceOnline() {
        ConnectivityManager mgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = mgr.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();

    }

    private void onUserSignedIn(String idToken) {
        Log.e("token", idToken);
        JsonObject loginBody = new JsonObject();
        loginBody.addProperty("id_token", idToken);

        ListenableFuture<MobileServiceUser> listenableFuture = mClient.login(MobileServiceAuthenticationProvider.Google, loginBody);
        Futures.addCallback(listenableFuture, new FutureCallback<MobileServiceUser>() {
            @Override
            public void onSuccess(MobileServiceUser result) {
                hideShowProgressBar();
                cacheUserToken(result);
                askIMEIPermissions();
            }

            @Override
            public void onFailure(Throwable t) {
                createAndShowDialog("Login Error: ", t.getMessage(), false);

            }
        });
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

    private void hideShowProgressBar() {
        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        mClient = SaveMe.getAzureClient(this);
        if (account != null && loadUserTokenCache(mClient)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

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

    private void askIMEIPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {
                Log.d("TAG", "Explanation for Permissions shown");

                new android.app.AlertDialog.Builder(this)
                        .setTitle("Access to Location")
                        .setMessage("IMEI Id of your device is needed to uniquely identify you and track your location.")
                        .setPositiveButton("OK", (dialog, which) -> ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE_IMEI_PERMISSION))
                        .show();
            } else {
                // No explanation needed; request the permission
                Log.d("Tag", "Permission Requested");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE_IMEI_PERMISSION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            createAndShowDialog("Login Successful: ", "Logged in to the mobile services", true);
            // Permission has already been granted
            Log.d("Tag", "Permission Granted");
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_IMEI_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    createAndShowDialog("Login Successful: ", "Logged in to the mobile services", true);
                } else {
                    Log.e("Tag", "Permissions couldn't be granted");
                    finish();
                }
                break;
            }
        }
    }

}

