package felix.views;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.SurfaceView;
import java.util.LinkedList;
/**
 * Created by MLF on 26/01/15.
 */
public class WaveformView extends SurfaceView {

    // The number of buffer frames to keep around (for a nice fade-out visualization).
    private static int HISTORY_SIZE = 6;
    // To make quieter sounds still show up well on the display, we use +/- 8192 as the amplitude
    // that reaches the top/bottom of the view instead of +/- 32767. Any samples that have
    // magnitude higher than this limit will simply be clipped during drawing.
    private static final float MAX_AMPLITUDE_TO_DRAW = 8192.0f*2;
    // The queue that will hold historical audio data.
    private final LinkedList<short[]> mAudioData;
    private final Paint mPaint;
    private int line1,line2;
    public void setLines(int line1, int line2){
        this.line1 = line1;
        this.line2 = line2;
    }

    private void _style(){
        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSPARENT);
    }
    public WaveformView(Context context) {
        this(context, null, 0); _style();
    }

    public WaveformView(Context context, AttributeSet attrs) {
        this(context, attrs, 0); _style();
    }
    public void setHistorySize(int size)
    {
        WaveformView.HISTORY_SIZE = size;
    }
    public WaveformView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        _style();
        line1 = line2 = 0;
        mAudioData = new LinkedList<short[]>();

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(0);
        mPaint.setAntiAlias(true);
    }

    /**
     * Updates the waveform view with a new "frame" of samples and renders it. The new frame gets
     * added to the front of the rendering queue, pushing the previous frames back, causing them to
     * be faded out visually.
     *
     * @param buffer the most recent buffer of audio samples
     */
    public synchronized void redraw(){
        // Update the display.
        Canvas canvas = getHolder().lockCanvas();
        if (canvas != null) {
            drawWaveform(canvas);
            getHolder().unlockCanvasAndPost(canvas);
        }
    }
    public synchronized void updateAudioData(short[] buffer) {
        short[] newBuffer;

        // We want to keep a small amount of history in the view to provide a nice fading effect.
        // We use a linked list that we treat as a queue for this.
        if (mAudioData.size() == HISTORY_SIZE) {
            newBuffer = mAudioData.removeFirst();
            if(newBuffer.length < buffer.length)
                newBuffer = buffer.clone();
            else
                System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
        } else {
            newBuffer = buffer.clone();
        }

        mAudioData.addLast(newBuffer);

        redraw();
    }

    /**
     * Repaints the view's surface.
     *
     * @param canvas the {@link Canvas} object on which to draw
     */
    private void drawWaveform(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        // Clear the screen each time because SurfaceView won't do this for us.
        float width = getWidth();
        float height = getHeight();
        float centerY = height / 2;

        // We draw the history from oldest to newest so that the older audio data is further back
        // and darker than the most recent data.
        int colorDelta = 255 / (HISTORY_SIZE + 1);
        int brightness = colorDelta;

        for (short[] buffer : mAudioData) {
            mPaint.setColor(Color.argb(brightness, 0, 0, 0));

            float lastX = -1;
            float lastY = -1;

            // For efficiency, we don't draw all of the samples in the buffer, but only the ones
            // that align with pixel boundaries.
            if( buffer.length == 0 )
                continue;

            for (int x = 0; x < width; x++) {
                int index = (int) ((x / width) * buffer.length);
                short sample = buffer[index];
                float y = (sample / MAX_AMPLITUDE_TO_DRAW) * centerY + centerY;
                float truy = y;

                if( index > line1 && index < line2 )
                {
                    mPaint.setColor(Color.argb(127, 16, 127, 212));
                    float ly = -height;
                    float cy = height;
                    if (lastX != -1)
                        canvas.drawLine(lastX, ly, x, cy, mPaint);
                }
                mPaint.setColor(Color.argb(brightness, 0, 0, 0));

                if (lastX != -1)
                    canvas.drawLine(lastX, lastY, x, y, mPaint);

                lastX = x;
                lastY = truy;
            }

            brightness += colorDelta;
        }
    }
}