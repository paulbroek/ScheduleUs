package nl.mprog.scheduleus;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul Broek on 1-6-2015.
 * 10279741
 * pauliusbroek@hotmail.com
 * DrawingViews are Views where user can draw a line for user time input
 */

public class DrawingView extends View implements View.OnClickListener{

    public int width;
    public int height;
    public int min_clocktime;
    public int max_clocktime;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    Context context;
    private Paint circlePaint;
    private Path circlePath;
    private Paint mPaint;
    private Paint mLines;
    private TextView up;
    private float pixels_per_hour;
    List <int[]> AvailableSlots;

    public DrawingView(Context c) {
        super(c);
        context=c;

        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        circlePaint = new Paint();
        circlePath = new Path();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.BLUE);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeJoin(Paint.Join.MITER);
        circlePaint.setStrokeWidth(4f);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(25);

        min_clocktime = 9;
        max_clocktime = 24;

    }

    public DrawingView(Context c, AttributeSet attrs) {
        super(c, attrs);
        context = c;

        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        circlePaint = new Paint();
        circlePath = new Path();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.BLUE);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeJoin(Paint.Join.MITER);
        circlePaint.setStrokeWidth(4f);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.FILL);

        mLines = new Paint();
        mLines.setColor(Color.BLACK);
        mLines.setStrokeWidth(2);

        min_clocktime = 9;
        max_clocktime = 24;


    }

    public DrawingView(Context c, AttributeSet attrs, int defStyle) {
        super(c, attrs, defStyle);
        context = c;
    }

    void reDraw() {
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        int n_lines = max_clocktime - min_clocktime;
        for (int i = 0; i < n_lines; i++)
            mCanvas.drawLine(width,i*height/n_lines,width-width/4,i*height/n_lines,mLines);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        width = w;
        height = h;

        int n_lines = max_clocktime - min_clocktime;
        for (int i = 0; i < n_lines; i++)
            mCanvas.drawLine(w,i*h/n_lines,w-w/4,i*h/n_lines,mLines);

        pixels_per_hour = (float) height / (max_clocktime-min_clocktime);
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(getContext(), "Werkt", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath( mPath,  mPaint);
        canvas.drawPath( circlePath,  circlePaint);
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 0;

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }
    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {

            mPaint.setStyle(Paint.Style.FILL);

            mCanvas.drawRect(0,mY,width,y,mPaint);

            mX = x;
            mY = y;

            circlePath.reset();
            circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
        }
    }
    private void touch_up() {
        mPath.lineTo(mX, mY);
        circlePath.reset();
        // commit the path to our offscreen
        //mCanvas.drawPath(mPath,  mPaint);

        // kill this so we don't double draw
        mPath.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }

    // Returns number of white lines in the DrawableView
    public float getWhiteLines() {
        int lines = 0;

        for (int y = 0; y < height; y++)
        {
            Boolean white_line = true;
            for (int x = 0; x < width; x++)
            {
                if (mBitmap.getPixel(x,y) != 0)
                    white_line = false;
            }
            if (white_line)
                lines++;
        }
        /*int test = 0;
        if (mBitmap.getWidth() > 3)
            test = mBitmap.getWidth();
        return test;*/

        int test = mBitmap.getWidth();
        return (float) lines;
    }

    // Returns bool array with availability per pixel
    public Boolean[] getAvailabilityArray() {
        Boolean[] availArray = new Boolean[height];

        for (int y = 0; y < height; y++)
        {
            availArray[y] = true;
            Boolean white_line = true;
            for (int x = 0; x < 3; x++)
            {
                // Pixel not white
                if (mBitmap.getPixel(x,y) != 0)
                    white_line = false;
            }

                availArray[y] = !white_line;
        }

        return availArray;
    }

    // Less than TIME_TOLERANCE will not be considered a time slot
    private static final int TIME_TOLERANCE = 5;
    private static final int MINUTE_ENTITY = 15;

    // List with pairs of available times like (10,11)
    public List<int[]> getAvailabilityList() {
        Boolean[] availArray = getAvailabilityArray();

        AvailableSlots = new ArrayList<int[]>();

        // Search for blocks of available time
        int j = 0;
        for (int i = 0; i < availArray.length; i++) {
            if (availArray[i] && i != (availArray.length-1))
                j++;
            else {
                // Add this time slot if its big enough, convert pixels to clock time
                if (j > TIME_TOLERANCE) {

                    int begin_hour = (int) (min_clocktime + (i-j) / pixels_per_hour);
                    int begin_minutes = (int)((((i-j) % pixels_per_hour)/pixels_per_hour)*60);
                    int begin_quarter = (begin_minutes / 15)*15;
                    if (Math.abs(begin_quarter - begin_minutes) > 7)
                        begin_quarter+=15;
                    if (begin_quarter == 60) {
                        begin_hour++;
                        begin_quarter = 0;
                    }
                    int end_hour = (int) (min_clocktime + i / pixels_per_hour);
                    int end_minutes = (int)(((i % pixels_per_hour)/pixels_per_hour)*60);
                    int end_quarter = (end_minutes / 15)*15;
                    if (Math.abs(end_quarter-end_minutes) > 7)
                        end_quarter+=15;
                    if (end_quarter == 60) {
                        end_hour++;
                        end_quarter = 0;
                    }
                    int begin_time = begin_hour * 100 + begin_quarter;
                    int end_time = end_hour * 100 + end_quarter;

                    AvailableSlots.add(new int[]{begin_hour,begin_quarter,end_hour,end_quarter});
                }
                j = 0;
            }
        }

        return AvailableSlots;
    }

    public float getTimeSize() {
        return getWhiteLines();
    }
}
