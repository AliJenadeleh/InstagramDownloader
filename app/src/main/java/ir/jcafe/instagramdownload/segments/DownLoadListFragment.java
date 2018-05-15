package ir.jcafe.instagramdownload.segments;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import ir.jcafe.instagramdownload.Classes.DataBase.DataBase;
import ir.jcafe.instagramdownload.R;


public class DownLoadListFragment extends Fragment{
    View v;
    ListView listView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);

         v = inflater.inflate(R.layout.downloadlist_layout,container,false);
         listView = (ListView) v.findViewById(R.id.list_downloadlist);
        Reload();

        return  v;
    }

    public void Reload(){
        ArrayList<String> list = DataBase.getSingletone(v.getContext()).GetDownloadList();
        if(list != null) {
            ArrayAdapter adapter =
                    new ArrayAdapter(v.getContext(), android.R.layout.simple_list_item_1, list);
            listView.setAdapter(adapter);
        }
    }

}
