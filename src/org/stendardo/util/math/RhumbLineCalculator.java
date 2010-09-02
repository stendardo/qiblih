package org.stendardo.util.math;
import static org.stendardo.util.math.GeoAngleUtils.deg2rad;
import static org.stendardo.util.math.GeoAngleUtils.rad2deg;
public class RhumbLineCalculator implements GeoAngleCalculator{

	@Override
	public double calculateBearing(double lat1, double long1, double lat2,
			double long2) {
		try
		{
			lat1 = deg2rad(lat1);
			lat2 = deg2rad(lat2);
			long1 = deg2rad(long1);
			long2 = deg2rad(long2);
			double dPhi = Math.log(Math.tan(lat2/2+Math.PI/4)/Math.tan(lat1/2+Math.PI/4));
			
			double dLon = long2 - long1;
			if (Math.abs(dLon) > Math.PI)
			{
				dLon = dLon>0 ? -(2*Math.PI-dLon) : (2*Math.PI+dLon);
			}
			return (rad2deg(Math.atan2(dLon,dPhi))+360)%360;

		} catch (ArithmeticException e)
		{
			return 0;
		}
	}

	@Override
	public double calculateDistance(double lat1, double long1, double lat2,
			double long2) {
		try
		{
			lat1 = deg2rad(lat1);
			lat2 = deg2rad(lat2);
			long1 = deg2rad(long1);
			long2 = deg2rad(long2);
			double dLat = lat2 - lat1;
			double dPhi = Math.log(Math.tan(lat2/2+Math.PI/4)/Math.tan(lat1/2+Math.PI/4));
			double q = dLat/dPhi;
			if (Double.isNaN(q)|| Double.isInfinite(q))
			{
				q = Math.cos(lat1);
			}
			double dLon = long2 - long1;
			if (Math.abs(dLon) > Math.PI)
			{
				dLon = dLon>0 ? -(2*Math.PI-dLon) : (2*Math.PI+dLon);
			}
			return Math.sqrt(dLat*dLat + q*q*dLon*dLon)* GeoAngleUtils.EARTH_RADIUS_KM;

		} catch (ArithmeticException e)
		{
			return 0;
		}

	}

}
