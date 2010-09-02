package org.stendardo.util.math;

public interface GeoAngleCalculator {
	/**
	 * Calculates an initial bearing for going from p1 to p2 following a path.
	 * @param lat1 latitude of p1 (degrees)
	 * @param long1 longitude of p1 (degrees)
	 * @param lat2 latitude of p2 (degrees)
	 * @param long2 longitude of p1 (degrees)
	 * @return the initial azimuth/bearing (degrees)
	 */
	public double calculateBearing(double lat1,double long1,double lat2,double long2);
	/**
	 * Calculates a distance from p1 to p2 following a path.
	 * @param lat1 latitude of p1 (degrees)
	 * @param long1 longitude of p1 (degrees)
	 * @param lat2 latitude of p2 (degrees)
	 * @param long2 longitude of p1 (degrees)
	 * @return the distance in kilometers
	 */
	public double calculateDistance(double lat1,double long1,double lat2,double long2);

}
