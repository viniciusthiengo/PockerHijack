package br.com.thiengo.pockerhijack.domain;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import br.com.thiengo.pockerhijack.MainActivity;
import br.com.thiengo.pockerhijack.R;
import br.com.thiengo.pockerhijack.extras.Util;

/**
 * Created by viniciusthiengo on 16/01/17.
 */

public class Notification implements View.OnTouchListener {
    private WindowManager windowManager;
    private RelativeLayout bubble;
    private WindowManager.LayoutParams params;

    private boolean isClicked = false;
    private boolean isInRightSide = false;
    private int width;
    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;

    public Notification( WindowManager windowManager, RelativeLayout layout ){
        this.windowManager = windowManager;
        setBubble( layout );
        setParams();
        setWidth();
    }

    private void setWidth() {
        Display display = windowManager.getDefaultDisplay();

        if( Util.isPlusEqualsApi13() ){
            Point size = new Point();
            display.getSize( size );
            width = size.x;
        }
        else{
            width = display.getWidth();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch ( event.getAction() ){
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
    }

    private void actionUpUpdate( MotionEvent event ){
        int desiredPosition;
        int posX = params.x + bubble.getWidth() / 2;

        if( posX < width / 2 ){
            desiredPosition = 0;
            isInRightSide = false;
        }
        else{
            desiredPosition = width;
            isInRightSide = true;
        }

        slowDrawBubbleMove( desiredPosition );
        updateWindowViews();
        callActivityIfClicked();
    }

    private void actionMoveUpdate( MotionEvent event ){
        int extraVal = isInRightSide ? bubble.getWidth() * -1 : 0;

        params.x = initialX + extraVal + (int)( event.getRawX() - initialTouchX );
        params.y = initialY + (int)( event.getRawY() - initialTouchY );

        windowManager.updateViewLayout( bubble, params );
        isClicked = false;
    }

    private void slowDrawBubbleMove( int desiredPosition ){
        int incDec = params.x < desiredPosition ? 1 : -1;

        while( params.x < desiredPosition
                || params.x > desiredPosition ){

            params.x += incDec;
            windowManager.updateViewLayout( bubble, params );
        }
    }

    private void updateWindowViews(){
        Bitmap bitmap = ((BitmapDrawable) ((ImageView) bubble.findViewById(R.id.cimv_profile)).getDrawable())
                .getBitmap();
        String text = ((TextView) bubble.findViewById(R.id.tv_message)).getText().toString();

        Context context = bubble.getContext();
        int layout = isInRightSide ? R.layout.bubble_notification_right : R.layout.bubble_notification_left;
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                Util.getDpsToPixels( 166 ),
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        RelativeLayout view = (RelativeLayout) LayoutInflater.from(context).inflate( layout, null, false );
        view.setLayoutParams( lp );

        ((ImageView) view.findViewById(R.id.cimv_profile)).setImageBitmap( bitmap );
        ((TextView) view.findViewById(R.id.tv_message)).setText( text );

        bubble.removeAllViews();
        bubble.addView( view );
    }

    private void callActivityIfClicked(){
        if( isClicked ){
            Intent intent = new Intent( bubble.getContext(), MainActivity.class );
            intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
            bubble.getContext().startActivity( intent );
        }
    }

    public RelativeLayout getBubble() {
        return bubble;
    }

    public void setBubble(RelativeLayout bubble) {
        this.bubble = bubble;
        this.bubble.setOnTouchListener( this );
    }

    public WindowManager.LayoutParams getParams() {
        return params;
    }

    public void setParams() {
        this.params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSPARENT
        );

        this.params.gravity = Gravity.TOP | Gravity.LEFT;
        this.params.x = 0;
        this.params.y = 200;
    }

    public void updateBubbleView( Message message ){
        ((TextView) bubble.findViewById(R.id.tv_message)).setText( message.getMessage() );

        Picasso.with( bubble.getContext() )
                .load( message.getUser().getImage() )
                .into( ((ImageView) bubble.findViewById(R.id.cimv_profile)) );
    }
}
