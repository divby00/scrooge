package org.wildcat.scrooge;


import android.app.Activity;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.widget.TextView;


public class AboutActivity extends Activity {

	private TextView	txtVersion	= null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		txtVersion = (TextView) findViewById(R.id.txtVersion);
		String version = "";
		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			version = pInfo.versionName;
		} catch (Exception e) {
		}
		txtVersion.setText("Scrooge v." + version);
	}
}
