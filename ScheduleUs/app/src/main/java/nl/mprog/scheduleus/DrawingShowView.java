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
 * Created by Paul Broek on 17-6-2015.
 * 10279741
 * pauliusbroek@hotmail.com
 * DrawingShowView is a showing a DrawingView, but user cannot swipe but only click it
 */

public class DrawingShowView extends View {

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
    private Paint mPaintPersonal;
    private Paint mLines;
    private TextView up;
    public float pixels_per_hour;
    private boolean[] availArray;
    private ArrayList<Boolean> availList;
    List <int[]> SharedAvailableSlots;
    List <int[]> PersonalAvailableSlots;

    public DrawingShowView(Context c) {
        super(c);
        context=c;
    }

    public DrawingShowView(Context c, AttributeSet attrs) {
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

        mPaintPersonal = new Paint();
        mPaintPersonal.setAntiAlias(true);
        mPaintPersonal.setDither(true);
        //Color lightGreen = new Color(255,102,153);
        mPaintPersonal.setColor(getResources().getColor(R.color.lightgreen));
        mPaintPersonal.setStyle(Paint.Style.FILL);

        mLines = new Paint();
        mLines.setColor(Color.BLACK);
        mLines.setStrokeWidth(2);

        min_clocktime = 9;
        max_clocktime = 24;



    }

    public DrawingShowView(Context c, AttributeSet attrs, int defStyle) {
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

        pixels_per_hour = (float) height / (max_clocktime-min_clocktime);

        // If user input already exists, we can draw this
        /*if (availList != null)
            for (int i = 0; i < availList.size(); i++)
                if (availList.get(i))
                    mCanvas.drawLine(width,i,0,i,mPaint);*/

        // First draw personal slots
        if (PersonalAvailableSlots != null)
            for (int i = 0; i < PersonalAvailableSlots.size(); i++) {

                /*for (int p = 0; p < 50*i; p++) {
                    mCanvas.drawLine(width,p,0,p,mPaint);
                }*/


                int beginhours = PersonalAvailableSlots.get(i)[0];
                int beginquarters = PersonalAvailableSlots.get(i)[1];
                int endhours = PersonalAvailableSlots.get(i)[2];
                int endquarters = PersonalAvailableSlots.get(i)[3];
                double n_hours = endhours + endquarters/60.0 - beginhours - beginquarters/60.0;

                double begin = (beginhours + beginquarters / 60.0 - min_clocktime)*pixels_per_hour;
                double n_pixels = n_hours*pixels_per_hour;
                for (int j = 0; j < (int) n_pixels; j++)
                    mCanvas.drawLine(width,(int)begin+j,0,(int)begin+j,mPaintPersonal);

                }

        // Now draw shared slots (partly over personal slots)
        if (SharedAvailableSlots != null)
            for (int i = 0; i < SharedAvailableSlots.size(); i++) {

                /*for (int p = 0; p < 50*i; p++) {
                    mCanvas.drawLine(width,p,0,p,mPaint);
                }*/


                int beginhours = SharedAvailableSlots.get(i)[0];
                int beginquarters = SharedAvailableSlots.get(i)[1];
                int endhours = SharedAvailableSlots.get(i)[2];
                int endquarters = SharedAvailableSlots.get(i)[3];
                double n_hours = endhours + endquarters/60.0 - beginhours - beginquarters/60.0;

                double begin = (beginhours + beginquarters / 60.0 - min_clocktime)*pixels_per_hour;
                double n_pixels = n_hours*pixels_per_hour;
                for (int j = 0; j < (int) n_pixels; j++)
                    mCanvas.drawLine(width,(int)begin+j,0,(int)begin+j,mPaint);

            }

        int n_lines = max_clocktime - min_clocktime;
        for (int i = 0; i < n_lines; i++)
            mCanvas.drawLine(w,i*h/n_lines,w-w/4,i*h/n_lines,mLines);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mPaint);
        canvas.drawPath(circlePath, circlePaint);
        /*for (int i = 0; i < 50; i++)
            if (true)
                canvas.drawLine(width,i,0,i,mPaint);*/

        /*try {
            for (int i = 0; i < availList.size(); i++) {
                canvas.drawLine(width,i,0,i,mPaint);
            }
        } catch (NullPointerException e)  {

        }*/
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 0;

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

        return (float) lines;
    }

    // Returns bool array with availability per pixel
    public boolean[] getAvailabilityArray() {
        boolean[] availArray = new boolean[height];

        for (int y = 0; y < height; y++)
        {
            availArray[y] = true;
            boolean white_line = true;
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

    public void setSharedAvailabilityList(ArrayList<int[]> l) {
        this.SharedAvailableSlots = l;
    }
    public void setPersonalAvailabilityList(ArrayList<int[]> l) {
        this.PersonalAvailableSlots = l;
    }

    private static final int TIME_TOLERANCE = 5;
    private static final int MINUTE_ENTITY = 15;

    // List with pairs of available times like (10,11)
    public List<int[]> getAvailabilityList() {
        boolean[] availArray = getAvailabilityArray();

        SharedAvailableSlots = new ArrayList<int[]>();

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

                    SharedAvailableSlots.add(new int[]{begin_hour,begin_quarter,end_hour,end_quarter});

                }
                j = 0;
            }
        }

        return SharedAvailableSlots;
    }

    public float getTimeSize() {
        return getWhiteLines();
    }
}