package org.wildcat.scrooge.constants;


public class Constants {

	public static final String	DB_NAME			= "scrooge";
	public static final int		DB_VERSION		= 1;
	public static final String	FIRST_TIME_FILE	= "scrooge_first_time";


	public static class Sql {

		public static final String	MAKE_TABLE_CATEGORY			= " create table tb_categoria (" + "id integer primary key autoincrement, nombre text not null);";
		public static final String	MAKE_TABLE_EXPENSE			= " create table tb_gasto ( " + " id_gasto integer primary key autoincrement, " + " importe real not null, " + " id_categoria integer , " + " fecha integer, " + " nombre_categoria text, " + " foreign key (id_categoria) references tb_categoria); ";
		public final static String	CHECK_CATEGORY_EXISTS		= " SELECT NOMBRE FROM TB_CATEGORIA ";
		public static final String	DELETE_EXPENSE_BY_CATEGORY	= " DELETE FROM TB_GASTO WHERE ID_GASTO IN (SELECT ID_GASTO FROM TB_GASTO WHERE ID_CATEGORIA = {0})";
		public static final String	EXPENSE_HAS_CATEGORY		= " SELECT ID_CATEGORIA FROM TB_GASTO WHERE ID_CATEGORIA = {0}";
		public final static String	GET_EXPENSE_BY_ID			= " SELECT ID_GASTO,IMPORTE,ID_CATEGORIA,FECHA FROM TB_GASTO WHERE ID_GASTO = {0}";
		public final static String	GET_CATEGORY				= " SELECT * FROM TB_CATEGORIA WHERE ID = {0} ";
		public final static String	DELETE_CATEGORY				= " DELETE FROM TB_CATEGORIA WHERE ID = {0}";
		public final static String	DELETE_EXPENSE				= " DELETE FROM TB_GASTO WHERE ID_GASTO = {0}";
		public final static String	GET_LAST_EXPENSE			= " SELECT ID_GASTO,IMPORTE,ID_CATEGORIA,FECHA FROM TB_GASTO ORDER BY FECHA DESC ";
		public static final String	GET_CATEGORY_BY_NAME		= " SELECT ID FROM TB_CATEGORIA WHERE NOMBRE = \"{0}\"";
		public static final String	EXISTS_CATEGORY				= " SELECT upper(NOMBRE) FROM TB_CATEGORIA WHERE NOMBRE LIKE \"{0}\"";
		public static final String	INSERT_CATEGORY				= " INSERT INTO TB_CATEGORIA VALUES (null,\"{0}\")";
		public final static String	INSERT_EXPENSE				= " INSERT INTO TB_GASTO VALUES (null, {0},{1},{2},\"{3}\")";
		public static final String	GET_CATEGORIES				= " SELECT * FROM TB_CATEGORIA ORDER BY NOMBRE";
		public static final String	GET_EXPENSES				= " SELECT * FROM TB_GASTO ORDER BY FECHA DESC";
	}
}
