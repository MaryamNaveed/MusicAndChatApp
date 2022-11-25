package com.ass2.i190426_i190435;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Base64;
import android.widget.Toast;

import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.security.MessageDigest;

public class MySinch extends Service implements SinchClientListener, CallListener {

    private String App_Key="25831274-bacc-43f8-8f24-dda31e543970";
    private String App_Secret="cLx49ZkFiECkK27m62cxBQ==";
    private String ENVIRONMENT = "ocra.api.sinch.com";

    private SinchClient sinchClient = null;
    private String userId="";
    private long mSigningSequence = 1;

    private void startInternal(String id){
        if(sinchClient==null){
            createClient(id);
        }

        if(sinchClient.isStarted()){

        }
        else{
            sinchClient.start();
        }


    }
    private void createClient(String id){
        userId=id;
        sinchClient = Sinch.getSinchClientBuilder()
                .context(getApplicationContext())
                .applicationKey(App_Key)
                .environmentHost(ENVIRONMENT)
                .userId(id).build();

        sinchClient.addSinchClientListener(this);





    }

    public MySinch() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return sinchServiceBinder;
    }

    @Override
    public void onClientStarted(SinchClient sinchClient) {
//        Toast.makeText(getApplicationContext(), "Started", Toast.LENGTH_LONG).show();
        sinchClient.startListeningOnActiveConnection();

    }

    @Override
    public void onClientFailed(SinchClient sinchClient, SinchError sinchError) {
//        Toast.makeText(getApplicationContext(), sinchError.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLogMessage(int i, String s, String s1) {

    }

    @Override
    public void onPushTokenRegistered() {
//        Toast.makeText(getApplicationContext(), "Registered", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPushTokenRegistrationFailed(SinchError sinchError) {
//        Toast.makeText(getApplicationContext(), sinchError.getCode(), Toast.LENGTH_LONG).show();
//        Toast.makeText(getApplicationContext(), sinchError.getMessage(), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onCredentialsRequired(ClientRegistration clientRegistration) {
        String jwt = JWT.create(App_Key, App_Secret, userId);

        clientRegistration.register(jwt);

    }

    @Override
    public void onUserRegistered() {

    }

    @Override
    public void onUserRegistrationFailed(SinchError sinchError) {

    }

    @Override
    public void onCallProgressing(Call call) {

    }

    @Override
    public void onCallEstablished(Call call) {

    }

    @Override
    public void onCallEnded(Call call) {

    }

    private SinchClassInitializationListerner sinchClassInitializationListerner=null;



    interface SinchClassInitializationListerner{
        void onStartedSuccessfully();
        void onFailed(SinchError error);
    }

    public class SinchServiceBinder extends Binder{
        public void start(String username) {
            startInternal(username);
        }
        public void setClientInitializationListener(SinchClassInitializationListerner listener ) {
            sinchClassInitializationListerner=listener;

        }
        public Call callUser(String username) {
            System.out.println(sinchClient);
            if (sinchClient!=null && sinchClient.isStarted()){
                return sinchClient.getCallClient().callUser(username);
            }

            return null;
        }
        void addCallControllerListener(CallClientListener callClientListener) {

            sinchClient.getCallClient().addCallClientListener(callClientListener);
        }


    }

    private SinchServiceBinder sinchServiceBinder= new SinchServiceBinder();






}