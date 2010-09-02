package org.stendardo.util.math.tst;

import org.junit.Test;
import org.stendardo.util.math.GeoAngleCalculator;
import org.stendardo.util.math.GreatCircleCalculator;
import org.stendardo.util.math.RhumbLineCalculator;
//Bahji: 32.943333,35.092222

public class TestGeoAngleCalculator {

	@Test
	public void testGreatCircle() throws Exception
	{
		GeoAngleCalculator c = new GreatCircleCalculator();
		System.out.println(c.calculateBearing(46.2, 6.1,32.943333,35.092222));
	}
	
	@Test
	public void testRhumbLine() throws Exception
	{
		GeoAngleCalculator c = new RhumbLineCalculator();
		System.out.println(c.calculateBearing(46.2, 6.1,32.943333,35.092222));
	}
}
