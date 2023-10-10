package es.ucm.fdi.educavial;
import androidx.room.*;
@Entity
public class Senal {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public Senal(){}
    protected String nombre;
    protected String descripcion;
    protected boolean aprendido;
    protected String codigo;

    protected String color;
    protected String forma;

    public Senal(String nombre,String descripcion,boolean aprendido,String codigo,String color, String forma){
        this.nombre=nombre;
        this.descripcion=descripcion;
        this.aprendido=aprendido;
        this.codigo=codigo;
        this.color=color;
        this.forma=forma;
    }

    public int getId() {
        return id;
    }
}

