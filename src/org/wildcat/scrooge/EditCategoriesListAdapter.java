package org.wildcat.scrooge;


import java.util.List;
import org.wildcat.scrooge.persistence.DatabaseManager;
import org.wildcat.scrooge.persistence.beans.TbCategoria;
import org.wildcat.scrooge.persistence.dao.ScroogeDAO;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class EditCategoriesListAdapter extends BaseAdapter {

	private Activity			activity	= null;
	private List<TbCategoria>	cats		= null;


	public void actualizarCategorias() {
		DatabaseManager dbManager = new DatabaseManager(activity, null);
		ScroogeDAO scroogeDao = dbManager.getScroogeDAO();
		cats = scroogeDao.getCategories();
		dbManager.close();
		notifyDataSetChanged();
	}


	public EditCategoriesListAdapter(Activity activity, List<TbCategoria> cats) {
		super();
		this.activity = activity;
		this.cats = cats;
	}


	public int getCount() {
		return cats.size();
	}


	public Object getItem(int position) {
		return cats.get(position);
	}


	public long getItemId(int position) {
		return cats.get(position).getId();
	}


	public View getView(int arg0, View convertView, ViewGroup parent) {
		final int id = arg0;
		LayoutInflater inflater = activity.getLayoutInflater();
		View view = inflater.inflate(R.layout.edit_categories_list_element, null);
		TextView lblCategory = (TextView) view.findViewById(R.id.lblEdCatTexto);
		lblCategory.setText(cats.get(arg0).getNombre());
		ImageView image = (ImageView) view.findViewById(R.id.btnListRemoveCat);
		ImageView btnModify = (ImageView) view.findViewById(R.id.btnListEditCat);
		image.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// Remove the element
				if (getCount() == 1) {
					Toast toast = Toast.makeText(activity, R.string.cant_remove_all_categories_have_to_leave_one, Toast.LENGTH_SHORT);
					toast.show();
					return;
				}
				AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
				dialog.setTitle(R.string.warning);
				dialog.setMessage(R.string.do_you_want_to_remove_category);
				dialog.setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						DatabaseManager dbManager = new DatabaseManager(activity, null);
						ScroogeDAO scroogeDao = dbManager.getScroogeDAO();
						boolean resultado = false;
						resultado = scroogeDao.gastoHasCategory(cats.get(id).getId());
						dbManager.close();
						if (resultado) {
							AlertDialog.Builder dialogDeleteExpenses = new AlertDialog.Builder(activity);
							dialogDeleteExpenses.setTitle(R.string.warning);
							dialogDeleteExpenses.setMessage(R.string.expenses_linked_to_the_category);
							dialogDeleteExpenses.setPositiveButton(R.string.yes_remove, new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									DatabaseManager dbManager = new DatabaseManager(activity, null);
									ScroogeDAO scroogeDao = dbManager.getScroogeDAO();
									Boolean result = scroogeDao.deleteExpensesWithCategory(cats.get(id).getId());
									if (!result) {
										Toast toast = Toast.makeText(activity, R.string.unable_to_remove_expenses_linked_to_the_category, Toast.LENGTH_SHORT);
										toast.show();
									} else {
										Boolean result3 = scroogeDao.deleteCategory(cats.get(id).getId());
										Toast tostada = null;
										if (result3) {
											tostada = Toast.makeText(activity, R.string.category_and_expenses_removed_correctly, Toast.LENGTH_SHORT);
											cats = scroogeDao.getCategories();
											notifyDataSetChanged();
										} else {
											tostada = Toast.makeText(activity, R.string.unable_to_remove_category, Toast.LENGTH_SHORT);
										}
										tostada.show();
									}
									dbManager.close();
								}
							});
							dialogDeleteExpenses.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
								}
							});
							dialogDeleteExpenses.show();
						} else {
							dbManager = new DatabaseManager(activity, null);
							scroogeDao = dbManager.getScroogeDAO();
							resultado = scroogeDao.deleteCategory(cats.get(id).getId());
							Toast tostada = null;
							if (resultado) {
								tostada = Toast.makeText(activity, R.string.category_removed_correctly, Toast.LENGTH_SHORT);
								cats = scroogeDao.getCategories();
								notifyDataSetChanged();
							} else {
								tostada = Toast.makeText(activity, R.string.unable_to_remove_category, Toast.LENGTH_SHORT);
							}
							tostada.show();
							dbManager.close();
						}
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
				/*
				 * Intent intent = new Intent(activity,
				 * UpdateExpenseActivity.class); intent.putExtra("id_gasto",
				 * expenses.get(id).getIdGasto());
				 * activity.startActivity(intent);
				 */
			}
		});
		return view;
	}
}
