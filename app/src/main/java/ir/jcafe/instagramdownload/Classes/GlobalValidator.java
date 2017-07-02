package ir.jcafe.instagramdownload.Classes;

/**
 * Created by hp on 6/19/2017.
 */

public class GlobalValidator {
    public final static String InstaStart = "https://www.instagram.com/p/";
    public final static String InstaStrongName = "com.instagram.android";
    public final static String Image_Folder_Name = "IGImages";
    public final static String Video_Folder_Name = "IGVideos";

    public static boolean IsInstaLink(String link)
    {
        if(!link.isEmpty() && link.length() > InstaStart.length()){
            return link.substring(0,InstaStart.length()).equalsIgnoreCase(InstaStart) ;
        }

        return  false;
    }
}
