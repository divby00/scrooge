package org.wildcat.scrooge;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.wildcat.scrooge.persistence.DatabaseManager;
import org.wildcat.scrooge.persistence.beans.TbCategoria;
import org.wildcat.scrooge.persistence.beans.TbGasto;
import org.wildcat.scrooge.persistence.dao.ScroogeDAO;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class ImportDatabaseActivity extends Activity implements OnClickListener, OnItemSelectedListener {

	private Activity		activity	= null;
	private Spinner			cmbBackups	= null;
	private Button			btnOk		= null;
	// private TextView lblMessage = null;
	private TextView		lblImportar	= null;
	private DatabaseData	data		= null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.import_database);
		this.activity = this;
		cmbBackups = (Spinner) findViewById(R.id.cmbBackups);
		btnOk = (Button) findViewById(R.id.btnOk);
		// lblMessage = (TextView) findViewById(R.id.lblMessage);
		lblImportar = (TextView) findViewById(R.id.lblImportar);
		cmbBackups.setOnItemSelectedListener(this);
		btnOk.setOnClickListener(this);
		setBackupsAdapter();
	}


	private void setBackupsAdapter() {
		File file = Environment.getExternalStorageDirectory();
		File dir = new File(file.getAbsolutePath() + "/Scrooge/Backups/");
		String[] files = dir.list();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getBaseContext(), android.R.layout.simple_spinner_item, files);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		cmbBackups.setAdapter(adapter);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnOk:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.warning);
			builder.setMessage(R.string.this_will_remove_all_data_and_store_new_data);
			builder.setPositiveButton(R.string.continu, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// So there we go!!
					try {
						List<TbCategoria> cats = data.getCats();
						List<TbGasto> exps = data.getExps();
						DatabaseManager dbManager = new DatabaseManager(activity, null);
						ScroogeDAO scroogeDao = dbManager.getScroogeDAO();
						scroogeDao.deleteExpenses();
						scroogeDao.deleteCategories();
						scroogeDao.insertCategories(cats);
						scroogeDao.insertExpenses(exps);
						dbManager.close();
					} catch (Exception e) {
						Toast toast = Toast.makeText(activity, R.string.there_is_an_error_importing_data, Toast.LENGTH_SHORT);
						toast.show();
					}
					Toast toast = Toast.makeText(activity, R.string.data_imported_correctly, Toast.LENGTH_SHORT);
					toast.show();
				}
			});
			builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			builder.show();
			break;
		}
	}


	private boolean validateBackup(String fileName) {
		boolean aux = false;
		String sdState = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(sdState)) {
			try {
				File file = Environment.getExternalStorageDirectory();
				File dir = new File(file.getAbsolutePath() + "/Scrooge/Backups/");
				dir.mkdirs();
				File f = new File(dir, fileName);
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
				String cad = null;
				Double fileVersion = null;
				cad = br.readLine();
				fileVersion = Double.parseDouble(cad.substring(15, 18));
				leerDatos(fileVersion, br);
				aux = true;
				Toast toast = Toast.makeText(this, R.string.correct_validation, Toast.LENGTH_SHORT);
				toast.show();
				btnOk.setVisibility(View.VISIBLE);
				lblImportar.setVisibility(View.VISIBLE);
			} catch (Exception e) {
				Toast toast = Toast.makeText(this, R.string.incorrect_validation, Toast.LENGTH_SHORT);
				toast.show();
				btnOk.setVisibility(View.INVISIBLE);
				lblImportar.setVisibility(View.INVISIBLE);
			}
		}
		return aux;
	}


	private void leerDatos(Double fileVersion, BufferedReader br) throws Exception {
		String tablaCatName = null;
		List<TbCategoria> cats = null;
		List<TbGasto> exps = null;
		try {
			tablaCatName = br.readLine();
			if (fileVersion == 1 || fileVersion == 2) {
				if (!"[CATEGORIAS]".equals(tablaCatName) && !"[CAT]".equals(tablaCatName)) {
					throw new Exception();
				}
				Integer numEle = getElementsNumber(br.readLine());
				if (numEle < 1)
					throw new Exception();
				String c = null;
				cats = new ArrayList<TbCategoria>();
				Integer totalCats = 0;
				while (true) {
					c = br.readLine();
					if ("[GASTOS]".equals(c) || "[EXP]".equals(c))
						break;
					TbCategoria category = getCategory(c);
					if (category != null) {
						cats.add(category);
						totalCats++;
					}
					category = null;
					c = null;
				}
				numEle = getElementsNumber(br.readLine());
				if (numEle < 1)
					throw new Exception();
				c = null;
				exps = new ArrayList<TbGasto>();
				Integer totalExps = 0;
				while (true) {
					c = br.readLine();
					if (c == null)
						break;
					TbGasto expense = getExpense(c);
					if (expense != null) {
						exps.add(expense);
						totalExps++;
					}
					expense = null;
					c = null;
				}
				// Parece que todo va bien, en los beans cats y exps están los
				// datos que se han leído.
				data = new DatabaseData();
				data.setCats(cats);
				data.setExps(exps);
			}
		} catch (Exception e) {
			// Validación fallada
			throw e;
		}
	}


	class DatabaseData {

		private List<TbGasto>		exps;
		private List<TbCategoria>	cats;


		public List<TbGasto> getExps() {
			return exps;
		}


		public void setExps(List<TbGasto> exps) {
			this.exps = exps;
		}


		public List<TbCategoria> getCats() {
			return cats;
		}


		public void setCats(List<TbCategoria> cats) {
			this.cats = cats;
		}
	}


	private Integer getElementsNumber(String cad) throws Exception {
		Integer elements = 0;
		try {
			Scanner sc = new Scanner(cad);
			sc.useDelimiter(";");
			while (sc.hasNext()) {
				elements++;
				sc.next();
			}
			sc.close();
		} catch (Exception e) {
			throw e;
		}
		return elements;
	}


	private TbCategoria getCategory(String cad) throws Exception {
		TbCategoria cat = null;
		try {
			Long valor1 = null;
			String valor2 = null;
			Scanner sc = new Scanner(cad);
			sc.useDelimiter(";");
			valor1 = sc.nextLong();
			valor2 = sc.next();
			sc.close();
			if (valor1 != null && valor2 != null) {
				cat = new TbCategoria();
				cat.setId(valor1);
				cat.setNombre(valor2);
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			throw e;
		}
		return cat;
	}


	private TbGasto getExpense(String cad) throws Exception {
		TbGasto exp = null;
		try {
			Integer valor1 = null;
			Integer valor2 = null;
			String valor3 = null;
			Double valor4 = null;
			Long valor5 = null;
			Scanner sc = new Scanner(cad);
			sc.useDelimiter(";");
			valor1 = sc.nextInt();
			valor2 = sc.nextInt();
			valor3 = sc.next();
			valor4 = Double.parseDouble(sc.next());
			valor5 = sc.nextLong();
			sc.close();
			if (valor1 != null && valor2 != null && valor3 != null && valor4 != null && valor5 != null) {
				exp = new TbGasto();
				exp.setIdGasto(valor1);
				exp.setIdCategoria(valor2);
				exp.setNombreCategoria(valor3);
				exp.setImporte(valor4);
				exp.setFecha(valor5);
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			throw e;
		}
		return exp;
	}


	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		String file = arg0.getSelectedItem().toString();
		btnOk.setVisibility(View.INVISIBLE);
		lblImportar.setVisibility(View.INVISIBLE);
		if (file != null && !"".equals(file))
			validateBackup(file);
	}


	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}
}
