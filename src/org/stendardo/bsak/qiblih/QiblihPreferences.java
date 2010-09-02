package org.stendardo.bsak.qiblih;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class QiblihPreferences extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.qiblih_prefs);
	}

}
