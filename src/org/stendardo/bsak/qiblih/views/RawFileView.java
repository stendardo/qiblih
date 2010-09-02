package org.stendardo.bsak.qiblih.views;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.stendardo.bsak.qiblih.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.widget.TextView;

public class RawFileView extends TextView{

	public void init(AttributeSet attrs)
	{
		setMovementMethod(new ScrollingMovementMethod());
		TypedArray file_attributes = getContext().obtainStyledAttributes(attrs, R.styleable.RawFileViewAttributes);
		String filename = file_attributes.getString(R.styleable.RawFileViewAttributes_filename);
		try
		{
			if (filename != null)
			{
				int id = getContext().getResources().getIdentifier(filename, "raw", getContext().getPackageName());
				BufferedReader bf = new BufferedReader(new InputStreamReader(getContext().getResources().openRawResource(id)));
				StringBuilder b = new StringBuilder();
				String line;
				while((line = bf.readLine()) != null)
				{
					b.append(line + "\n");
				}
				setText(b.toString());
			}
		}
		catch (Exception e)
		{
			
		}
	}
	
	public RawFileView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}

	public RawFileView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	public RawFileView(Context context) {
		super(context);
	}

}
