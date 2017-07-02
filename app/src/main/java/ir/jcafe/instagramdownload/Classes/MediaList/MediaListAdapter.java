package ir.jcafe.instagramdownload.Classes.MediaList;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ir.jcafe.instagramdownload.R;
import ir.jcafe.instagramdownload.segments.MediaListFragment;

public class MediaListAdapter extends RecyclerView.Adapter<MediaHolder> {

    private List<MediaItem> items;

    public MediaListAdapter(List<MediaItem> items){
        this.items = items;
    }

    @Override
    public MediaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view  = inflater.inflate(R.layout.media_item_card,null);
        return new MediaHolder(view);
    }

    @Override
    public void onBindViewHolder(MediaHolder holder, int position) {
        ((MediaHolder)holder).bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
