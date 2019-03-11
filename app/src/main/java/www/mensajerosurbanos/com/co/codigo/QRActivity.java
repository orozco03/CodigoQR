package www.mensajerosurbanos.com.co.codigo;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission_group.CAMERA;

public class QRActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private ZXingScannerView miScannerView;
    private Button btnQR, btnGenerar;
    private ImageView imageView;
    private TextView UIDText;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    private final int REQUEST_ACCESS_FINE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        btnQR = findViewById(R.id.btn_QR);
        btnGenerar = findViewById(R.id.btn_Generar);
        imageView = findViewById(R.id.imageViewQR);
        UIDText = findViewById(R.id.UIDTextVew);

        GenerarQR();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null){
            String uid = user.getUid();

            UIDText.setText(uid);
        }

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user !=null){
                    setUserData(user);
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA )!= PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new  String[]{Manifest.permission.CAMERA},REQUEST_ACCESS_FINE);
    }

    private void GenerarQR() {
        btnGenerar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generarCode();
            }
        });
    }

    private void generarCode() {
        String UID = UIDText.getText().toString();
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        try {
            BitMatrix bitMatrix =  multiFormatWriter.encode("Resultado: "+ UID, BarcodeFormat.QR_CODE,2000,2000);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imageView.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void setUserData(FirebaseUser user) {
        UIDText.setText(user.getUid());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_ACCESS_FINE){
            if (grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED ){
                Toast.makeText(getApplicationContext(),R.string.bien,Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(),R.string.permised_not,Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void btnEscanear(View view) {
        miScannerView = new ZXingScannerView(this);
        setContentView(miScannerView);
        miScannerView.setResultHandler(this);
        miScannerView.startCamera();
    }

    @Override
    public void handleResult(Result result) {

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Resultado Del Scanner");
        alertDialog.setMessage(result.getText());
        alertDialog.setIcon(R.drawable.ic_qr);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Scanner leído", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialog.show();
        miScannerView.resumeCameraPreview(this);
    }
}
