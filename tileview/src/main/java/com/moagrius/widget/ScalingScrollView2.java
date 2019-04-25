package com.moagrius.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ScaleGestureDetector;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import java.lang.ref.WeakReference;

public class ScalingScrollView2 extends FrameLayout {

  public enum MinimumScaleMode {CONTAIN, COVER, NONE}

  private ScaleGestureDetector mScaleGestureDetector;
  private ScaleChangedListener mScaleChangedListener;

  private ZoomScrollAnimator mZoomScrollAnimator;

  private MinimumScaleMode mMinimumScaleMode = MinimumScaleMode.COVER;

  private float mScale = 1f;
  private float mMinScale = 0f;
  private float mMaxScale = 1f;
  private float mEffectiveMinScale = 0f;

  private boolean mWillHandleContentSize;
  private boolean mShouldVisuallyScaleContents;
  private boolean mShouldLoopScale = true;

  public ScalingScrollView2(Context context) {
    super(context);
  }

  public ScalingScrollView2(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public ScalingScrollView2(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public void setScaleChangedListener(ScaleChangedListener scaleChangedListener) {
    mScaleChangedListener = scaleChangedListener;
  }

  public float getScale() {
    return mScale;
  }

  public void setScale(float scale) {
    mScale = scale;
  }



















  public interface ScaleChangedListener {
    void onScaleChanged(ScalingScrollView2 scalingScrollView, float currentScale, float previousScale);
  }

  /**
   * @author Mike Dunn, 2/2/18.
   */

  private static class ZoomScrollAnimator extends ValueAnimator implements ValueAnimator.AnimatorUpdateListener {

    private WeakReference<ScalingScrollView2> mScalingScrollViewWeakReference;
    private ZoomScrollAnimator.ScaleAndScrollState mStartState = new ZoomScrollAnimator.ScaleAndScrollState();
    private ZoomScrollAnimator.ScaleAndScrollState mEndState = new ZoomScrollAnimator.ScaleAndScrollState();
    private boolean mHasPendingZoomUpdates;
    private boolean mHasPendingScrollUpdates;

    public ZoomScrollAnimator(ScalingScrollView2 scalingScrollView) {
      super();
      addUpdateListener(this);
      setFloatValues(0f, 1f);
      setInterpolator(new ZoomScrollAnimator.QuinticInterpolator());
      mScalingScrollViewWeakReference = new WeakReference<>(scalingScrollView);
    }

    private boolean setupScrollAnimation(int x, int y) {
      ScalingScrollView2 scalingScrollView = mScalingScrollViewWeakReference.get();
      if (scalingScrollView != null) {
        mStartState.x = scalingScrollView.getScrollX();
        mStartState.y = scalingScrollView.getScrollY();
        mEndState.x = x;
        mEndState.y = y;
        return mStartState.x != mEndState.x || mStartState.y != mEndState.y;
      }
      return false;
    }

    private boolean setupZoomAnimation(float scale) {
      ScalingScrollView2 scalingScrollView = mScalingScrollViewWeakReference.get();
      if (scalingScrollView != null) {
        mStartState.scale = scalingScrollView.getScale();
        mEndState.scale = scale;
        return mStartState.scale != mEndState.scale;
      }
      return false;
    }

    public void animate(int x, int y, float scale) {
      ScalingScrollView2 scalingScrollView = mScalingScrollViewWeakReference.get();
      if (scalingScrollView != null) {
        mHasPendingZoomUpdates = setupZoomAnimation(scale);
        mHasPendingScrollUpdates = setupScrollAnimation(x, y);
        if (mHasPendingScrollUpdates || mHasPendingZoomUpdates) {
          start();
        }
      }
    }

    public void animateZoom(float scale) {
      ScalingScrollView2 scalingScrollView = mScalingScrollViewWeakReference.get();
      if (scalingScrollView != null) {
        mHasPendingZoomUpdates = setupZoomAnimation(scale);
        if (mHasPendingZoomUpdates) {
          start();
        }
      }
    }

    public void animateScroll(int x, int y) {
      ScalingScrollView2 scalingScrollView = mScalingScrollViewWeakReference.get();
      if (scalingScrollView != null) {
        mHasPendingScrollUpdates = setupScrollAnimation(x, y);
        if (mHasPendingScrollUpdates) {
          start();
        }
      }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
      ScalingScrollView2 scalingScrollView = mScalingScrollViewWeakReference.get();
      if (scalingScrollView != null) {
        float progress = (float) animation.getAnimatedValue();
        if (mHasPendingZoomUpdates) {
          float scale = mStartState.scale + (mEndState.scale - mStartState.scale) * progress;
          scalingScrollView.setScale(scale);
        }
        if (mHasPendingScrollUpdates) {
          int x = (int) (mStartState.x + (mEndState.x - mStartState.x) * progress);
          int y = (int) (mStartState.y + (mEndState.y - mStartState.y) * progress);
          scalingScrollView.scrollTo(x, y);
        }
      }
    }

    private static class ScaleAndScrollState {
      public int x;
      public int y;
      public float scale;
    }

    // https://android.googlesource.com/platform/frameworks/support/+/master/v7/recyclerview/src/main/java/android/support/v7/widget/RecyclerView.java#514
    private static class QuinticInterpolator implements Interpolator {
      @Override
      public float getInterpolation(float t) {
        t -= 1.0f;
        return t * t * t * t * t + 1.0f;
      }
    }
  }


}
