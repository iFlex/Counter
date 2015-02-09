package felix.views;
import java.util.LinkedList;

import engine.Processing.Processor;
import engine.Processing.Recogniser;
import engine.Processing.algorithms.NaiveRecogniserMk3;
import engine.Processing.algorithms.RawRidgeRecogniser;
import engine.util.Counter;
import engine.util.Data;
import android.util.Log;
import java.util.ListIterator;
import android.media.AudioTrack;
import android.media.AudioManager;
import android.media.AudioFormat;
/**
 * Created by MLF on 08/02/15.
 */
public class modelMaker extends Recogniser {
    private Data data;
    private Data model;
    private long startPosition;
    private long endPosition;


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

    public modelMaker(Counter counter)
    {
        super(counter);
        this.data = new Data();
        this.model = new Data();
    }

    public void process(Data data) {
        this.data.extend(data);
        Log.d("god new frame:",":"+data.get().length);
    }

    public void reset()
    {
        this.data = new Data();
        this.counter.reset();
    }

    public void detectEvent()
    {
        Counter nr_counter = new Counter();
        NaiveRecogniserMk3 nr = new NaiveRecogniserMk3((double)5000 ,1024, nr_counter); //TODO remove hard coded variables
        nr.process(this.data);
        LinkedList<Long> positions = nr.getPositions();
        try {
            this.startPosition = positions.get(0);
            this.endPosition = positions.get(1);
        }
        catch(Exception e){
            startPosition = 0;
            endPosition = data.get().length/2;
        }
        Log.d("BGLABLA:","c:"+nr_counter.getCount()+" s:"+startPosition+" e:"+endPosition);
    }

    public short[] extractModel()
    {
        double[] modeldata;
        modeldata = new double[(int)(this.endPosition - this.startPosition)];
        double[] thedata = this.data.get();
        short[] graphdata = new short[thedata.length];
        int j = 0; long i = this.startPosition;

        for(;i<this.endPosition; j++, i++)
            modeldata[j] = thedata[(int)i];

        for(i=0;i<thedata.length;++i)
            graphdata[(int)i] = (short) thedata[(int)i];

        Log.d("DATADATA:","td:"+thedata.length+" md:"+modeldata.length);
        this.model = new Data(modeldata, modeldata.length);

        return graphdata;
    }

    public boolean checkCorrectness(int count)
    {
        Counter c = new Counter();
        extractModel();
        RawRidgeRecogniser rr = new RawRidgeRecogniser(c);
        rr.setRawModel(this.model);
        rr.process(this.data);
        Log.d("RRRRRRR:","c:"+c.getCount()+" cr:"+count);
        return (c.getCount() == count);
    }

    public short[] getModel(){
        double[] modeldata = model.get();
        short[] graphdata = new short[modeldata.length];
        long i = this.startPosition;

        for( i = 0 ; i < modeldata.length ; i++)
            graphdata[(int)i] = (short)modeldata[(int)i];

        return graphdata;
    }
    AudioTrack atrack = null;
    public void playbackSelection(){
        cancelPlayback();

        double[] rdta = data.get();
        short[] pbkdata = new short[rdta.length];
        int j = 0;
        for( int i = 0 ; i < rdta.length; ++i ) {
            pbkdata[i] = (short)rdta[i];
            //pbkdata[j++] = (byte)((int)rdta[i] & 0xff);
            //pbkdata[j++] = (byte)(((int)rdta[i]>>8) & 0xff);
        }
        Log.d("PLAYBACK:","len:"+pbkdata.length);
        atrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT,pbkdata.length, AudioTrack.MODE_STATIC);
        atrack.setPlaybackRate(44100);
        atrack.write(pbkdata, 0, pbkdata.length);
        atrack.play();
    }

    public void cancelPlayback(){

       if(atrack != null) {
           atrack.stop();
           atrack.release();
           atrack = null;
       }
    }
}
