package com.ass2.i190426_i190435;

import android.app.Application;
import android.widget.Toast;

import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OSNotificationReceivedEvent;
import com.onesignal.OneSignal;

import org.json.JSONObject;

public class MyApplication extends Application {
    private static final String ONESIGNAL_APP_ID = "e3764d5a-386f-4bbf-a091-c90629bc4a4c";

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

//        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.DEBUG, OneSignal.LOG_LEVEL.DEBUG);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        // promptForPushNotifications will show the native Android notification permission prompt.
        // We recommend removing the following code and instead using an In-App Message to prompt for notification permission (See step 7)
        OneSignal.promptForPushNotifications();

        OneSignal.setNotificationOpenedHandler(new OneSignal.OSNotificationOpenedHandler() {
            @Override
            public void notificationOpened(OSNotificationOpenedResult osNotificationOpenedResult) {

//                JSONObject notf=osNotificationOpenedResult.toJSONObject();
//                System.out.println("Notification has been opened and JSON Object is: "+notf);
//                Toast.makeText(MyApplication.this, "Opened"+osNotificationOpenedResult.toString(), Toast.LENGTH_SHORT).show();

            }


        });

        OneSignal.setNotificationWillShowInForegroundHandler(new OneSignal.OSNotificationWillShowInForegroundHandler() {

            @Override
            public void notificationWillShowInForeground(OSNotificationReceivedEvent osNotificationReceivedEvent) {

            }
        });

    }
}