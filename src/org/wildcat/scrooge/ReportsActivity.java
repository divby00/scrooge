package org.wildcat.scrooge;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import org.wildcat.scrooge.persistence.DatabaseManager;
import org.wildcat.scrooge.persistence.beans.TbCategoria;
import org.wildcat.scrooge.persistence.beans.TbGasto;
import org.wildcat.scrooge.persistence.dao.ScroogeDAO;
import org.wildcat.scrooge.persistence.filter.ReportFilter;
import org.wildcat.scrooge.utils.Logger;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class ReportsActivity extends Activity implements OnClickListener {

	private static List<TbGasto>	staticExpenses;
	private EditText				editFrom			= null;
	private EditText				editTo				= null;
	private EditText				editImporte			= null;
	private Button					btnShowReport		= null;
	private Button					btnFrom				= null;
	private Button					btnTo				= null;
	private Button					btnClearFrom		= null;
	private Button					btnClearTo			= null;
	private Button					btnClearImp			= null;
	private Spinner					cmbCategories		= null;
	private Spinner					cmbOperador			= null;
	private int						mYearFrom			= -1;
	private int						mMonthFrom			= -1;
	private int						mDayFrom			= -1;
	private int						mYearTo				= -1;
	private int						mMonthTo			= -1;
	private int						mDayTo				= -1;
	private static final int		DATE_DIALOG_FROM	= 0;
	private static final int		DATE_DIALOG_TO		= 1;
	private DatabaseManager			dbManager;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reports);
		editImporte = (EditText) findViewById(R.id.editIm);
		editFrom = (EditText) findViewById(R.id.editFrom);
		editTo = (EditText) findViewById(R.id.editTo);
		btnFrom = (Button) findViewById(R.id.btnFrom);
		btnTo = (Button) findViewById(R.id.btnTo);
		btnClearFrom = (Button) findViewById(R.id.btnClearDateFrom);
		btnClearTo = (Button) findViewById(R.id.btnClearExpense);
		btnClearImp = (Button) findViewById(R.id.btnClearImporte);
		btnShowReport = (Button) findViewById(R.id.btnShowReport);
		cmbCategories = (Spinner) findViewById(R.id.cmbCats);
		cmbOperador = (Spinner) findViewById(R.id.cmbOperador);
		if (dbManager == null) {
			dbManager = new DatabaseManager(this, null);
			Logger.message("Se ha creado el objeto para manipular la BD");
		}
		setCategoriesAdapter();
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.operators, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		cmbOperador.setAdapter(adapter);
		btnShowReport.setOnClickListener(this);
		btnClearFrom.setOnClickListener(this);
		btnClearTo.setOnClickListener(this);
		btnClearImp.setOnClickListener(this);
		btnFrom.setOnClickListener(new View.OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				showDialog(DATE_DIALOG_FROM);
			}
		});
		btnTo.setOnClickListener(new View.OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				showDialog(DATE_DIALOG_TO);
			}
		});
	}


	@Override
	protected void onResume() {
		super.onResume();
		setCategoriesAdapter();
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		DateFormat df = SimpleDateFormat.getDateInstance();
		String text = df.format(cal.getTime());
		editFrom.setText(text);
		mYearFrom = cal.get(Calendar.YEAR);
		mMonthFrom = cal.get(Calendar.MONTH);
		mDayFrom = cal.get(Calendar.DAY_OF_MONTH);
		cal = GregorianCalendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, 1);
		df = SimpleDateFormat.getDateInstance();
		text = df.format(cal.getTime());
		editTo.setText(text);
		mYearTo = cal.get(Calendar.YEAR);
		mMonthTo = cal.get(Calendar.MONTH);
		mDayTo = cal.get(Calendar.DAY_OF_MONTH);
	}


	@Deprecated
	protected Dialog onCreateDialog(int id, Bundle args) {
		Calendar cal = GregorianCalendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, 1);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		switch (id) {
		case DATE_DIALOG_FROM:
			return new DatePickerDialog(this, dateSetListenerFrom, year, month, 1);
		case DATE_DIALOG_TO:
			return new DatePickerDialog(this, dateSetListenerTo, year, month, day);
		}
		return null;
	}


	private DatePickerDialog.OnDateSetListener	dateSetListenerFrom	= new DatePickerDialog.OnDateSetListener() {

																		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
																			mYearFrom = year;
																			mMonthFrom = monthOfYear;
																			mDayFrom = dayOfMonth;
																			updateFrom();
																		}
																	};
	private DatePickerDialog.OnDateSetListener	dateSetListenerTo	= new DatePickerDialog.OnDateSetListener() {

																		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
																			mYearTo = year;
																			mMonthTo = monthOfYear;
																			mDayTo = dayOfMonth;
																			updateTo();
																		}
																	};


	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_FROM:
			if (editFrom.getText() != null && !"".equals(editFrom.getText())) {
				DateFormat df = SimpleDateFormat.getDateInstance();
				Calendar cal = df.getCalendar();
				return new DatePickerDialog(this, dateSetListenerFrom, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
			}
			return new DatePickerDialog(this, dateSetListenerFrom, mYearFrom, mMonthFrom, mDayFrom);
		case DATE_DIALOG_TO:
			if (editTo.getText() != null && !"".equals(editTo.getText())) {
				DateFormat df = SimpleDateFormat.getDateInstance();
				Calendar cal = df.getCalendar();
				cal.add(Calendar.DAY_OF_MONTH, 1);
				return new DatePickerDialog(this, dateSetListenerTo, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
			}
			return new DatePickerDialog(this, dateSetListenerTo, mYearTo, mMonthTo, mDayTo);
		}
		return null;
	}


	private void updateFrom() {
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, mDayFrom);
		cal.set(Calendar.MONTH, mMonthFrom);
		cal.set(Calendar.YEAR, mYearFrom);
		DateFormat df = SimpleDateFormat.getDateInstance();
		String text = df.format(cal.getTime());
		editFrom.setText(text);
	}


	private void updateTo() {
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, mDayTo);
		cal.set(Calendar.MONTH, mMonthTo);
		cal.set(Calendar.YEAR, mYearTo);
		DateFormat df = SimpleDateFormat.getDateInstance();
		String text = df.format(cal.getTime());
		editTo.setText(text);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnShowReport:
			Double importe = null;
			Object catObject = cmbCategories.getSelectedItem();
			Object opeObject = cmbOperador.getSelectedItem();
			if (editImporte.getText() != null && !"".equals(editImporte.getText().toString())) {
				importe = Double.parseDouble(editImporte.getText().toString());
			}
			if (editImporte.getText().length() > 7) {
				Toast tostada = Toast.makeText(this, R.string.import_must_not_have_more_than_7d, Toast.LENGTH_SHORT);
				tostada.show();
				editImporte.setText("");
				return;
			}
			ReportFilter reportFilter = new ReportFilter(importe, opeObject.toString(), mDayFrom, mMonthFrom + 1, mYearFrom, mDayTo, mMonthTo + 1, mYearTo, catObject.toString());
			DatabaseManager db = new DatabaseManager(this, null);
			ScroogeDAO scroogeDao = db.getScroogeDAO();
			List<TbGasto> gastos = scroogeDao.getExpensesWithFilter(reportFilter);
			setExpensesWithFilter(gastos);
			db.close();
			if (gastos != null) {
				Intent intent = new Intent(this, ReportListActivity.class);
				this.startActivity(intent);
			} else {
				Toast toast = Toast.makeText(this, R.string.expenses_not_found, Toast.LENGTH_SHORT);
				toast.show();
			}
			break;
		case R.id.btnClearDateFrom:
			mDayFrom = -1;
			mMonthFrom = 0;
			mYearFrom = -1;
			editFrom.setText("");
			break;
		case R.id.btnClearExpense:
			mDayTo = -1;
			mMonthTo = 0;
			mYearTo = -1;
			editTo.setText("");
			break;
		case R.id.btnClearImporte:
			editImporte.setText("");
			break;
		}
	}


	private void setCategoriesAdapter() {
		List<TbCategoria> categories = dbManager.getScroogeDAO().getCategories();
		TbCategoria todas = new TbCategoria();
		todas.setId(-1);
		todas.setNombre(getString(R.string.all_categories));
		categories.add(0, todas);
		String[] cts = new String[categories.size()];
		for (int i = 0; i < categories.size(); i++) {
			TbCategoria c = categories.get(i);
			cts[i] = c.getNombre();
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getBaseContext(), android.R.layout.simple_spinner_item, cts);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		cmbCategories.setAdapter(adapter);
	}


	public static List<TbGasto> getExpensesWithFilter() {
		return staticExpenses;
	}


	public static void setExpensesWithFilter(List<TbGasto> expenses) {
		staticExpenses = expenses;
	}
}
