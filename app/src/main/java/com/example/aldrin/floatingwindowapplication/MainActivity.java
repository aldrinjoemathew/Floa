package com.example.aldrin.floatingwindowapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button btnSticky;
    private Context mContext;
    private TextView tvHeadsUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        btnSticky = (Button) findViewById(R.id.btn_sticky);
        tvHeadsUp = (TextView) findViewById(R.id.tv_heads_up);
        tvHeadsUp.setText(getString(R.string.heads_up));
        if (Build.VERSION.SDK_INT > 22) {
            if (Settings.canDrawOverlays(this)) {
                btnSticky.setClickable(true);
            } else {
                buildAlertMessageNoOverlayPermission();
            }
        }

        btnSticky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, FloatingService.class);
                startService(intent);
                finish();
            }
        });
    }

    /**
     * To show an alert message to enable GPS.
     */
    private void buildAlertMessageNoOverlayPermission() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable draw over other apps permission")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        finish();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
