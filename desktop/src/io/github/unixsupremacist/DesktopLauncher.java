package io.github.unixsupremacist;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.useVsync(false);
		config.setForegroundFPS(100);
		config.setTitle("FreeCMB");
		config.setWindowedMode(1720, 720);
		new Lwjgl3Application(new CMBMain(), config);
	}
}
