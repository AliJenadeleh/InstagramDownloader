package ir.jcafe.instagramdownload.Classes.DownloadPack;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ir.jcafe.instagramdownload.Classes.GlobalValidator;
import ir.jcafe.instagramdownload.Classes.Interfaces.RunnableBoolParam;

/**
 * Created by hp on 6/19/2017.
 */

public class DirectDownload extends AsyncTask<String,Void,Boolean>{

    private final static String sharedDataTag = "window._sharedData";
    private final static String sharedEndTag = ";</script>";
    private final static String entryDataTag = "entry_data";
    private final static String postPageTag = "PostPage";
    private final static String graphqlTag = "graphql";
    private final static String shortCodeMediaTag = "shortcode_media";


    private Context context;
    public DirectDownload(Context context){
        this.context = context;
    }

    private String GetContent(String link){

        try {
            URL url = new URL(link);
            HttpURLConnection cnn = (HttpURLConnection) url.openConnection();
            InputStreamReader sr = new InputStreamReader(cnn.getInputStream());

            char[] buffer = new char[1024];
            StringBuilder result = new StringBuilder();
            int cnt;

            while((cnt = sr.read(buffer,0,buffer.length))>0)
                result.append(buffer,0,cnt);

            sr.close();
            cnn.disconnect();

            String content = result.toString();
                    result = null;

            int start = content.indexOf(sharedDataTag);
            start = content.indexOf("{",start);
            int end = content.indexOf(sharedEndTag,start);

            return content.substring(start,end);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return "";
    }

    private Runnable preExecute;
    private RunnableBoolParam postExecute;

    public void setPreeExecute(Runnable run){
        preExecute = run;
    }

    public void setPostExecute(RunnableBoolParam run){
        postExecute = run;
    }

    @Override
    protected void onPreExecute() {
        if(preExecute != null){
            preExecute.run();
        }
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(String... params) {

        String link = params[0];
        DownloadBase downloadBase = new DownloadBase(context);
        try {
            return downloadBase.Download(link);
        }catch(Exception ex) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if(postExecute != null)
            postExecute.Run(aBoolean);


        super.onPostExecute(aBoolean);
    }
}
