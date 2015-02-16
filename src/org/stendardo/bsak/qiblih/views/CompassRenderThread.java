package org.stendardo.bsak.qiblih.views;


public class CompassRenderThread extends Thread{
	private long previousTime = 0;
	private double previousAngle = 0;
	private PointLocatorView view;
	
	private final double degreesPerSecond = 60;
	private boolean isRendering = false;
	public CompassRenderThread(PointLocatorView view) {
		this.view = view;
	}
	
	public synchronized void startRendering()
	{
		isRendering = true;
		notify();
	}
	
	public synchronized void stopRendering()
	{
		isRendering = false;
	}
	
	@Override
	public void run() {
		
		while(!interrupted())
		{
			
			synchronized(this) {
				while (!isRendering)
				{
					try
					{
						wait();
					}
					catch(InterruptedException e)
					{
						return;
					}
					
				}
				if (previousTime == 0)
				{
					previousTime = System.currentTimeMillis();
					
					//previousAngle = view.getAzimuth();
					previousAngle = (view.getAzimuth() - view.getCurrentOrientation()+720)%360;
					view.drawCompass(view.getHolder(),previousAngle);
					continue;
				}
				long currentTime = System.currentTimeMillis();;
				long elapsedTime = currentTime - previousTime;
				previousTime = currentTime;
				double streak = degreesPerSecond * (elapsedTime/1000.);
				//double currentAngle = view.getAzimuth();
				double currentAngle = (view.getAzimuth() - view.getCurrentOrientation()+720)%360;
				double shortestAngle = (currentAngle - previousAngle+360)%360;
				while (shortestAngle > 180)
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
				view.drawCompass(view.getHolder(),previousAngle);
				
			}
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
