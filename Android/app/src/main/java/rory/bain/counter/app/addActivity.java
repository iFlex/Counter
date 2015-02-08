package rory.bain.counter.app;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.musicg.wave.Wave;

/**
 * Created by rorybain on 29/01/15.
 */
public class addActivity extends Activity{
    public static Intent i;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_sound);
        final Button addFinished = (Button) findViewById(R.id.addFinished);
//        final EditText text1 = (EditText) findViewById(R.id.textView1);
        final EditText countText = (EditText) findViewById(R.id.textView2);
//        final EditText text3 = (EditText) findViewById(R.id.textView3);

        //Setting up the keyboard next button to finish editing
        countText.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || actionId == EditorInfo.IME_ACTION_DONE) {
                    returnToMainMenu();
                }
                return false;
            }


        });

        //Adding the button in case the user doesn't know how to use the keyboard
        addFinished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               returnToMainMenu();
            }
        });

    }

    private void returnToMainMenu() {
//////////////////////////////////////////////////////////////////
//        Remember to uncomment this and put in real data
//////////////////////////////////////////////////////////////////
//        MainActivity.libraryDB.insertRow(text1.getText().toString(), text2.getText().toString(), text2.getText().toString(), 1, 1);
        i = new Intent(getBaseContext(), MainActivity.class);
        startActivity(i);
    }
}
