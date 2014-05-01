package org.wildcat.scrooge;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import org.wildcat.scrooge.persistence.DatabaseManager;
import org.wildcat.scrooge.persistence.beans.TbGasto;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class ReportListActivity extends ListActivity {

	private Button		btnExportCSV	= null;
	private TextView	lblSuma			= null;
	private Activity	activity		= null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.expenses_list);
		this.activity = this;
		List<TbGasto> expenses = ReportsActivity.getExpensesWithFilter();
		Double sum = (double) 0;
		for (TbGasto e : expenses) {
			sum += e.getImporte();
		}
		setListAdapter(new ReportListAdapter(this, expenses));
		btnExportCSV = (Button) findViewById(R.id.btnExportCSV);
		lblSuma = (TextView) findViewById(R.id.lblSuma);
		btnExportCSV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				exportCSV();
			}
		});
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);
		lblSuma.setText(MessageFormat.format(getString(R.string.total), nf.format(sum)));
	}


	@SuppressLint("SimpleDateFormat")
	private void exportCSV() {
		String sdState = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(sdState)) {
			Calendar cal = GregorianCalendar.getInstance();
			DateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
			String fileName = df.format(cal.getTime()) + ".csv";
			try {
				File file = Environment.getExternalStorageDirectory();
				File dir = new File(file.getAbsolutePath() + "/Scrooge/Reports/");
				dir.mkdirs();
				File f = new File(dir, fileName);
				OutputStreamWriter fout = new OutputStreamWriter(new FileOutputStream(f));
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
				DatabaseManager dbManager = new DatabaseManager(this, null);
				List<TbGasto> exp = dbManager.getScroogeDAO().getExpenses();
				dbManager.close();
				NumberFormat nf = NumberFormat.getInstance();
				nf.setMinimumFractionDigits(2);
				nf.setMaximumFractionDigits(2);
				DateFormat df1 = DateFormat.getInstance();
				for (TbGasto e : exp) {
					fout.write(e.getIdGasto() + ";");
					fout.write(e.getIdCategoria() + ";");
					fout.write(e.getNombreCategoria() + ";");
					fout.write(nf.format(e.getImporte()) + ";");
					cal.setTimeInMillis(e.getFecha());
					fout.write(df1.format(cal.getTime()) + "\n");
				}
				fout.close();
				Toast tostada = Toast.makeText(activity.getApplicationContext(), MessageFormat.format(getString(R.string.data_exported_to_file_of_persistence_drive), fileName), Toast.LENGTH_LONG);
				tostada.show();
			} catch (Exception e) {
				Toast tostada = Toast.makeText(activity.getApplicationContext(), MessageFormat.format(getString(R.string.unable_to_save_file), fileName), Toast.LENGTH_SHORT);
				tostada.show();
			}
		} else {
			Toast tostada = Toast.makeText(activity.getApplicationContext(), R.string.no_storage_found_or_read_only, Toast.LENGTH_SHORT);
			tostada.show();
		}
	}
}
