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

    private static final int RECORDER_SAMPLERATE = 44100;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private int audioForm = 0;
    private Handler handler;
    private AudioRecord recorder = null;
    private Thread recordingThread = null;
    private boolean isRecording = false;

    int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
    int BytesPerElement = 2; // 2 bytes in 16bit format
    int minBuffSize;
    byte[] audioData;

    private static int[] mSampleRates = new int[] { 44100 };
    public AudioRecord findAudioRecord() {
        Log.i("AudioCapturer:","Encodings:"+AudioFormat.ENCODING_PCM_8BIT+";"+AudioFormat.ENCODING_PCM_16BIT);
        Log.i("AudioCapturer:","Channel:"+AudioFormat.CHANNEL_IN_MONO);
        for (int rate : mSampleRates) {
            for (short audioFormat : new short[] { AudioFormat.ENCODING_PCM_8BIT , AudioFormat.ENCODING_PCM_16BIT }) {
                    try {
                        Log.i("AudioCapturer:", "Attempting rate " + rate + "Hz, bytes: " + audioFormat + ", channel: " + AudioFormat.CHANNEL_IN_MONO);
                        audioForm = audioFormat;
                        int bufferSize = AudioRecord.getMinBufferSize(rate, AudioFormat.CHANNEL_IN_MONO, audioFormat);

                        if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
                            // check if we can instantiate and have a success
                            AudioRecord recorder = new AudioRecord(AudioSource.DEFAULT, rate, AudioFormat.CHANNEL_IN_MONO, audioFormat, bufferSize);

                            if (recorder.getState() == AudioRecord.STATE_INITIALIZED)
                                return recorder;
                        }
                    } catch (Exception e) {
                        Log.e("AudioCapturer:", rate + "Exception, keep trying.",e);
                    }
            }
        }
        return null;
    }

    public void _stop(){

    }

    //THREADING
    private void updateUI(int val){
        Message m = new Message();
        m.arg1 = val;
        handler.sendMessage(m);
    }
    public MicrophoneIn(){
        Log.i("AudioCapturer:","Initialising recorder...");
        //(int audioSource, int sampleRateInHz, int channelConfig, int audioFormat, int bufferSizeInBytes)
        recorder = findAudioRecord();

        if(recorder == null || recorder.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.e("Microphone:","Error, could not initialize audio");
        }
        else {
            Log.i("AudioCapturer:","Found configuration");
            int minBufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
                    RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
        }
    }
    public static short[] shortMe(byte[] bytes) {
        short[] out = new short[bytes.length / 2]; // will drop last byte if odd number
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        for (int i = 0; i < out.length; i++) {
            out[i] = bb.getShort();
        }
        //fft
        /*DoubleFFT_1D fftDo = new DoubleFFT_1D(out.length);
        double []samplefft = new double[out.length * 2];
        fftDo.realForwardFull(samplefft);

        int j = 0;
        for( int i = 0 ; i < samplefft.length; i+=2 )
            out[j++] = (short)samplefft[i];
        */

        return out;
    }
    public void run(){
        if(recorder != null)
        {
            Log.i("DATA:","Starting recording...");
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
            audioData = new byte[BufferElements2Rec * BytesPerElement];
            recorder.startRecording();
            isRecording = true;
            while(canRun) {
                int count = 0;
                int len = recorder.read(audioData, 0, BufferElements2Rec);
                //Log.d("a","WORKING");
                if(audioData != null) {

                    MainActivity.waveVisuals.updateAudioData(shortMe(audioData));
                    Data d = new Data(audioData,len,2,true,false);
                    push(d);
                    //Log.d("D:",":"+d.toString());
                }
            }
            recorder.stop();

            isRecording = false;
            recorder.stop();
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

