package io.github.unixsupremacist;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import lombok.Getter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import static com.badlogic.gdx.math.MathUtils.random;

public class CMBMain extends ApplicationAdapter {
	SpriteBatch batch;
	private OrthographicCamera camera;
	static int x;
	static int y;
	@Getter
	static ArrayList<String> options = new ArrayList<>();
	static HashMap<String, HashMap<String, Launcher>> menu = new HashMap<>();
	static ArrayList<String> submenus = new ArrayList<>();
	ArrayList<String> debugs = new ArrayList<>();
	InputHandler input = new InputHandler();
	BitmapFont font;
	Background background;
	Texture defaultIcon;
	static Music music;
	String trackName;

	public void playBGM(){
		if (music != null){
			music.dispose();
		}
		FileHandle fh = Gdx.files.absolute("/run/media/unix/NVME/Audio/Music/Anime Music/");
		if (fh.exists()){
			FileHandle track = fh.list()[random.nextInt(fh.list().length)];
			trackName = track.nameWithoutExtension();
			music = Gdx.audio.newMusic(track);

			music.setVolume(0.01f);
			music.play();
		}
	}

	public void populateMenus(){
		HashMap<String, Launcher> launchers = new HashMap<>();
		launchers.put("retroarch", new ExternalLauncher(new Texture("retroarch.png"), "org.libretro.RetroArch"));
		launchers.put("lutris", new ExternalLauncher(new Texture("lutris.png"), "lutris"));
		launchers.put("jellyfin", new ExternalLauncher(new Texture("jellyfin.png"), "jellyfinmediaplayer"));
		menu.put("Launchers", launchers);
		HashMap<String, Launcher> launchers2 = new HashMap<>();
		launchers2.put("retroarch", new ExternalLauncher(new Texture("retroarch.png"), "org.libretro.RetroArch"));
		launchers2.put("jellyfin", new ExternalLauncher(new Texture("jellyfin.png"), "jellyfinmediaplayer"));
		menu.put("Launchers2", launchers2);
		newRetrocore("GCN", "dolphin", "retroarch.png", ".nkit", "/run/media/unix/NVME/Games/Roms/Neoset/gcn/");
		newRetrocore("Wii", "dolphin", "retroarch.png", ".nkit", "/run/media/unix/NVME/Games/Roms/Neoset/wii/phy/");
		newRetrocore("SDC", "flycast", "retroarch.png", "", "/run/media/unix/NVME/Games/Roms/Neoset/sdc/");
		HashMap<String, Launcher> sys = new HashMap<>();
		sys.put("Exit", new Exiter(new Texture("retroarch.png")));
		menu.put("System", sys);
		for (String submenu : menu.keySet())
			submenus.add(submenu);
	}

	public void newRetrocore(String name, String core, String texture, String extension, String dir){
		HashMap<String, Launcher> coremenu = new HashMap<>();
		Texture icon;
		ExternalLauncher launcher;

		FileHandle handle = Gdx.files.absolute(dir);
		if (handle.isDirectory()){
			for (FileHandle rom : handle.list()){
				FileHandle iconPath = Gdx.files.internal("tempiconcover/"+rom.nameWithoutExtension().replace(extension, "")+".png");
				if (iconPath.exists()){
					icon = new Texture(iconPath);
				} else {
					icon = new Texture(texture);
				}

				ArrayList<String> commands = new ArrayList<>();
				commands.add("org.libretro.RetroArch");
				commands.add("-L");
				commands.add("/home/unix/.var/app/org.libretro.RetroArch/config/retroarch/cores/"+core+"_libretro.so");
				commands.add(rom.path());
				FileHandle bgPath = Gdx.files.internal("tempbackgroundcover/"+rom.nameWithoutExtension().replace(extension, "")+".jpg");
				if(bgPath.exists()){
					launcher = new ExternalLauncher(icon, new Background(new Texture(bgPath)), commands);
				} else {
					launcher = new ExternalLauncher(icon, commands);
				}
				coremenu.put(rom.nameWithoutExtension().replace(extension, ""), launcher);
			}
		}
		menu.put(name, coremenu);
	}

	@Override
	public void create () {
		Gdx.input.setInputProcessor(input);
		Controllers.addListener(input);
		camera = new OrthographicCamera();
		background = new Background(new Texture("bg.jpg"));
		defaultIcon = new Texture("retroarch.png");
		populateMenus();


		batch = new SpriteBatch();
		x = 0;
		y = 0;
		font = new BitmapFont();
	}

	private void setupCamera(){
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.update();
		ScreenUtils.clear(0, 0, 0, 1);
		batch.setProjectionMatrix(camera.combined);
		debugs.add(String.valueOf(Gdx.graphics.getFramesPerSecond()));
		if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) music.stop();
		debugs.add(trackName);
	}

	@Override
	public void render () {
		if (music == null){
			playBGM();
		} else if (!music.isPlaying()){
			playBGM();
		}

		setupCamera();
		batch.begin();
		batch.setColor(0.6f, 0.6f, 0.6f, 1f);
		if (x >= menu.size())
			x = 0;
		else if (x < 0)
			x = menu.size()-1;
		if (y >= menu.get(submenus.get(x)).size())
			y = 0;
		else if (y < 0)
			y = menu.get(submenus.get(x)).size()-1;

		options.clear();
		for (String option : menu.get(submenus.get(x)).keySet()){
			options.add(option);
		}
		Background iconbg = menu.get(submenus.get(x)).get(getOptions().get(y)).bg;
		if (iconbg != null){
			batch.draw(iconbg.getTexture(), iconbg.getX(), iconbg.getY(), iconbg.getWidth(), iconbg.getHeight());
		} else {
			batch.draw(background.getTexture(), background.getX(), background.getY(), background.getWidth(), background.getHeight());
		}
		batch.setColor(1, 1, 1, 1);


		var i = 0;
		var selsize = 96;
		var unselsize = 64;
		var gaps = selsize+(selsize/4);
		var smallgap = selsize+(selsize/16);
		var start = Gdx.graphics.getWidth() / 4 - gaps * 2;

		for (String menu : submenus){
			if (i == x) {
				batch.draw(defaultIcon, start + gaps * i - gaps * x, ((Gdx.graphics.getHeight() + gaps) / 2) + gaps, selsize, selsize);
			} else {
				batch.draw(defaultIcon, start + 16 + gaps * i - gaps * x, ((Gdx.graphics.getHeight() + gaps) / 2) + gaps + 16, unselsize, unselsize);
			}
			i++;
		}

		i = 0;

		for (Launcher launcher : menu.get(submenus.get(x)).values()){
			if (i != y){
				batch.setColor(1, 1, 1, 0.3f);
				font.setColor(1, 1, 1, 0.3f);
			}
			if (i == y){
				batch.draw(launcher.getTexture(), start, ((Gdx.graphics.getHeight() + smallgap + selsize - launcher.getHeight(selsize)) / 2) - smallgap*i + smallgap*y, launcher.getWidth(selsize), launcher.getHeight(selsize));
				font.draw(batch, options.get(i), start + gaps, ((Gdx.graphics.getHeight() + smallgap) / 2) - smallgap*i + smallgap*y + smallgap/2);
			} else if (i > y){
				batch.draw(launcher.getTexture(), start + 16, ((Gdx.graphics.getHeight() + smallgap + unselsize - launcher.getHeight(unselsize)) / 2) + 16 - smallgap*i + smallgap*y, launcher.getWidth(unselsize), launcher.getHeight(unselsize));
				font.draw(batch, options.get(i), start + gaps, ((Gdx.graphics.getHeight() + smallgap) / 2) - smallgap*i + smallgap*y + smallgap/2);
			} else {
				batch.draw(launcher.getTexture(), start + 16, ((Gdx.graphics.getHeight() + smallgap + unselsize - launcher.getHeight(unselsize)) / 2) + 16  - smallgap*i + smallgap*y + gaps, launcher.getWidth(unselsize), launcher.getHeight(unselsize));
				font.draw(batch, options.get(i), start + gaps, ((Gdx.graphics.getHeight() + smallgap) / 2) - smallgap*i + smallgap*y + gaps + smallgap/2);
			}

			batch.setColor(1, 1, 1, 1);
			font.setColor(1, 1, 1, 1);

			i++;
		}

		i = 0;
		for (String debug : debugs){
			i++;
			font.draw(batch, debug, 25, Gdx.graphics.getHeight() - (20*i));
		}
		debugs.clear();
		batch.end();
	}

	public static void runExternalApp(ArrayList<String> commands){
		try {
			music.pause();
			ProcessBuilder builder = new ProcessBuilder(commands);
			Process process = builder.start();
			process.waitFor();
			music.play();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		//img.dispose();
	}
}
