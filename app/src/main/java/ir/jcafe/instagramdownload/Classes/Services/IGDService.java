package ir.jcafe.instagramdownload.Classes.Services;

import android.content.Context;
import ir.jcafe.instagramdownload.Classes.ClipboardPack.ClipboardDetector;
import ir.jcafe.instagramdownload.Classes.DownloadPack.DownloadSequence;

public class IGDService {

    private static ClipboardDetector detector = null;
    private static DownloadSequence downloadSequence = null;

    private static void initialDetector(Context context){
        if(detector == null)
            detector = new ClipboardDetector(context);
    }

    private static void initialDownloadSequence(Context context){
        if(downloadSequence == null)
            downloadSequence = new DownloadSequence(context, new Runnable() {
                @Override
                public void run() {
                    downloadSequence = null;
                }
            });
    }

    public static boolean isActivated(Context context){
        return detector!= null && detector.isActivate();
    }

    public static void start(Context context){
        initialDetector(context);
            detector.tryStart();

            autoDownloadStart(context);
    }

    public static void stop(Context context){
        if(detector != null) {
            detector.stop();
            detector = null;
        }

        if(downloadSequence != null){
            downloadSequence.cancel(true);
            downloadSequence = null;
        }
    }

    public static void autoDownloadStart(Context context){
        initialDownloadSequence(context);
        if(!downloadSequence.IsRunning()) {
            try {
                downloadSequence.execute();
            }catch(Exception ex){
                downloadSequence =null;
                initialDownloadSequence(context);
                downloadSequence.execute();
            }
        }
    }
}
