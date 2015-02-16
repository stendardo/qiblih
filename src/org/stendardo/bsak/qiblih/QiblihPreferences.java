package org.stendardo.bsak.qiblih;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

public class QiblihPreferences extends PreferenceActivity
{
    private static int prefs=R.xml.qiblih_prefs;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        try {
            getClass().getMethod("getFragmentManager");
            addPreferences();
        } catch (NoSuchMethodException e) { //Api < 11
        	compatibilityAddPreferences();
        }
    }

    @SuppressWarnings("deprecation")
    protected void compatibilityAddPreferences()
    {
        addPreferencesFromResource(prefs);
    }

    @TargetApi(11)
    protected void addPreferences()
    {
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PF()).commit();
    }

    @TargetApi(11)
    public static class PF extends PreferenceFragment
    {       
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(QiblihPreferences.prefs);
        }
    }

	@Override
	protected boolean isValidFragment(String fragmentName) {
		return PF.class.getName().equals(fragmentName);
	}
    
    
}