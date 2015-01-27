package engine.audio;

import android.media.*;
import android.media.MediaRecorder.AudioSource;
import android.util.Log;
import android.os.*;

import engine.util.Data;
import rory.bain.counter.app.MainActivity;
import java.nio.ByteBuffer;
import org.jtransforms.fft.DoubleFFT_1D;
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
        mAudioBuffer = new short[ mBufferSize / 2 ];
        recorder = new AudioRecord(AudioSource.MIC, SAMPLING_RATE,AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, mBufferSize);
        Log.d("Recorder:","bufflen:"+mBufferSize+" rec:"+recorder);
    }

    public void run(){
        if(recorder != null) {
            Log.i("DATA:","Starting recording...");
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);

            //FFT
            DoubleFFT_1D fftDo = new DoubleFFT_1D(mBufferSize/2);

            recorder.startRecording();
            canRun = true;
            isRecording = true;
            while(canRun) {
                int count = 0;
                int len = recorder.read(mAudioBuffer, 0, mBufferSize / 2);
                if(mAudioBuffer != null) {
                    Data d = new Data(mAudioBuffer,len);
                    push(d);
                    /*
                    double[] dta = d.get();
                    double[] fftdta = new double[dta.length*2];

                    for( int i = 0; i < dta.length; ++i )
                        fftdta[i] = dta[i];

                    fftDo.realForwardFull(fftdta);
                    for( int i = 0; i < dta.length ; i++ )
                        mAudioBuffer[i] = (short)fftdta[i*2];
                    */
                    MainActivity.waveVisuals.updateAudioData(mAudioBuffer);
                    //Log.d("D:",":"+d.toString());
                }
                else
                    Log.d("NODATA","...");
            }
            recorder.stop();

            isRecording = false;
            /*
            recorder.release();
            recorder = null;
            recordingThread = null;
            */
        }
        else
            Log.d("Info:","Running thread but recorder is not valid");
    }
}

