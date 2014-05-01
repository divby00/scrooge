package org.wildcat.scrooge;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import org.wildcat.scrooge.constants.Constants;
import org.wildcat.scrooge.persistence.DatabaseManager;
import org.wildcat.scrooge.persistence.dao.ScroogeDAO;
import org.wildcat.scrooge.utils.Logger;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;


public class WelcomeActivity extends Activity {

	private Activity	activity;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		this.activity = this;
		if (isFirstTime()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.hello);
			builder.setMessage(R.string.first_time_text);
			builder.setPositiveButton(R.string.input_categories_later, new OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					DatabaseManager db = new DatabaseManager(activity.getBaseContext(), null);
					ScroogeDAO scroogeDao = db.getScroogeDAO();
					Resources res = getResources();
					String noCat = res.getString(R.string.no_category);
					scroogeDao.insertCategory(noCat);
					db.close();
					saveFirstTime();
					dialog.cancel();
					Intent intent = new Intent(activity.getBaseContext(), ScroogeActivity.class);
					startActivity(intent);
					activity.finish();
				}
			});
			builder.setNegativeButton(R.string.use_default_categories, new OnClickListener() {

				/* Use default categories */
				public void onClick(DialogInterface dialog, int which) {
					DatabaseManager db = new DatabaseManager(activity.getBaseContext(), null);
					ScroogeDAO scroogeDao = db.getScroogeDAO();
					Resources res = getResources();
					String[] cts = res.getStringArray(R.array.default_categories);
					scroogeDao.insertDefaultCategories(cts);
					db.close();
					saveFirstTime();
					dialog.cancel();
					Intent intent = new Intent(activity.getBaseContext(), ScroogeActivity.class);
					startActivity(intent);
					activity.finish();
				}
			});
			builder.show();
		} else {
			Intent intent = new Intent(activity.getBaseContext(), ScroogeActivity.class);
			startActivity(intent);
			this.finish();
		}
	}


	private boolean isFirstTime() {
		try {
			InputStreamReader ir = new InputStreamReader(this.openFileInput(Constants.FIRST_TIME_FILE));
			BufferedReader br = new BufferedReader(ir);
			ir.close();
			br.close();
			return false;
		} catch (Exception ex) {
			Logger.message(Log.ERROR, "No existe el fichero " + Constants.FIRST_TIME_FILE);
		}
		return true;
	}


	private boolean saveFirstTime() {
		try {
			OutputStreamWriter outputFile = new OutputStreamWriter(this.openFileOutput(Constants.FIRST_TIME_FILE, Context.MODE_PRIVATE));
			outputFile.write("");
			outputFile.close();
			return true;
		} catch (Exception e) {
			Logger.message(Log.ERROR, "No se ha podido crear el fichero " + Constants.FIRST_TIME_FILE);
		}
		return false;
	}
}
