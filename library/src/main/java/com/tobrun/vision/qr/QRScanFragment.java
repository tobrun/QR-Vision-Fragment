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
package com.tobrun.vision.qr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class QRScanFragment extends Fragment implements CameraSourcePreview.OnCameraErrorListener {

    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private OnScanCompleteListener mCallback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnScanCompleteListener) {
            mCallback = (OnScanCompleteListener) activity;
        } else {
            throw new ClassCastException("Activity hosting the QRScanFragment must implement the OnScanCompleteListener interface");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_barcode, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPreview = (CameraSourcePreview) view.findViewById(R.id.preview);
        mPreview.setOnCameraErrorListener(this);
        onCreateDetector(view);
    }

    private void onCreateDetector(@NonNull final View view) {
        final Context context = view.getContext().getApplicationContext();
        final BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context).build();
        barcodeDetector.setProcessor(new MultiProcessor.Builder<>(new MultiProcessor.Factory<Barcode>() {
            @Override
            public Tracker<Barcode> create(final Barcode barcode) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onScanComplete(barcode.displayValue);
                        mPreview.stop();
                    }
                });
                return new Tracker<>();
            }
        }).build());

        if (!barcodeDetector.isOperational()) {
            IntentFilter lowStorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            if (context.registerReceiver(null, lowStorageFilter) != null) {
                // Low storage
                mCallback.onCameraError(R.string.camera_error_low_storage);
            } else {
                // Native libs unavailable
                mCallback.onCameraError(R.string.camera_error_dependencies);
            }
            return;
        }

        final ViewTreeObserver observer = view.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                CameraSource.Builder builder = new CameraSource.Builder(context, barcodeDetector)
                        .setFacing(CameraSource.CAMERA_FACING_BACK)
                        .setRequestedPreviewSize(view.getMeasuredWidth(), view.getMeasuredHeight())
                        .setRequestedFps(30.0f);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    builder = builder.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                }

                mCameraSource = builder.build();
                startCameraSource();
            }
        });
    }

    public void restart() {
        startCameraSource();
    }

    @Override
    public void onResume() {
        super.onResume();
        startCameraSource();
    }

    private void startCameraSource() {
        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource);
            } catch (IOException e) {
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPreview != null) {
            mPreview.release();
        }
    }

    @Override
    public void onCameraError() {
        if (mCallback != null) {
            mCallback.onCameraError(R.string.camera_error_open);
        }
    }

    public interface OnScanCompleteListener {
        @UiThread
        void onScanComplete(@NonNull final String qrCode);

        @UiThread
        void onCameraError(@StringRes int errorTextRes);
    }
}