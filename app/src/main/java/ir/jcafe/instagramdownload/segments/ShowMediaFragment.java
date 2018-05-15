package ir.jcafe.instagramdownload.segments;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;

import ir.jcafe.instagramdownload.Classes.DataBase.DataBase;
import ir.jcafe.instagramdownload.Classes.Interfaces.RunnableById;
import ir.jcafe.instagramdownload.Classes.MediaList.MediaItem;
import ir.jcafe.instagramdownload.R;

public class ShowMediaFragment extends Fragment {

    private View view;
    private MediaItem item;
    private RunnableById removeFileRunnable;

    private void showNoImage(ImageView img,VideoView video){
        img.setImageResource(R.drawable.noimage);
        video.setVisibility(View.GONE);
        img.setVisibility(View.VISIBLE);
    }
    private void showImage(ImageView img,VideoView video,File f){
        img.setImageURI(Uri.fromFile(f));
        video.setVisibility(View.GONE);
        img.setVisibility(View.VISIBLE);

    }
    private void showVideo(VideoView video,ImageView img){
        MediaController mc = new MediaController(view.getContext());
        video.setVideoPath(item.Src);
        img.setVisibility(View.GONE);
        video.setVisibility(View.VISIBLE);
        mc.setMediaPlayer(video);
        video.setMediaController(mc);
    }


    public void setMediaItem(MediaItem item){
        this.item = item;
    }

    public void setRemoveFileRunnable(RunnableById removeFile){
        this.removeFileRunnable = removeFile;
    }
/*
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(item != null) {
            outState.putString("itemSrc", item.Src);
            outState.putBoolean("isVideo",item.isVideo);
            outState.putInt("itemId",item.Id);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        if(savedInstanceState != null && !savedInstanceState.isEmpty()){
            String src = savedInstanceState.getString("itemSrc");

            if(!src.isEmpty()){
                MediaItem tmp = new MediaItem();
                tmp.Src = src;
                tmp.isVideo = savedInstanceState.getBoolean("isVideo");
                tmp.Id = savedInstanceState.getInt("itemId");
                setMediaItem(tmp);
            }
        }
        super.onViewStateRestored(savedInstanceState);
    }
*/

    private static String decodeUnicode(String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len;) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed   \\uxxxx   encoding.");
                        }

                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }

    private void loadDescribe(EditText txt){
        String tmp = DataBase.getSingletone(view.getContext()).GetDescription(item.Id);
        txt.setText(decodeUnicode(tmp));
        txt.setOnKeyListener(null);
    }

    private void reload(){
        EditText txtDescribe = (EditText) view.findViewById(R.id.txtDescription);
        ImageView img = (ImageView) view.findViewById(R.id.imgMedia);
        VideoView video = (VideoView) view.findViewById(R.id.videoMedia);
        if(item != null) {
            File f = new File(item.Src);
            if(!f.exists()){
                showNoImage(img,video);
            }else{
                if(!item.isVideo)
                    showImage(img,video,f);
                else
                    showVideo(video,img);
            }

            loadDescribe(txtDescribe);

        }else{
            showNoImage(img,video);
            txtDescribe.setText(R.string.nocomment);
        }
    }

    private void DelMedia(){
        new AlertDialog.Builder(getContext())
                .setIcon(R.drawable.ig64)
                .setCancelable(true)
                .setTitle(R.string.alertTitle)
                .setMessage(R.string.alertDelMessage)
                .setNegativeButton(R.string.alertNegative, new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // no
                        Log.i("JCafe","Remove Media No");
                    }
                })
                .setPositiveButton(R.string.alertPosetive, new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // yes
                        if (removeFileRunnable != null)
                            removeFileRunnable.run(item.Id, view.getContext());
                        //Toast.makeText(view.getContext(), "clicked", Toast.LENGTH_SHORT).show();
                        Log.i("JCafe","Remove Media Yes");
                    }
                }).show();
    }


    private void DoShare(){
        File f = new File(item.Src);
        if(!f.exists()){
            //showNoImage(img,video);
            Log.i("JCafe","No resource to share");
        }else{
            String fileType;

            if(!item.isVideo)
              fileType = "image/*";
            else
                fileType = "video/*";

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType(fileType);

            Uri uri = Uri.fromFile(f);
            intent.putExtra(Intent.EXTRA_STREAM,uri);

            try {
                String shareTitle = getString(R.string.menuShare);
                Intent i = Intent.createChooser(intent, shareTitle);
                startActivity(i);
            }catch(Exception ex){
                Log.i("JCafe","Share error");
            }
        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {

       super.onCreateView(inflater, container, savedInstanceState);

         view = inflater.inflate(R.layout.show_media_layout,container,false);
         reload();


        ((Button)view.findViewById(R.id.btnRemove)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //final Boolean canceltst = false;
          DelMedia();
            }
        });


        ((Button) view.findViewById(R.id.btnShare)).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoShare();
            }
        });


        return view;
    }
}
