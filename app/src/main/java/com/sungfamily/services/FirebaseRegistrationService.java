package com.sungfamily.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FirebaseRegistrationService  extends FirebaseInstanceIdService
{
    private final String TAG = this.getClass().getSimpleName();

    @Override
    public void onTokenRefresh()
    {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
    }


    public static String getFBToken(){
        return FirebaseInstanceId.getInstance().getToken();
    }


}
