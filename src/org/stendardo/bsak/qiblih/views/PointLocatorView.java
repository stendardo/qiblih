package org.stendardo.bsak.qiblih.views;

import org.stendardo.bsak.qiblih.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class PointLocatorView extends SurfaceView implements SurfaceHolder.Callback{
	private Thread rendererThread;
	private BitmapDrawable compass;
	private BitmapDrawable arrow;
	private BitmapDrawable markers;
	private double azimuth = 0.0;
	private double currentOrientation = 0.0;
	private Paint paint = new Paint();
	public void init(AttributeSet as)
	{
		Resources res = this.getContext().getResources();
		this.compass = (BitmapDrawable)res.getDrawable(R.drawable.plain_compass);
		this.arrow = (BitmapDrawable)res.getDrawable(R.drawable.bahai_arrow);
		this.markers = (BitmapDrawable)res.getDrawable(R.drawable.markers);
		this.getHolder().addCallback(this);
	}
	public BitmapDrawable getCompass() {
		return compass;
	}
	public void setCompass(BitmapDrawable compass) {
		this.compass = compass;
	}
	public BitmapDrawable getArrow() {
		return arrow;
	}
	public void setArrow(BitmapDrawable arrow) {
		this.arrow = arrow;
	}
	public PointLocatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}
	public PointLocatorView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}
	public synchronized  double getAzimuth() {
		return azimuth;
	}
	public synchronized void setAzimuth(double azimuth) {
		this.azimuth = azimuth;
	}
	
	public synchronized double getCurrentOrientation() {
		return currentOrientation;
	}
	public synchronized void setCurrentOrientation(double currentOrientation) {
		this.currentOrientation = currentOrientation;
	}
	/**
     * @see android.view.View#measure(int, int)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    	int side = Math.min(getMeasuredWidth(),getMeasuredHeight());
    	setMeasuredDimension(side, side);
    	/*int width = measureWidthHeight(widthMeasureSpec);
    	int height = measureWidthHeight(heightMeasureSpec);
        setMeasuredDimension(width,height);*/
    }

    /**
     * Determines the width of this view
     * @param measureSpec A measureSpec packed into an int
     * @return The width of the view, honoring constraints from measureSpec
     */
    private int measureWidthHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
        	Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        	result = Math.min(display.getWidth(),display.getHeight()); 
            

            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by measureSpec
                result = Math.min(result, specSize);
            }
        }

        return result;
    }
	public void drawCompass(SurfaceHolder h,double angle) {
		Canvas canvas = h.lockCanvas(null);
		
		Matrix s = new Matrix();
		s.postScale((float)getWidth()/compass.getBitmap().getWidth(), (float)getWidth()/compass.getBitmap().getWidth());
		Matrix m = new Matrix(s);
		m.postRotate((float)(angle), getWidth()/2, getHeight()/2);
		Matrix m2 = new Matrix(s);
		m2.postRotate((float)(angle-azimuth+360)%360, getWidth()/2, getHeight()/2);
		canvas.drawBitmap(compass.getBitmap(),s,paint);
		canvas.drawBitmap(markers.getBitmap(), m2, paint);
		canvas.drawBitmap(arrow.getBitmap(),m,paint);
		h.unlockCanvasAndPost(canvas);
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		
		rendererThread = new CompassRenderThread(this);
		rendererThread.start();
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (rendererThread != null)
		{
			rendererThread.interrupt();
			try
			{
				rendererThread.join();
			}
			catch(InterruptedException e)
			{
			}
		}
		
	}
	public Thread getRendererThread() {
		return rendererThread;
	}
	
}
