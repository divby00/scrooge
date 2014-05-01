package org.wildcat.scrooge.utils;


import android.util.Log;


public class Logger {

	public static final boolean	DEBUG	= false;
	public static final String	TAG		= "Registro";


	public static void message(String msg) {
		if (!DEBUG)
			return;
		Log.i(TAG, msg);
	}


	public static void message(int level, String msg) {
		if (!DEBUG)
			return;
		switch (level) {
		case Log.INFO:
			Log.i(TAG, msg);
			break;
		case Log.DEBUG:
			Log.d(TAG, msg);
			break;
		case Log.ERROR:
			Log.e(TAG, msg);
			break;
		case Log.WARN:
			Log.w(TAG, msg);
			break;
		case Log.VERBOSE:
			Log.v(TAG, msg);
			break;
		default:
			Log.i(TAG, msg);
		}
	}
}
