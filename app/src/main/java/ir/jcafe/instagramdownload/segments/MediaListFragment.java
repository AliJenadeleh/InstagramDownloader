package ir.jcafe.instagramdownload.segments;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ir.jcafe.instagramdownload.Classes.DataBase.DataBase;
import ir.jcafe.instagramdownload.Classes.Interfaces.RunnableById;
import ir.jcafe.instagramdownload.Classes.MediaList.MediaItem;
import ir.jcafe.instagramdownload.Classes.MediaList.MediaListAdapter;
import ir.jcafe.instagramdownload.R;

import static ir.jcafe.instagramdownload.Classes.Services.IGDService.HasMoreToShow;


public class MediaListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    View view;
    RecyclerView recyclerView;

    private RunnableById showMedia;

    public void setShowMedia(RunnableById showMedia){
        this.showMedia = showMedia;
    }
     SwipeRefreshLayout swipeRefreshLayout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.media_list_layout,container,false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerMediaList);

        reload();

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.sRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        return view;
    }

    private void reload(){
        List<MediaItem> items = DataBase.getSingletone(view.getContext()).GetMediaList();

        MediaListAdapter adapter = new MediaListAdapter(items);
        adapter.setShowMedia(showMedia);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(),2));

        HasMoreToShow(false);
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        if(HasMoreToShow())
            reload();


        swipeRefreshLayout.setRefreshing(false);
    }
}
