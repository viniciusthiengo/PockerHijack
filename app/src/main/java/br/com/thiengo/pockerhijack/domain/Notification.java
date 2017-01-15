package br.com.thiengo.pockerhijack.domain;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
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

import br.com.thiengo.pockerhijack.MainActivity;
import br.com.thiengo.pockerhijack.R;
import br.com.thiengo.pockerhijack.extras.Util;

/**
 * Created by viniciusthiengo on 15/01/17.
 */

public class Notification implements View.OnTouchListener {
    private boolean isInRightSide = false;
    private boolean isClicked = false;
    private RelativeLayout bubble;
    private WindowManager windowManager;
    private WindowManager.LayoutParams params;

    private int width;
    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;


    public Notification( WindowManager windowManager, RelativeLayout layout ){
        this.windowManager = windowManager;
        this.setBubble( layout );
        this.setWidth();
        this.setParams();
    }

    private void setWidth() {
        Display display = windowManager.getDefaultDisplay();

        if( Util.isPlusEqualsApi13() ){
            Point size = new Point();
            display.getSize(size);
            width = size.x;
        }
        else{
            width = display.getWidth();
        }
    }

    public RelativeLayout getBubble() {
        return bubble;
    }

    private void setBubble(RelativeLayout bubble) {
        this.bubble = bubble;
        this.bubble.setOnTouchListener(this);
    }

    public WindowManager.LayoutParams getParams() {
        return params;
    }

    private void setParams() {
        this.params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        this.params.gravity = Gravity.TOP | Gravity.LEFT;
        this.params.x = 0;
        this.params.y = 100;
    }

    public void updateBubbleView( Message message ){
        ((TextView) bubble.findViewById(R.id.tv_message)).setText( message.getMessage() );

        Picasso.with( bubble.getContext() )
                .load( message.getUser().getImage() )
                .into( ((CircularImageView) bubble.findViewById(R.id.cimv_profile)) );
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                actionDownUpdate( event );
                return true;
            case MotionEvent.ACTION_UP:
                actionUpUpdate( event );
                return true;
            case MotionEvent.ACTION_MOVE:
                actionMoveUpdate( event );
                return true;
        }
        return false;
    }

    private void actionDownUpdate( MotionEvent event ){
        initialX = params.x;
        initialY = params.y;
        initialTouchX = event.getRawX();
        initialTouchY = event.getRawY();
        isClicked = true;
        Log.i("Log", "ACTION_DOWN");
    }

    private void actionUpUpdate( MotionEvent event ){
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
        slowDrawBubbleMove( desiredPosition );
        updateContentParams( desiredPosition == 0 );
        callActivityIfClicked();
        Log.i("Log", "ACTION_UP");
    }

    private void actionMoveUpdate( MotionEvent event ){
        int extraVal = isInRightSide ? bubble.getWidth() * -1 : 0;
        isClicked = false;

        params.x = initialX + extraVal + (int) (event.getRawX() - initialTouchX);
        params.y = initialY + (int) (event.getRawY() - initialTouchY);
        windowManager.updateViewLayout(bubble, params);
        Log.i("Log", "ACTION_MOVE");
    }

    private void callActivityIfClicked(){
        if( isClicked ){
            Intent intent = new Intent( bubble.getContext(), MainActivity.class);
            intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
            bubble.getContext().startActivity(intent);
        }
    }

    private void slowDrawBubbleMove( int desiredPosition ){
        int initialPosition = params.x;
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

    private void updateContentParams( boolean isToLeft ){
        RelativeLayout view;
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                Util.getDpsToPixels( 166 ),
                RelativeLayout.LayoutParams.WRAP_CONTENT );
        Bitmap bitmap = ((BitmapDrawable) ((ImageView)bubble.findViewById(R.id.cimv_profile)).getDrawable()).getBitmap();
        String text = ((TextView) bubble.findViewById(R.id.tv_message)).getText().toString();
        Context context = bubble.getContext();

        bubble.removeAllViews();
        if( isToLeft ){
            view = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.bubble_notification_left, null);
        }
        else{
            view = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.bubble_notification_right, null);
        }

        ((ImageView)view.findViewById(R.id.cimv_profile)).setImageBitmap( bitmap );
        ((TextView)view.findViewById(R.id.tv_message)).setText( text );

        view.setLayoutParams(lp);
        bubble.addView( view );
    }
}
