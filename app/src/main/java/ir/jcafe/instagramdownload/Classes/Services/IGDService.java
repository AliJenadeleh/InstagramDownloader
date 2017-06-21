package ir.jcafe.instagramdownload.Classes.Services;

import android.content.Context;

import ir.jcafe.instagramdownload.Classes.ClipboardPack.ClipboardDetector;
import ir.jcafe.instagramdownload.Classes.DataBase.DataBase;

public class IGDService {

    private static ClipboardDetector detector;
    private static DataBase db;

    public static boolean isActivated(){
        return detector != null || db != null;
    }

    private static void start(Context context){
        if(db == null) {
            db = new DataBase(context);
        }

        if(detector == null) {
            detector = new ClipboardDetector(context,db);
            detector.tryStart();
        }

    }

    public static void tryStartAutoDownload(Context context){
        //start auto download
    }

    public static void tryStart(Context context){
        if(!isActivated()){
            start(context);
        }
    }

    public static void stop(){
        detector.stop();
         detector = null;
        db.stop();
         db = null;
    }


}
