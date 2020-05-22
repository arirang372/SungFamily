package com.sungfamily.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

/**
 * Created by john on 3/21/2017.
 */

public abstract class BasePhoneCallReceiver extends BroadcastReceiver
{
    private Context ctx;
    protected static final String TAG = "BasePhoneCallReceiver";
    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static String savedNumber;
    private static boolean isIncoming;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        ctx = context;

        if(intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL"))
        {
            savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
           // LOGD(TAG, "Outgoing call is taking place :: number is " + savedNumber);
        }
        else // incoming call....
        {
            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            int state = 0;
            if(stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                state = TelephonyManager.CALL_STATE_IDLE;
            }
            else if(stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            }
            else if(stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                state = TelephonyManager.CALL_STATE_RINGING;
            }
            onCallStateChanged(context, state, number);

          //  LOGD(TAG, "Incoming call is taking place :: number is " + number);
        }
    }

    protected void onIncomingCallStarted(Context ctx, String number){}


    public void onCallStateChanged(Context context, int state, String number) {
        if(lastState == state){
            //No change, debounce extras
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                savedNumber = number;
                onIncomingCallStarted(context, number);
                break;
//            case TelephonyManager.CALL_STATE_OFFHOOK:
//                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
//                if(lastState != TelephonyManager.CALL_STATE_RINGING){
//                    isIncoming = false;
//                    callStartTime= ba.getCurrentDateTime();
//                    onOutgoingCallStarted(context, savedNumber, callStartTime);
//                }
//                break;
//            case TelephonyManager.CALL_STATE_IDLE:
//                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
//                if(lastState == TelephonyManager.CALL_STATE_RINGING){
//                    //Ring but no pickup-  a miss
//                    onMissedCall(context, savedNumber, callStartTime);
//                }
//                else if(isIncoming){
//                    onIncomingCallEnded(context, savedNumber, callStartTime, ba.getCurrentDateTime());
//                }
//                else{
//                    onOutgoingCallEnded(context, savedNumber, callStartTime, ba.getCurrentDateTime());
//                }
//                break;
        }
        lastState = state;
    }

//    TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
//            telephony.listen(new PhoneStateListener(){
//    @Override
//    public void onCallStateChanged(int state, String incomingNumber)
//    {
//        super.onCallStateChanged(state, incomingNumber);
//
//        if(TelephonyManager.CALL_STATE_OFFHOOK == state)
//        {
//            LOGD(TAG, "Call was answered...");
//            ProviderAccess access = new ProviderAccess(context, new ProviderAccessHelper());
//            List<User> users = (List<User>) access.getAll(new User());
//            if (users.size() == 1) {
//                User user = users.get(0);
//                CallLog log = new CallLog();
//                log = log.getLogByMid(context, user.m_id);
//                if (log != null && log.m_id != 0) {
//                    //send a message to hcc intake app...
//                    CloudMessage message = new CloudMessage();
//                    message.setFromApplicationName(ctx.getPackageName());
//                    message.hcc_id = user.m_id;
//                    message.m_id = log.m_id;
//                    message.caller = log.caller;
//                    message.phone = log.phone;
//                    message.setAction(ActionType.VIEW_PC_ROC_DETAILS.toString());
//
//                    user.setCloudMessage(message);
//
//                    try {
//                        HttpResponse httpResponse = new HttpResponse();
//                        httpResponse.setCurrentUser(user);
//                        final RetrofitTwoServiceWrapper<HccIntakeService> wrapper = new RetrofitTwoServiceWrapper<>(httpResponse, AppMetadata.TO_APPLICATION_NAME);
//                        HccIntakeService service = ServiceFactory.createService(AppMetadata.HCC_INTAKE_SERVICE);
//                        service.sendCloudMessage(wrapper.getRequest())
//                                .subscribeOn(Schedulers.io())
//                                .observeOn(Schedulers.newThread())
//                                .subscribe(new Observer<Response<ResponseBody>>() {
//                                    @Override
//                                    public void onCompleted() {
//
//                                    }
//
//                                    @Override
//                                    public void onError(Throwable e) {
//                                        LOGD(TAG, e.getMessage());
//                                    }
//
//                                    @Override
//                                    public void onNext(Response<ResponseBody> response) {
//                                        try {
//                                            BufferedReader reader = null;
//                                            if (response.body() != null) {
//                                                reader = new BufferedReader(new InputStreamReader(response.body().byteStream()));
//                                                //convert the json string back to object
//                                                Gson gson = new GsonBuilder().disableHtmlEscaping()
//                                                        .setPrettyPrinting()
//                                                        .serializeNulls()
//                                                        .create();
//
//                                                HttpResponse serverResponse = gson.fromJson(reader, HttpResponse.class);
//                                                LOGD("Server Message", serverResponse.getResponseMessage());
//                                            }
//                                        } catch (Exception ex) {
//                                            ex.printStackTrace();
//                                        }
//                                    }
//                                });
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                    }
//                }
//
//            }
//        }
//    }
//},PhoneStateListener.LISTEN_CALL_STATE);
//





}
