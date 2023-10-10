package es.ucm.fdi.educavial;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@androidx.room.Database(entities = {Senal.class},version = 1)
public abstract class Database extends RoomDatabase {

    public abstract SenalDAO SenalDAO();
    public static Database instance;
    public static Database getDatabase(final Context context){
        if(instance==null){
            synchronized (Database.class){
                if(instance==null){
                    instance= Room.databaseBuilder(context.getApplicationContext(), Database.class,"basededato").fallbackToDestructiveMigration().build();
                }
            }
        }
        return instance;
    }

}
