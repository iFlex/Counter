package rory.bain.counter.app;
import android.widget.EditText;
import android.widget.TextView;
import rory.bain.counter.app.R;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.os.*;

import rory.bain.counter.app.MainActivity;
import felix.views.*;

@SuppressLint("NewApi")
public class home_Fragment extends Fragment {
    int count;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater .inflate(R.layout.home_fragment, container, false);
        final TextView resultText = (TextView) rootView.findViewById(R.id.countText);

        Button startButton = (Button) rootView.findViewById(R.id.startButton);
        Button resButton = (Button) rootView.findViewById(R.id.resetButton);
        MainActivity.waveVisuals = (WaveformView) rootView.findViewById(R.id.waveform_view);

        MainActivity.handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                resultText.setText(String.valueOf(MainActivity.counter.getCount()));
            }
        };

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MainActivity.processor.isRunning())
                    MainActivity.processor.start();
                else
                    MainActivity.processor.stop();
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
}
