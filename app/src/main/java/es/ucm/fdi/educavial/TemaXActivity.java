package es.ucm.fdi.educavial;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.Locale;

public class TemaXActivity extends AppCompatActivity {

    private final static int MAX_BAR_VALUE = 100;
    private final static String TAG = "TemaXActivity";
    private TextView message, title, titulo, texto;
    private Button ant, sig;
    private AlertDialog dialog, endDialog;
    private int pos, barValue;
    private ImageButton play;
    private ArrayList<String> cont = new ArrayList<String>();
    private ArrayList<String> contTitulo = new ArrayList<String>();
    private ArrayList<Integer> img = new ArrayList<Integer>();
    private ShapeableImageView imagen;
    private ProgressBar bar;
    TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_tema_xactivity);

        Log.d(TAG, "Creando actionBar");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setHomeAsUpIndicator(R.drawable.volver);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.theme_1);
        }

        Log.d(TAG, "Inicializando el progress Bar");
        bar = findViewById(R.id.progressBar);
        bar.setScaleY(2.5f);
        bar.setMax(MAX_BAR_VALUE);
        bar.setProgressTintList(ColorStateList.valueOf(Color.BLACK));

        Log.d(TAG, "Inicializando el contenido del temario");
        img.add(R.drawable.stop);
        img.add(R.drawable.senal_v_30);
        img.add(R.drawable.p_anda);
        cont.add(getResources().getString(R.string.content_1_text));
        cont.add(getResources().getString(R.string.content_2_text));
        cont.add(getResources().getString(R.string.content_3_text));
        contTitulo.add(getResources().getString(R.string.content_1_title));
        contTitulo.add(getResources().getString(R.string.content_2_title));
        contTitulo.add(getResources().getString(R.string.content_3_title));

        pos = 0;
        barValue = MAX_BAR_VALUE / cont.size();

        Log.d(TAG, "Inicializando imagen, título y texto");
        imagen = findViewById(R.id.imagen);
        titulo = findViewById(R.id.titulo);
        texto = findViewById(R.id.texto);
        imagen.setImageResource(R.drawable.stop);
        titulo.setText(R.string.content_1_title);
        texto.setText(R.string.content_1_text);

        Log.d(TAG, "Inicializando el botón anterior");
        ant = findViewById(R.id.ant);
        ant.setEnabled(false);
        ant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Botón anterior pulsado");
                stopRepro();
                pos--;
                if(pos == 0){
                    ant.setEnabled(false);
                }
                changeContent();
                bar.setProgress(bar.getProgress() - barValue);
            }
        });

        Log.d(TAG, "Inicializando el botón siguiente");
        sig = findViewById(R.id.sig);
        sig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Botón siguiente pulsado");
                stopRepro();
                pos++;
                bar.setProgress( bar.getProgress() + barValue);
                if(pos == cont.size()){
                    endDialog.show();
                }
                if(pos < cont.size()) {
                    ant.setEnabled(true);
                    changeContent();

                }
            }
        });

        Log.d(TAG, "Inicializando Text To Speech");
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i!=TextToSpeech.ERROR){
                    textToSpeech.setLanguage(Locale.getDefault());
                    textToSpeech.setSpeechRate(0.7f);
                }
            }
        });

        Log.d(TAG, "Inicializando el botón de reproducir");
        play = findViewById(R.id.reproducir);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Botón de reproducir pulsado");
                if(textToSpeech != null && textToSpeech.isSpeaking()){
                    textToSpeech.stop();
                }
                else{
                    Bundle params = new Bundle();
                    params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "hello");
                    textToSpeech.speak(titulo.getText().toString() + texto.getText().toString(), TextToSpeech.QUEUE_FLUSH, params, "hello");
                }
            }
        });

        Log.d(TAG, "Mostrar/Ocultar el botón de reproducir en funcion del valor de authomatic_read");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean showButton = sharedPreferences.getBoolean("authomatic_read", false);
        if(showButton)
            play.setVisibility(View.VISIBLE);
        else
            play.setVisibility(View.INVISIBLE);

        Log.d(TAG, "Creando mensaje del fin");
        AlertDialog.Builder aux = new AlertDialog.Builder(this);
        aux.setTitle(R.string.congratulation);
        aux.setMessage(R.string.cong_info);
        aux.setPositiveButton(R.string.return_to_syllabus, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        endDialog = aux.create();
        endDialog.setCancelable(false);

        Log.d(TAG, "Creando mensaje de ayuda");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_alert_dialog, null);
        message = (TextView) customLayout.findViewById(R.id.help_text);
        title = (TextView) customLayout.findViewById(R.id.help_title);
        message.setText(R.string.alert_theme_text);
        title.setText(R.string.alert_title);
        builder.setView(customLayout);
        dialog = builder.create();
    }

    private void changeContent(){
        titulo.setText(contTitulo.get(pos));
        texto.setText(cont.get(pos));
        imagen.setImageResource(img.get(pos));
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

    private void stopRepro(){
        if(textToSpeech != null){
            textToSpeech.shutdown();
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if(textToSpeech != null){
            textToSpeech.shutdown();
        }
    }
}