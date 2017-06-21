package ir.jcafe.instagramdownload.Classes.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by hp on 6/21/2017.
 */

public class DataBase {
    private final static String DB_Name = "IGDdb";
    private final static int VER = 1;

    private final static String Table_Files = "TFiles";
    private final static String Table_Links = "TLinks";
    private final static String Table_Description = "TDescribe";



    private SQLiteDatabase db;

    public  DataBase(Context context){
        db = context.openOrCreateDatabase(DB_Name,Context.MODE_PRIVATE,null);
        InitialDataBase();
    }

    private void CreateTables(){

        String cmd = "create table if not exists ["+ Table_Files +
                "](id integer primary key autoincrement,[fname] nvarchar(128) not null" +
                ",describeID int)";
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
}
