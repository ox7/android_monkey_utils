package com.autonavi.androidmonkey;

import android.util.Log;

public class MonkeyLog {
	
	private final static String TAG = "monkey_log";
	
	public static void l(String msg) {
		Log.i(TAG, msg);
	}
}
