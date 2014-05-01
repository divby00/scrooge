package org.wildcat.scrooge;


import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;


@SuppressWarnings("deprecation")
public class ScroogeActivity extends TabActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scrooge);
		Resources res = getResources();
		TabHost host = getTabHost();
		host.addTab(host.newTabSpec("tab1").setIndicator("", res.getDrawable(R.drawable.tab_expenses)).setContent(new Intent(this, ExpensesActivity.class)));
		host.addTab(host.newTabSpec("tab2").setIndicator("", res.getDrawable(R.drawable.tab_reports)).setContent(new Intent(this, ReportsActivity.class)));
		host.addTab(host.newTabSpec("tab3").setIndicator("", res.getDrawable(R.drawable.tab_config)).setContent(new Intent(this, AdminActivity.class)));
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.scrooge, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent(this, AboutActivity.class);
		startActivity(intent);
		return true;
	}
}
