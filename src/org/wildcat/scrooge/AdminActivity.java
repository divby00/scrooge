package org.wildcat.scrooge;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import org.wildcat.scrooge.persistence.DatabaseManager;
import org.wildcat.scrooge.persistence.beans.TbCategoria;
import org.wildcat.scrooge.persistence.beans.TbGasto;
import org.wildcat.scrooge.persistence.dao.ScroogeDAO;
import org.wildcat.scrooge.utils.Logger;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class AdminActivity extends Activity implements OnClickListener {

	private final String	LAST_BACKUP_FILE	= "last_backup";
	private Button			btnEditCategories	= null;
	private Button			btnEditExpenses		= null;
	private Button			btnMakeBackup		= null;
	private Button			btnImportBackup		= null;
	private TextView		lblBackup			= null;
	private Activity		activity			= null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.activity = this;
		setContentView(R.layout.admin);
		btnMakeBackup = (Button) findViewById(R.id.btnExportDatabase);
		btnImportBackup = (Button) findViewById(R.id.btnImportDatabase);
		btnEditExpenses = (Button) findViewById(R.id.btnEditExpenses);
		btnEditCategories = (Button) findViewById(R.id.btnEditCategories);
		lblBackup = (TextView) findViewById(R.id.lblBackup);
		String lastBackup = checkLastBackup();
		if (lastBackup == null || "".equals(lastBackup)) {
			lblBackup.setText(R.string.backup_dont_made);
		} else {
			lblBackup.setText(MessageFormat.format(getString(R.string.last_backup), lastBackup));
		}
		btnMakeBackup.setOnClickListener(this);
		btnImportBackup.setOnClickListener(this);
		btnEditExpenses.setOnClickListener(this);
		btnEditCategories.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnEditCategories:
			makeEditCategories();
			break;
		case R.id.btnEditExpenses:
			makeEditExpenses();
			break;
		case R.id.btnExportDatabase:
			makeDatabaseBackup();
			break;
		case R.id.btnImportDatabase:
			makeImportDatabase();
		}
	}


	private void makeEditCategories() {
		DatabaseManager dbManager = new DatabaseManager(activity, null);
		List<TbCategoria> cats = dbManager.getScroogeDAO().getCategories();
		dbManager.close();
		if (cats == null || cats.isEmpty()) {
			Toast toast = Toast.makeText(activity, R.string.categories_not_found, Toast.LENGTH_SHORT);
			toast.show();
		} else {
			Intent intent = new Intent(this, EditCategoriesListActivity.class);
			activity.startActivity(intent);
		}
	}


	private void makeEditExpenses() {
		DatabaseManager dbManager = new DatabaseManager(activity, null);
		List<TbGasto> exps = dbManager.getScroogeDAO().getExpenses();
		dbManager.close();
		if (exps == null || exps.isEmpty()) {
			Toast toast = Toast.makeText(activity, R.string.expenses_not_found, Toast.LENGTH_SHORT);
			toast.show();
		} else {
			Intent intent = new Intent(this, EditExpensesListActivity.class);
			activity.startActivity(intent);
		}
	}


	private void makeImportDatabase() {
		String sdState = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(sdState)) {
			File file = Environment.getExternalStorageDirectory();
			File dir = new File(file.getAbsolutePath() + "/Scrooge/Backups/");
			String[] files = dir.list();
			if (files != null && files.length != 0) {
				Intent intent = new Intent(this, ImportDatabaseActivity.class);
				this.startActivity(intent);
			} else {
				Toast tostada = Toast.makeText(activity.getApplicationContext(), R.string.backups_not_foud, Toast.LENGTH_SHORT);
				tostada.show();
			}
		} else {
			Toast tostada = Toast.makeText(activity.getApplicationContext(), R.string.not_found_storage_unit, Toast.LENGTH_SHORT);
			tostada.show();
		}
	}


	@SuppressLint("SimpleDateFormat")
	private void makeDatabaseBackup() {
		String sdState = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(sdState)) {
			Calendar cal = GregorianCalendar.getInstance();
			DateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
			String fileName = df.format(cal.getTime()) + ".backup";
			try {
				DatabaseManager db = new DatabaseManager(this, null);
				ScroogeDAO scroogeDao = db.getScroogeDAO();
				List<TbGasto> exp = scroogeDao.getExpenses();
				List<TbCategoria> cat = scroogeDao.getCategories();
				db.close();
				File file = Environment.getExternalStorageDirectory();
				File dir = new File(file.getAbsolutePath() + "/Scrooge/Backups/");
				dir.mkdirs();
				File f = new File(dir, fileName);
				OutputStreamWriter fout = new OutputStreamWriter(new FileOutputStream(f));
				fout.write("Scrooge Backup 2.0\n");
				fout.write("[CAT]\n");
				fout.write(getString(R.string.cvs_id_category));
				fout.write(";");
				fout.write(getString(R.string.cvs_category_name));
				fout.write("\n");
				for (TbCategoria c : cat) {
					fout.write(c.getId() + ";");
					fout.write(c.getNombre() + "\n");
				}
				fout.write("[EXP]\n");
				fout.write(getString(R.string.cvs_id_expense));
				fout.write(";");
				fout.write(getString(R.string.cvs_id_category));
				fout.write(";");
				fout.write(getString(R.string.cvs_category_name));
				fout.write(";");
				fout.write(getString(R.string.cvs_amount));
				fout.write(";");
				fout.write(getString(R.string.cvs_date));
				fout.write("\n");
				for (TbGasto e : exp) {
					fout.write(e.getIdGasto() + ";");
					fout.write(e.getIdCategoria() + ";");
					fout.write(e.getNombreCategoria() + ";");
					fout.write(e.getImporte() + ";");
					fout.write(e.getFecha() + "\n");
				}
				fout.close();
				writeLastBackup();
				Toast tostada = Toast.makeText(activity.getApplicationContext(), MessageFormat.format(getString(R.string.backup_made_correctly), fileName), Toast.LENGTH_LONG);
				tostada.show();
			} catch (Exception e) {
				Toast tostada = Toast.makeText(activity.getApplicationContext(), MessageFormat.format(getString(R.string.unable_to_save_backup), fileName), Toast.LENGTH_SHORT);
				tostada.show();
			}
		} else {
			Toast tostada = Toast.makeText(activity.getApplicationContext(), R.string.no_storage_found_or_read_only, Toast.LENGTH_SHORT);
			tostada.show();
		}
	}


	private void writeLastBackup() {
		try {
			Calendar cal = GregorianCalendar.getInstance();
			DateFormat df = SimpleDateFormat.getDateInstance();
			OutputStreamWriter outputFile = new OutputStreamWriter(this.openFileOutput(LAST_BACKUP_FILE, Context.MODE_PRIVATE));
			outputFile.write(df.format(cal.getTime()));
			outputFile.close();
			lblBackup.setText(MessageFormat.format(getString(R.string.last_backup), df.format(cal.getTime())));
		} catch (Exception e) {
			Logger.message(Log.ERROR, "No se ha podido crear el fichero " + LAST_BACKUP_FILE);
		}
	}


	public String checkLastBackup() {
		String last = null;
		try {
			InputStreamReader ir = new InputStreamReader(this.openFileInput(LAST_BACKUP_FILE));
			BufferedReader br = new BufferedReader(ir);
			last = br.readLine();
			ir.close();
			br.close();
		} catch (Exception ex) {
			Logger.message(Log.ERROR, "No existe el fichero " + LAST_BACKUP_FILE);
		}
		return last;
	}
}
