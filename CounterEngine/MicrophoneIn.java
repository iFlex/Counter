package com.example.mlf.amplitudecounter;
import android.media.*;
import android.media.MediaRecorder.AudioSource;
import android.util.Log;
import android.os.*;
/*
 * Created by MLF on 17/11/14.
 */

public class AudioCapturer extends AudioIn{
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
    //specific
    int treshold = 1000;
    private static int[] mSampleRates = new int[] { 44100, 22050,11025,8000 };
    public AudioRecord findAudioRecord() {
        for (int rate : mSampleRates) {
            for (short audioFormat : new short[] { AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT }) {
                for (short channelConfig : new short[] { AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO }) {
                    try {
                        Log.d("Data:", "Attempting rate " + rate + "Hz, bits: " + audioFormat + ", channel: "
                                + channelConfig);
                        audioForm = audioFormat;
                        int bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);

                        if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
                            // check if we can instantiate and have a success
                            AudioRecord recorder = new AudioRecord(AudioSource.DEFAULT, rate, channelConfig, audioFormat, bufferSize);

                            if (recorder.getState() == AudioRecord.STATE_INITIALIZED)
                                return recorder;
                        }
                    } catch (Exception e) {
                        Log.e("Error:", rate + "Exception, keep trying.",e);
                    }
                }
            }
        }
        return null;
    }

    public AudioCapturer(Handler h){
        handler = h;
        Log.i("AudioCapturer:","Initialising recorder...");
        //(int audioSource, int sampleRateInHz, int channelConfig, int audioFormat, int bufferSizeInBytes)
        recorder = findAudioRecord();
        if(recorder.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.i("DATA:","Error, could not initialize audio");
        }
        else {
            int minBufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
                    RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
        }
    }

    public void start(){
        Log.i("DATA:","Starting recording...");

        audioData = new byte[BufferElements2Rec * BytesPerElement];
        recorder.startRecording();
        isRecording = true;
        recordingThread = new Thread(new Runnable() {
            public void run() {
                while(true) {
                    int count = 0;
                    int len = recorder.read(audioData, 0, BufferElements2Rec);

                    if(audioData == null)
                        return;

                    new Data(audioData);
                }
            }
        }, "AudioRecorder Thread");
        recordingThread.start();
    }
    public void stop(){
        // stops the recording activity
        if (null != recorder) {
            Log.i("DATA:","Stopping recording");

            isRecording = false;
            recorder.stop();
            recorder.release();
            recorder = null;
            recordingThread = null;
        }
    }

   //THREADING
   private void updateUI(int val){
        Message m = new Message();
        m.arg1 = val;
        handler.sendMessage(m);
   }

}

