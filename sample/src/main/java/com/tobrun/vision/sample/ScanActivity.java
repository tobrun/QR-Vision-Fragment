/*
 * Copyright (C) Tobrun Van Nuland
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tobrun.vision.sample;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.tobrun.vision.qr.QRScanFragment;

public class ScanActivity extends AppCompatActivity implements QRScanFragment.OnScanCompleteListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, new QRScanFragment())
                    .commit();
        }
    }

    @Override
    public void onScanComplete(@NonNull String qrCode) {
        Toast.makeText(this, qrCode, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onCameraError(@StringRes int errorTextRes) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.camera_error)
                .setMessage(errorTextRes)
                .setNeutralButton(R.string.camera_error_dismiss, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
    }
}
