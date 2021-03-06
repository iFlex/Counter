package rory.bain.counter.app;

import rory.bain.counter.app.R;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.app.Application;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("NewApi")
/**
 * Created by rorybain on 28/01/15.
 */

public class library_fragment extends Fragment{

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View rootView = inflater .inflate(R.layout.library_view, container, false);
        final Button addButton = (Button) rootView.findViewById(R.id.addButton);
        final ArrayList<Integer> rowIDS = new ArrayList<Integer>();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(library_fragment.this.getActivity(), addActivity.class);
                startActivity(i);
            }
        });

        MainActivity.libraryDB.open();


        Cursor cursor = MainActivity.libraryDB.getAllRows();
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        ArrayList<String> items = new ArrayList<String>();

        if(cursor.moveToLast()) {
            do {

                int id = cursor.getInt(libraryDBAdapter.COL_ROWID);
                String name = cursor.getString(libraryDBAdapter.COL_NAME);
                String icon = cursor.getString(libraryDBAdapter.COL_ICON);
                String sample = cursor.getString(libraryDBAdapter.COL_SAMPLE);
                int used = cursor.getInt(libraryDBAdapter.COL_USED);
                int broken = cursor.getInt(libraryDBAdapter.COL_BROKEN);

                items.add(name);
                rowIDS.add(id);

            } while (cursor.moveToPrevious());
        }
        deleteAdapter adapter2 = new deleteAdapter(items, this.getActivity(), rowIDS);

//        SimpleAdapter adapter = new SimpleAdapter(this.getActivity(), data,
//                android.R.layout.simple_list_item_2,
//                new String[] {"name", "sample"},
//                new int[] {android.R.id.text1,
//                        android.R.id.text2});

        ListView list = (ListView) rootView.findViewById(R.id.libraryList);
        list.setAdapter(adapter2);

        return rootView;
    }
}
