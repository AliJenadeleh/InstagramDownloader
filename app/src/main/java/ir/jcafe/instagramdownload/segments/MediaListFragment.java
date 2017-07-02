package ir.jcafe.instagramdownload.segments;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ir.jcafe.instagramdownload.Classes.DataBase.DataBase;
import ir.jcafe.instagramdownload.Classes.MediaList.MediaItem;
import ir.jcafe.instagramdownload.Classes.MediaList.MediaListAdapter;
import ir.jcafe.instagramdownload.R;


public class MediaListFragment extends Fragment {

    View view;
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.media_list_layout,container,false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerMediaList);

        List<MediaItem> items = DataBase.getSingletone(view.getContext()).GetMediaList();

        MediaListAdapter adapter = new MediaListAdapter(items);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(),2));

        return view;
    }
}
