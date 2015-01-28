package rory.bain.counter.app;
import android.app.Activity;
import android.database.Cursor;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import rory.bain.counter.app.R;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import android.widget.SimpleAdapter;

import java.util.Date;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
@SuppressLint("NewApi")
public class history_Fragment extends Fragment {

//    public DBAdapter myDB;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View rootView = inflater .inflate(R.layout.history_view, container, false);
        openDB();

        Button resetSQL = (Button) rootView.findViewById(R.id.resetSQL);

        resetSQL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Button clicked, refresh view to see changes");
                MainActivity.myDB.deleteAll();
                ListView listViews = (ListView) rootView.findViewById(R.id.historyList);
                listViews.setVisibility(View.INVISIBLE);
//                String date = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy").format(new Date());
//                MainActivity.myDB.insertRow(40, date, "Books");
//                MainActivity.myDB.insertRow(32, date, "Books");
//                MainActivity.myDB.insertRow(12, date, "Books");
//                MainActivity.myDB.insertRow(10, date, "Books");
//                MainActivity.myDB.insertRow(8, date, "Books");
//                MainActivity.myDB.insertRow(56, date, "Books");
//                MainActivity.myDB.insertRow(3, date, "books");
//                MainActivity.myDB.insertRow(78, date, "Books");
//                MainActivity.myDB.insertRow(23, date, "Books");
//                MainActivity.myDB.insertRow(40, date, "Books");
//                MainActivity.myDB.insertRow(31, date, "Books");
//                MainActivity.myDB.insertRow(11, date, "Books");
//                MainActivity.myDB.insertRow(13, date, "Books");


            }
        });

        Cursor cursor = MainActivity.myDB.getAllRows();

//        ArrayList<String> arrayListItems = new ArrayList<String>();
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();



        if(cursor.moveToLast()) {
            do {
                Map<String, String> datum = new HashMap<String, String>(2);


                int id = cursor.getInt(DBAdapter.COL_ROWID);
                int count = cursor.getInt(DBAdapter.COL_COUNT);
                String date = cursor.getString(DBAdapter.COL_DATE);
                String sound = cursor.getString(DBAdapter.COL_SOUND);

                datum.put("date", date);
                datum.put("count", String.valueOf(count));
                data.add(datum);

            } while (cursor.moveToPrevious());
        }

//        String[] myItems = new String[arrayListItems.size()];
//        myItems = arrayListItems.toArray(myItems);

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//                getActivity().getApplicationContext(),
//                R.layout.history_item_layout,
//                myItems);

        SimpleAdapter adapter = new SimpleAdapter(this.getActivity(), data,
                android.R.layout.simple_list_item_2,
                new String[] {"count", "date"},
                new int[] {android.R.id.text1,
                        android.R.id.text2});

        ListView list = (ListView) rootView.findViewById(R.id.historyList);
        list.setAdapter(adapter);

        return rootView;
    }







    @Override
    public void onDestroy() {
        super.onDestroy();
        closeDB();
    }

    private void openDB() {
        MainActivity.myDB.open();
    }

    private void closeDB() {
        MainActivity.myDB.close();
    }


}