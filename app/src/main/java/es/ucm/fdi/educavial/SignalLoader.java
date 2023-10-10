package es.ucm.fdi.educavial;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SignalLoader extends AsyncTaskLoader<String> {

    private String mQueryString;

    public SignalLoader(Context context, String queryString){
        super(context);
        mQueryString = queryString;
    }
    @Nullable
    @Override
    public String loadInBackground() {
        String s = NetworkUtils.getSignalInfoJson(this.mQueryString);
        Log.d("loadInBackground", s);
        String res = "";
        try{
            JSONObject jsonObject = new JSONObject(s);
            JSONObject queryObject = jsonObject.getJSONObject("query");
            JSONObject pagesObject = queryObject.getJSONObject("pages");
            JSONObject menosUnoObject = pagesObject.getJSONObject("-1");
            JSONArray imageinfoArray = (JSONArray) menosUnoObject.get("imageinfo");
            JSONObject info = imageinfoArray.getJSONObject(0);
            res = info.getString("url");
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
        return res;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
