package org.wildcat.scrooge;


import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import org.wildcat.scrooge.persistence.DatabaseManager;
import org.wildcat.scrooge.persistence.beans.TbCategoria;
import org.wildcat.scrooge.persistence.beans.TbGasto;
import org.wildcat.scrooge.persistence.dao.ScroogeDAO;
import org.wildcat.scrooge.utils.Logger;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class ExpensesActivity extends Activity implements OnClickListener {

	private DatabaseManager	dbManager		= null;
	private Spinner			cmbCategorias	= null;
	private EditText		editImporte		= null;
	private Button			btnHecho		= null;
	private Button			btnRevert		= null;
	private Button			btnClearExpense	= null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.expenses);
		cmbCategorias = (Spinner) findViewById(R.id.cmbCategorias);
		editImporte = (EditText) findViewById(R.id.editImpor);
		btnHecho = (Button) findViewById(R.id.btnHecho);
		btnRevert = (Button) findViewById(R.id.btnRevert);
		btnClearExpense = (Button) findViewById(R.id.btnClearImporte);
		if (dbManager == null) {
			dbManager = new DatabaseManager(this, null);
			Logger.message("Se ha creado el objeto para manipular la BD");
		}
		setCategoriesAdapter();
		btnHecho.setOnClickListener(this);
		btnRevert.setOnClickListener(this);
		btnClearExpense.setOnClickListener(this);
	}


	@Override
	protected void onResume() {
		super.onResume();
		if (dbManager == null) {
			dbManager = new DatabaseManager(this, null);
			Logger.message("Se ha creado el objeto para manipular la BD");
		}
		setCategoriesAdapter();
	}


	@Override
	protected void onRestart() {
		super.onRestart();
		if (dbManager == null) {
			dbManager = new DatabaseManager(this, null);
			Logger.message("Se ha creado el objeto para manipular la BD");
		}
		setCategoriesAdapter();
	}


	@Override
	protected void onStart() {
		super.onStart();
		if (dbManager == null) {
			dbManager = new DatabaseManager(this, null);
			Logger.message("Se ha creado el objeto para manipular la BD");
		}
		setCategoriesAdapter();
	}


	public void onClick(View v) {
		Integer idVista = 0;
		idVista = v.getId();
		switch (idVista) {
		case R.id.btnHecho:
			Object catObject = cmbCategorias.getSelectedItem();
			String categoria = (catObject != null) ? catObject.toString() : "";
			String importe = editImporte.getText().toString();
			if ("".equals(importe)) {
				Toast tostada = Toast.makeText(this, R.string.input_expense, Toast.LENGTH_SHORT);
				tostada.show();
				return;
			}
			if (importe.length() > 7) {
				Toast tostada = Toast.makeText(this, R.string.amount_no_more_7d, Toast.LENGTH_SHORT);
				tostada.show();
				editImporte.setText("");
				return;
			}
			Double imp = -1D;
			try {
				imp = Double.valueOf(importe);
			} catch (Exception e) {
				Logger.message(Log.ERROR, "Se ha producido un error: " + e.getMessage());
				Toast tostada = Toast.makeText(this, R.string.input_correct_expense, Toast.LENGTH_SHORT);
				tostada.show();
			}
			if (imp != -1) {
				ScroogeDAO scroogeDao = dbManager.getScroogeDAO();
				Boolean result = scroogeDao.insertExpense(imp, categoria);
				if (result == true) {
					Toast tostada = Toast.makeText(this, R.string.expense_saved_ok, Toast.LENGTH_SHORT);
					tostada.show();
					editImporte.setText("");
				} else {
					Toast tostada = Toast.makeText(this, R.string.error_saving_expense, Toast.LENGTH_SHORT);
					tostada.show();
				}
			} else {
				Toast tostada = Toast.makeText(this, R.string.error_saving_expense, Toast.LENGTH_SHORT);
				tostada.show();
			}
			break;
		case R.id.btnRevert:
			ScroogeDAO scroogeDao = dbManager.getScroogeDAO();
			final TbGasto gasto = scroogeDao.getLastExpense();
			TbCategoria cat = null;
			if (gasto == null) {
				final Toast tostada = Toast.makeText(this.getBaseContext(), R.string.data_not_found, Toast.LENGTH_SHORT);
				tostada.show();
			} else {
				cat = scroogeDao.getCategory(gasto.getIdCategoria().longValue());
				AlertDialog.Builder alerta = new AlertDialog.Builder(this);
				alerta.setTitle(R.string.warning);
				Calendar cal = GregorianCalendar.getInstance();
				cal.setTimeInMillis(gasto.getFecha());
				DateFormat df = SimpleDateFormat.getDateInstance();
				NumberFormat nf = NumberFormat.getInstance();
				nf.setMinimumFractionDigits(2);
				nf.setMaximumFractionDigits(2);
				String msg = MessageFormat.format(getString(R.string.want_to_remove_expense_input_day_of_category_with_import), df.format(cal.getTime()), cat.getNombre(), nf.format(gasto.getImporte()));
				alerta.setMessage(msg);
				alerta.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						borrarGasto(gasto);
					}
				});
				alerta.setNegativeButton(R.string.dont_delete, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
				alerta.show();
			}
			break;
		case R.id.btnClearImporte:
			editImporte.setText("");
			break;
		}
	}


	private void setCategoriesAdapter() {
		List<TbCategoria> categories = dbManager.getScroogeDAO().getCategories();
		String[] cts = new String[categories.size()];
		for (int i = 0; i < categories.size(); i++) {
			TbCategoria c = categories.get(i);
			cts[i] = c.getNombre();
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getBaseContext(), android.R.layout.simple_spinner_item, cts);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		cmbCategorias.setAdapter(adapter);
	}


	private boolean borrarGasto(TbGasto gasto) {
		boolean result = false;
		ScroogeDAO scroogeDao = dbManager.getScroogeDAO();
		result = scroogeDao.deleteExpense(gasto.getIdGasto());
		Toast tostada = null;
		if (result) {
			tostada = Toast.makeText(this.getBaseContext(), R.string.expense_removed_correctly, Toast.LENGTH_SHORT);
		} else {
			tostada = Toast.makeText(this.getBaseContext(), R.string.unable_to_remove_expense, Toast.LENGTH_SHORT);
		}
		tostada.show();
		return result;
	}
}
