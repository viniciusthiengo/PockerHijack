package br.com.thiengo.pockerhijack.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import br.com.thiengo.pockerhijack.MainActivity;
import br.com.thiengo.pockerhijack.R;
import br.com.thiengo.pockerhijack.domain.Message;
import br.com.thiengo.pockerhijack.domain.Notification;
import br.com.thiengo.pockerhijack.domain.User;
import br.com.thiengo.pockerhijack.extras.Util;

/**
 * Created by viniciusthiengo on 15/01/17.
 */

public class BubbleNotification extends Service {
    private WindowManager windowManager;
    private boolean isInRightSide = false;
    private boolean isClicked = false;
    //private RelativeLayout bubble;
    private List<Notification> bubbles;
    //private WindowManager.LayoutParams params;

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

    private RelativeLayout getBubble( User user ){
        RelativeLayout bubble = getBubbleFromList( user );
        if( bubble == null ){
            bubble = getNewBubble( user );
        }

        return bubble;
    }

    private RelativeLayout getBubbleFromList( User user ){
        for( RelativeLayout layout : bubbles ){
            if( layout.getId() == user.getId() ){
                return layout;
            }
        }

        return null;
    }

    private RelativeLayout getNewBubble( User user ){
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final int width = size.x;

        final RelativeLayout bubble = (RelativeLayout) LayoutInflater.from(this).inflate( R.layout.bubble_notification, null, false );
        bubble.setId( user.getId() );
        bubbles.add( bubble );

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;

        bubble.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        Log.i("Log", "ACTION_DOWN");
                        isClicked = true;
                        return true;
                    case MotionEvent.ACTION_UP:
                        Log.i("Log", "ACTION_UP");

                        int desiredPosition;
                        int posX = params.x + bubble.getWidth() / 2;
                        if( posX > width / 2 ){
                            desiredPosition = width;
                            isInRightSide = true;
                        }
                        else{
                            desiredPosition = 0;
                            isInRightSide = false;
                        }
                        slowDrawBuubleMove( bubble, params.x, desiredPosition );
                        updateContentParams( bubble, desiredPosition == 0 );

                        if( isClicked ){
                            Intent intent = new Intent(BubbleNotification.this, MainActivity.class);
                            intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                            startActivity(intent);
                        }

                        return true;
                    case MotionEvent.ACTION_MOVE:
                        int extraVal = isInRightSide ? bubble.getWidth() * -1 : 0;
                        isClicked = false;

                        params.x = initialX + extraVal + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(bubble, params);
                        Log.i("Log", "ACTION_MOVE");

                        return true;
                }
                return false;
            }
        });

        windowManager.addView(bubble, params);
        return bubble;
    }

    private void slowDrawBuubleMove( RelativeLayout bubble, int initialPosition, int desiredPosition ){
        boolean isToGrow = initialPosition < desiredPosition;
        WindowManager.LayoutParams params = (WindowManager.LayoutParams) bubble.getLayoutParams();

        while(true){
            if( isToGrow && initialPosition < desiredPosition ){
                initialPosition++;
            }
            else if( !isToGrow && initialPosition > desiredPosition ){
                initialPosition--;
            }
            else{
                break;
            }
            params.x = initialPosition;
            windowManager.updateViewLayout(bubble, params);
        }
    }

    private void updateContentParams( RelativeLayout bubble, boolean isToLeft ){
        RelativeLayout view;
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                Util.getDpsToPixels( 166 ),
                RelativeLayout.LayoutParams.WRAP_CONTENT );

        Bitmap bitmap = ((BitmapDrawable) ((ImageView)bubble.findViewById(R.id.cimv_profile)).getDrawable()).getBitmap();
        String text = ((TextView) bubble.findViewById(R.id.tv_message)).getText().toString();

        bubble.removeAllViews();
        if( isToLeft ){
            view = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.bubble_notification_left, null);
        }
        else{
            view = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.bubble_notification_right, null);
        }

        ((ImageView)view.findViewById(R.id.cimv_profile)).setImageBitmap( bitmap );
        ((TextView)view.findViewById(R.id.tv_message)).setText( text );

        view.setLayoutParams(lp);
        bubble.addView( view );
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Message message = intent.getParcelableExtra( Message.KEY );
        RelativeLayout bubble = getBubble( message.getUser() );
        updateBubble( bubble, message );

        return super.onStartCommand(intent, flags, startId);
    }

    private void updateBubble( RelativeLayout bubble, Message message ){
        ((TextView) bubble.findViewById(R.id.tv_message)).setText( message.getMessage() );

        Picasso.with(this)
            .load( message.getUser().getImage() )
            .into( ((CircularImageView) bubble.findViewById(R.id.cimv_profile)) );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bubbles.size() > 0){
            for( RelativeLayout layout : bubbles ){
                windowManager.removeView( layout );
            }
        }
    }
}
