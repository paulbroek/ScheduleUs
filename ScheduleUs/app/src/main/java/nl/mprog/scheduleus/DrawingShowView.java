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
    public float pixels_per_hour;

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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        width = w;
        height = h;

        pixels_per_hour = (float) height / (max_clocktime-min_clocktime);

        // First draw personal slots
        if (PersonalAvailableSlots != null)
            for (int i = 0; i < PersonalAvailableSlots.size(); i++) {

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

        // Now draw shared slots (partly overlapping personal slots)
        if (SharedAvailableSlots != null)
            for (int i = 0; i < SharedAvailableSlots.size(); i++) {

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
            mCanvas.drawLine(w, i*h/n_lines, w-w/4, i*h/n_lines, mLines);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mPaint);
        canvas.drawPath(circlePath, circlePaint);
    }

    public void setSharedAvailabilityList(ArrayList<int[]> l) {
        this.SharedAvailableSlots = l;
    }
    public void setPersonalAvailabilityList(ArrayList<int[]> l) {
        this.PersonalAvailableSlots = l;
    }
}