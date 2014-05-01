package org.wildcat.scrooge.persistence.dao;


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import org.wildcat.scrooge.constants.Constants;
import org.wildcat.scrooge.persistence.beans.TbCategoria;
import org.wildcat.scrooge.persistence.beans.TbGasto;
import org.wildcat.scrooge.persistence.filter.ReportFilter;
import org.wildcat.scrooge.utils.Logger;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class ScroogeDAO {

	private SQLiteDatabase	db	= null;


	public ScroogeDAO(SQLiteDatabase db) {
		this.db = db;
	}


	public List<TbGasto> getExpenses() {
		List<TbGasto> expenses = null;
		Cursor cursor = db.rawQuery(Constants.Sql.GET_EXPENSES, null);
		if (cursor.moveToFirst()) {
			expenses = new ArrayList<TbGasto>();
			do {
				TbGasto exp = new TbGasto();
				exp.setIdGasto(cursor.getInt(0));
				exp.setImporte(cursor.getDouble(1));
				exp.setIdCategoria(cursor.getInt(2));
				exp.setFecha(cursor.getLong(3));
				exp.setNombreCategoria(cursor.getString(4));
				expenses.add(exp);
				exp = null;
			} while (cursor.moveToNext());
		}
		cursor.close();
		return expenses;
	}


	public List<TbCategoria> getCategories() {
		List<TbCategoria> categorias = null;
		Cursor cursor = null;
		cursor = db.rawQuery(Constants.Sql.GET_CATEGORIES, null);
		if (cursor.moveToFirst()) {
			categorias = new ArrayList<TbCategoria>();
			do {
				TbCategoria categoria = new TbCategoria();
				categoria.setId(cursor.getLong(0));
				categoria.setNombre(cursor.getString(1));
				categorias.add(categoria);
				categoria = null;
			} while (cursor.moveToNext());
		}
		cursor.close();
		return categorias;
	}


	public boolean insertExpense(Double importe, String categoria) {
		if ("".equals(categoria) || categoria == null)
			return false;
		if ("".equals(importe) || importe == null)
			return false;
		Long key = getCategoryKeyByName(categoria);
		if (key == -1)
			return false;
		Calendar cal = GregorianCalendar.getInstance();
		Long time = cal.getTimeInMillis();
		try {
			db.execSQL(MessageFormat.format(Constants.Sql.INSERT_EXPENSE, Double.toString(importe), Long.toString(key), Long.toString(time), categoria));
		} catch (SQLiteConstraintException e) {
			return false;
		}
		return true;
	}


	public boolean insertCategory(String categoria) {
		String sql = MessageFormat.format(Constants.Sql.INSERT_CATEGORY, categoria);
		if ("".equals(categoria) || categoria == null)
			return false;
		try {
			db.execSQL(sql);
		} catch (Exception e) {
			return false;
		}
		return true;
	}


	public boolean existsCategory(String category) {
		boolean aux = false;
		Cursor cursor = db.rawQuery(MessageFormat.format(Constants.Sql.EXISTS_CATEGORY, category.toUpperCase(Locale.getDefault())), null);
		if (cursor.moveToFirst())
			aux = true;
		cursor.close();
		return aux;
	}


	public Long getCategoryKeyByName(String nombre) {
		Long resultado = -1L;
		if ("".equals(nombre) || nombre == null)
			return resultado;
		try {
			String sql = MessageFormat.format(Constants.Sql.GET_CATEGORY_BY_NAME, nombre);
			Cursor cursor = db.rawQuery(sql, null);
			if (cursor.moveToFirst()) {
				do {
					resultado = cursor.getLong(0);
				} while (cursor.moveToNext());
			}
			cursor.close();
		} catch (Exception e) {
			Logger.message(Log.ERROR, "Se ha producido un error: " + e.getMessage());
		}
		return resultado;
	}


	public TbGasto getLastExpense() {
		TbGasto tbGasto = null;
		try {
			Cursor cursor = db.rawQuery(Constants.Sql.GET_LAST_EXPENSE, null);
			if (cursor.moveToFirst()) {
				tbGasto = new TbGasto();
				tbGasto.setIdGasto(cursor.getInt(0));
				tbGasto.setImporte(cursor.getDouble(1));
				tbGasto.setIdCategoria(cursor.getInt(2));
				tbGasto.setFecha(cursor.getLong(3));
			}
			cursor.close();
		} catch (Exception e) {
			Logger.message(Log.DEBUG, "Se ha producido un error: " + e.getMessage());
		}
		return tbGasto;
	}


	public boolean deleteExpense(Integer idGasto) {
		boolean aux = false;
		try {
			String sql = MessageFormat.format(Constants.Sql.DELETE_EXPENSE, Integer.toString(idGasto));
			db.execSQL(sql);
			aux = true;
		} catch (Exception e) {
			Logger.message(Log.DEBUG, "Se ha producido un error: " + e.getMessage());
			aux = false;
		}
		return aux;
	}


	public boolean deleteCategory(Long idCat) {
		boolean aux = false;
		try {
			db.execSQL(MessageFormat.format(Constants.Sql.DELETE_CATEGORY, Long.toString(idCat)));
			aux = true;
		} catch (Exception e) {
			Logger.message(Log.DEBUG, "Se ha producido un error: " + e.getMessage());
			aux = false;
		}
		return aux;
	}


	public TbCategoria getCategory(Long idCat) {
		TbCategoria cat = null;
		try {
			Cursor cursor = db.rawQuery(MessageFormat.format(Constants.Sql.GET_CATEGORY, Long.toString(idCat)), null);
			if (cursor.moveToFirst()) {
				cat = new TbCategoria();
				cat.setId(cursor.getLong(0));
				cat.setNombre(cursor.getString(1));
			}
			cursor.close();
		} catch (Exception e) {
			Logger.message(Log.ERROR, "Se ha producido un error: " + e.getMessage());
		}
		return cat;
	}


	public boolean insertDefaultCategories(String[] cts) {
		boolean aux = true;
		for (String c : cts) {
			if (!insertCategory(c))
				aux = false;
		}
		return aux;
	}


	public List<TbGasto> getExpensesWithFilter(ReportFilter reportFilter) {
		Calendar calFrom = null;
		Calendar calTo = null;
		Long timeFrom = null;
		Long timeTo = null;
		if (reportFilter.getfDay() != null && !"".equals(reportFilter.getfDay()) && reportFilter.getfDay() != -1 && reportFilter.getfMonth() != null && !"".equals(reportFilter.getfMonth()) && reportFilter.getfMonth() != 0 && reportFilter.getfYear() != null && !"".equals(reportFilter.getfYear()) && reportFilter.getfYear() != -1) {
			calFrom = GregorianCalendar.getInstance();
			calFrom.set(Calendar.DAY_OF_MONTH, reportFilter.getfDay());
			calFrom.set(Calendar.MONTH, reportFilter.getfMonth() - 1);
			calFrom.set(Calendar.YEAR, reportFilter.getfYear());
			calFrom.set(Calendar.HOUR_OF_DAY, 0);
			calFrom.set(Calendar.MINUTE, 0);
			calFrom.set(Calendar.SECOND, 0);
			timeFrom = calFrom.getTimeInMillis();
		}
		if (reportFilter.gettDay() != null && !"".equals(reportFilter.gettDay()) && reportFilter.gettDay() != -1 && reportFilter.gettMonth() != null && !"".equals(reportFilter.gettMonth()) && reportFilter.gettMonth() != 0 && reportFilter.gettYear() != null && !"".equals(reportFilter.gettYear()) && reportFilter.gettYear() != -1) {
			calTo = GregorianCalendar.getInstance();
			calTo.set(Calendar.DAY_OF_MONTH, reportFilter.gettDay());
			calTo.set(Calendar.MONTH, reportFilter.gettMonth() - 1);
			calTo.set(Calendar.YEAR, reportFilter.gettYear());
			calTo.set(Calendar.HOUR_OF_DAY, 23);
			calTo.set(Calendar.MINUTE, 59);
			calTo.set(Calendar.SECOND, 59);
			timeTo = calTo.getTimeInMillis();
		}
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM TB_GASTO WHERE 1=1 ");
		if (timeFrom != null)
			sb.append(" AND FECHA >= " + timeFrom);
		if (timeTo != null)
			sb.append(" AND FECHA <= " + timeTo);
		if (reportFilter.getCategory() != null && !"".equals(reportFilter.getCategory()) && !"Todas".equals(reportFilter.getCategory())) {
			Long idCat = -1L;
			idCat = getCategoryKeyByName(reportFilter.getCategory());
			if (idCat != -1)
				sb.append(" AND ID_CATEGORIA = " + idCat + " ");
		}
		if (reportFilter.getImporte() != null && !"".equals(reportFilter.getImporte())) {
			sb.append("  AND IMPORTE " + reportFilter.getOperator() + " " + reportFilter.getImporte());
		}
		sb.append(" ORDER BY FECHA DESC ");
		List<TbGasto> gastos = null;
		Cursor cursor = db.rawQuery(sb.toString(), null);
		if (cursor.moveToFirst()) {
			gastos = new ArrayList<TbGasto>();
			do {
				TbGasto gasto = new TbGasto();
				gasto.setIdGasto(cursor.getInt(0));
				gasto.setImporte(cursor.getDouble(1));
				gasto.setIdCategoria(cursor.getInt(2));
				gasto.setFecha(cursor.getLong(3));
				gasto.setNombreCategoria(cursor.getString(4));
				gastos.add(gasto);
				gasto = null;
			} while (cursor.moveToNext());
		}
		cursor.close();
		return gastos;
	}


	public void deleteExpenses() throws Exception {
		db.delete("TB_GASTO", null, null);
	}


	public void deleteCategories() throws Exception {
		db.delete("TB_CATEGORIA", null, null);
	}


	public void insertCategories(List<TbCategoria> cats) throws Exception {
		ContentValues values = new ContentValues();
		for (TbCategoria c : cats) {
			values.put("ID", c.getId());
			values.put("NOMBRE", c.getNombre());
			db.insert("TB_CATEGORIA", null, values);
		}
	}


	public void insertExpenses(List<TbGasto> exps) throws Exception {
		ContentValues values = new ContentValues();
		for (TbGasto e : exps) {
			values.put("ID_GASTO", e.getIdGasto());
			values.put("IMPORTE", e.getImporte());
			values.put("ID_CATEGORIA", e.getIdCategoria());
			values.put("FECHA", e.getFecha());
			values.put("NOMBRE_CATEGORIA", e.getNombreCategoria());
			db.insert("TB_GASTO", null, values);
		}
	}


	public TbGasto getExpenseById(Integer id) {
		TbGasto tbGasto = null;
		try {
			Cursor cursor = db.rawQuery(MessageFormat.format(Constants.Sql.GET_EXPENSE_BY_ID, Integer.toString(id)), null);
			if (cursor.moveToFirst()) {
				tbGasto = new TbGasto();
				tbGasto.setIdGasto(cursor.getInt(0));
				tbGasto.setImporte(cursor.getDouble(1));
				tbGasto.setIdCategoria(cursor.getInt(2));
				tbGasto.setFecha(cursor.getLong(3));
			}
			cursor.close();
		} catch (Exception e) {
			Logger.message(Log.DEBUG, "Se ha producido un error: " + e.getMessage());
		}
		return tbGasto;
	}


	public Boolean gastoHasCategory(Long idCat) {
		Boolean resultado = false;
		try {
			Cursor cursor = db.rawQuery(MessageFormat.format(Constants.Sql.EXPENSE_HAS_CATEGORY, Long.toString(idCat)), null);
			if (cursor.moveToFirst()) {
				Integer var = cursor.getInt(0);
				if (var != null && var != 0)
					resultado = true;
			}
			cursor.close();
		} catch (Exception e) {
			Logger.message(Log.DEBUG, "Se ha producido un error: " + e.getMessage());
		}
		return resultado;
	}


	public Boolean deleteExpensesWithCategory(Long id) {
		Boolean result = false;
		try {
			db.execSQL(MessageFormat.format(Constants.Sql.DELETE_EXPENSE_BY_CATEGORY, Long.toString(id)));
			result = true;
		} catch (Exception e) {
			Logger.message(Log.DEBUG, "Se ha producido un error: " + e.getMessage());
			result = false;
		}
		return result;
	}


	public Boolean checkCategoryExists(String catName) {
		List<String> results = new ArrayList<String>();
		String cat = catName.toUpperCase(Locale.getDefault());
		try {
			Cursor cursor = db.rawQuery(Constants.Sql.CHECK_CATEGORY_EXISTS, null);
			if (cursor.moveToFirst()) {
				do {
					results.add(cursor.getString(0).toUpperCase(Locale.getDefault()));
				} while (cursor.moveToNext());
				cursor.close();
			}
			for (String s : results) {
				if (cat.equals(s))
					return true;
			}
		} catch (Exception e) {
			Logger.message(Log.DEBUG, "Se ha producido un error: " + e.getMessage());
			return true;
		}
		return false;
	}


	public Boolean updateExpense(TbGasto exp) {
		try {
			ContentValues values = new ContentValues();
			values.put("IMPORTE", exp.getImporte());
			values.put("ID_CATEGORIA", exp.getIdCategoria());
			values.put("FECHA", exp.getFecha());
			values.put("NOMBRE_CATEGORIA", exp.getNombreCategoria());
			db.update("TB_GASTO", values, "ID_GASTO = " + exp.getIdGasto(), null);
		} catch (Exception e) {
			Logger.message(Log.DEBUG, "Se ha producido un error: " + e.getMessage());
			return false;
		}
		return true;
	}
}
