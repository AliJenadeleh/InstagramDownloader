package ir.jcafe.instagramdownload.Classes;

import android.content.Context;
import android.content.SharedPreferences;

public class StoragePreference {

    private final static String SharedPreferencesName = "IGDStorage";
    private final static String Last_Image_ID = "LastImageID";
    private final static String Last_Video_ID = "LastVideoID";

    private static SharedPreferences GetPreference(Context context)
    {
        return context.getSharedPreferences(SharedPreferencesName,Context.MODE_PRIVATE);
    }

    public static int GetLastImageID(Context context){
        return GetPreference(context).getInt(Last_Image_ID,100);
    }

    public static void SetLastImageID(Context context,int Id){
        GetPreference(context).edit().putInt(Last_Image_ID,Id).commit();
    }

    public static int GetLastVideoID(Context context){
        return GetPreference(context).getInt(Last_Video_ID,100);
    }

    public static void SetLastVideoID(Context context,int Id){
        GetPreference(context).edit().putInt(Last_Video_ID,Id).commit();
    }

}
