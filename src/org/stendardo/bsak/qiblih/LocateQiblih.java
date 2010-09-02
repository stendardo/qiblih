package org.stendardo.bsak.qiblih;

import android.location.Location;
import android.os.Bundle;

public class LocateQiblih extends CompassActivity {
	public static final double QIBLIH_LAT = 32.943333;
	public static final double QIBLIH_LON = 35.092222;
    @Override
	public Location getLocation() {
		Location l = new Location("constant");
		l.setLatitude(QIBLIH_LAT);
		l.setLongitude(QIBLIH_LON);
		return l;
	}

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locateqiblih);
        
    }
}