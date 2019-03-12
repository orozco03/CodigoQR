package www.mensajerosurbanos.com.co.codigo;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;


public class QRActivity extends AppCompatActivity {

    private Button btnQR, btnGenerar, btnAtras;
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
        btnAtras = findViewById(R.id.btn_atras);
        imageView = findViewById(R.id.imageViewQR);
        UIDText = findViewById(R.id.UIDTextVew);

        //icono

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_icon);

        btnAtras();
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
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan a barcode");
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(true);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }


    public void handleResult(String mensaje) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Resultado Del Scanner");
        alertDialog.setMessage(mensaje);
        alertDialog.setIcon(R.drawable.ic_qr);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "QR leído", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "No fue posible capturar el código QR", Toast.LENGTH_LONG).show();
            } else {
                result.getContents();
                handleResult(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void btnAtras(){
        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
}
