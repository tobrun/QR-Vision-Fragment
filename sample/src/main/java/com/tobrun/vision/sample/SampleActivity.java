package com.tobrun.vision.sample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.tobrun.vision.qr.QRScanFragment;

public class SampleActivity extends AppCompatActivity implements QRScanFragment.OnScanCompleteListener {

    private static final int REQUEST_CODE_PERMISSION_CAMERA = 0x0000000;
    private boolean mPermissionAccepted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        // Check Camera permission
        int resultCode = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (resultCode == PackageManager.PERMISSION_GRANTED) {
            addQrScanFragment();
        } else {
            requestCameraPermission();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPermissionAccepted) {
            addQrScanFragment();
            mPermissionAccepted = false;
        }
    }

    private void addQrScanFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, new QRScanFragment())
                .commit();
    }

    @Override
    public void onScanComplete(@NonNull String qrCode) {
        Toast.makeText(this, qrCode, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCameraError() {
        Toast.makeText(this, "Camera error",Toast.LENGTH_SHORT).show();
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            // Permission not granted, add more context why permission is required
            Snackbar.make(findViewById(android.R.id.content), R.string.request_code_extra_context,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(SampleActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    REQUEST_CODE_PERMISSION_CAMERA);
                        }
                    })
                    .show();
        } else {
            // Camera permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSION_CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_CAMERA:
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPermissionAccepted = true;
                } else {
                    requestCameraPermission();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
