package es.ucm.fdi.educavial;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class SenalRepository {
    private SenalDAO Senaldao;
    private LiveData<List<Senal>> senal;
    SenalRepository(Application application){
        Database db= Database.getDatabase(application);
        Senaldao=db.SenalDAO();
        senal=Senaldao.GetAllSenals();
    }
    public LiveData<List<Senal>> GetAllSenals(){
        return senal;
    }
    public LiveData<List<Senal>> GetSenalsbycolor(){
        senal=Senaldao.GetSenalsbycolor();
        return senal;
    }
    public LiveData<List<Senal>> GetSenalsbynombre(){
        senal=Senaldao.GetSenalsbynombre();
        return senal;
    }
    public LiveData<List<Senal>> GetSenalsbyforma(){
        senal=Senaldao.GetSenalsbyforma();
        return senal;
    }
    public LiveData<List<Senal>> getAllSenalsById(){return Senaldao.GetAllSenalsById();}
    public void deleteAllSenals() {
        new SenalRepository.DeleteAllAsyncTask(Senaldao).execute();
    }

    void insert(Senal senal){
        new insertAsyncTask(Senaldao).execute(senal);
    }

    private static class insertAsyncTask extends AsyncTask<Senal,Void,Void>{
        private SenalDAO taskDao;
        insertAsyncTask(SenalDAO Senaldao){
            taskDao=Senaldao;
        }
        @Override
        protected Void doInBackground(Senal... senal){
            taskDao.insertAllSenal(senal[0]);
            return null;
        }

    }

    public Senal getSenalBycodigo(String codigo) {
        try {
            return new getSenalBycodigoAsyncTask(Senaldao).execute(codigo).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static class getSenalBycodigoAsyncTask extends AsyncTask<String, Void, Senal> {
        private SenalDAO taskDao;

        getSenalBycodigoAsyncTask(SenalDAO dao) {
            taskDao = dao;
        }
        @Override
        protected Senal doInBackground(String... ids) {
            return taskDao.getSenalBycodigo(ids[0]);
        }
    }

    private static class DeleteAllAsyncTask extends AsyncTask<Void, Void, Void> {
        private SenalDAO taskDao;

        DeleteAllAsyncTask(SenalDAO senalDao) {
            taskDao = senalDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            taskDao.deleteAllSenals();
            return null;
        }
    }
    public void updateValorBooleanoById(String id, boolean valor) {
        new UpdateValorBooleanoAsyncTask(Senaldao).execute(id, valor);
    }

    private static class UpdateValorBooleanoAsyncTask extends AsyncTask<Object, Void, Void> {
        private SenalDAO senalDao;

        UpdateValorBooleanoAsyncTask(SenalDAO senalDao) {
            this.senalDao = senalDao;
        }

        @Override
        protected Void doInBackground(Object... params) {
            String id = (String) params[0];
            boolean valor = (boolean) params[1];
            senalDao.updateValorBooleanoById(id, valor);
            return null;
        }
    }

}
