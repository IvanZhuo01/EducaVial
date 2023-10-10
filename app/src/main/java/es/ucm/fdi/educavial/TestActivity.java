package es.ucm.fdi.educavial;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import pl.droidsonroids.gif.GifImageView;

public class TestActivity extends AppCompatActivity {

    private final static String TAG = "TestActivity";
    private TextView message, title, endDialogText, endDialogTitle, exitDialogTitle, exitDialogText;
    private AppCompatButton bt1, bt2, bt3;
    private Button dialogBtn;
    private ImageButton ibt1, ibt2, ibt3;
    private View ov1,ov2,ov3;
    private AlertDialog dialog, endDialog, exitDialog;
    private Object ant, ant1;
    private GifImageView gif;
    private final AppCompatButton[] buttons= new AppCompatButton[3];
    private final ImageButton[] images=new ImageButton[3];
    private final String[] buttonTexts = new String[3];
    private final int[] drawableImages= new int[3];
    private boolean resultado = true;
    private int cont = 0;
     private Senalviewmodel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_test);

        Log.d(TAG, "Creando actionBar");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setHomeAsUpIndicator(R.drawable.volver);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.examination_title);
        }

        Log.d(TAG, "Inicializando los botones");
        buttonTexts[0]=getResources().getString(R.string.prohibido);
        buttonTexts[1]=getResources().getString(R.string.velocidad);
        buttonTexts[2]=getResources().getString(R.string.stop);
        drawableImages[0]=R.drawable.p_anda;
        drawableImages[1]=R.drawable.senal_v_30;
        drawableImages[2]=R.drawable.stop;

        bt1 = findViewById(R.id.button5);
        bt2 = findViewById(R.id.button6);
        bt3 = findViewById(R.id.button7);

        ibt1 = findViewById(R.id.botonSenal1);
        ibt2 = findViewById(R.id.botonSenal2);
        ibt3 = findViewById(R.id.botonSenal3);

        ov1 = findViewById(R.id.color_overlay1);
        ov2 = findViewById(R.id.color_overlay2);
        ov3 = findViewById(R.id.color_overlay3);

        buttons[0]=bt1;
        buttons[1]=bt2;
        buttons[2]=bt3;

        images[0]=ibt1;
        images[1]=ibt2;
        images[2]=ibt3;

        List<AppCompatButton> buttonList = new ArrayList<AppCompatButton>(Arrays.asList(buttons));
        Collections.shuffle(buttonList);
        for (int i = 0; i<buttonList.size();i++){
            buttonList.get(i).setText(buttonTexts[i]);
        }

        List<ImageButton> senalList = new ArrayList<ImageButton>(Arrays.asList(images));
        Collections.shuffle(senalList);
        for (int i = 0; i<senalList.size();i++){
            senalList.get(i).setImageResource(drawableImages[i]);
            senalList.get(i).setContentDescription(buttonTexts[i]);
        }

        addListener(bt1,ov1);
        addListener(bt2,ov2);
        addListener(bt3,ov3);
        addListener(ibt1,ov1);
        addListener(ibt2,ov2);
        addListener(ibt3,ov3);

        Log.d(TAG, "Creando mensaje de ayuda");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_alert_dialog, null);
        message = (TextView) customLayout.findViewById(R.id.help_text);
        title = (TextView) customLayout.findViewById(R.id.help_title);
        message.setText(R.string.alert_test_text);
        title.setText(R.string.alert_title);
        builder.setView(customLayout);
        dialog = builder.create();
        dialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        Log.d(TAG, "Creando mensaje del fin");
        AlertDialog.Builder aux = new AlertDialog.Builder(this);
        View customDialogLayout = getLayoutInflater().inflate(R.layout.custom_alert_dialog_test, null);
        gif = (GifImageView) customDialogLayout.findViewById(R.id.gif);
        gif.setImageResource(R.drawable.happy);
        endDialogTitle = (TextView) customDialogLayout.findViewById(R.id.title);
        endDialogText = (TextView) customDialogLayout.findViewById(R.id.text);
        dialogBtn = (Button) customDialogLayout.findViewById(R.id.button);
        dialogBtn.setText(R.string.cont);
        dialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endDialog.dismiss();
                finish();
            }
        });
        endDialogTitle.setText(R.string.cong_title);
        endDialogText.setText(R.string.cong_text);
        aux.setView(customDialogLayout);

        endDialog = aux.create();
        endDialog.setCanceledOnTouchOutside(false);

        Log.d(TAG, "Creando mensaje de salida");
        AlertDialog.Builder exit = new AlertDialog.Builder(this);
        exit.setPositiveButton(getResources().getString(R.string.cont), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        exit.setNegativeButton(getResources().getString(R.string.exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        exit.setTitle(R.string.exit_title);
        exit.setMessage(R.string.exit_text);
        exitDialog = exit.create();


        viewModel= ViewModelProviders.of(this).get(Senalviewmodel.class);

    }

    private void addListener(Object obj, Object obj1){
        if(obj instanceof AppCompatButton){
            AppCompatButton res = ((AppCompatButton)obj);
            res.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    disable(res);
                    if(res.isEnabled()){
                        res.setBackgroundResource(R.drawable.forma_boton_seleccionada);
                        res.setEnabled(false);


                    }

                    if(cont % 2 == 0){
                        if(res.getText().equals(((ImageButton)ant).getContentDescription())){
                            res.setBackgroundResource(R.drawable.forma_boton_correcto);
                            ((View) ant1).setBackgroundResource(R.drawable.forma_boton_correcto);

                        }
                        else{
                            res.setBackgroundResource(R.drawable.forma_boton_incorrecta);
                            ((View) ant1).setBackgroundResource(R.drawable.forma_boton_incorrecta);

                            resultado = false;
                        }
                        enable(res);
                    }
                    if(cont == 6){
                        fail();
                        endDialog.show();

                    }
                    ant = res;
                }
            });
        }
        else if(obj instanceof ImageButton){
            ImageButton res = ((ImageButton)obj);
            View res1 = ((View)obj1);
            res.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    disable(res);
                    if(res.isEnabled()){
                        res.setEnabled(false);
                        res1.setBackgroundResource(R.drawable.forma_boton_seleccionada);
                    }
                    if(cont % 2 == 0) {
                        if(res.getContentDescription().equals(((AppCompatButton)ant).getText())){
                            res1.setBackgroundResource(R.drawable.forma_boton_correcto);
                            ((AppCompatButton) ant).setBackgroundResource(R.drawable.forma_boton_correcto);
                            res.setBackgroundResource(R.drawable.forma_boton_correcto);

                        }
                        else{
                            res1.setBackgroundResource(R.drawable.forma_boton_incorrecta);
                            ((AppCompatButton) ant).setBackgroundResource(R.drawable.forma_boton_incorrecta);
                            res.setBackgroundResource(R.drawable.forma_boton_incorrecta);
                            resultado = false;

                        }
                        enable(res);
                    }
                    if(cont == 6){
                        fail();
                        endDialog.show();
                    }
                    ant = res;
                    ant1 = res1;
                }
            });
        }

    }

    private void disable(Object btn){
        if(btn instanceof ImageButton) {
            if (ibt1 != (btn) && !ibt1.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.forma_boton_seleccionada).getConstantState())) {
                ibt1.setEnabled(false);
            }

            if (ibt2 != (btn) && !ibt2.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.forma_boton_seleccionada).getConstantState())){
                ibt2.setEnabled(false);
            }
            if(ibt3 != (btn) && !ibt3.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.forma_boton_seleccionada).getConstantState())){
                ibt3.setEnabled(false);
            }
        }
        else if(btn instanceof Button){
            if(bt1 != (btn) && !bt1.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.forma_boton_seleccionada).getConstantState())){
                bt1.setEnabled(false);
            }
            if(bt2 != (btn) && !bt2.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.forma_boton_seleccionada).getConstantState())){
                bt2.setEnabled(false);
            }
            if(bt3 != (btn) && !bt3.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.forma_boton_seleccionada).getConstantState())){
                bt3.setEnabled(false);
            }
        }
        cont++;
    }

    private void enable(Object btn){
        if(!ibt1.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.forma_boton_seleccionada).getConstantState())
            && !ibt1.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.forma_boton_incorrecta).getConstantState())
            && !ibt1.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.forma_boton_correcto).getConstantState()))
            ibt1.setEnabled(true);
        if(!ibt2.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.forma_boton_seleccionada).getConstantState())
                && !ibt2.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.forma_boton_incorrecta).getConstantState())
                && !ibt2.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.forma_boton_correcto).getConstantState()))
            ibt2.setEnabled(true);
        if(!ibt3.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.forma_boton_seleccionada).getConstantState())
                && !ibt3.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.forma_boton_incorrecta).getConstantState())
                && !ibt3.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.forma_boton_correcto).getConstantState()))
            ibt3.setEnabled(true);
        if(!bt1.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.forma_boton_seleccionada).getConstantState())
                && !bt1.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.forma_boton_incorrecta).getConstantState())
                && !bt1.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.forma_boton_correcto).getConstantState()))
            bt1.setEnabled(true);
        if(!bt2.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.forma_boton_seleccionada).getConstantState())
                && !bt2.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.forma_boton_incorrecta).getConstantState())
                && !bt2.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.forma_boton_correcto).getConstantState()))
            bt2.setEnabled(true);
        if(!bt3.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.forma_boton_seleccionada).getConstantState())
                && !bt3.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.forma_boton_incorrecta).getConstantState())
                && !bt3.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.forma_boton_correcto).getConstantState()))
            bt3.setEnabled(true);
    }

    private void fail(){
        if(!resultado){
            endDialogTitle.setTextColor(ResourcesCompat.getColor(getResources(), R.color.red, null));
            endDialogTitle.setText(R.string.f_title);
            endDialogText.setTextColor(ResourcesCompat.getColor(getResources(), R.color.red, null));
            endDialogText.setText(R.string.f_text);
            dialogBtn.setText(R.string.again);
            gif.setImageResource(R.drawable.sad);
            dialogBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    endDialog.dismiss();
                    recreate();
                }
            });
        }
        else{
            viewModel.updateValorBooleanoById("R-2",true);
            viewModel.updateValorBooleanoById("R-301-30", true);
            viewModel.updateValorBooleanoById("R-116", true);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                exitDialog.show();
                return true;
            case R.id.help:
                dialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}