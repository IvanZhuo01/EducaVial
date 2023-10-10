package es.ucm.fdi.educavial;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import es.ucm.fdi.educavial.R;

public class TemarioActivity extends AppCompatActivity {

    private final static String TAG = "TemarioActivity";
    private TextView message, title;
    private AlertDialog dialog;
    private Button tema1, tema2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_temario);

        Log.d(TAG, "Creando actionBar");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setHomeAsUpIndicator(R.drawable.volver);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.syllabus_title);
        }

        Log.d(TAG, "Creando mensaje de ayuda");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_alert_dialog, null);
        message = (TextView) customLayout.findViewById(R.id.help_text);
        title = (TextView) customLayout.findViewById(R.id.help_title);
        message.setText(R.string.alert_syllabus_text);
        title.setText(R.string.alert_title);
        builder.setView(customLayout);
        dialog = builder.create();

        Log.d(TAG, "Añadiendo listeners a cada tema");
        tema2 = findViewById(R.id.theme_2);
        tema2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tema2();
            }
        });

        tema1 = findViewById(R.id.theme_1);
        tema1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tema1();
            }
        });
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

    private void tema2(){
        Log.d(TAG, "Tema 2 pulsado");
        Intent i = new Intent(this, ReproducirActivity.class);
        startActivity(i);
    }

    private void tema1(){
        Log.d(TAG, "Tema 1 pulsado");
        Intent i = new Intent(this, TemaXActivity.class);
        startActivity(i);
    }
}