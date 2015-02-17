package felix.views;
import java.util.LinkedList;

import engine.Processing.Processor;
import engine.Processing.Recogniser;
import engine.Processing.algorithms.NaiveRecogniserMk3;
import engine.Processing.algorithms.RawRidgeRecogniser;
import engine.util.Counter;
import engine.util.Data;

import android.media.AudioRecord;
import android.util.Log;
import java.util.ListIterator;
import android.media.AudioTrack;
import android.media.AudioManager;
import android.media.AudioFormat;
/**
 * Created by MLF on 08/02/15.
 */
public class modelMaker extends Recogniser {
    public Data sample;
    public Data model;
    private long startPosition;
    private long endPosition;
    private int stage = 0;
    private Recogniser EventDetector;
    private Recogniser CorrectnessChecker;
    private WaveformView visualiser = null;
    AudioTrack atrack = null;
    short[] audiopadd;
    int minbuflen = 0;

    public void setStartPosition(long position)
    {
        this.startPosition = position;
    }
    public long getStartPosition()
    {
        return this.startPosition;
    }

    public void setEndPosition(long position)
    {
        this.endPosition = position;
    }
    public long getEndPosition()
    {
        return this.endPosition;
    }

    public void setVisualiser(WaveformView w) {
        visualiser = w;
    }

    public modelMaker(Counter counter)
    {
        super(counter);
        sample = new Data();
        model = new Data();
        stage = 0;

        EventDetector = new NaiveRecogniserMk3((double)5000 ,1024, counter); //TODO remove hard coded variables
        CorrectnessChecker = new RawRidgeRecogniser(counter);
        /////////////////////////////////////////////////////
        minbuflen = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        audiopadd = new short[minbuflen+1];
        for( int i = 0 ; i < audiopadd.length ; ++i )
            audiopadd[i] = 0;
    }

    public void process(Data data) {
        if(stage == 0) {
            sample.extend(data);
            EventDetector.process(data);
            if( visualiser != null )
                visualiser.updateAudioData(getSample());
        }
        else {
            CorrectnessChecker.setRawModel(model);
            CorrectnessChecker.process(sample);
        }
    }

    public void reset()
    {
        sample = new Data();
        model = new Data();
        counter.reset();
        stage = 0;
    }

    public void detectEventBoundaries()
    {
        stage = 0;
        LinkedList<Long> positions = EventDetector.getPositions();
        try {
            this.startPosition = positions.get(0);
            this.endPosition = positions.get(1);
        }
        catch(Exception e){
            startPosition = 0;
            endPosition = 0;
        }
        Log.d("BGLABLA:","c:"+counter.getCount()+" s:"+startPosition+" e:"+endPosition);
    }

    public short[] extractModel()
    {
        counter.reset();
        stage = 1;
        double[] modeldata;
        modeldata = new double[(int)(this.endPosition - this.startPosition)];
        double[] thedata = sample.get();
        short[] graphdata = new short[thedata.length];
        int j = 0; long i = this.startPosition;

        for(;i<this.endPosition && i < thedata.length && j < modeldata.length; j++, i++) {
            modeldata[j] = thedata[(int) i];
        }

        for( i = 0 ; i < thedata.length ; ++i )
            graphdata[(int)i] = (short) thedata[(int)i];

        Log.d("DATADATA:","td:"+thedata.length+" md:"+modeldata.length+" s:"+this.startPosition+" e:"+this.endPosition);
        this.model = new Data(modeldata, modeldata.length);

        return graphdata;
    }

    public boolean checkCorrectness(int correct) {
        Log.d("Checking correctness:","C:"+correct+" cnt:"+counter.getCount());
        return ( counter.getCount() == correct && stage ==1 );
    }

    public short[] getModel(){
        double[] modeldata = model.get();
        short[] graphdata = new short[modeldata.length];
        long i = this.startPosition;
        Log.d("DATADATA:"," md:"+modeldata.length+" s:"+this.startPosition+" e:"+this.endPosition);

        for( i = 0 ; i < modeldata.length ; i++)
            graphdata[(int)i] = (short)modeldata[(int)i];

        return graphdata;
    }
    public short[] getSample(){
        double[] modeldata = sample.get();
        short[] graphdata = new short[modeldata.length];
        long i = this.startPosition;

        for( i = 0 ; i < modeldata.length ; i++)
            graphdata[(int)i] = (short)modeldata[(int)i];

        return graphdata;
    }
    public void playbackSelection(){
        cancelPlayback();

        double[] rdta = sample.get();
        int len = rdta.length;
        len += ( len%2 == 1 ) ? 1 : 0 ;
        short[] pbkdata = new short[len];
        int j = 0;
        for( int i = 0 ; i < rdta.length; ++i ) {
            pbkdata[i] = (short)rdta[i];
        }
        pbkdata[len-1] = 0;

        Log.d("PLAYBACK:","len:"+pbkdata.length+" = "+rdta.length+" time:"+((double)pbkdata.length / 44100 ));
        try {
            atrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, pbkdata.length, AudioTrack.MODE_STATIC);
        }catch(Exception e){
            Log.d("ERROR:","Cand ininialise audio track with length:"+rdta.length+" err:"+e);
            return;
        }
        atrack.setPlaybackRate(44100);
        atrack.write(pbkdata, 0, pbkdata.length);
        atrack.play();
        long start = System.currentTimeMillis();
        long now = start;
        long diff = 0;
        do{
            now = System.currentTimeMillis();
            diff = now - start;
        }
        while( diff < ((double)rdta.length*2)/44100 );
        Log.d("DONE!","DONE!");
    }

    public void cancelPlayback(){

       if(atrack != null) {
           atrack.stop();
           atrack.release();
           atrack = null;
       }
    }
    public int totalPlayed=0;
    public void playRaw(short[] raw){
        if(atrack == null ) {
            cancelPlayback();
            try {
                atrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, minbuflen, AudioTrack.MODE_STREAM);
            } catch (Exception e) {
                Log.d("ERROR:", "Cand ininialise audio track with length:" + raw.length + " err:" + e);
                return;
            }
            atrack.setPlaybackRate(44100);
            atrack.play();
        }
        atrack.write(raw, 0, raw.length);
        totalPlayed += raw.length;
        if( minbuflen > totalPlayed )
            atrack.write(audiopadd, 0, minbuflen);
        else
            totalPlayed %= minbuflen;
    }

    public int getSoundLength(){
        return sample.getLength();
    }
}
