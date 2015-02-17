package engine.audio;

import android.media.*;
import android.media.MediaRecorder.AudioSource;
import android.util.Log;
import android.os.*;

import engine.util.Data;
import rory.bain.counter.app.MainActivity;
import java.nio.ByteBuffer;
import org.jtransforms.fft.DoubleFFT_1D;
import java.util.Arrays;
/**
 * Created by MLF on 07/12/14.
 */
public class MicrophoneIn extends AudioIn {

    private static final int SAMPLING_RATE = 44100;
    private int audioForm = 0;
    int mBufferSize;
    short[] mAudioBuffer;
    AudioRecord recorder;

    private Handler handler;
    private Thread recordingThread = null;
    private boolean isRecording = false;

    //THREADING
    private void updateUI(int val){
        Message m = new Message();
        m.arg1 = val;
        handler.sendMessage(m);
    }

    public MicrophoneIn() {
        Log.i("AudioCapturer:", "Initialising recorder...");
        //TODO: put init here

        mBufferSize = AudioRecord.getMinBufferSize(SAMPLING_RATE, AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);
        mAudioBuffer = new short[ mBufferSize ];
        recorder = new AudioRecord(AudioSource.MIC, SAMPLING_RATE,AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, mBufferSize);
        Log.d("Recorder:","bufflen:"+mBufferSize+" rec:"+recorder);
    }

    public void run(){
        if(recorder != null) {
            Log.i("DATA:","Starting recording...");
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);

            recorder.startRecording();
            canRun = true;
            isRecording = true;
            if( MainActivity.timedebug == 0 )
                MainActivity.timedebug = System.currentTimeMillis();

            while(canRun) {
                int count = 0;
                int len = recorder.read(mAudioBuffer, 0, mBufferSize);
                //MainActivity.rec[MainActivity.recpos++] =  Arrays.copyOf(mAudioBuffer, len);

                if(mAudioBuffer != null) {
                    Data d = new Data(mAudioBuffer,len);
                    push(d);
                    //MainActivity.waveVisuals.updateAudioData(mAudioBuffer);
                }
                else
                    Log.d("NODATA","...");
            }
            recorder.stop();

            isRecording = false;

            recorder.release();
            recorder = null;
            /*
            recordingThread = null;
            */
        }
        else
            Log.d("Info:","Running thread but recorder is not valid");
    }
}

