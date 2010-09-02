package org.stendardo.bsak.qiblih.views;

import android.view.SurfaceHolder;

public class CompassRenderThread extends Thread{
	private long previousTime = 0;
	private double previousAngle = 0;
	private PointLocatorView view;
	private SurfaceHolder holder;
	private final double degreesPerSecond = 60;
	public CompassRenderThread(PointLocatorView view) {
		this.view = view;
		this.holder = view.getHolder();
	}

	@Override
	public void run() {
		while(!interrupted())
		{
			if (previousTime == 0)
			{
				previousTime = System.currentTimeMillis();
				
				//previousAngle = view.getAzimuth();
				previousAngle = (view.getAzimuth() - view.getCurrentOrientation()+720)%360;
				view.drawCompass(holder,previousAngle);
				continue;
			}
			long currentTime = System.currentTimeMillis();;
			long elapsedTime = currentTime - previousTime;
			previousTime = currentTime;
			double streak = degreesPerSecond * (elapsedTime/1000.);
			//double currentAngle = view.getAzimuth();
			double currentAngle = (view.getAzimuth() - view.getCurrentOrientation()+720)%360;
			double shortestAngle = (currentAngle - previousAngle+360)%360;
			if (shortestAngle > 180)
			{
				shortestAngle -= 360;
			}
			streak *= Math.signum(shortestAngle);
			if (Math.abs(streak)>=Math.abs(shortestAngle))
			{
				previousAngle = currentAngle;
			}
			else
			{
				previousAngle = (previousAngle + streak+360)%360;
			}
			
			view.drawCompass(holder,previousAngle);
			try
			{
				sleep(20);
			}
			catch(InterruptedException e)
			{
				return;
			}
		}
	}

}
