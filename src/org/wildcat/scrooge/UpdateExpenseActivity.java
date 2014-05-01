package org.wildcat.scrooge;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import org.wildcat.scrooge.persistence.DatabaseManager;
import org.wildcat.scrooge.persistence.beans.TbCategoria;
import org.wildcat.scrooge.persistence.beans.TbGasto;
import org.wildcat.scrooge.persistence.dao.ScroogeDAO;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;


public class UpdateExpenseActivity extends Activity implements OnClickListener {

	private Calendar			expenseCalendar		= null;
	private Integer				newYear				= -1;
	private Integer				newMonth			= -1;
	private Integer				newDay				= -1;
	private TbGasto				exp					= null;
	private EditText			editUpdateDate		= null;
	private EditText			editUpdateExpense	= null;
	private Spinner				cmbUpdateCats		= null;
	private Button				btnUpdate			= null;
	private Button				btnClearExpense		= null;
	private ImageView			btnUpdateDate		= null;
	private static final int	DATE_DIALOG			= 0;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_expense);
		Bundle params = this.getIntent().getExtras();
		int id = (Integer) params.get("id_gasto");
		editUpdateDate = (EditText) findViewById(R.id.editUpdateDate);
		editUpdateExpense = (EditText) findViewById(R.id.editUpdateExp);
		btnUpdate = (Button) findViewById(R.id.btnUpdate);
		cmbUpdateCats = (Spinner) findViewById(R.id.cmbUpdateCats);
		btnClearExpense = (Button) findViewById(R.id.btnClearExpense);
		btnUpdateDate = (ImageView) findViewById(R.id.btnUpdateDate);
		btnUpdateDate.setOnClickListener(new View.OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				showDialog(DATE_DIALOG);
			}
		});
		btnClearExpense.setOnClickListener(this);
		btnUpdate.setOnClickListener(this);
		DatabaseManager dbManager = new DatabaseManager(this, null);
		ScroogeDAO scroogeDao = dbManager.getScroogeDAO();
		exp = scroogeDao.getExpenseById(id);
		List<TbCategoria> cats = scroogeDao.getCategories();
		TbCategoria cat = scroogeDao.getCategory(exp.getIdCategoria().longValue());
		dbManager.close();
		String selectedCategory = cat.getNombre();
		int position = setCategoriesAdapter(cats, selectedCategory);
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTimeInMillis(exp.getFecha());
		expenseCalendar = cal;
		DateFormat df = SimpleDateFormat.getDateInstance();
		cmbUpdateCats.setSelection(position);
		editUpdateDate.setText(df.format(cal.getTime()));
		editUpdateExpense.setText(exp.getImporte().toString());
	}


	private int setCategoriesAdapter(List<TbCategoria> cats, String selectedCatName) {
		List<TbCategoria> categories = cats;
		String[] cts = new String[categories.size()];
		int position = -1;
		for (int i = 0; i < categories.size(); i++) {
			TbCategoria c = categories.get(i);
			cts[i] = c.getNombre();
			if (selectedCatName.toUpperCase(Locale.getDefault()).equals(c.getNombre().toUpperCase(Locale.getDefault())))
				position = i;
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getBaseContext(), android.R.layout.simple_spinner_item, cts);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		cmbUpdateCats.setAdapter(adapter);
		return position;
	}


	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btnClearExpense:
			editUpdateExpense.setText("");
			break;
		case R.id.btnUpdate:
			validarCampos();
			break;
		default:
			break;
		}
	}


	private Boolean validarCampos() {
		Boolean aux = true;
		try {
			if ("".equals(editUpdateExpense.getText().toString())) {
				Toast toast = Toast.makeText(this, R.string.you_have_to_input_an_amount, Toast.LENGTH_SHORT);
				toast.show();
				aux = false;
				return aux;
			}
			if (editUpdateExpense.getText().length() > 7) {
				Toast toast = Toast.makeText(this, R.string.amount_has_to_be_less_than_7d, Toast.LENGTH_SHORT);
				toast.show();
				aux = false;
				return aux;
			}
			if ("".equals(editUpdateDate.getText().toString())) {
				Toast toast = Toast.makeText(this, R.string.you_have_to_input_a_date, Toast.LENGTH_SHORT);
				toast.show();
				aux = false;
				return aux;
			}
			TbGasto newExp = new TbGasto();
			newExp.setIdGasto(exp.getIdGasto());
			newExp.setImporte(Double.parseDouble(editUpdateExpense.getText().toString()));
			/*
			 * String[] elemFecha =
			 * editUpdateDate.getText().toString().split("/"); List<String>
			 * listaFecha = Arrays.asList(elemFecha);
			 */
			DateFormat df = SimpleDateFormat.getDateInstance();
			Date goodDate = df.parse(editUpdateDate.getText().toString());
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTime(goodDate);
			// cal.set(Calendar.DAY_OF_MONTH,
			// Integer.parseInt(listaFecha.get(0)));
			// cal.set(Calendar.MONTH, Integer.parseInt(listaFecha.get(1)));
			// cal.set(Calendar.YEAR, Integer.parseInt(listaFecha.get(2)));
			// cal.add(Calendar.MONTH, -1);
			newExp.setFecha(cal.getTimeInMillis());
			newExp.setNombreCategoria(cmbUpdateCats.getSelectedItem().toString());
			DatabaseManager dbManager = new DatabaseManager(this, null);
			ScroogeDAO scroogeDao = dbManager.getScroogeDAO();
			Long catId = scroogeDao.getCategoryKeyByName(cmbUpdateCats.getSelectedItem().toString());
			newExp.setIdCategoria(catId.intValue());
			if (!scroogeDao.updateExpense(newExp)) {
				Toast toast = Toast.makeText(this, R.string.unable_to_update_expense, Toast.LENGTH_SHORT);
				toast.show();
			} else {
				Toast toast = Toast.makeText(this, R.string.expense_updated_correctly, Toast.LENGTH_SHORT);
				toast.show();
			}
			dbManager.close();
		} catch (Exception e) {
			Toast toast = Toast.makeText(this, R.string.unable_to_update_expense, Toast.LENGTH_SHORT);
			toast.show();
		}
		return aux;
	}


	private Calendar getExpenseCalendar() {
		return expenseCalendar;
	}


	@Deprecated
	protected Dialog onCreateDialog(int id, Bundle args) {
		Calendar cal = getExpenseCalendar();
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		return new DatePickerDialog(this, dateSetListener, year, month, day);
	}


	private DatePickerDialog.OnDateSetListener	dateSetListener	= new DatePickerDialog.OnDateSetListener() {

																	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
																		newYear = year;
																		newMonth = monthOfYear;
																		newDay = dayOfMonth;
																		updateDate();
																	}
																};


	private void updateDate() {
		Calendar cal = getExpenseCalendar();
		cal.set(Calendar.DAY_OF_MONTH, newDay);
		cal.set(Calendar.MONTH, newMonth);
		cal.set(Calendar.YEAR, newYear);
		DateFormat df = SimpleDateFormat.getDateInstance();
		String text = df.format(cal.getTime());
		editUpdateDate.setText(text);
	}
}
