package ir.jcafe.instagramdownload.Classes.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import ir.jcafe.instagramdownload.Classes.Services.IGDService;
import ir.jcafe.instagramdownload.R;

public class IGDWidget extends AppWidgetProvider {

    private void showState(Context context,AppWidgetManager manager,int[] ids
            ,boolean setOnClickListener,boolean changeState){
        RemoteViews rv = new RemoteViews(context.getPackageName(),R.layout.igd_widget_layout);

        int resource ;

        if(changeState){
            if(IGDService.isActivated(context)) {
                IGDService.stop(context);
                Toast.makeText(context,R.string.autodownloadstop,Toast.LENGTH_SHORT).show();
            }else {
                IGDService.start(context);
                Toast.makeText(context,R.string.autodownloadstart,Toast.LENGTH_SHORT).show();
            }
        }


        if(IGDService.isActivated(context))
            resource = R.drawable.igdacsm;
        else
            resource = R.drawable.igdsm;


        rv.setImageViewResource(R.id.ibtnWidget,resource);
        AppWidgetManager wm;

        if(manager == null)
            wm = AppWidgetManager.getInstance(context);
        else
            wm = manager;


        if(setOnClickListener){
            Intent intent = new Intent(context,IGDWidget.class);
            PendingIntent pi = PendingIntent.getBroadcast(context,0,intent,0);
            rv.setOnClickPendingIntent(R.id.ibtnWidget,pi);
        }

        if(ids == null) {
            ComponentName cn= new ComponentName(context,IGDWidget.class);
            wm.updateAppWidget(cn, rv);
        }
        else
            wm.updateAppWidget(ids,rv);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction() == null)
            showState(context,null,null,false,true);
        else
            super.onReceive(context, intent);

        Log.i("axInfo","receive");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        showState(context,appWidgetManager,appWidgetIds,true,false);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.i("axInfo","update");
    }
}
