package br.com.thiengo.pockerhijack.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import br.com.thiengo.pockerhijack.R;
import br.com.thiengo.pockerhijack.domain.Message;
import br.com.thiengo.pockerhijack.domain.Notification;
import br.com.thiengo.pockerhijack.domain.User;

/**
 * Created by viniciusthiengo on 15/01/17.
 */

public class BubbleNotification extends Service {
    private WindowManager windowManager;
    private List<Notification> bubbles;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        bubbles = new ArrayList<>();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
    }

    private Notification getBubble( User user ){
        Notification bubble = getBubbleFromList( user );
        if( bubble == null ){
            bubble = getNewBubble( user );
        }

        return bubble;
    }

    private Notification getBubbleFromList( User user ){
        for( Notification n : bubbles ){
            if( n.getBubble().getId() == user.getId() ){
                return n;
            }
        }

        return null;
    }

    private Notification getNewBubble( User user ){
        RelativeLayout layout = (RelativeLayout) LayoutInflater.from(this).inflate( R.layout.bubble_notification, null, false );
        layout.setId( user.getId() );

        Notification bubble = new Notification( windowManager, layout );

        bubbles.add( bubble );
        windowManager.addView( bubble.getBubble(), bubble.getParams() );

        return bubble;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if( intent.getParcelableExtra( Message.KEY ) != null ){
            Message message = intent.getParcelableExtra( Message.KEY );
            Notification bubble = getBubble( message.getUser() );
            bubble.updateBubbleView( message );
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bubbles.size() > 0){
            for( Notification bubble : bubbles ){
                windowManager.removeView( bubble.getBubble() );
            }
        }
    }
}
