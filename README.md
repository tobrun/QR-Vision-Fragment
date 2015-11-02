# QR-Vision-Fragment

## What
Fragment library wrapper around Google's mobile vision API.

## Why


## How
 -  add `QRScanFragment`
 
   ```java
    getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, new QRScanFragment())
                .commit();
    ```
                    
 -  add `QRScanFragment.OnScanCompleteListener` interface 

    ```java
     Activity implements QRScanFragment.OnScanCompleteListener
    ```
    
Full sample code can be found in [ScanQRCodeActivity](https://github.com/tobrun/QR-Vision-Fragment/blob/master/sample/src/main/java/com/tobrun/vision/sample/ScanQRCodeActivity.java)
