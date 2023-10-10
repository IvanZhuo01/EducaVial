package es.ucm.fdi.educavial;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PerfilActivity extends AppCompatActivity {

    private final static String TAG = "PerfilActivity";
    private TextView message, title, user, email;
    private AlertDialog dialog,dialog1;
    private Button profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_perfil);

        Log.d(TAG, "Creando actionBar");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setHomeAsUpIndicator(R.drawable.volver);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.profile_title);
        }

        Log.d(TAG, "Creando mensaje de ayuda");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_alert_dialog, null);
        message = (TextView) customLayout.findViewById(R.id.help_text);
        title = (TextView) customLayout.findViewById(R.id.help_title);
        message.setText(R.string.alert_profile_text);
        title.setText(R.string.alert_title);
        builder.setView(customLayout);
        dialog = builder.create();

        dialog.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        SharedPreferences prefs = getSharedPreferences("my_prefs", MODE_PRIVATE);

        profile = findViewById(R.id.EditarPerfil);
        Button listaSenal=findViewById(R.id.listaSenales);
        listaSenal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ListaSenalesActivity.class);
                startActivity(i);
            }
        });
        AppCompatButton tutorial_ad=findViewById(R.id.rewatchTutorial);
        tutorial_ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), TutorialActivity.class);
                startActivity(i);
            }
        });
        String correo = prefs.getString("correo", "");
        String usuario = prefs.getString("usuario", "");


        email = findViewById(R.id.user_email);
        user = findViewById(R.id.user_name);

        email.setText(correo);
        user.setText(usuario);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(PerfilActivity.this, R.style.MyAlertDialogStyle);
                View customLayout1 = getLayoutInflater().inflate(R.layout.custom_alert_dialog_profile, null);
                builder1.setView(customLayout1);

                boolean isFirstTime = prefs.getBoolean("is_first_time", true);
                final EditText coro = customLayout1.findViewById(R.id.editTextTextEmailAddress);
                final EditText usu = customLayout1.findViewById(R.id.editTextTextPersonName);
                coro.setText(prefs.getString("correo",""));
                usu.setText(prefs.getString("usuario",""));

                builder1.setPositiveButton("Aceptar",null );
                dialog1 = builder1.create();
                View.OnClickListener myListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Guardar el correo y usuario ingresados
                        String correo = coro.getText().toString();
                        String usuario = usu.getText().toString();

                        if (TextUtils.isEmpty(correo)) {
                            Toast.makeText(PerfilActivity.this, "Los campos no pueden estar vacíos", Toast.LENGTH_SHORT).show();

                        } else if (TextUtils.isEmpty(usuario)) {
                            Toast.makeText(PerfilActivity.this, "Los campos no pueden estar vacíos", Toast.LENGTH_SHORT).show();
                            
                        } else {
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("correo", correo);
                            editor.putString("usuario", usuario);
                            editor.putBoolean("is_first_time", false);
                            editor.apply();
                            recreate();

                            dialog1.dismiss();
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

                    dialog1.show();
                }
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
}