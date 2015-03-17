package rory.bain.counter.app;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by rorybain on 08/02/15.
 */
public class naming_fragment extends Fragment{
    public static Intent i;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final View rootView = inflater.inflate(R.layout.naming_fragment, container, false);
        final EditText soundName = (EditText) rootView.findViewById(R.id.soundName);
        final EditText soundIcon = (EditText) rootView.findViewById(R.id.soundIcon);
        final Button finishedButton = (Button) rootView.findViewById(R.id.namingFinished);

        soundIcon.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || actionId == EditorInfo.IME_ACTION_DONE) {
                    //        MainActivity.libraryDB.insertRow(text1.getText().toString(), text2.getText().toString(), text2.getText().toString(), 1, 1);
                    i = new Intent(getActivity(), MainActivity.class);
                    startActivity(i);
                }
                return false;
            }


        });

        finishedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:Insert sample file path for identifying this sound. As this is a fragment
                //On top of addAcivity, we take the data from it as well.
                //Insert row takes title of sound, icon file path, sound file path, 1 or 0 for used, 1 or 0 for broken.
                MainActivity.libraryDB.insertRow(soundName.getText().toString(), null, "Insert sample path here", 1, 1);
                i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
            }
        });
        // Inflate the layout for this fragment
        return rootView;
    }
}
