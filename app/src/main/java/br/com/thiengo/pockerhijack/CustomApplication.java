package br.com.thiengo.pockerhijack;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.onesignal.OSNotification;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.thiengo.pockerhijack.domain.Message;
import br.com.thiengo.pockerhijack.domain.User;
import br.com.thiengo.pockerhijack.extras.Util;
import br.com.thiengo.pockerhijack.services.NotificationService;


/**
 * Created by viniciusthiengo on 15/01/17.
 */

public class CustomApplication extends Application
            implements OneSignal.NotificationReceivedHandler {
    @Override
    public void onCreate() {
        super.onCreate();
        OneSignal
            .startInit(this)
            .setNotificationReceivedHandler(this)
            .init();
    }

    @Override
    public void notificationReceived(OSNotification notification) {
        Log.i("log", "Notification: " + notification.payload.additionalData.toString());

        if( !MainActivity.isOpened
                && Util.isSystemAlertPermissionGranted(this) ){

            Message message = getMessage( notification );

            Intent intent = new Intent( this, NotificationService.class);
            intent.putExtra( Message.KEY, message );
            startService( intent );
        }
    }


    private Message getMessage(OSNotification notification){
        Message message = new Message();

        try{
            JSONObject jsonObject = notification.payload.additionalData;
            User user = new User();
            user.setId( jsonObject.getInt("user_id") );
            user.setImage( jsonObject.getString("user_image") );

            message.setMessage( notification.payload.body );
            message.setUser( user );
        }
        catch(JSONException e){}

        return message;
    }
}
