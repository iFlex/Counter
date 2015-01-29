package rory.bain.counter.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
        final EditText text1 = (EditText) findViewById(R.id.textView1);
        final EditText text2 = (EditText) findViewById(R.id.textView2);


        addFinished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.libraryDB.insertRow(text1.getText().toString(), text2.getText().toString(), text2.getText().toString(), 1, 1);
                i = new Intent(getBaseContext(), MainActivity.class);
                startActivity(i);
            }
        });

    }


}
