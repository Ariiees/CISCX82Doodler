package com.example.ciscx82doodler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

public class DooleView extends View {

    private Paint paint;
    private Path path;
    private Bitmap bitmap;
    private Canvas canvas;

    public DooleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        path = new Path();

        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        paint.setAlpha(255);
        paint.setStrokeCap(Paint.Cap.ROUND);

        setWillNotDraw(false);
    }

    public void setBrushSize(float size) {
        paint.setStrokeWidth(size);
    }

    public void setBrushColor(int color) {
        paint.setColor(color);
    }

    public void setBrushOpacity(int alpha) {
        paint.setAlpha(alpha);
    }

    public void clearCanvas() {
        if (bitmap != null) {
            canvas.drawColor(Color.WHITE);
            invalidate();
        }
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.drawPath(path, paint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                break;
            case MotionEvent.ACTION_UP:
                canvas.drawPath(path, paint);
                path.reset();
                break;
        }
        invalidate();
        return true;
    }
}
