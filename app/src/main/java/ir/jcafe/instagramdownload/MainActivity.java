package ir.jcafe.instagramdownload;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.net.URI;

import ir.jcafe.instagramdownload.Classes.ClipboardPack.ClipboardDetector;
import ir.jcafe.instagramdownload.Classes.DataBase.DataBase;
import ir.jcafe.instagramdownload.Classes.DownloadPack.DownloadSingle;
import ir.jcafe.instagramdownload.Classes.GlobalValidator;
import ir.jcafe.instagramdownload.Classes.Interfaces.RunnableById;
import ir.jcafe.instagramdownload.Classes.MediaList.MediaItem;
import ir.jcafe.instagramdownload.Classes.Services.IGDService;
import ir.jcafe.instagramdownload.segments.MediaListFragment;
import ir.jcafe.instagramdownload.segments.ShowMediaFragment;

public class MainActivity extends AppCompatActivity {

    private boolean exitTag = false;

    private final static String Font_Face_Name = "fontawesome-webfont.ttf";
    private final static int POSITION_DOWNLOAD_LIST = 1;//dev mode
    private final static int POSITION_MEDIA_LIST = 2;
    private final static int POSITION_SHOW_MEDIA = 3;
    private final static String TELEGRAM_PACKAGE = "org.telegram.messenger";
    private final static String INSTAGRAM_PACKAGE = "com.instagram.android";
    private final static String AJ_PAGE = "https://www.instagram.com/_u/jcafeir";
    private final static String AJ_PAGE_2 = "https://www.instagram.com/jcafeir";
    private final static String TG_CHANNEL = "jcafeir";
    private Runnable preDownload,postDownload,onError;
    private RunnableById showMediaRunnable,delFileRunnable;

    private int position = 0;

    private void ReLoadPosition(){
        if(position == POSITION_MEDIA_LIST){
            showMediaList();
        }
    }

    private void showMediaList(){
        MediaListFragment fragment = new MediaListFragment();
        fragment.setShowMedia(showMediaRunnable);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentsPlace,fragment)
                .commit();
        this.position = POSITION_MEDIA_LIST;
    }

    private void showMedia(int Id,Context context){
        MediaItem item =
                DataBase.getSingletone(context).GetMediaItem(Id);
        if(item != null){
            ShowMediaFragment mediaFragment = new ShowMediaFragment();
            mediaFragment.setMediaItem(item);
            mediaFragment.setRemoveFileRunnable(delFileRunnable);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentsPlace,mediaFragment)
                    .commit();

            IGDService.setItemId(item.Id);
            position = POSITION_SHOW_MEDIA;
        }
    }

    private void ShowSnackBar(View v,String Message) {
        Snackbar.make(v, Message, Snackbar.LENGTH_LONG).show();
    }

    private void ShowSnackBar(View v,int MessageResource) {
        Snackbar.make(v, MessageResource, Snackbar.LENGTH_LONG).show();
    }

    private int GetXColor(int Id){
        if(Build.VERSION.SDK_INT >= 23)
            return getColor(Id);
        else
            return ContextCompat.getColor(getApplicationContext(),Id);
    }

    private void InitialTBMain(){
        Toolbar tbMain = (Toolbar) findViewById(R.id.tbMain);
        setSupportActionBar(tbMain);

        ActionBar actionBar=(ActionBar) getSupportActionBar();
        actionBar.setTitle(R.string.appTitle);

        tbMain.setNavigationIcon(R.drawable.back2);
        tbMain.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

    }

    private void initialRunnables(final Button v){

        preDownload = new Runnable() {
            @Override
            public void run() {
                v.setEnabled(false);
                ShowSnackBar(v,R.string.downloadstart);
                ProgressBar p= (ProgressBar) findViewById(R.id.progLoading);
                p.setVisibility(View.VISIBLE);
            }
        };

        postDownload = new Runnable() {
            @Override
            public void run() {
                v.setEnabled(true);
                ShowSnackBar(v,R.string.successful);
                ProgressBar p= (ProgressBar) findViewById(R.id.progLoading);
                p.setVisibility(View.GONE);
                ReLoadPosition();
            }
        };

        onError = new Runnable() {
            @Override
            public void run() {
                v.setEnabled(true);
                ShowSnackBar(v,R.string.error);
                ProgressBar p= (ProgressBar) findViewById(R.id.progLoading);
                p.setVisibility(View.GONE);
            }
        };

        showMediaRunnable = new RunnableById() {
            @Override
            public void run(int Id,Context context) {
                showMedia(Id,context);
            }
        };

        delFileRunnable = new RunnableById() {
            @Override
            public void run(int Id, Context context) {
                DataBase.getSingletone(context).deleteFile(Id);
                goBack();
                Toast.makeText(context,R.string.fileremoved,Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void btnDownload_OnClick(Button btn){
        // https://www.instagram.com/p/Bb3nm8kAsh5/?taken-by=ali.jenadeleh
        String link = ClipboardDetector.FromClipboard(this);
        //String link = "https://www.instagram.com/p/Bb3nm8kAsh5";
        if(GlobalValidator.IsInstaLink(link)) {
            try {
                new DownloadSingle(this,postDownload,preDownload,onError).execute(link);
            } catch (Exception ex){
                ex.printStackTrace();
                ShowSnackBar(btn,R.string.error);
            }
        }else{
            ShowSnackBar(btn,R.string.nolink);
        }
    }

    private void setAutoDownloadButtonColor(){
        Button btn = (Button) findViewById(R.id.btnAutoDownload);
        if(btn != null) {
            if (IGDService.isActivated(this)) {
                btn.setTextColor(GetXColor(R.color.colorAcivated));
            } else {
                btn.setTextColor(GetXColor(R.color.colorTextPrimary));
            }
        }
    }

    private void btnAutoDownload_OnClick(Button btn){
        if(IGDService.isActivated(this))
        {
            IGDService.stop(this);
            btn.setTextColor(GetXColor(R.color.colorTextPrimary));
            Toast.makeText(this,R.string.autodownloadstop,Toast.LENGTH_SHORT).show();
        }else{
            IGDService.start(this);
            btn.setTextColor(GetXColor(R.color.colorAcivated));
            Toast.makeText(this,R.string.autodownloadstart,Toast.LENGTH_SHORT).show();
        }
    }

    private void initialButtons(){
        Button btnDownload, btnAutoDownload;

        btnDownload = (Button) findViewById(R.id.btnDownload);
        btnAutoDownload = (Button) findViewById(R.id.btnAutoDownload);

        Typeface typeface = Typeface.createFromAsset(getAssets(),Font_Face_Name);
        btnAutoDownload.setTypeface(typeface);
        btnDownload.setTypeface(typeface);

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnDownload_OnClick((Button)v);
            }
        });
        btnAutoDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAutoDownload_OnClick((Button)v);
            }
        });

        initialRunnables(btnDownload);
    }

    private void InitialComponents() {

        initialButtons();
    }

    private void StartInstaGram(){
        Intent intent =
                this.getPackageManager().getLaunchIntentForPackage(GlobalValidator.InstaStrongName);
        if(intent != null)
            startActivity(intent);
    }

     private void gotoJCafePage(){
        // https://www.instagram.com/ali.jenadeleh/
        Uri pageUri = Uri.parse(AJ_PAGE);
        Intent intent = new Intent(Intent.ACTION_VIEW,pageUri);
        intent.setPackage(INSTAGRAM_PACKAGE);

        String title = getString(R.string.appTitle);
        Intent i = Intent.createChooser(intent,title);

        try {

            if (i != null)
                startActivity(i);
            else {
                Uri.parse(AJ_PAGE_2);
                intent = new Intent(Intent.ACTION_VIEW, pageUri);
                intent.setPackage(INSTAGRAM_PACKAGE);
                i = Intent.createChooser(intent, title);
                if (i != null)
                    startActivity(i);
            }
        }catch(Exception ex){
            Log.i("JCafe",ex.getMessage());
        }

    }

    private void doShare(){
        if(position == POSITION_SHOW_MEDIA){
            int id = IGDService.getItemId();
            if(id > -1){

                MediaItem item =  DataBase.getSingletone(this).GetMediaItem(id);
                Intent intent = new Intent(Intent.ACTION_SEND);

                Uri u = Uri.fromFile( new File(item.Src));

                int inx = item.Src.lastIndexOf(".");
                String extension;
                try {
                    extension = item.Src.substring(inx + 1);
                }catch(Exception ex){
                    extension = "*";
                }

                if(item.isVideo){
                    intent.setType("video/" + extension);
                }else{
                    intent.setType("image/" + extension);
                }

                String tmp = getString(R.string.appTitle);
                intent.putExtra(Intent.ACTION_MEDIA_SHARED,u);
                Intent i = Intent.createChooser(intent,tmp);

                if(i!= null)
                    startActivity(i);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.menuInstagram:
                StartInstaGram();
                return true;
            case R.id.menuJCafe:
                gotoJCafePage();
                return true;
            case R.id.menuTelegram:
                gotoTelegramChannel();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void gotoTelegramChannel(){
        Uri uri = Uri.parse("http://t.me/" + MainActivity.TG_CHANNEL);

        Intent intent = new Intent(Intent.ACTION_VIEW,uri);

        String title = getString(R.string.menuTelegram);
        Intent i = Intent.createChooser(intent,title);

        if(i != null) {

            intent.setPackage(TELEGRAM_PACKAGE);
            try {
                startActivity(intent);
            } catch (Exception ex) {
                Log.i("JCafe", "Telegram channel opening error");
                Log.i("JCafe", ex.getMessage());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_menu,menu);

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitialTBMain();

        InitialComponents();


        // after initialComponents
        setAutoDownloadButtonColor();


        showMediaList();
    }

    private void goExit(){
        System.exit(0);
    }

    private void tryExit(){
        if(!IGDService.isActivated(this)) {
            if (exitTag)
                goExit();
            else {
                exitTag = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exitTag = false;
                    }
                }, 500);
                Toast.makeText(this, R.string.tryexit, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void goBack(){
        if(position != POSITION_MEDIA_LIST) {
            position = POSITION_MEDIA_LIST;
            ReLoadPosition();
        }else{
            tryExit();
        }
    }
    @Override
    public void onBackPressed() {
        if(!IGDService.isActivated(this))
         goBack();
        else
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        try {
            setAutoDownloadButtonColor();
            super.onResume();
            ReLoadPosition();
        }catch (Exception ex){

        }
    }

    @Override
    protected void onRestart() {
        //ReLoadPosition();
        super.onRestart();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("appPos",position);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        if(savedInstanceState != null && !savedInstanceState.isEmpty()){
            int pos = savedInstanceState.getInt("appPos",-1);
            if(pos >-1){
             position = pos;
            }

            if(pos == POSITION_SHOW_MEDIA){
                showMedia(IGDService.getItemId(),this);
            }else{
                ReLoadPosition();
            }
        }


        super.onRestoreInstanceState(savedInstanceState);
    }

}
