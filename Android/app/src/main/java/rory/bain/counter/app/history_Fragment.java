package rory.bain.counter.app;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.widget.ListView;

import java.text.ParseException;
import java.util.Date;
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
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import android.util.Log;
@SuppressLint("NewApi")
public class history_Fragment extends Fragment {

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View rootView = inflater .inflate(R.layout.history_view, container, false);
        openDB();

        Button resetSQL = (Button) rootView.findViewById(R.id.resetSQL);

        resetSQL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(history_Fragment.this.getActivity())
                        .setTitle("Reset Data")
                        .setMessage("Are you sure you want to reset history data?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.myDB.deleteAll();
                                ListView listViews = (ListView) rootView.findViewById(R.id.historyList);
                                listViews.setVisibility(View.INVISIBLE);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        Cursor cursor = MainActivity.myDB.getAllRows();
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();

        if(cursor.moveToLast()) {
            do {
                Map<String, String> datum = new HashMap<String, String>(2);

                int id = cursor.getInt(historyDBAdapter.COL_ROWID);
                int count = cursor.getInt(historyDBAdapter.COL_COUNT);
                String date = cursor.getString(historyDBAdapter.COL_DATE);
                String sound = cursor.getString(historyDBAdapter.COL_SOUND);

                datum.put("date", convertToNewTime(date));
                if (sound.isEmpty()){
                    datum.put("count", String.valueOf(count));
                }else {
                    datum.put("count", String.valueOf(count) + " - " + sound);
                }
                data.add(datum);

            } while (cursor.moveToPrevious());
        }

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

    private String convertToNewTime(String date) {
        String resultDate = null;

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
        try
        {
            Date d1 = formatter.parse(date);
            Date d2 = new Date();
            long diff = d2.getTime() - d1.getTime();
            long diffSeconds = diff / 1000;

            int SECOND = 1;
            int MINUTE = 60 * SECOND;
            int HOUR = 60 * MINUTE;
            int DAY = 24 * HOUR;
            int MONTH = 30 * DAY;
            Log.d("Date diff", diff + "");

            if (diffSeconds < 0)
            {
                return "0 Seconds ago";
            }
            if (diffSeconds < 1 * MINUTE)
            {
                return "Less than a minute ago";
            }
            if (diffSeconds < 2 * MINUTE)
            {
                return "About a minute ago";
            }
            if (diffSeconds < 7 * MINUTE)
            {
                return "About 5 minutes ago";
            }
            if (diffSeconds < 12 * MINUTE)
            {
                return "About 10 minutes ago";
            }
            if (diffSeconds < 20 * MINUTE)
            {
                return "About 15 minutes ago";
            }
            if (diffSeconds < 35 * MINUTE)
            {
                return "About half an hour ago";
            }
            if (diffSeconds < 50 * MINUTE)
            {
                return "About 45 minutes ago";
            }
            if (diffSeconds < 65 * MINUTE)
            {
                return "About an hour ago";
            }
            if (diffSeconds < 24 * HOUR)
            {
                return "A few hours ago";
            }
            if (diffSeconds < 48 * HOUR)
            {
                return "Yesterday";
            }
            if (diffSeconds < 30 * DAY)
            {
                //TODO:
                //CHECK THIS VALUE IS RIGHT. MAY NEED TO BE DIVIDED BY 1000 AS WELL
                long diffDays = diff / (24 * 60 * 60 );
                return  diffDays + " days ago";
            }
        }
        catch (ParseException e){
            e.printStackTrace();
        }

        return resultDate;
    }


}