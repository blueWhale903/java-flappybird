package com.hgl.game;

import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.hgl.game.flappybird;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = new Intent(this, AndroidLauncher.class);
		startActivity(intent);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new flappybird(), config);
	}
}
