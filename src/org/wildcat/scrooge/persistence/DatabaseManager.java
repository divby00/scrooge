package org.wildcat.scrooge.persistence;


import org.wildcat.scrooge.constants.Constants;
import org.wildcat.scrooge.persistence.dao.ScroogeDAO;
import org.wildcat.scrooge.utils.Logger;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseManager extends SQLiteOpenHelper {

	private ScroogeDAO		scroogeDao	= null;
	private SQLiteDatabase	db			= null;


	public DatabaseManager(Context context, CursorFactory factory) {
		super(context, Constants.DB_NAME, factory, Constants.DB_VERSION);
		this.db = this.getWritableDatabase();
		scroogeDao = new ScroogeDAO(db);
	}


	public ScroogeDAO getScroogeDAO() {
		return this.scroogeDao;
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		/* Script de BD para Scrooge */
		StringBuilder sb = new StringBuilder();
		sb.append(Constants.Sql.MAKE_TABLE_CATEGORY);
		db.execSQL(sb.toString());
		sb.setLength(0);
		sb.append(Constants.Sql.MAKE_TABLE_EXPENSE);
		db.execSQL(sb.toString());
	}


	public SQLiteDatabase getReadDb() {
		return getReadableDatabase();
	}


	public SQLiteDatabase getWriteDb() {
		return getWritableDatabase();
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}


	public void close(SQLiteDatabase db) {
		if (db.isOpen()) {
			Logger.message("Base datos creada");
			db.close();
		}
	}
}
