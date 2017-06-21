package ir.jcafe.instagramdownload.Classes.DownloadPack;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ir.jcafe.instagramdownload.Classes.GlobalValidator;
import ir.jcafe.instagramdownload.Classes.StoragePreference;

/**
 * Created by hp on 6/19/2017.
 */

public class DownloadBase {

    private int nextStart;

    private Context context;
    public DownloadBase(Context context){
        this.context = context;
    }

    String content;
    private final static String sharedDataTag = "window._sharedData";
    private final static String sharedEndTag = ";</script>";

    private final static String Type_Tag = "__typename";
    private final static String Type_Image = "GraphImage";
    private final static String Type_Video = "GraphVideo";
    private final static String Type_Sidecar = "GraphSidecar";
    private final static String DisplayURL_Tag = "display_url";
    private final static String VideoURL_Tag = "video_url";
    private final static String Sidecar_Chilren_Tag = "edge_sidecar_to_children";

    private final static String Image_Folder_Name = "IGImages";
    private final static String Video_Folder_Name = "IGVideos";


    private String GetContent(String link) throws IOException {

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
    }

    private String GetJSONValue(int Start, String Tag){
        int start = content.indexOf(Tag,Start);
        start = content.indexOf(":",start);
        int end = content.indexOf(",",start);
            nextStart = end; // to use in sidecar type
        return content.substring(start + 1,end).replace("\"","").trim();
    }

    private String GetJSONValue(String Tag){
        return GetJSONValue(0,Tag);
    }

    private String GetContentType(int start){
        return GetJSONValue(start,Type_Tag);
    }

    private String GetContentType(){
        return GetJSONValue(Type_Tag);
    }

    private String GetItemLink(boolean isVideo) {
        if (!isVideo) {
            return GetJSONValue(DisplayURL_Tag);
        } else {
            return GetJSONValue(VideoURL_Tag);
        }
    }

    private int GetMediaIndex(boolean isVideo){
        if(isVideo)
            return StoragePreference.GetLastVideoID(context);

        return StoragePreference.GetLastImageID(context);
    }

    private void SetMediaIndex(boolean isVideo,int Index){
        if(isVideo)
            StoragePreference.SetLastVideoID(context,Index);
        else
            StoragePreference.SetLastImageID(context,Index);
    }

    private File GetFilePath(String path,String extension,boolean isVideo){

         int inx = GetMediaIndex(isVideo);

        File f;
        do {
            f = new File(path + "/" + inx + extension);
            inx++;
        }while(f.exists());

         SetMediaIndex(isVideo,inx);
        Log.i("axFilePath",f.getAbsolutePath());

        return f;
    }

    private File GetMediaPath(boolean isVideo,String extension){

        String path = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();

        if(isVideo){
            path += "/" + Video_Folder_Name + "/";
        }else{
            path += "/" + Image_Folder_Name + "/";
        }

        File f = new File(path);

        Log.i("axBasePath",path);

        if(f.isDirectory() & !f.exists())
            f.mkdir();

        return GetFilePath(path,extension,isVideo);
    }

    private String GetExtension(String link){
        int inx = link.lastIndexOf(".");
        return link.substring(inx);
    }

    private void SendBroadCast(File f){
        Intent intent =new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(f);
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

    private boolean DownloadMedia(String link,boolean isVideo){
        boolean result = true;
        String extension = GetExtension(link);
        File out = GetMediaPath(isVideo,extension);

        HttpURLConnection cnn = null;
        InputStream sr = null;
        FileOutputStream fo = null;

        try {
            URL url = new URL(link);
            cnn = (HttpURLConnection) url.openConnection();

            sr = cnn.getInputStream();
            out.createNewFile();
            fo = new FileOutputStream(out);

            byte[] buffer = new byte[1024];
            int cnt;

            while((cnt = sr.read(buffer,0,buffer.length))>0){
                fo.write(buffer,0,cnt);
            }

            cnn.disconnect();
            fo.flush();

            SendBroadCast(out);
        }
        catch (Exception ex){
            if(out.exists())
                out.delete();
            ex.printStackTrace();
            result = false;
        }
        finally {
            if(sr != null){
                try{
                    sr.close();
                    sr = null;
                }catch(Exception ex){}
            }

            if(fo != null){
                try{
                    fo.close();
                    fo = null;
                }catch(Exception ex){}
            }

            if(cnn != null){
                try{
                    cnn.disconnect();
                    cnn = null;
                }catch(Exception ex){}
            }

        }
         // add to db
        return result;
    }

    private void SidecarDownload(){
        nextStart  = content.indexOf(Sidecar_Chilren_Tag) + Sidecar_Chilren_Tag.length();
        ///// TODO: 6/21/2017  Add Download Files to TFiles table
        ///// TODO: 6/21/2017  Download Description 
        ///// TODO: 6/21/2017 Add Description to Description table
        String cType;
        while(!( cType = GetContentType(nextStart)).isEmpty()){
            String link;
            if(cType.equalsIgnoreCase(Type_Image)){
                 link = GetJSONValue(nextStart,DisplayURL_Tag);
                DownloadMedia(link,false);
            }else if(cType.equalsIgnoreCase(Type_Video)){
                link = GetJSONValue(nextStart,VideoURL_Tag);
                DownloadMedia(link,true);
            }else{
                break;
            }
        }

    }

    private boolean ParseContent(){
        String cType = GetContentType();
      if(cType.equalsIgnoreCase(Type_Image)){
          return DownloadMedia(GetItemLink(false),false);
      }else if(cType.equalsIgnoreCase(Type_Video)){
            return DownloadMedia(GetItemLink(true),true);
      }else if(cType.equalsIgnoreCase(Type_Sidecar)){
          try {
              SidecarDownload();
              return true;
          }catch(Exception ex){
              ex.printStackTrace();
          }
      }

        return false;
    }

    public boolean Download(String link) throws IOException{
        boolean result = false;
        if(GlobalValidator.IsInstaLink(link))
        {
             content = GetContent(link);
            if(!content.isEmpty()){
                ParseContent();

                result = true;
            }
        }
        content = "";
        return result;
    }
}
