package ir.jcafe.instagramdownload.Classes.DownloadPack;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.view.View;

import java.io.IOException;

import ir.jcafe.instagramdownload.R;

/**
 * Created by hp on 7/1/2017.
 */

public class DownloadSingle extends AsyncTask<String,Void,Boolean> {

    private DownloadBase download;
    private Context context;
    private Runnable postExecute,preExecute,onError;

    public DownloadSingle(Context context,Runnable postExecute,Runnable preExecute,Runnable onError){
        this.context = context;
        download = new DownloadBase(context);
        this.postExecute = postExecute;
        this.preExecute = preExecute;
        this.onError = onError;
    }

    @Override
    protected void onPostExecute(Boolean o) {
        if(o && postExecute != null)
            new Handler().post(postExecute);
        super.onPostExecute(o);
    }

    @Override
    protected Boolean doInBackground(String... params) {
        if(params.length > 0) {
            String url = params[0];
            try {
                return download.Download(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void onError(){
        if(onError != null)
            new Handler().post(onError);
    }

    @Override
    protected void onPreExecute() {
        if(preExecute != null)
            new Handler().post(preExecute);
        super.onPreExecute();
    }
}
