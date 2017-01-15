package br.com.thiengo.pockerhijack.domain;

import android.view.WindowManager;
import android.widget.RelativeLayout;

import java.util.List;

/**
 * Created by viniciusthiengo on 15/01/17.
 */

public class Notification {
    private boolean isInRightSide = false;
    private boolean isClicked = false;
    private RelativeLayout bubble;
    private WindowManager.LayoutParams params;

    public boolean isInRightSide() {
        return isInRightSide;
    }

    public void setInRightSide(boolean inRightSide) {
        isInRightSide = inRightSide;
    }

    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }

    public RelativeLayout getBubble() {
        return bubble;
    }

    public void setBubble(RelativeLayout bubble) {
        this.bubble = bubble;
    }

    public WindowManager.LayoutParams getParams() {
        return params;
    }

    public void setParams(WindowManager.LayoutParams params) {
        this.params = params;
    }
}
