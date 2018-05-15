package ir.jcafe.instagramdownload.Classes.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ir.jcafe.instagramdownload.Classes.GlobalValidator;
import ir.jcafe.instagramdownload.Classes.MediaList.MediaItem;

public class DataBase {

    private static DataBase singletone;

    private final static String DB_Name = "IGDdb";
    private final static int VER = 1;

    private final static String Table_Files = "TFiles";
    private final static String Table_Links = "TLinks";
    private final static String Table_Description = "TDescribe";


    private SQLiteDatabase db;
//    private Context context;

    public  DataBase(Context context){
        db = context.openOrCreateDatabase(DB_Name,Context.MODE_PRIVATE,null);
        //this.context = context;
        InitialDataBase();
    }

    private void CreateTables(){

        String cmd = "create table if not exists ["+ Table_Files +
                "](id integer primary key autoincrement,[fname] nvarchar(128) not null" +
                ",isvideo bool default 0,describeID int)";
        db.execSQL(cmd);

        cmd = "create table if not exists [" + Table_Links +
                "](id integer primary key autoincrement,[link] nvarchar(128) not null " +
                ",isdone bool default 0)";

        db.execSQL(cmd);

        cmd = "create table if not exists [" + Table_Description +
                "](id integer primary key autoincrement,[describe] nvarchar(1024))";

        db.execSQL(cmd);
    }

    public void InitialDataBase(){
        if(VER > db.getVersion()){
            CreateTables();
            db.setVersion(VER);
        }
    }

    public void stop(){
        db.close();
        db = null;
    }

    public void addLink(String link){
        db.execSQL("insert into ["+ Table_Links +"](link) values('"+ link +"')");
    }

    public void addFile(String fname,int descriptionID,boolean isVideo){
        int isv = 0;
        if(isVideo)
            isv = 1;
        ContentValues values = new ContentValues(3);
         values.put("fname",fname);
         values.put("isvideo",isv);
         values.put("describeID",descriptionID);

        db.insert(Table_Files,null,values);
    }

    public int addDescribe(String text){

        ContentValues values = new ContentValues();
            values.put("describe",text);
        db.insert(Table_Description, null, values);

        Cursor cursor = db.rawQuery("select last_insert_rowid()",null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public void setLinkDone(String link){
        db.rawQuery("update ["+ Table_Links +"] set isdone=1 where link='" + link +"' and isdone=0"
                ,null);
    }

    public void deleteLink(String link){
        Log.i("axDelete",link);
        db.execSQL("delete from ["+ Table_Links +"] where link='" + link  +"'");
    }

    public String getNextLink(){

        Cursor cursor = db.rawQuery("select [link] from [" + Table_Links + "] where isdone = 0 limit 1",null);
        cursor.moveToFirst();
        if(cursor.getCount() > 0)
        return cursor.getString(0);

        return "";

    }

    public ArrayList<String> GetDownloadList(){
        ArrayList<String> result = null;
        Cursor cursor = db.rawQuery("select [link] from ["+ Table_Links+"] where isdone=0",null);
        if(cursor.getCount() > 0)
        {
            result = new ArrayList<String>();

            cursor.moveToFirst();
            do {
                result.add(cursor.getString(0));
            }while(cursor.moveToNext());

        }

        return result;
    }

    public static DataBase getSingletone(Context context){
        if(singletone == null){
            singletone = new DataBase(context);
        }
        return singletone;
    }

    private static String getSrcPath(MediaItem item,String fName){

        String basePath = android.os.Environment.getExternalStorageDirectory() + "/";

        if(item.isVideo) {
            return basePath + "/" +
                    GlobalValidator.Video_Folder_Name + "/" + fName;
        }else{
            return basePath + "/" +
                    GlobalValidator.Image_Folder_Name + "/" + fName;
        }
    }

    public ArrayList<MediaItem> GetMediaList(){
        String cmd = "select [id],[fname],[isvideo] from [" +Table_Files+"] order by [id] desc";
        Cursor cursor = db.rawQuery(cmd,null);
        ArrayList<MediaItem> items = new ArrayList<MediaItem>();

        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            int inxIsVideo = cursor.getColumnIndex("isvideo");
            int inxFile =  cursor.getColumnIndex("fname");
            int inxId = cursor.getColumnIndex("id");
            //String basePath = android.os.Environment.getExternalStorageDirectory() + "/";

            do{

                MediaItem item = new MediaItem();
                String t = cursor.getString(inxIsVideo);
                item.isVideo = (t.equals("1"))?true:false;
                item.Id = cursor.getInt(inxId);

                item.Src = getSrcPath(item,cursor.getString(inxFile));
                /*
                if(item.isVideo) {
                    item.Src = basePath + "/" +
                            GlobalValidator.Video_Folder_Name + "/" + cursor.getString(inxFile);
                }else{
                    item.Src = basePath + "/" +
                            GlobalValidator.Image_Folder_Name + "/" + cursor.getString(inxFile);
                }
                */
                items.add(item);
            }while(cursor.moveToNext());

        }

        return items;
    }

    public MediaItem GetMediaItem(int Id){
        MediaItem result = null;

        String cmd = "select [id],[fname],[isvideo] from [" +Table_Files+"] where id=" + Id;

        Cursor cursor = db.rawQuery(cmd,null);

        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            int inxIsVideo = cursor.getColumnIndex("isvideo");
            int inxFile =  cursor.getColumnIndex("fname");
            int inxId = cursor.getColumnIndex("id");
            String basePath = android.os.Environment.getExternalStorageDirectory() + "/";

                result = new MediaItem();
                String t = cursor.getString(inxIsVideo);
                result.isVideo = (t.equals("1"))?true:false;
                result.Id = cursor.getInt(inxId);

                if(result.isVideo) {
                    result.Src = basePath + "/" +
                            GlobalValidator.Video_Folder_Name + "/" + cursor.getString(inxFile);
                }else{
                    result.Src = basePath + "/" +
                            GlobalValidator.Image_Folder_Name + "/" + cursor.getString(inxFile);
                }

        }

        return result;
    }

    private int getDescribeId(int fileId){
        String cmd = "select [describeID] from [" +Table_Files+"] where id=" + fileId;
        Cursor cursor = db.rawQuery(cmd,null);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            return cursor.getInt(0);
        }
        return -1;
    }

    public String GetDescription(int Id){
        String result = "";
        int describeId = getDescribeId(Id);
        if(describeId > -1){
            String cmd = "select [describe] from [" + Table_Description
                                                        + "] where [id]=" + describeId;
            Cursor cursor = db.rawQuery(cmd,null);
            if(cursor.getCount() > 0){
                cursor.moveToFirst();
                return cursor.getString(0);
            }
        }
        return result;
    }

    public void deleteFile(int fileId){
        int descId = getDescribeId(fileId);
        String cmd;
        if(descId > -1){
            cmd = "delete from [" + Table_Description + "] where id=" + descId ;
            db.execSQL(cmd);
        }
        cmd = "delete from [" + Table_Files + "] where id=" + fileId;
        db.execSQL(cmd);
    }
}
