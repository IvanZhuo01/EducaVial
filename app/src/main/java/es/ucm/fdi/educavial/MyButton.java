package es.ucm.fdi.educavial;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import kotlin.text.UStringsKt;

public class MyButton extends AppCompatButton {
    private int idButton;
    private String descripcionSenal;
    private String codigo;
    private int posicionEnLista;
    private boolean aprendida;
    public MyButton(@NonNull Context context) {
        super(context);
    }
    public int getIdButton() {
        return idButton;
    }

    public void setIdButton(int idButton) {
        this.idButton = idButton;
    }

    public String getDescripcionSenal() {
        return descripcionSenal;
    }

    public void setDescripcionSenal(String descripcionSenal) {
        this.descripcionSenal = descripcionSenal;
    }
    public String getCodigo() {
        return codigo;
    }
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getPosicionEnLista() {
        return posicionEnLista;
    }

    public void setPosicionEnLista(int posicionEnLista) {
        this.posicionEnLista = posicionEnLista;
    }

    public boolean isAprendida() {
        return aprendida;
    }

    public void setAprendida(boolean aprendida) {
        this.aprendida = aprendida;
    }
    void copy(MyButton src){
        setIdButton(src.getIdButton());
        setAprendida(src.isAprendida());
        setCodigo(src.getCodigo());
        setPosicionEnLista(src.getPosicionEnLista());
        setDescripcionSenal(src.getDescripcionSenal());
        this.setCompoundDrawablesWithIntrinsicBounds(null,src.getCompoundDrawables()[1],null,null);
        this.setText(src.getText());
    }
}
