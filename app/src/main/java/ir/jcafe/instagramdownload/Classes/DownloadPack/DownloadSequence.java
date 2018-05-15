package ir.jcafe.instagramdownload.Classes.DownloadPack;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import java.io.IOException;
import ir.jcafe.instagramdownload.Classes.DataBase.DataBase;
import ir.jcafe.instagramdownload.MainActivity;
import ir.jcafe.instagramdownload.R;

import static ir.jcafe.instagramdownload.Classes.Services.IGDService.HasMoreToShow;

public class DownloadSequence extends AsyncTask {
    private Context context;
    private Toast toast;
    private DataBase db;
    private DownloadBase download;
    private boolean isRunning;
    private Runnable onFinish;
    Notification notification;

    public DownloadSequence(Context context,Runnable onFinish){
        this.context = context;
        db = DataBase.getSingletone(context);
        download = new DownloadBase(context);
        isRunning = false;
        this.onFinish = onFinish;
        toast = Toast.makeText(context, R.string.successful,Toast.LENGTH_SHORT);
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
                toast.show();
                HasMoreToShow(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        isRunning = false;
        return null;
    }

    private void showNotifation(){
        if(notification == null){
            String title = context.getText(R.string.appTitle).toString();
            String text = context.getText(R.string.successful).toString();

            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pi = PendingIntent.getActivity(context,0,intent,0);

            notification = new Notification.Builder(context)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setSmallIcon(R.drawable.igdsm)
                    .setAutoCancel(true)
                    .setContentIntent(pi)
                    .build();
        }

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0,notification);
    }

    @Override
    protected void onPostExecute(Object o) {
        if(onFinish != null)
            new Handler().post(onFinish);

        showNotifation();

        super.onPostExecute(o);
    }
}
