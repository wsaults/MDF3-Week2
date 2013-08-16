/*
 * project 	ThingTag
 * 
 * package 	com.fullsail.thingtag
 * 
 * @author 	William Saults
 * 
 * date 	Aug 15, 2013
 */
package com.fullsail.thingtag;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

// TODO: Auto-generated Javadoc
/**
 * The Class MainActivity.
 */
public class MainActivity extends Activity implements LocationListener {

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;

	private final int VIB_NOTE_ID = 2;
	private final int SND_NOTE_ID = 4;
	//	private final int TXT_NOTE_ID = 5;

	private Uri fileUri;
	Context context = this;
	public static Boolean connected = false;
	TextView lat;
	TextView lon;

	LocationManager lm;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); 

		lat = (TextView) findViewById(R.id.lat);
		lon = (TextView) findViewById(R.id.lon);

		// Test Network Connetion
		connected = Connectivity.getConnectionStatus(context);

		lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		if (lm == null) {
			new AlertDialog.Builder(context)
			.setTitle("Error")
			.setMessage("Location Manager is unavaliable")
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {}
			})
			.show();
		} else {

			if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3*1000 /*msec*/, 0 /*meters*/, this);
			} else if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3*1000 /*msec*/, 0 /*meters*/, this);
			} else {
				try {
					throw (new Exception("No location provider is available!"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		// Launch camera
		Button cameraButton = (Button) findViewById(R.id.tagItButton);
		cameraButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!connected)  {
					noConnectionAlert();
					return;
				}
				// create Intent to take a picture and return control to the calling application
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

				fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
				intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

				// start the image capture Intent
				startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
			}
		});

		// Launch video
		Button videoButton = (Button) findViewById(R.id.tagItVideoButton);
		videoButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!connected)  {
					noConnectionAlert();
					return;
				}
				//create new Intent
				Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
				fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);  // create a file to save the video
				//				intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);  // set the image file name

				intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // set the video image quality to high

				// start the Video Capture Intent
				startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);

			}
		});

		// View tags
		Button viewButton = (Button) findViewById(R.id.viewTagsButton);
		viewButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent galleryIntent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(galleryIntent , 0);

			}
		});
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * No connection alert.
	 */
	public void noConnectionAlert() {
		// Alert the user that there is no internet connection			
		new AlertDialog.Builder(context)
		.setTitle("Internet connectivity")
		.setMessage("Cannot store GPS data without internet.")
		.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {}
		})
		.show();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, fileUri));
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				// Image captured and saved to fileUri specified in the Intent
				//	            Toast.makeText(this, "Image saved to:\n" + data.getData(), Toast.LENGTH_LONG).show();
				NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				Notification notification = new Notification();
				nm.notify(VIB_NOTE_ID, notification);
				nm.notify(SND_NOTE_ID, notification);

				//				NotificationCompat.Builder mBuilder =
				//				        new NotificationCompat.Builder(this)
				//				        .setContentTitle("Great job!!!")
				//				        .setContentText("You just tagged an image!");
				//				// Creates an explicit intent for an Activity in your app
				////				Intent resultIntent = new Intent(this, ResultActivity.class);
				//
				//				TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
				//
				////				stackBuilder.addParentStack(ResultActivity.class);
				////				stackBuilder.addNextIntent(resultIntent);
				//				PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
				//				mBuilder.setContentIntent(resultPendingIntent);
				//				// mId allows you to update the notification later on.
				//				nm.notify(TXT_NOTE_ID, mBuilder.build());

			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the image capture
			} else {
				// Image capture failed, advise user
			}
		}

		if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				Intent a = new Intent(getApplicationContext(),VideoPlaybackActivity.class);
				a.putExtra("videoUri", data.getData());
				a.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				setResult(RESULT_OK, a);
				startActivityForResult(a,0);

				// Video captured and saved to fileUri specified in the Intent
				//	            Toast.makeText(this, "Video saved to:\n" + data.getData(), Toast.LENGTH_LONG).show();
			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the video capture
			} else {
				// Video capture failed, advise user
			}
		}
	}

	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;

	/**
	 * Create a file Uri for saving an image or video.
	 *
	 * @param type the type
	 * @return the output media file uri
	 */
	private static Uri getOutputMediaFileUri(int type){
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/**
	 * Create a File for saving an image or video.
	 *
	 * @param type the type
	 * @return the output media file
	 */
	private static File getOutputMediaFile(int type){
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES), "ThingTag");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (! mediaStorageDir.exists()){
			if (! mediaStorageDir.mkdirs()){
				Log.d("ThingTag", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE){
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");
		} else if(type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_"+ timeStamp + ".mp4");
		} else {
			return null;
		}

		return mediaFile;
	}

	/* (non-Javadoc)
	 * @see android.location.LocationListener#onLocationChanged(android.location.Location)
	 */
	@Override
	public void onLocationChanged(Location arg0) {
		if (!connected)  {
			noConnectionAlert();
			return;
		}
		lat.setText(String.valueOf(arg0.getLatitude()));
		lon.setText(String.valueOf(arg0.getLongitude()));
	}

	/* (non-Javadoc)
	 * @see android.location.LocationListener#onProviderDisabled(java.lang.String)
	 */
	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see android.location.LocationListener#onProviderEnabled(java.lang.String)
	 */
	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see android.location.LocationListener#onStatusChanged(java.lang.String, int, android.os.Bundle)
	 */
	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
	}

}
