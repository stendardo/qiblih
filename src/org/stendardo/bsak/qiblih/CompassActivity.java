package org.stendardo.bsak.qiblih;

import java.lang.reflect.Method;
import java.util.List;

import org.stendardo.bsak.qiblih.views.PointLocatorView;
import org.stendardo.util.math.GeoAngleCalculator;
import org.stendardo.util.math.GreatCircleCalculator;
import org.stendardo.util.math.RhumbLineCalculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

//Bahji: 32.943333,35.092222
abstract public class CompassActivity extends Activity{
	private static final int EDIT_ID = 0;
	private static final int CLOSE_ID = 1;
	private static final int README_ID = 2;
	private static final int LICENSE_ID = 3;
	private boolean hasErrors = false;
	private LocationManager locationManager;
	private SensorManager sensorManager;
	private Method display_getRotation;
	private Sensor gravitySensor;
	private Sensor geomagneticSensor;
	private GeoAngleCalculator rhumbLineCalculator = new RhumbLineCalculator();
	private GeoAngleCalculator greatCircleCalculator = new GreatCircleCalculator();
	private float currentOrientation = 0;
	private float[] currentUnbufferedAngles = new float[11];
	
	private static final int REFRESH = 0; 
	private Handler updater = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what)
			{
			case REFRESH:
				update();
			}
		}
	};
	private double getDisplayRotation()
	{
		try
		{
			Object o = display_getRotation.invoke(((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay());
			int symbolicRotation = (Integer)o;
			int res = 90*symbolicRotation;
			if (res >=180)
			{
				res -= 360;
			}
			return res;
		}
		catch (NullPointerException e)
		{
			return 0;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	private void errorBox(String str)
	{
		if (hasErrors)
		{
			return;
		}
		hasErrors = true;
		AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
		 
        // set the message to display
        alertbox.setMessage(str);

        // add a neutral button to the alert box and assign a click listener
        alertbox.setNeutralButton("Ok", new DialogInterface.OnClickListener() {

            // click listener on the alert box
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        alertbox.show();
	}
	private void update()
	{
		Location l = getCurrentPosition();
		if (l == null)
		{
			return;
		}
		Location l2 = getLocation();
		GeomagneticField gmf = new GeomagneticField((float)l.getLatitude(),(float)l.getLongitude(), (float)l.getAltitude(), System.currentTimeMillis());
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		boolean rhumbLine =  sp.getBoolean("COMPASS_UseRhumbLine", false);
		GeoAngleCalculator bearingCalculator = rhumbLine?rhumbLineCalculator:greatCircleCalculator;
		double azimuth = bearingCalculator.calculateBearing(l.getLatitude(), l.getLongitude(), l2.getLatitude(), l2.getLongitude());
		getView().setAzimuth(azimuth);
		TextView bearing_view = (TextView)findViewById(R.id.bearing_view);
		TextView distance_view = (TextView)findViewById(R.id.distance_view);
		TextView metricView = (TextView)findViewById(R.id.metric_used);
		bearing_view.setText(getResources().getString(R.string.bearing)+" "+Math.round(azimuth));
		metricView.setText(rhumbLine?getResources().getString(R.string.using_rhumb_line):getResources().getString(R.string.using_great_circle));
		getView().setCurrentOrientation((currentOrientation + getDisplayRotation() + gmf.getDeclination()+360)%360);
		distance_view.setText(getResources().getString(R.string.distance)+" "+Math.round(bearingCalculator.calculateDistance(l.getLatitude(), l.getLongitude(), l2.getLatitude(), l2.getLongitude())));
	}
	private SensorEventListener orientationSensorListener = new SensorEventListener() {
		
		float[] mGravity;
		float[] mGeomagnetic;
		float[] R = new float[9];
		float[] I = new float[9];
		float orientation[] = new float[3];
		
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
			{
				mGravity = event.values;
			}
			else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
			{
				mGeomagnetic = event.values;
			}
			if (mGravity != null && mGeomagnetic != null) {
				boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
				if (success)
				{
					SensorManager.getOrientation(R, orientation);
					float currentUnbufferedAngle = orientation[0];
					float x = (float)Math.cos(currentUnbufferedAngle);
					float y = (float)Math.sin(currentUnbufferedAngle);
					for (int i = currentUnbufferedAngles.length-1; i > 0 ; i--)
					{
						x += Math.cos(currentUnbufferedAngles[i-1]);
						y += Math.sin(currentUnbufferedAngles[i-1]);
						currentUnbufferedAngles[i] = currentUnbufferedAngles[i-1];
					}
					currentUnbufferedAngles[0] = currentUnbufferedAngle;
					currentOrientation = (float)((180.0/Math.PI)*Math.atan2(y, x));
					updater.sendEmptyMessage(REFRESH);
				}
			}
			
			
		}
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			
			
		}
	};
	private LocationListener locationListener = new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
			
		}
		
		@Override
		public void onProviderEnabled(String provider) {
		
			
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			
		}
		
		@Override
		public void onLocationChanged(Location location){
			updater.sendEmptyMessage(REFRESH);
		}
	};
	public PointLocatorView getView()
	{
		return (PointLocatorView) findViewById(R.id.point_locator_view);
	}

	public abstract Location getLocation();
	
	public Location getCurrentPosition()
	{
		Location res = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (res == null)
		{
			res = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		return res;
	}

	
	
	@Override
	protected void onPause() {
		super.onPause();
		locationManager.removeUpdates(locationListener);
        sensorManager.unregisterListener(orientationSensorListener);
        ((PointLocatorView)findViewById(R.id.point_locator_view)).onPause();
	}
	@Override
    protected void onResume()
    {
		super.onResume();
		
		try
		{
			for (String s:locationManager.getProviders(true))
			{
				locationManager.requestLocationUpdates(s,16000, 5, locationListener);
			}
			sensorManager.registerListener(orientationSensorListener,geomagneticSensor, SensorManager.SENSOR_DELAY_GAME);
			sensorManager.registerListener(orientationSensorListener,gravitySensor, SensorManager.SENSOR_DELAY_GAME);
		} catch (Exception e)
		{
			errorBox(getResources().getString(R.string.error_no_gps));
		}
		((PointLocatorView)findViewById(R.id.point_locator_view)).onResume();
		
    }
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try
		{
			display_getRotation = Display.class.getMethod("getRotation", new Class[] { } );
		}
		catch (NoSuchMethodException ignore)
		{
			throw new RuntimeException(ignore);
		}
		try
		{
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		}
		catch (Exception e)
		{
			errorBox(getResources().getString(R.string.error_no_gps));
		}
	    sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);;
	    
	    
	    //List<Sensor> temp = sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
	    //if (!temp.isEmpty())
	    //{
	    gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	    geomagneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
	    
	    //}
	}
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, EDIT_ID, Menu.NONE, getResources().getString(R.string.cmd_preferences))
                .setAlphabeticShortcut('p');
        menu.add(Menu.NONE, README_ID, Menu.NONE, getResources().getString(R.string.cmd_readme)).setAlphabeticShortcut('r');
        menu.add(Menu.NONE, LICENSE_ID, Menu.NONE, getResources().getString(R.string.cmd_license)).setAlphabeticShortcut('l');
        return(super.onCreateOptionsMenu(menu));
        
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        		case EDIT_ID:
                	startActivity(new Intent(this, QiblihPreferences.class));
                	return true;
                case README_ID:
                	startActivity(new Intent(this, ReadmeActivity.class));
                	return true;
                case LICENSE_ID:
                	startActivity(new Intent(this, LicenseActivity.class));
                	return true;
        }
        
        return(super.onOptionsItemSelected(item));
    }

    
}
