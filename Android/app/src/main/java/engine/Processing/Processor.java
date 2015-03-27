/*
* Author: Milorad Liviu Felix & Pedro Avelar
* Sat 6 December 2014 17:49GMT
* Interface for the sound recognition algorithm
*/
package engine.Processing;
// Code with explanatory comments in ./Desktop/src/engine
import java.util.concurrent.atomic.AtomicBoolean;
import engine.audio.*;
import engine.util.*;
import engine.Processing.algorithms.*;

import android.util.Log;
import java.io.*;
import android.content.Context;
import rory.bain.counter.app.MainActivity;
// Code with explanatory comments in ./Desktop/src/engine
public class Processor implements Runnable
{
    private AudioIn audioIn;
    private Recogniser n;
    private Thread t;
    private AtomicBoolean running;
    private boolean canRun;
    private Counter count;
    private Object callback;
    private String callbackMethod;
    //warning only set before starting processor ( unsafe to set otherwise )
    public boolean ExitOnNoData;

    void _init(Counter c){
        ExitOnNoData = false;
        callback = null;
        count = c;
        running = new AtomicBoolean(false);
        canRun = false;
        audioIn = null;
    }
    public Processor(Counter c)
    {
        _init(c);
        n = new NaiveRecogniserMk3((double)5000, 512, count);
        //n = new FastRidgeRecogniser(count);
    }
    public Processor(Counter c, Recogniser r)
    {
        _init(c);
        n = r;
    }
    public synchronized void setCallback( Object c, String method ){
        callback  = c;
        callbackMethod = method;
    }
    public synchronized void setModel(String path){
        n.setModel(path);
    }
    public synchronized void setRawModel(Data d){
        n.setRawModel(d);
    }
    public synchronized void setInput(String nameOrPath){
        canRun = false;
        if( audioIn != null )
        {
            audioIn.stop();
            audioIn = null;
        }

        if( nameOrPath.equals(".../microphone"))
            audioIn = new MicrophoneIn();
        else
            audioIn = new FileIn(nameOrPath);
    }

    public synchronized void setRawInput( Data i ){
        audioIn = new AudioIn();
        audioIn.push(i);
    }

    public void drain(){
        while( true )
        {
            Data d = audioIn.getNext();
            if( d != null )
                n.process(d);
            else {
                break;
            }
        }
    }

    public void run(){
        running.set(true);
        while(canRun)
        {

            Data d = audioIn.getNext();
            if( d != null )
                n.process(d);
            else {

                if( callback != null ) {
                    System.out.println("Calling:"+callbackMethod);
                    try {
                        callback.getClass().getMethod(callbackMethod).invoke(callback);
                    } catch (SecurityException e) {
                        Log.d("Processor Callback:"," Security exception: "+ e);
                    } catch (NoSuchMethodException e) {
                        Log.d("Processor Callback:"," No such method : "+ e);
                    } catch (Exception e) {
                        Log.d("Processor Callback:"," Run error: "+ e);
                        for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
                            System.out.println(ste);
                        }
                    }
                }
                if(ExitOnNoData)
                    break;
            }
        }
        running.set(false);
    }

    protected void _init(){
        if(running.get() == true)
            stop();

        canRun = true;
        if( audioIn == null )
            audioIn= new MicrophoneIn();
    }

    public void start(){
        _init();

        t = new Thread(this);
        t.start();
        audioIn.start();
    }

    public void blockingRun(){
        _init();
        audioIn.start();
        run();
        audioIn.stop();
        stop();
    }
    public void stop(){
        canRun = false;
        if(audioIn != null)
        {
            audioIn.stop();
            audioIn = null;
        }
        if( t != null)
        {
            try {
                t.join();
            } catch ( Exception e){
                t = null;
            }
        }
    }

    public void drainStop(){
        canRun = false;
        if( t != null)
        {
            try {
                t.join();
            } catch ( Exception e){
                t = null;
            }
        }
        if(audioIn != null)
        {
            audioIn.drainStop();
            drain();
            audioIn = null;
        }
    }

    public boolean isRunning(){
        return running.get();
    }
}
