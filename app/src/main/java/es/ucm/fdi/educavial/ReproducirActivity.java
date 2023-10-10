package es.ucm.fdi.educavial;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

public class ReproducirActivity extends AppCompatActivity {
    private VideoView video;
    private AlertDialog dialog;
    private TextView message, title;
    private final static String TAG = "ReproducirActivity";
    private int currentPosition = 0; // save current position of video playback

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproducir);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //Inicializamos la clase VideoView asociandole el fichero de Video
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setHomeAsUpIndicator(R.drawable.volver);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.content_header);
        }

        video=(VideoView) findViewById(R.id.videoView);
        String path = "android.resource://" + getPackageName() + "/"+ R.raw.carreteras;
        video.setVideoURI(Uri.parse(path));

        MediaController media = new MediaController(this);
        media.setAnchorView(video);
        video.setMediaController(media);

        Log.d(TAG, "Creando mensaje de ayuda");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_alert_dialog, null);
        message = (TextView) customLayout.findViewById(R.id.help_text);
        title = (TextView) customLayout.findViewById(R.id.help_title);
        message.setText(R.string.alert_video_text);
        title.setText(R.string.alert_title);
        builder.setView(customLayout);
        dialog = builder.create();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getSupportActionBar().hide();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            getSupportActionBar().show();
        }
    }

    // save the current position of the video playback when the activity is paused
    @Override
    protected void onPause() {
        super.onPause();
        currentPosition = video.getCurrentPosition();
        video.pause();
    }

    // restore the saved position of the video playback when the activity is resumed
    @Override
    protected void onResume() {
        super.onResume();
        video.seekTo(currentPosition);
        video.start();
    }

    // save the current position of the video playback when the activity is stopped
    @Override
    protected void onStop() {
        super.onStop();
        currentPosition = video.getCurrentPosition();
        video.stopPlayback();
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
