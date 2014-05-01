package org.wildcat.scrooge;


import java.util.List;
import org.wildcat.scrooge.persistence.DatabaseManager;
import org.wildcat.scrooge.persistence.beans.TbCategoria;
import org.wildcat.scrooge.persistence.dao.ScroogeDAO;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


public class EditCategoriesListActivity extends ListActivity implements OnClickListener {

	private EditText					editCat			= null;
	private ImageView					btnEditClear	= null;
	private ImageView					btnAddCat		= null;
	private EditCategoriesListAdapter	listAdapter		= null;


	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_categories_list);
		editCat = (EditText) findViewById(R.id.editCatTextName);
		btnEditClear = (ImageView) findViewById(R.id.editCatClearName);
		btnAddCat = (ImageView) findViewById(R.id.editCatAddName);
		DatabaseManager dbManager = new DatabaseManager(this, null);
		ScroogeDAO scroogeDao = dbManager.getScroogeDAO();
		List<TbCategoria> cats = scroogeDao.getCategories();
		dbManager.close();
		listAdapter = new EditCategoriesListAdapter(this, cats);
		setListAdapter(listAdapter);
		btnEditClear.setOnClickListener(this);
		btnAddCat.setOnClickListener(this);
		releaseFocus(btnAddCat);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.editCatClearName:
			editCat.setText("");
			break;
		case R.id.editCatAddName:
			if ("".equals(editCat.getText().toString())) {
				Toast toast = Toast.makeText(this, R.string.input_name_for_category, Toast.LENGTH_SHORT);
				toast.show();
			} else {
				// Introducimos la categoría, para ello miramos a ver si ya
				// existe una categoría con ese nombre,
				// si es el caso damos un mensaje de error, si no intentamos
				// meterla en la base de datos. Antes
				// de nada miramos que el nombre de la categoría no tenga
				// más de 15 caracteres.
				if (editCat.getText().length() > 15) {
					Toast toast = Toast.makeText(this, R.string.category_name_no_more_15c, Toast.LENGTH_SHORT);
					toast.show();
					return;
				}
				DatabaseManager dbManager = new DatabaseManager(this, null);
				ScroogeDAO scroogeDao = dbManager.getScroogeDAO();
				// Capitalizamos la cadena
				StringBuilder sb = new StringBuilder(editCat.getText().toString());
				sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
				if (scroogeDao.checkCategoryExists(sb.toString())) {
					Toast toast = Toast.makeText(this, R.string.category_exists, Toast.LENGTH_SHORT);
					toast.show();
				} else {
					if (!scroogeDao.insertCategory(sb.toString())) {
						Toast toast = Toast.makeText(this, R.string.unable_to_add_category, Toast.LENGTH_SHORT);
						toast.show();
					} else {
						editCat.setText("");
						Toast toast = Toast.makeText(this, R.string.category_added_correctly, Toast.LENGTH_SHORT);
						toast.show();
						listAdapter.actualizarCategorias();
					}
				}
				dbManager.close();
			}
			break;
		}
	}


	public static void releaseFocus(View view) {
		ViewParent parent = view.getParent();
		ViewGroup group = null;
		View child = null;
		while (parent != null) {
			if (parent instanceof ViewGroup) {
				group = (ViewGroup) parent;
				for (int i = 0; i < group.getChildCount(); i++) {
					child = group.getChildAt(i);
					if (child != view && child.isFocusable())
						child.requestFocus();
				}
			}
			parent = parent.getParent();
		}
	}
}
