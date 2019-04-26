package com.moagrius.view;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

public class DoubleTapGestureDetector {

  private int mDoubleTapSlopSquare;
  private static final int DOUBLE_TAP_TIMEOUT = ViewConfiguration.getDoubleTapTimeout();
  private GestureDetector.OnDoubleTapListener mDoubleTapListener;
  private MotionEvent mLastDownEvent;
  private long mLastDownTimestamp;

  public DoubleTapGestureDetector(Context context, GestureDetector.OnDoubleTapListener listener) {
    mDoubleTapListener = listener;
    int doubleTapSlop = ViewConfiguration.get(context).getScaledDoubleTapSlop();
    mDoubleTapSlopSquare = doubleTapSlop * doubleTapSlop;
  }

  public boolean onTouchEvent(MotionEvent event) {
    switch (event.getActionMasked()) {
      case MotionEvent.ACTION_DOWN:
        Log.d("DT", "action down");
        if (mLastDownEvent == null) {
          mLastDownEvent = event;
          mLastDownTimestamp = System.currentTimeMillis();
          return false;
        }
        // if not null, this could be the second tap
        // fist, see if it happened fast enough
        long elapsed = System.currentTimeMillis() - mLastDownTimestamp;
        if (elapsed > DOUBLE_TAP_TIMEOUT) {
          reset();
          return false;
        }
        // make sure the finger didn't wander too far
        final int deltaX = (int) (event.getY() - mLastDownEvent.getX());
        final int deltaY = (int) (event.getX() - mLastDownEvent.getY());
        int distance = (deltaX * deltaX) + (deltaY * deltaY);
        if (distance > mDoubleTapSlopSquare) {
          reset();
          return false;
        }
        // we made it this far, so it didn't wander and happened within proscribed delay
        // it's a double tap
        mDoubleTapListener.onDoubleTap(event);
        reset();
        return true;
      case MotionEvent.ACTION_CANCEL:
        reset();
        return false;
    }
    return false;
  }

  private void reset() {
    mLastDownTimestamp = 0;
    mLastDownEvent = null;
  }

}


