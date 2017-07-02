package ir.jcafe.instagramdownload.Classes.DownloadPack;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import java.io.IOException;
import ir.jcafe.instagramdownload.Classes.DataBase.DataBase;

public class DownloadSequence extends AsyncTask {
    private Context context;
    private DataBase db;
    private DownloadBase download;
    private boolean isRunning;
    private Runnable onFinish;

    public DownloadSequence(Context context,Runnable onFinish){
        this.context = context;
        db = DataBase.getSingletone(context);
        download = new DownloadBase(context);
        isRunning = false;
        this.onFinish = onFinish;
    }

    public boolean IsRunning(){
        return isRunning;
    }

    @Override
    protected Object doInBackground(Object[] params) {

        isRunning = true;
        String nextLink;

        while(!(nextLink = db.getNextLink()).isEmpty()){
            try {
                download.Download(nextLink);
                db.deleteLink(nextLink);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        isRunning = false;
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        if(onFinish != null)
            new Handler().post(onFinish);
        super.onPostExecute(o);
    }
}
