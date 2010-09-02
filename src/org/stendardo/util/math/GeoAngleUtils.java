package org.stendardo.util.math;

public class GeoAngleUtils {
	public static final double EARTH_RADIUS_KM = 6371;
	public static double deg2rad(double degrees)
	{
		return (Math.PI/180)*degrees;
	}
	public static double rad2deg(double radians)
	{
		return (180/Math.PI)*radians;
	}
}
