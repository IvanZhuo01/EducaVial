package es.ucm.fdi.educavial;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.app.LoaderManager;
import androidx.preference.PreferenceManager;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import es.ucm.fdi.educavial.R;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageButton settings, profile, tutorial_ad;
    private Button learn, examination, scan;
    private AlertDialog dialog1;
    private final static String TAG = "MainActivity";

    private Senalviewmodel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);
        viewModel=ViewModelProviders.of(this).get(Senalviewmodel.class);
        Log.d(TAG, "Mostrar durante un segundo la pantalla con icono");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Log.d(TAG, "Ocultando el actionBar");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        Log.d(TAG, "Inicializando variables");
        settings = findViewById(R.id.ajustes);
        profile = findViewById(R.id.perfil);
        tutorial_ad = findViewById(R.id.tutorial_ad);
        learn = findViewById(R.id.aprender);
        examination = findViewById(R.id.evaluar);
        scan= findViewById(R.id.escanear);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                config();
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                perfil();
            }
        });



        Log.d(TAG, "Mostrar/Ocultar el botón de reproducir tutorial en funcion del valor de tutorial");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                if(s.equals("tutorial_ad")){
                    tutorial_btn(sharedPreferences);
                }
            }
        });
        tutorial_btn(sharedPreferences);

        tutorial_ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tutorial();
            }
        });

        learn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                temario();
            }
        });

        examination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                evaluar();
            }
        });
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {escanear();}
        });

        SharedPreferences prefs = getSharedPreferences("my_prefs", MODE_PRIVATE);
        boolean isFirstTime = prefs.getBoolean("is_first_time", true);
        AlertDialog.Builder tuto = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        View customLayoutTuto = getLayoutInflater().inflate(R.layout.custom_dialog_tutorial, null);
        tuto.setView(customLayoutTuto);
        tuto.setPositiveButton("Ver tutorial", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tutorial();
            }
        });
        tuto.setNegativeButton("Saltar tutorial", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        Dialog tutorial = tuto.create();
        tutorial.setCancelable(false);

        if (isFirstTime) {
            // La aplicación está siendo iniciada por primera vez
            // Mostrar un diálogo para solicitar correo y usuario
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
            View customLayout = getLayoutInflater().inflate(R.layout.custom_activity_dialog_main, null);
            builder.setView(customLayout);

            builder.setPositiveButton("Aceptar",null );
            dialog1 = builder.create();
            View.OnClickListener myListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText coro = customLayout.findViewById(R.id.editTextTextEmailAddress);
                    EditText usu = customLayout.findViewById(R.id.editTextTextPersonName);
                    // Guardar el correo y usuario ingresados
                    String correo = coro.getText().toString();
                    String usuario = usu.getText().toString();

                    if (TextUtils.isEmpty(correo)) {
                        Toast.makeText(MainActivity.this, "Los campos no pueden estar vacíos", Toast.LENGTH_SHORT).show();

                    } else if (TextUtils.isEmpty(usuario)) {
                        Toast.makeText(MainActivity.this, "Los campos no pueden estar vacíos", Toast.LENGTH_SHORT).show();

                    } else {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("correo", correo);
                        editor.putString("usuario", usuario);
                        editor.putBoolean("is_first_time", false);
                        editor.apply();
                        dialog1.dismiss();
                        tutorial.show();
                    }
                }

            };
            if (dialog1 != null) {
                dialog1.setCanceledOnTouchOutside(false);
                dialog1.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button b = dialog1.getButton(DialogInterface.BUTTON_POSITIVE);
                        b.setOnClickListener(myListener);
                    }
                });
            }
            dialog1.show();

        }


    }

    private void config(){
        Log.i(TAG, "Botón de configuraciones pulsado");
        Intent i = new Intent(this, AjustesActivity.class);
        startActivity(i);
    }

    private void perfil(){
        Log.i(TAG, "Botón de perfil pulsado");
        Intent i = new Intent(this, PerfilActivity.class);
        startActivity(i);
    }

    private void tutorial(){
        Log.i(TAG, "Botón de tutorial pulsado");
        Intent i = new Intent(this, TutorialActivity.class);
        startActivity(i);
    }

    private void temario(){
        Log.i(TAG, "Botón de aprender pulsado");
        Intent i = new Intent(this, TemarioActivity.class);
        startActivity(i);
    }

    private void evaluar(){
        Log.i(TAG, "Botón de evaluar pulsado");
        Intent i = new Intent(this, EvaluacionActivity.class);
        startActivity(i);
    }
    private void escanear(){
        Log.i(TAG, "Botón de escanear pulsado");
        Intent i = new Intent(this,EscanearActivity.class);
        startActivity(i);
    }

    private void tutorial_btn(SharedPreferences sharedPreferences){
        boolean showButton = sharedPreferences.getBoolean("tutorial_ad",false);
        if(showButton) {
            tutorial_ad.setVisibility(View.VISIBLE);
        }
        else{
            tutorial_ad.setVisibility(View.INVISIBLE);
        }
    }
}