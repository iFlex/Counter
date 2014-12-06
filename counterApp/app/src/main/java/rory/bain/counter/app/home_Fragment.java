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
@SuppressLint("NewApi")
public class home_Fragment extends Fragment {
    int count;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater .inflate(R.layout.home_fragment, container, false);
        final TextView resultText = (TextView) rootView.findViewById(R.id.countText);
        count = 0;

        Button startButton = (Button) rootView.findViewById(R.id.startButton);
        Button resButton = (Button) rootView.findViewById(R.id.resetButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count ++;
                resultText.setText(String.valueOf(count));
            }
        });

        resButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = 0;
                resultText.setText(String.valueOf(count));
            }
        });

        return rootView;
    }
}
