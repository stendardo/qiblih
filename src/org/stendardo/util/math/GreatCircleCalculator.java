package org.stendardo.util.math;
import static org.stendardo.util.math.GeoAngleUtils.deg2rad;
import static org.stendardo.util.math.GeoAngleUtils.rad2deg;
public class GreatCircleCalculator implements GeoAngleCalculator {

	@Override
	public double calculateBearing(double lat1, double long1, double lat2, double long2) {
		double lat1r = deg2rad(lat1);
		double lat2r = deg2rad(lat2);
		double dlong = deg2rad(long2)-deg2rad(long1);
		double x,y,z;
		try
		{
			y = Math.sin(dlong)*Math.cos(lat2r);
			
			
			x = Math.cos(lat1r)*Math.sin(lat2r) - Math.sin(lat1r)*Math.cos(lat2r)*Math.cos(dlong);
			z = Math.atan2(y,x);

		} catch (ArithmeticException e)
		{
			return 0;
		}
		return (rad2deg(z)+360)%360;
	}

	@Override
	public double calculateDistance(double lat1, double long1, double lat2,
			double long2) {
		try
		{
			
			double dLat = deg2rad(lat2-lat1);
			double dLon = deg2rad(long2-long1); 
			double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
			        Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * 
			        Math.sin(dLon/2) * Math.sin(dLon/2); 
			double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
			return GeoAngleUtils.EARTH_RADIUS_KM * c;
		}
		catch(ArithmeticException e)
		{
			return 0;
		}
		
	}

}
