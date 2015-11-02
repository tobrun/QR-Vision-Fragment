# QR-Vision-Fragment
Android AAR library to scan QR codes using the mobile vision API


```java
public class SampleActivity extends AppCompatActivity implements QRScanFragment.OnScanCompleteListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

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

```
