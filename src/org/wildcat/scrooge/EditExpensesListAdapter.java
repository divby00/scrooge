package org.wildcat.scrooge;


import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import org.wildcat.scrooge.persistence.DatabaseManager;
import org.wildcat.scrooge.persistence.beans.TbGasto;
import org.wildcat.scrooge.persistence.dao.ScroogeDAO;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class EditExpensesListAdapter extends BaseAdapter {

	private Activity		activity	= null;
	private List<TbGasto>	expenses	= null;


	public EditExpensesListAdapter(Activity activity, List<TbGasto> expenses) {
		super();
		this.activity = activity;
		this.expenses = expenses;
	}


	@Override
	public int getCount() {
		if (expenses != null) {
			return expenses.size();
		} else {
			return 0;
		}
	}


	@Override
	public Object getItem(int id) {
		return expenses.get(id);
	}


	@Override
	public long getItemId(int arg0) {
		return expenses.get(arg0).getIdGasto();
	}


	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		final int id = arg0;
		LayoutInflater inflater = activity.getLayoutInflater();
		View view = inflater.inflate(R.layout.edit_expenses_list_element, null);
		TextView lblExpense = (TextView) view.findViewById(R.id.lblEditExpense);
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);
		lblExpense.setText(nf.format(expenses.get(arg0).getImporte()).toString());
		TextView lblCategory = (TextView) view.findViewById(R.id.lblEdCatText);
		lblCategory.setText(expenses.get(arg0).getNombreCategoria());
		TextView lblDate = (TextView) view.findViewById(R.id.lblEditDate);
		if (expenses.get(arg0).getFecha() != null) {
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTimeInMillis(expenses.get(arg0).getFecha());
			DateFormat df = SimpleDateFormat.getDateInstance();
			lblDate.setText(df.format(cal.getTime()));
		}
		ImageView image = (ImageView) view.findViewById(R.id.btnListRemoveCat);
		ImageView btnModify = (ImageView) view.findViewById(R.id.btnListEditCat);
		image.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// Remove the element
				AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
				dialog.setTitle(R.string.warning);
				dialog.setMessage(R.string.do_you_want_to_remove_this_expense);
				dialog.setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						DatabaseManager dbManager = new DatabaseManager(activity, null);
						ScroogeDAO scroogeDao = dbManager.getScroogeDAO();
						boolean resultado = false;
						resultado = scroogeDao.deleteExpense(expenses.get(id).getIdGasto());
						Toast tostada = null;
						if (resultado) {
							tostada = Toast.makeText(activity, R.string.expense_removed_correctly, Toast.LENGTH_SHORT);
							expenses = scroogeDao.getExpenses();
							notifyDataSetChanged();
						} else {
							tostada = Toast.makeText(activity, R.string.unable_to_remove_expense, Toast.LENGTH_SHORT);
						}
						tostada.show();
						dbManager.close();
					}
				});
				dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
					}
				});
				dialog.show();
			}
		});
		btnModify.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(activity, UpdateExpenseActivity.class);
				intent.putExtra("id_gasto", expenses.get(id).getIdGasto());
				activity.startActivity(intent);
			}
		});
		return view;
	}
}
