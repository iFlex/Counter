package rory.bain.counter.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.musicg.wave.Wave;
import android.app.Fragment;
import android.app.FragmentManager;
import android.widget.VideoView;
import felix.views.WaveformView;
import felix.views.modelMaker;
import engine.util.Counter;
import engine.Processing.Processor;
import android.util.Log;
/**
 * Created by rorybain on 29/01/15.
 */
public class addActivity extends Activity{
    private boolean dataEntered;
    public static Intent i;
    public static modelMaker mMaker;
    private Processor sampler;
    public static WaveformView waveVisuals;
    public static WaveformView modelVisuals;
    private long overall = 0;
    private int correctCount = 0;
    private addActivity thisReff = this;
    private boolean advance = false;

    public void checkResults(){
        advance = mMaker.checkCorrectness(correctCount);
        Log.d("Count results:","Correct:"+correctCount+" advancing:"+advance);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        dataEntered = false;
        super.onCreate(savedInstanceState);

        Counter c = new Counter();
        mMaker = new modelMaker(c);
        sampler = new Processor(c,mMaker);
        setContentView(R.layout.add_sound);
        waveVisuals = (WaveformView) findViewById(R.id.waveform_view);
        modelVisuals = (WaveformView) findViewById(R.id.modelform_view);
        waveVisuals.setHistorySize(1);
        mMaker.setVisualiser(waveVisuals);

        final Button startButton = (Button) findViewById(R.id.addStart);
//        final Button resetButton = (Button) findViewById(R.id.addReset);
        final Button addFinished = (Button) findViewById(R.id.addFinished);
        final Button playback    = (Button) findViewById(R.id.playback);
        final EditText countText = (EditText) findViewById(R.id.textView2);
        final DrawView dView = (DrawView) findViewById(R.id.dview);
        dView.initiateTrimmer();

//        final LinearLayout lin = (LinearLayout) findViewById(R.id.addSound_InnerLinLayout);
//        final DrawView dView = new DrawView(this);
//        lin.addView(dView);


        //Setting up the keyboard next button to finish editing
        countText.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || actionId == EditorInfo.IME_ACTION_DONE) {
                //}
                return false;
            }


        });

        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //start recording if not already started, if it is already recording, then stop
                if(sampler.isRunning()) {
                    startButton.setText("Start");
                    sampler.drainStop(); //quick - threading
                    mMaker.detectEventBoundaries(); //quick
                    waveVisuals.setLines((int)mMaker.getStartPosition(),(int)mMaker.getEndPosition()); //quick
                    waveVisuals.updateAudioData(mMaker.extractModel()); // slow
                    //modelVisuals.updateAudioData(mMaker.getModel()); // slow
                    Log.d("Samplign time:",(System.currentTimeMillis() - MainActivity.timedebug)+"ms"+" overall:"+(System.currentTimeMillis() - overall ) );
                } else {
                    startButton.setText("Stop");
                    sampler.setCallback(null,"");
                    sampler.ExitOnNoData = false;

                    mMaker.reset();
                    sampler.start();
                    MainActivity.timedebug = 0;
                    overall = System.currentTimeMillis();
                }
            }
        });

//        resetButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                //Delete the current recording, clear the screen. We may need some visual representation that a recording was made too.
//                sampler.stop();
//            }
//        });

        //Adding the button in case the user doesn't know how to use the keyboard
        addFinished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler h = new Handler();
                correctCount = 0;
                try{
                    correctCount = Integer.parseInt(countText.getText().toString());
                }
                catch(Exception e){
                    correctCount = 0;
                }
                Log.i("CORRECT COUNT:",":"+correctCount);
                if( correctCount == 0 ){
                    sendUserMessage("Please add the correct count of events to continue");
                    return;
                }
                dataEntered = true;
                //process
                mMaker.extractModel();
                sampler.setRawInput( mMaker.sample );
                sampler.setCallback(thisReff,"checkResults");
                sampler.ExitOnNoData = true;
                sampler.start();
                h.postDelayed(sampler, 5000);

                LinearLayout myLayout = (LinearLayout) findViewById(R.id.addSound_linLayout);
                for ( int i = 0; i < myLayout.getChildCount();  i++ ){
                    View view = myLayout.getChildAt(i);
                    view.setVisibility(View.GONE); // Or whatever you want to do with the view.
                }
                myLayout.setWeightSum(1);
                TextView pleaseWaitText = new TextView(addActivity.this);
                pleaseWaitText.setTextSize(30);
                pleaseWaitText.setText("Loading..");
                pleaseWaitText.setGravity(1);
                myLayout.addView(pleaseWaitText);
                Log.d("Checking...","Checking model against sample");
            }
        });
        playback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                short[] m = mMaker.extractModel();
                short[] s = mMaker.getModel();
                mMaker.playRaw(s);
                Log.d("_The Model:","len:"+m.length);
            }
        });
    }

    public void returnToMainMenu() {
//////////////////////////////////////////////////////////////////
//        Remember to uncomment this and put in real data
//////////////////////////////////////////////////////////////////
//        MainActivity.libraryDB.insertRow(text1.getText().toString(), text2.getText().toString(), text2.getText().toString(), 1, 1);
        i = new Intent(getBaseContext(), MainActivity.class);
        startActivity(i);
    }

    private void sendUserMessage(String message){
        AlertDialog aDia = new AlertDialog.Builder(addActivity.this).create();
        aDia.setTitle("Oops!");
        aDia.setMessage(message);
        aDia.setButton("Ok!", new DialogInterface.OnClickListener() {
            public void onClick (DialogInterface dialog, int which) {
                //Do we want to do anything here?
                return;
            }
        });
        aDia.show();
    }

    @Override
    public void onBackPressed() {
        if (dataEntered) {
            sendUserMessage("Sorry! You can't go back while a sound is processing!");
        } else {
            super.onBackPressed();
        }
    }
}
