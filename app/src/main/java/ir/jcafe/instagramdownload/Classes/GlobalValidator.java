package ir.jcafe.instagramdownload.Classes;

/**
 * Created by hp on 6/19/2017.
 */

public class GlobalValidator {
    public final static String InstaStart = "https://www.instagram.com/p/";
    public final static String InstaStrongName = "com.instagram.android";

    public static boolean IsInstaLink(String link)
    {
        if(!link.isEmpty() && link.length() > InstaStart.length()){
            return link.substring(0,InstaStart.length()).equalsIgnoreCase(InstaStart) ;
        }

        return  false;
    }
}
