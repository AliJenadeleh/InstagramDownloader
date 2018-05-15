package ir.jcafe.instagramdownload.Classes.MediaList;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import ir.jcafe.instagramdownload.Classes.Interfaces.RunnableById;
import ir.jcafe.instagramdownload.R;

public class MediaHolder extends RecyclerView.ViewHolder
                        implements ImageView.OnClickListener{
    private View view;
    private MediaItem item;
    private RunnableById showMedia;

    public void setShowMedia(RunnableById delegate){
        this.showMedia = delegate;
    }

    public void bind(MediaItem item){
        this.item = item;
        ImageView img = (ImageView) view.findViewById(R.id.imgItem);
        ImageView imgPlay = (ImageView) view.findViewById(R.id.imgVideoCover);
        File f = new File(item.Src);
        if(f.exists()) {
            if (!item.isVideo) {
                img.setImageURI(Uri.fromFile(new File(item.Src)));
                imgPlay.setImageResource(0);
                imgPlay.setVisibility(View.GONE);
            } else {
                Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(item.Src,
                        MediaStore.Images.Thumbnails.MINI_KIND);
                img.setImageBitmap(thumbnail);

                imgPlay.setImageResource(R.drawable.video_cover);
                imgPlay.setVisibility(View.VISIBLE);
            }
        }else{
            img.setImageResource(R.drawable.noimage);
        }
        img.setOnClickListener(this);
    }

    public MediaHolder(View itemView) {
        super(itemView);
        view = itemView;
    }

    @Override
    public void onClick(View v) {
        if(showMedia != null) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    showMedia.run(item.Id,view.getContext());
                    //Toast.makeText(view.getContext(),"clicked Me",Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}
