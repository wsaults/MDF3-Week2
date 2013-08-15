/*
 * project 	Weather
 * 
 * package 	com.fullsail.weather
 * 
 * @author 	William Saults
 * 
 * date 	Aug 8, 2013
 */
package com.fullsail.thingtag;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/*
 * This class is all about checking for internet connectivity.
 */
public class Connectivity {
	static Boolean _conn = false;
	static String _connectionType = "Unavailable";
	
	private static Connectivity instance = null;
	protected Connectivity() {
		// Exists to defeat instantiation.
	}
	public static Connectivity getInstance() {
		if(instance == null) {
			instance = new Connectivity();
		}
		return instance;
	}
	
	public static String getConnectionType(Context context) {
		netInfo(context);
		return _connectionType;
	}
	
	public static Boolean getConnectionStatus(Context context) {
		netInfo(context);
		return _conn;
	}
	
	private static void netInfo(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni != null) {
			if (ni.isConnected()) {
				_connectionType= ni.getTypeName();
				_conn = true;
			}
		}
	}
}
