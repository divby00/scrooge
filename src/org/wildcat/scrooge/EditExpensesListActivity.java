package org.wildcat.scrooge;


import java.util.List;
import org.wildcat.scrooge.persistence.DatabaseManager;
import org.wildcat.scrooge.persistence.beans.TbGasto;
import org.wildcat.scrooge.persistence.dao.ScroogeDAO;
import android.app.ListActivity;
import android.os.Bundle;


public class EditExpensesListActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_expenses_list);
		prepare();
	}


	@Override
	protected void onResume() {
		super.onResume();
		prepare();
	}


	private void prepare() {
		DatabaseManager dbManager = new DatabaseManager(this, null);
		ScroogeDAO scroogeDao = dbManager.getScroogeDAO();
		List<TbGasto> expenses = scroogeDao.getExpenses();
		dbManager.close();
		setListAdapter(new EditExpensesListAdapter(this, expenses));
	}
}
