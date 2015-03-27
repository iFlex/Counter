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
import android.util.Log;
import android.app.Activity;
import android.util.Base64;

import engine.util.Counter;
import engine.util.Data;
import felix.views.modelMaker;

/**
 * Created by rorybain on 08/02/15.
 */
public class naming_fragment extends Fragment{
    public static Intent i;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final View rootView = inflater.inflate(R.layout.naming_fragment, container, false);
        final EditText soundName = (EditText) rootView.findViewById(R.id.soundName);
        //final EditText soundIcon = (EditText) rootView.findViewById(R.id.soundIcon);
        final Button finishedButton = (Button) rootView.findViewById(R.id.namingFinished);

        /*soundIcon.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        */
        finishedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:Insert sample file path for identifying this sound. As this is a fragment
                //On top of addAcivity, we take the data from it as well.
                //Insert row takes title of sound, icon file path, sound file path, 1 or 0 for used, 1 or 0 for broken.
                addActivity.mMaker.extractModel();
                short[] rawdata = addActivity.mMaker.getModel();
                byte[] zbytes = new byte[rawdata.length*2];
                int j = 0;
                for( int i = 0; i < rawdata.length; ++i ){
                    zbytes[j++]     = (byte)((rawdata[i] >> 8) & 0xff);
                    zbytes[j++]     = (byte)(rawdata[i]&0xff);
                }
                //
                String data = Base64.encodeToString(zbytes,Base64.DEFAULT);
                Activity a = (Activity) getActivity();
                if( a instanceof addActivity){
                    ((addActivity) a).returnToMainMenu(soundName.getText().toString(),"",data);
                }
                //playback - just to check conversion ( there is an issue there )
                /*byte[] _rawdata = Base64.decode(data, Base64.DEFAULT);
                short[] _data = new short[_rawdata.length / 2];
                j = 0;
                for (int i = 0; i < _rawdata.length; i += 2) {
                    _data[j] = (short)_rawdata[i];
                    _data[j] <<= 8;
                    _data[j++] |= _rawdata[i + 1];
                }
                //set as model
                modelMaker pbk = new modelMaker(new Counter());
                pbk.playRaw(_data);*/
                //MainActivity.libraryDB.open();
                //MainActivity.libraryDB.insertRow(soundName.getText().toString(), null, data, 1, 1);
                //MainActivity.libraryDB.close();
                //i = new Intent(getActivity(), MainActivity.class);
                //startActivity(i);
            }
        });
        // Inflate the layout for this fragment
        return rootView;
    }
}
