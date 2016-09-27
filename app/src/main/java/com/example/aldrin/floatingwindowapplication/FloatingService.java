package com.example.aldrin.floatingwindowapplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class FloatingService extends Service {

    private WindowManager windowManager;
    private ImageView chatHead;
    private LinearLayout mRootLayout;
    private Context mContext;
    private ImageView ivClose;
    private EditText etContent;
    private ImageView ivMinimize;

    private int mPrevDragX;
    private int mPrevDragY;
    private Boolean mShowContent = true;

    final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT);

    public FloatingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Typeface myCustomFont = Typeface.createFromAsset(getAssets(), "fonts/florence.ttf");
        mContext = this;
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mRootLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.layout_note, null);
        chatHead = (ImageView) mRootLayout.findViewById(R.id.iv_show);
        etContent = (EditText) mRootLayout.findViewById(R.id.et_content);
        ivClose = (ImageView) mRootLayout.findViewById(R.id.iv_close_note);
        ivMinimize = (ImageView) mRootLayout.findViewById(R.id.iv_minimize);
        etContent.setTypeface(myCustomFont);
        chatHead.setOnTouchListener(new HeadTouchListener());
        params.gravity = Gravity.LEFT;
        params.x = 0;
        params.y = 100;

        windowManager.addView(mRootLayout, params);

        etContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                chatHead.animate().rotation(20);
                etContent.setCursorVisible(true);
                params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
                windowManager.updateViewLayout(mRootLayout, params);
                return false;
            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                windowManager.removeViewImmediate(mRootLayout);
                stopSelf();
            }
        });

        ivMinimize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                chatHead.animate().rotation(0);
                etContent.setCursorVisible(false);
                etContent.setVisibility(View.GONE);
                ivClose.setVisibility(View.GONE);
                ivMinimize.setVisibility(View.GONE);
                windowManager.updateViewLayout(mRootLayout, params);
            }
        });
    }

    /**
     * Listens to the touch events on the tray.
     */
    private class HeadTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            final int action = event.getActionMasked();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    dragHead(action, (int)event.getRawX(), (int)event.getRawY());
                    break;
                default:
                    return false;
            }
            return true;
        }
    }


    /**
     * Drags the head as per touch info
     */
    private void dragHead(int action, int x, int y) {
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mPrevDragY = y;
                mPrevDragX = x;
                break;

            case MotionEvent.ACTION_MOVE:
                float deltaY = y - mPrevDragY;
                float deltaX = x - mPrevDragX;
                if (deltaY<5 && deltaY>-5) {
                    if (deltaX>50) {
                        params.gravity = Gravity.RIGHT;
                    } else if (deltaX <-50) {
                        params.gravity = Gravity.LEFT;
                    } else {
                        break;
                    }
                }
                if (etContent.isShown()) {
                    params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                    chatHead.animate().rotation(0);
                    etContent.setVisibility(View.GONE);
                    ivClose.setVisibility(View.GONE);
                    ivMinimize.setVisibility(View.GONE);
                    windowManager.updateViewLayout(mRootLayout, params);
                } else {
                    mShowContent = false;
                }

                params.y += deltaY;
                mPrevDragY = y;
                windowManager.updateViewLayout(mRootLayout, params);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (etContent.isShown() || !mShowContent) {
                    params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                    chatHead.animate().rotation(0);
                    etContent.setCursorVisible(false);
                    windowManager.updateViewLayout(mRootLayout, params);
                    mShowContent = true;
                } else {
                    chatHead.animate().rotation(0);
                    etContent.setVisibility(View.VISIBLE);
                    ivClose.setVisibility(View.VISIBLE);
                    ivMinimize.setVisibility(View.VISIBLE);
                }
                break;
        }
    }
}
