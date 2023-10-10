package es.ucm.fdi.educavial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import es.ucm.fdi.educavial.ml.Model;
import com.google.common.util.concurrent.ListenableFuture;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class EscanearActivity extends AppCompatActivity {
    private PreviewView previewView;
    private Camera camera;
    private Senalviewmodel viewModel;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ScaleGestureDetector scaleGestureDetector;
    private ImageButton scan;
    private int analysisSize = 128;
    private AlertDialog dialog;
    private TextView message, title;
    private final static String TAG = "ReproducirActivity";

    private boolean sol = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_escanear);

        viewModel= ViewModelProviders.of(this).get(Senalviewmodel.class);

        Log.d(TAG, "Creando actionBar");
        ActionBar actionBar =getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setHomeAsUpIndicator(R.drawable.volver);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.escanear);
        }

        if (ActivityCompat.checkSelfPermission(EscanearActivity.this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            // You can directly ask for the permission.
            ActivityCompat.requestPermissions(EscanearActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
        }
        startCamera();
        scan=findViewById(R.id.scanButton);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                analyze();
            }
        });

        Log.d(TAG, "Creando mensaje de ayuda");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_alert_dialog, null);
        message = (TextView) customLayout.findViewById(R.id.help_text);
        title = (TextView) customLayout.findViewById(R.id.help_title);
        message.setText(R.string.alert_scanner_text);
        title.setText(R.string.alert_title);
        builder.setView(customLayout);
        dialog = builder.create();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
             startCamera();
            }
            else{
                Toast.makeText(this, "Esta aplicación necesita utilizar la cámara para fucionar.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void startCamera(){
        previewView = findViewById(R.id.previewView);
        cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().setTargetResolution(new Size(1920,1080)).build();
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();
                camera = cameraProvider.bindToLifecycle(
                        ((LifecycleOwner) this),
                        cameraSelector,
                        preview);

                preview.setSurfaceProvider(
                        previewView.getSurfaceProvider());

            } catch (InterruptedException | ExecutionException e) {
            }
        }, ContextCompat.getMainExecutor(this));
        scaleGestureDetector=new ScaleGestureDetector(this, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(@NonNull ScaleGestureDetector detector) {
                float ratio=camera.getCameraInfo().getZoomState().getValue().getZoomRatio()*detector.getScaleFactor();
                camera.getCameraControl().setZoomRatio(ratio);
                return true;
            }

            @Override
            public boolean onScaleBegin(@NonNull ScaleGestureDetector detector) {
                return true;
            }

            @Override
            public void onScaleEnd(@NonNull ScaleGestureDetector detector) {

            }
        });
        previewView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scaleGestureDetector.onTouchEvent(event);
                if (event.getAction()==MotionEvent.ACTION_DOWN){
                    camera.getCameraControl().startFocusAndMetering(
                    new FocusMeteringAction.Builder(previewView.getMeteringPointFactory().createPoint(event.getX(),event.getY()),FocusMeteringAction.FLAG_AF).
                            setAutoCancelDuration(5, TimeUnit.SECONDS).
                            build()
                    );
                }
                return true;
            }
        });
    }
    public void analyze() {
        ImageView imageView = findViewById(R.id.imageView2);
        Bitmap bitmap = previewView.getBitmap();
        Bitmap senal = Bitmap.createBitmap(imageView.getWidth(),imageView.getHeight(), Bitmap.Config.ARGB_8888);
        int[] pixels= new int[imageView.getWidth()*imageView.getHeight()];
        bitmap.getPixels(pixels,0,senal.getWidth(),imageView.getLeft(),imageView.getTop(),senal.getWidth(),senal.getHeight());
        senal.setPixels(pixels,0,senal.getWidth(),0,0,senal.getWidth(),senal.getHeight());
        String analysisResult= analizarSenal(senal);
        inflateSenalLayout(senal,analysisResult);
    }

    private void inflateSenalLayout(Bitmap senal, String analysisResult) {
        AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        View customLayout = getLayoutInflater().inflate(R.layout.escaneo_correcto_layout, null);
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        ImageView imageView = customLayout.findViewById(R.id.senalEscaneada);
        imageView.setImageBitmap(senal);
        TextView info = (TextView) customLayout.findViewById(R.id.info);
        TextView title = (TextView) customLayout.findViewById(R.id.resultado);
        title.setText("");
        info.setText(analysisResult);
        if(sol){
            Senal s = viewModel.getSenalBycodigo(analysisResult);
            if(s != null){
                title.setText(s.nombre);
                info.setText(s.descripcion);
            }
        }


        Button retur = (Button) customLayout.findViewById(R.id.escanear_volver);

        dialog.show();
        retur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
    private String analizarSenal(Bitmap senal){
        String ret = "";
        Bitmap scaledSignal = Bitmap.createScaledBitmap(senal,analysisSize,analysisSize,false);
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * analysisSize*analysisSize*3);
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues = new int[analysisSize*analysisSize];
        scaledSignal.getPixels(intValues,0,scaledSignal.getWidth(),0,0,scaledSignal.getWidth(),scaledSignal.getHeight());
        int pixelIndex =0;
        for (int i=0;i<analysisSize;i++){
            for (int j=0;j<analysisSize;j++){
                int val = intValues[pixelIndex++];
                byteBuffer.putFloat(((val>>16)&0xFF)*(1.f));
                byteBuffer.putFloat(((val>>8)&0xFF)*(1.f));
                byteBuffer.putFloat((val&0xFF)*(1.f));
            }
        }
        try {
            Model model = Model.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 128, 128, 3}, DataType.FLOAT32);

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
            float[] confidence = outputFeature0.getFloatArray();
            int max=0;
            float maxConfidence=0;
            for (int i =0;i<confidence.length;i++){
                if(confidence[i]>maxConfidence){
                    maxConfidence=confidence[i];
                    max=i;
                }
            }
            String[] classes={"R-1",
                    "R-2",
                    "R-100",
                    "R-101",
                    "R-105",
                    "R-106",
                    "R-107",
                    "R-108",
                    "R-111",
                    "R-113",
                    "R-114",
                    "R-116",
                    "R-117",
                    "R-200",
                    "R-201",
                    "R-205",
                    "R-300",
                    "R-301-30",
                    "R-301-50",
                    "R-301-70",
                    "R-301-90",
                    "R-301-100",
                    "R-301-120",
                    "R-302",
                    "R-303",
                    "R-304",
                    "R-305",
                    "R-307",
                    "R-308",
                    "R-400a",
                    "R-400b",
                    "R-400c",
                    "R-400d",
                    "R-402",
                    "R-407a",
                    "R-411",
                    "R-413",
                    "P-1",
                    "P-1a",
                    "P-1b",
                    "P-1c",
                    "P-3",
                    "P-4",
                    "P-13a",
                    "P-13b",
                    "P-14a",
                    "P-14b",
                    "P-15",
                    "P-15a",
                    "P-17",
                    "P-19",
                    "P-20",
                    "P-21",
                    "P-23",
                    "P-24",
                    "P-25",
                    "P-26",
                    "P-27",
                    "P-34",
                    "P-50",
                    "S-1",
                    "S-1a",
                    "S-2",
                    "S-2a",
                    "S-5",
                    "S-11",
                    "S-11a",
                    "S-13",
                    "S-15a",
                    "S-17",
                    "S-18",
                    "S-19",
                    "S-22",
                    "S-24",
                    "S-25",
                    "S-26a",
                    "S-26b",
                    "S-26c",
                    "S-50a",
                    "S-50b",
                    "S-52b",
                    "S-60b",
                    "S-61a",
                    "S-102",
                    "S-105",
                    "S-107",
                    "S-122",
                    "S-123",
                    "Unknown",
                    "S-7-40",
                    "S-7-80",
                    "S-17-minus",
                    "R-301-20",
                    "R-301-40",
                    "R-301-60",
                    "R-301-80",
                    "R-403a",
                    "R-401a",
                    "R-403c"
            };
            if (maxConfidence<0.9) ret="No se ha podido reconocer la señal, prueba otra vez.";
            else{
                sol = true;
                ret = classes[max];
            }
            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
        return ret;
    }



    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.help:
                dialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}