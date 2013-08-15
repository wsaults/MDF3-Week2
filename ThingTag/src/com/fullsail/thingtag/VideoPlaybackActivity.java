package com.fullsail.thingtag;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoPlaybackActivity extends Activity {
	
	VideoView vw;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_playback);
		
		vw = (VideoView) findViewById(R.id.videoView1);
		
		Bundle data = getIntent().getExtras();
		if (data != null){
			Uri videoUri = (Uri) data.get("videoUri");
			vw.setVideoURI(videoUri);
			vw.setMediaController(new MediaController(this));
			vw.start();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.video_playback, menu);
		return true;
	}

}
