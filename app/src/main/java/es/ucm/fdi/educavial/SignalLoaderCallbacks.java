package es.ucm.fdi.educavial;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

public class SignalLoaderCallbacks implements LoaderManager.LoaderCallbacks<String>{
    public static final String EXTRA_QUERY = "queryString";
    private Context mContext;

    private int i = 0;
    public SignalLoaderCallbacks(Context c){ mContext=c; }
    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        return new SignalLoader(mContext, args.getString(EXTRA_QUERY));
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        i++;
        if(data != null)
            Log.d("onloadFinished " + i, data);
        else Log.d("onloadFinished" + i, "Vacio");
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}
