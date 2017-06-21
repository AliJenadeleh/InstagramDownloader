package ir.jcafe.instagramdownload.Classes.ClipboardPack;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

import ir.jcafe.instagramdownload.Classes.DataBase.DataBase;
import ir.jcafe.instagramdownload.Classes.GlobalValidator;
import ir.jcafe.instagramdownload.Classes.Services.IGDService;
import ir.jcafe.instagramdownload.R;

/**
 * Created by hp on 6/19/2017.
 */

public class ClipboardDetector {

    private DataBase db;
    private Context context;
    private ClipboardManager manager;
    private ClipboardManager.OnPrimaryClipChangedListener listener;


    public ClipboardDetector(Context context,DataBase db){
        this.context = context;
        manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

        this.db = db;
    }


    private void initialListener(){
        listener = new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                if(manager.hasPrimaryClip()){
                    String cValue = manager.getPrimaryClip().getItemAt(0).getText().toString();
                    if(GlobalValidator.IsInstaLink(cValue))
                    {
                        db.addLink(cValue);
                        IGDService.tryStartAutoDownload(context);
                        Toast.makeText(context,R.string.addtolist,Toast.LENGTH_LONG).show();
                    }

                }

            }
        };
        manager.addPrimaryClipChangedListener(listener);
    }

    public void tryStart(){
        if(listener == null)
        {
            initialListener();
        }
    }

    public void stop(){
        if(listener != null)
        {
            manager.removePrimaryClipChangedListener(listener);
            listener = null;
        }
    }

    public static String FromClipboard(Context context){
        ClipboardManager manager = (ClipboardManager)
                context.getSystemService(Context.CLIPBOARD_SERVICE);

        if(manager.hasPrimaryClip())
        {
            String value = manager.getPrimaryClip().getItemAt(0).getText().toString();
            if(GlobalValidator.IsInstaLink(value))
                return  value;
        }

        return "";
    }
}
