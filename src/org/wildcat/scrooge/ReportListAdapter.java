package org.wildcat.scrooge;


import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import org.wildcat.scrooge.persistence.beans.TbGasto;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class ReportListAdapter extends BaseAdapter {

	private Activity		activity	= null;
	private List<TbGasto>	expenses	= null;


	public ReportListAdapter(Activity activity, List<TbGasto> expenses) {
		super();
		this.activity = activity;
		this.expenses = expenses;
	}


	@Override
	public int getCount() {
		return expenses.size();
	}


	@Override
	public Object getItem(int arg0) {
		return expenses.get(arg0);
	}


	@Override
	public long getItemId(int arg0) {
		return arg0;
	}


	@Override
	public View getView(int position, View arg1, ViewGroup arg2) {
		LayoutInflater inflater = activity.getLayoutInflater();
		View view = inflater.inflate(R.layout.expenses_list_element, null);
		TextView lblExpense = (TextView) view.findViewById(R.id.lblExpense);
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);
		lblExpense.setText(nf.format(expenses.get(position).getImporte()).toString());
		TextView lblCategory = (TextView) view.findViewById(R.id.lblCategory);
		lblCategory.setText(expenses.get(position).getNombreCategoria());
		TextView lblDate = (TextView) view.findViewById(R.id.lblDate);
		if (expenses.get(position).getFecha() != null) {
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTimeInMillis(expenses.get(position).getFecha());
			DateFormat df = SimpleDateFormat.getDateInstance();
			lblDate.setText(df.format(cal.getTime()));
		}
		return view;
	}
}
