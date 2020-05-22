///**
// * Copyright 2016 Google Inc. All Rights Reserved.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.sungfamily;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.pm.PackageManager;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.provider.Settings;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//import com.fastaccess.permission.base.PermissionHelper;
//import com.fastaccess.permission.base.callback.OnPermissionCallback;
//import com.google.android.gms.auth.api.signin.GoogleSignIn;
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//import com.google.android.gms.auth.api.signin.GoogleSignInClient;
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
//import com.google.android.gms.common.api.ApiException;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthCredential;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.auth.GoogleAuthProvider;
//import com.sungfamily.constants.Constant;
//import com.sungfamily.models.HttpCourierResponse;
//import com.sungfamily.receiver.BasePhoneCallReceiver;
//import com.sungfamily.rest.RemoteService;
//import io.reactivex.Observer;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.disposables.Disposable;
//import io.reactivex.schedulers.Schedulers;
//import retrofit2.Response;
//import static com.sungfamily.constants.Constant.APP_DETAILS_CLASS_NAME;
//import static com.sungfamily.constants.Constant.APP_DETAILS_PACKAGE_NAME;
//import static com.sungfamily.constants.Constant.APP_PKG_NAME_21;
//import static com.sungfamily.constants.Constant.APP_PKG_NAME_22;
//import static com.sungfamily.constants.Constant.SCHEME;
//
//public class GoogleSignInActivity extends AppCompatActivity implements
//        View.OnClickListener, OnPermissionCallback {
//
//    private static final String TAG = "GoogleActivity";
//    private static final int RC_SIGN_IN = 9001;
//
//    private FirebaseAuth mAuth;
//    private GoogleSignInClient mGoogleSignInClient;
//    private TextView mStatusTextView;
//    private TextView mDetailTextView;
//    private Button btnWriteReview;
//    private Button btnWriteMessage;
//    private static PermissionHelper permissionHelper;
//    private IncomingCallReceiver receiver;
//
//    private boolean isUserSignedIn;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_google);
//        receiver = new IncomingCallReceiver();
//        // Views
//        mStatusTextView = findViewById(R.id.status);
//        mDetailTextView = findViewById(R.id.detail);
//        btnWriteReview = findViewById(R.id.btnWriteReview);
//        btnWriteReview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v)
//            {
//                startActivity(new Intent(GoogleSignInActivity.this, MainActivity.class));
//            }
//        });
//
//        btnWriteMessage = findViewById(R.id.btnWriteMessage);
//        btnWriteMessage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v)
//            {
//                startActivity(new Intent(GoogleSignInActivity.this, ChatActivity.class));
//            }
//        });
//
//        findViewById(R.id.sign_in_button).setOnClickListener(this);
//        findViewById(R.id.sign_out_button).setOnClickListener(this);
//        findViewById(R.id.disconnect_button).setOnClickListener(this);
//
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//
//        mAuth = FirebaseAuth.getInstance();
//    }
//
//
//    @Override
//    public void onStart()
//    {
//        super.onStart();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
//        {
//            if(permissionHelper == null)
//                permissionHelper = PermissionHelper.getInstance(this);
//
//            if(!hasPermissionGranted())
//            {
//                permissionHelper.setForceAccepting(true) // default is false. its here so you know that it exists.
//                        .request(Constant.MULTI_PERMISSIONS);
//            }
//        }
//
//        if(isUserSignedIn)
//        {
//            btnWriteReview.setVisibility(View.VISIBLE);
//            btnWriteMessage.setVisibility(View.VISIBLE);
//        }
//        else
//        {
//            btnWriteReview.setVisibility(View.GONE);
//            btnWriteMessage.setVisibility(View.GONE);
//        }
//    }
//
//    @Override
//    public void onResume()
//    {
//        super.onResume();
//        IntentFilter filter = new IntentFilter();
//        filter.addAction("android.intent.action.PHONE_STATE");
//        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
//        registerReceiver(receiver, filter);
//    }
//
//    @Override
//    public void onStop()
//    {
//        super.onStop();
//
//        unregisterReceiver(receiver);
//    }
//
//    private boolean hasPermissionGranted(){
//        for(int i = 0; i < Constant.MULTI_PERMISSIONS.length -1 ; i++){
//            int res = this.checkCallingOrSelfPermission( Constant.MULTI_PERMISSIONS[i]);
//            if (res != PackageManager.PERMISSION_GRANTED){
//                return false;
//            }
//        }
//        return true;
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data)
//    {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == RC_SIGN_IN)
//        {
//            isUserSignedIn = true;
//            btnWriteReview.setVisibility(View.VISIBLE);
//            btnWriteMessage.setVisibility(View.VISIBLE);
//
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            try
//            {
//                GoogleSignInAccount account = task.getResult(ApiException.class);
//                firebaseAuthWithGoogle(account);
//            }
//            catch (ApiException e)
//            {
//                updateUI(null);
//            }
//
//        }
//
//        if(requestCode == 1)
//        {
//            if(resultCode == -1)
//            {
//                openSettingsScreen(this, this.getPackageName());
//            }
//        }
//    }
//
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
//    {
//        if(requestCode == 1)
//        {
//            for(int r : grantResults)
//            {
//                if(r == -1)
//                {
//                    openSettingsScreen(this, this.getPackageName());
//                    return;
//                }
//            }
//
//            permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }
//
//
//    public void openSettingsScreen(Context context, String packageName)
//    {
//        Intent intent = new Intent();
//        final int apiLevel = Build.VERSION.SDK_INT;
//        if (apiLevel >= 9)
//        { // above 2.3
//            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//            Uri uri = Uri.fromParts(SCHEME, packageName, null);
//            intent.setData(uri);
//        }
//        else
//        { // below 2.3
//            final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22 : APP_PKG_NAME_21);
//            intent.setAction(Intent.ACTION_VIEW);
//            intent.setClassName(APP_DETAILS_PACKAGE_NAME, APP_DETAILS_CLASS_NAME);
//            intent.putExtra(appPkgName, packageName);
//        }
//        context.startActivity(intent);
//    }
//
//    private void firebaseAuthWithGoogle(GoogleSignInAccount acct)
//    {
//
//        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
//                        } else {
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            updateUI(null);
//                        }
//
//                    }
//                });
//    }
//
//    private void signIn() {
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }
//    // [END signin]
//
//    private void signOut() {
//        // Firebase sign out
//        mAuth.signOut();
//        btnWriteReview.setVisibility(View.GONE);
//        btnWriteMessage.setVisibility(View.GONE);
//        // Google sign out
//        mGoogleSignInClient.signOut().addOnCompleteListener(this,
//                new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        updateUI(null);
//                    }
//                });
//    }
//
//    private void revokeAccess() {
//        // Firebase sign out
//        mAuth.signOut();
//
//        // Google revoke access
//        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
//                new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        updateUI(null);
//                    }
//                });
//    }
//
//    private void updateUI(FirebaseUser user) {
//        if (user != null) {
//            mStatusTextView.setText(getString(R.string.google_status_fmt, user.getEmail()));
//            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));
//
//            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
//            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
//
//            isUserSignedIn = true;
//
//        } else {
//            mStatusTextView.setText(R.string.signed_out);
//            mDetailTextView.setText(null);
//
//            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
//            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
//
//            isUserSignedIn = false;
//        }
//    }
//
//    @Override
//    public void onClick(View v) {
//        int i = v.getId();
//        if (i == R.id.sign_in_button) {
//            signIn();
//        } else if (i == R.id.sign_out_button) {
//            signOut();
//        } else if (i == R.id.disconnect_button) {
//            revokeAccess();
//        }
//    }
//
//    @Override
//    public void onPermissionGranted(@NonNull String[] permissionName) {
//
//    }
//
//    @Override
//    public void onPermissionDeclined(@NonNull String[] permissionName) {
//
//    }
//
//    @Override
//    public void onPermissionPreGranted(@NonNull String permissionsName) {
//
//    }
//
//    @Override
//    public void onPermissionNeedExplanation(@NonNull String permissionName) {
//
//    }
//
//    @Override
//    public void onPermissionReallyDeclined(@NonNull String permissionName) {
//
//    }
//
//    @Override
//    public void onNoPermissionNeeded() {
//
//    }
//
//    private class IncomingCallReceiver extends BasePhoneCallReceiver
//    {
//
//        protected void onIncomingCallStarted(Context context, String number)
//        {
//            //send fb message
//            RemoteService remoteService =  RemoteService.Factory.getInstance(GoogleSignInActivity.this);
//            remoteService.sendFirebaseMessage(Constant.RECIPIENT_FB_TOKEN, "open_app")
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(new Observer<Response<HttpCourierResponse>>() {
//                            @Override
//                            public void onSubscribe(Disposable d)
//                            {
//
//                            }
//
//                            @Override
//                            public void onNext(Response<HttpCourierResponse> response)
//                            {
//                                if(response.isSuccessful())
//                                {
//                                    //do nothing...
//
//                                }
//                            }
//
//                            @Override
//                            public void onError(Throwable e)
//                            {
//                                e.printStackTrace();
//                            }
//
//                            @Override
//                            public void onComplete() {
//
//                            }
//                        });
//
//        }
//    }
//
//}
