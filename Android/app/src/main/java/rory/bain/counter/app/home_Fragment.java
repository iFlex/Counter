package rory.bain.counter.app;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Color;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.os.*;
import android.util.Log;
import android.database.Cursor;
import felix.views.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("NewApi")
public class home_Fragment extends Fragment {
    int count;
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View rootView = inflater.inflate(R.layout.home_fragment, container, false);
        final TextView resultText = (TextView) rootView.findViewById(R.id.countText);

        //TODO Link this certainty text label so that it displays certainty correctly.
        final TextView certaintyText = (TextView) rootView.findViewById(R.id.certaintyLabel);


        resultText.setText("0");
        MainActivity.counter.reset();
        final Button startButton = (Button) rootView.findViewById(R.id.startButton);
//        final Button menuButton = (Button) rootView.findViewById(R.id.menuButton);
        Button resButton = (Button) rootView.findViewById(R.id.resetButton);
        MainActivity.waveVisuals = (WaveformView) rootView.findViewById(R.id.waveform_view);

        MainActivity.libraryDB.open();
        MainActivity.myDB.open();
        Cursor cursor = MainActivity.libraryDB.getAllRows();
        HorizontalScrollView scrollView = (HorizontalScrollView) rootView.findViewById(R.id.horizontalScrollView2);
        LinearLayout linLayout = new LinearLayout(this.getActivity());
        linLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 10, 0);


        if (cursor.moveToLast()) {
            do {
                int id = cursor.getInt(libraryDBAdapter.COL_ROWID);
                Button nextButton = new Button(this.getActivity());
                nextButton.setBackgroundResource(R.drawable.sound_button);
                nextButton.setTextColor(getResources().getColor(R.color.white));
                nextButton.setText(cursor.getString(libraryDBAdapter.COL_NAME));
                linLayout.addView(nextButton, layoutParams);
                nextButton.setOnClickListener(buttonListListener);
                //Tag should be something that we can identify library objects using
                nextButton.setTag(cursor.getInt(libraryDBAdapter.COL_ROWID));
            } while (cursor.moveToPrevious());
        }
        scrollView.addView(linLayout);

        MainActivity.handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                Log.d("Count:",MainActivity.counter.getCount()+":"+inputMessage.arg1);
                resultText.post(new Runnable() {
                    @Override
                    public void run() {
                        resultText.setText(MainActivity.counter.getCount()+"");
                    }
                });
            }
        };

        MainActivity.counter.setCommChannel(MainActivity.handler);


        //Currently not working
//        menuButton.setOnClickListener(new OnClickListener() {
//            @Override
//        public void onClick(View v) {
//                //Display menu bar
//            }
//        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MainActivity.processor.isRunning()) {
                    MainActivity.processor.start();
                    startButton.setText("Stop");
                    startButton.setSelected(true);
                }
                else {
                    MainActivity.processor.stop();
                    startButton.setText("Start");
                    startButton.setSelected(false);
                    String date = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy").format(new Date());
                    MainActivity.myDB.insertRow(MainActivity.counter.getCount(), date, "Books");
                }
            }
        });

        resButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.counter.reset();
                resultText.setText(String.valueOf(MainActivity.counter.getCount()));
            }
        });

        return rootView;
    }
    private OnClickListener buttonListListener = new OnClickListener() {
        public void onClick(View view) {
            //Once xml button styles have been defined, make the view set to the 'highlighted' style
            int v = (int) view.getTag();
            MainActivity.libraryDB.open();
            //Should use a cursor to tidy this up, however for now it just needs to be able to access the data
            //Also need to implemented selected sound
            String sample = MainActivity.libraryDB.getRow(v).getString(libraryDBAdapter.COL_SAMPLE);
        }

    };
}
