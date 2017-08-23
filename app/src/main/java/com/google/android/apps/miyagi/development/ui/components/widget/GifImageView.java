package com.google.android.apps.miyagi.development.ui.components.widget;

/**
 * Created by jerzyw on 20.12.2016.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.SystemClock;
import android.support.annotation.DrawableRes;
import android.support.annotation.RawRes;
import android.util.AttributeSet;
import android.view.View;

import java.io.InputStream;


public class GifImageView extends View {

    private InputStream mInputStream;
    private Movie mMovie;
    private long mStart;
    private Context mContext;
    private float mMovieScale;

    /**
     * Creates new intance of GifImageView displaying GIF images.
     */
    public GifImageView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    /**
     * Creates new intance of GifImageView displaying GIF images.
     */
    public GifImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    /**
     * Creates new intance of GifImageView displaying GIF images.
     */
    public GifImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
        setFocusable(true);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mMovie != null) {
            int height = MeasureSpec.getSize(heightMeasureSpec);
            int drawAreaHeight = height - getPaddingTop() - getPaddingBottom();
            float scaleX = (float) drawAreaHeight / mMovie.height();
            widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) (mMovie.width() * scaleX), MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        invalidateMovieScale();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        long now = SystemClock.uptimeMillis();

        if (mStart == 0) {
            mStart = now;
        }

        if (mMovie != null) {
            int duration = mMovie.duration();
            if (duration == 0) {
                duration = 1000;
            }

            int relTime = (int) ((now - mStart) % duration);
            mMovie.setTime(relTime);

            canvas.save();
            canvas.translate(getPaddingLeft(), getPaddingTop());
            canvas.scale(mMovieScale, mMovieScale);
            mMovie.draw(canvas, 0, 0);
            canvas.restore();

            invalidate();
        }
    }

    public void setGifImageResource(@RawRes @DrawableRes int gifDrawableId) {
        requestLayout();
        playGifMovie(gifDrawableId);
    }

    private void playGifMovie(@RawRes @DrawableRes int gifDrawableId) {
        mInputStream = mContext.getResources().openRawResource(gifDrawableId);
        mMovie = Movie.decodeStream(mInputStream);
        invalidateMovieScale();
        invalidate();
    }

    private void invalidateMovieScale() {
        if (mMovie != null) {
            int movieWidth = mMovie.width();
            int movieHeight = mMovie.height();

            int drawAreaWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
            int drawAreaHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();

            float scaleX = (float) drawAreaWidth / movieWidth;
            float scaleY = (float) drawAreaHeight / movieHeight;
            mMovieScale = Math.min(scaleX, scaleY);
        } else {
            mMovieScale = 1;
        }
    }
}
