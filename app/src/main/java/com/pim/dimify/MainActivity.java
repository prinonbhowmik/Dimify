package com.pim.dimify;

import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import me.tankery.lib.circularseekbar.CircularSeekBar;

public class MainActivity extends AppCompatActivity {

    private boolean flashLightStatus = false;
    private CircleImageView flashicon;
    private CircularSeekBar seekBar;
    private int b,B;
    private TextView textView;
    private AdView adView;
    private Context context;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flashicon = findViewById(R.id.flashicon);
        seekBar = findViewById(R.id.progress_circular);
        textView = findViewById(R.id.percentTv);
        adView=findViewById(R.id.adView);
        b= Settings.System.getInt(getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS,0);
        B = (int) (b/2.55);

        textView.setText(""+B+"%");
        seekBar.setProgress(b);
        seekBar.setMax(255);

        if (!flashLightStatus){
            flashOn();
        }else{
            flashOff();
        }

        flashicon.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                if (!flashLightStatus){
                    flashOn();
                }else{
                    flashOff();
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
                context=getApplicationContext();

                if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.M){
                    boolean canWrite=Settings.System.canWrite(context);
                    if(canWrite){
                        b= (int) (progress/2.55);
                        textView.setText(""+b+"%");
                        Settings.System.putInt(context.getContentResolver(),
                                Settings.System.SCREEN_BRIGHTNESS_MODE,
                                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                        Settings.System.putInt(context.getContentResolver(),
                                Settings.System.SCREEN_BRIGHTNESS, (int) progress);
                    }else {
                        Intent intent=new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
            }
            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {
            }
        });

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void flashOff() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraId, false);
            }
            flashLightStatus = false;
            flashicon.setImageResource(R.drawable.ic_flashlight_off);
            Toasty.custom(this,"Flashlight Off!",
                    R.drawable.ic_flashlight_off,R.color.themeColor,
                    1000,true,true).show();
        } catch (Exception e) {
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void flashOn() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraId, true);
            }
            flashLightStatus = true;
            flashicon.setImageResource(R.drawable.ic_flashlight);
            Toasty.custom(this,"Flashlight On!",
                    R.drawable.ic_flashlight,R.color.themeColor,
                    1000,true,true).show();

        } catch (Exception e) {
        }
    }


}