package io.github.unixsupremacist;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import static com.badlogic.gdx.math.MathUtils.random;

public class CMBMain extends ApplicationAdapter {
	SpriteBatch batch;
	private OrthographicCamera camera;
	static int x, y;
	@Getter
	static ArrayList<String> options = new ArrayList<>();
	public static Map<String, Map<String, Launcher>> menu = new HashMap<>();
	static ArrayList<String> submenus = new ArrayList<>();
	public static ArrayList<String> debugs = new ArrayList<>();
	InputHandler input = new InputHandler();
	BitmapFont font;
	Background background;
	Texture defaultIcon;
	static Music music;
	String trackName;
	public static String StorageDirectory = "/home/unix/cmbtest/";
	Map<String, String> installedFlatpaks;
	@Getter
	static Robot robot;
	@Getter
	static boolean appRunning;
	@Getter @Setter static String mapping = "renpy";
	FileHandle pathbg;
	Background iconbg;
	List<Texture> icons;
	int lastX;


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

	public void convertPNGS(String name){
        try {
			FileHandle out = Gdx.files.absolute(CMBMain.StorageDirectory+"flatpak"+name+".png");
			FileHandle path = Gdx.files.absolute("/var/lib/flatpak/exports/share/icons/hicolor/scalable/apps/"+name+".svg");
			if(!out.exists()){
				if(path.exists()){
					String svg_URI_input = Paths.get(path.path()).toUri().toURL().toString();
					TranscoderInput input_svg_image = new TranscoderInput(svg_URI_input);
					OutputStream png_ostream = new FileOutputStream(out.path());
					TranscoderOutput output_png_image = new TranscoderOutput(png_ostream);
					PNGTranscoder my_converter = new PNGTranscoder();
					my_converter.transcode(input_svg_image, output_png_image);
					png_ostream.flush();
					png_ostream.close();
				} else {
					path = Gdx.files.absolute("/var/lib/flatpak/exports/share/icons/hicolor/512x512/apps/"+name+".png");
					if (path.exists()){
						path.copyTo(out);
					} else {
						path = Gdx.files.absolute("/var/lib/flatpak/exports/share/icons/hicolor/256x256/apps/"+name+".png");
						if (path.exists()){
							path.copyTo(out);
						}
					}
				}
			}
        } catch (IOException | TranscoderException e) {
            throw new RuntimeException(e);
        }
    }

	public void populateMenus(){
		Retroarch.populateCores();
		menu.put("Flatpak", Flatpak.newFlatpakCore("retroarch.png"));

		Map<String, Launcher> sys = new HashMap<>();
		sys.put("Exit", new Exiter(Gdx.files.internal("retroarch.png")));
		menu.put("System", sys);
		for (String submenu : menu.keySet())
			submenus.add(submenu);
	}

	@Override
	public void create () {
		Gdx.input.setInputProcessor(input);
		Controllers.addListener(input);
		camera = new OrthographicCamera();
		background = new Background(new Texture("bg.jpg"));
		defaultIcon = new Texture("retroarch.png");
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
		installedFlatpaks = Flatpak.getInstalled();
		for (String id : installedFlatpaks.keySet()) convertPNGS(id);
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

		Controller controller = Controllers.getCurrent();

		if (music == null){
			playBGM();
		} else if (!music.isPlaying() && !appRunning){
			playBGM();
		}

		if (controller != null){
			Point location = MouseInfo.getPointerInfo().getLocation();
			var axisLeftX = controller.getAxis(controller.getMapping().axisLeftX);
			var axisLeftY = controller.getAxis(controller.getMapping().axisLeftY);


			if (axisLeftX < -0.1f || axisLeftX > 0.1f || axisLeftY < -0.1f || axisLeftY > 0.1f)
				robot.mouseMove((int) (location.x+(500 * axisLeftX * Gdx.graphics.getDeltaTime())), (int) (location.y+(500 * axisLeftY * Gdx.graphics.getDeltaTime())));
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


		if (menu.get(submenus.get(x)).get(getOptions().get(y)).bg != null){
			if (pathbg != menu.get(submenus.get(x)).get(getOptions().get(y)).bg){
				pathbg = menu.get(submenus.get(x)).get(getOptions().get(y)).bg;
				if(iconbg != null)
					iconbg.dispose();
				iconbg = new Background(new Texture(pathbg));
			}
		} else {
			pathbg = null;
			if(iconbg != null)
				iconbg.dispose();
			iconbg = null;
		}

		if (iconbg != null){
			batch.draw(iconbg.getTexture(), iconbg.getX(), iconbg.getY(), iconbg.getWidth(), iconbg.getHeight());
		} else {
			batch.draw(background.getTexture(), background.getX(), background.getY(), background.getWidth(), background.getHeight());
		}

		batch.setColor(1, 1, 1, 1);


		var selsize = 96;
		var unselsize = 64;
		var gaps = selsize+(selsize/4);
		var smallgap = selsize+(selsize/16);
		var start = Gdx.graphics.getWidth() / 4 - gaps * 2;

		for (int i = 0; i < submenus.size(); i++){
			if (i == x) {
				batch.draw(defaultIcon, start + gaps * i - gaps * x, ((Gdx.graphics.getHeight() + gaps) / 2) + gaps, selsize, selsize);
			} else {
				if (i != x){
					double a;
					if(i > x){
						a = 0.9 / 1 / Math.pow(1.1, i-x);
					} else {
						a = 0.9 / 1 / Math.pow(1.6, x-i);
					}
					batch.setColor(1, 1, 1, (float) a);
				}
				batch.draw(defaultIcon, start + 16 + gaps * i - gaps * x, ((Gdx.graphics.getHeight() + gaps) / 2) + gaps + 16, unselsize, unselsize);
				batch.setColor(1, 1, 1, 1);
			}
		}

		if (lastX != x){
			lastX = x;
			for (Texture icon : icons)
				if(icon != null)
					icon.dispose();
			icons.clear();
			icons = null;
		}

		if (icons == null){
			icons = new ArrayList<>();

			//for (Launcher launcher : menu.get(submenus.get(x)).values()){
			for (int i = 0; i < menu.get(submenus.get(x)).size(); i++){
				if(i < y-4 || i > y+7){
					icons.add(null);
				}else{
					icons.add(new Texture(menu.get(submenus.get(x)).get(options.get(i)).getTexture()));
				}
			}
		}


		for (int i = 0; i < menu.get(submenus.get(x)).size(); i++){
			Launcher launcher = menu.get(submenus.get(x)).get(options.get(i));
			if (icons.get(i) == null) {
				if (i > y-4 && i < y+7) icons.set(i, new Texture(launcher.getTexture()));
				else continue;
			}
			if (i != y){
				double a;
				if(i > y){
					a = 0.9 / 1 / Math.pow(1.3, i-y);
				} else {
					a = 0.9 / 1 / Math.pow(1.6, y-i);
				}
				batch.setColor(1, 1, 1, (float) a);
				font.setColor(1, 1, 1, (float) a);
			}
			if (i == y){
				batch.draw(icons.get(i), start, ((Gdx.graphics.getHeight() + smallgap + selsize - launcher.getHeight(selsize, icons.get(i).getWidth(), icons.get(i).getHeight())) / 2) - smallgap*i + smallgap*y, launcher.getWidth(selsize, icons.get(i).getWidth(), icons.get(i).getHeight()), launcher.getHeight(selsize, icons.get(i).getWidth(), icons.get(i).getHeight()));
				font.draw(batch, options.get(i), start + gaps, ((Gdx.graphics.getHeight() + smallgap) / 2) - smallgap*i + smallgap*y + smallgap/2);
			} else if (i > y){
				batch.draw(icons.get(i), start + 16, ((Gdx.graphics.getHeight() + smallgap + unselsize - launcher.getHeight(unselsize, icons.get(i).getWidth(), icons.get(i).getHeight())) / 2) + 16 - smallgap*i + smallgap*y, launcher.getWidth(unselsize, icons.get(i).getWidth(), icons.get(i).getHeight()), launcher.getHeight(unselsize, icons.get(i).getWidth(), icons.get(i).getHeight()));
				font.draw(batch, options.get(i), start + gaps, ((Gdx.graphics.getHeight() + smallgap) / 2) - smallgap*i + smallgap*y + smallgap/2);
			} else {
				batch.draw(icons.get(i), start + 16, ((Gdx.graphics.getHeight() + smallgap + unselsize - launcher.getHeight(unselsize, icons.get(i).getWidth(), icons.get(i).getHeight())) / 2) + 16  - smallgap*i + smallgap*y + gaps, launcher.getWidth(unselsize, icons.get(i).getWidth(), icons.get(i).getHeight()), launcher.getHeight(unselsize, icons.get(i).getWidth(), icons.get(i).getHeight()));
				font.draw(batch, options.get(i), start + gaps, ((Gdx.graphics.getHeight() + smallgap) / 2) - smallgap*i + smallgap*y + gaps + smallgap/2);
			}

			batch.setColor(1, 1, 1, 1);
			font.setColor(1, 1, 1, 1);
		}

		for (int i = 0; i < debugs.size(); i++)
			font.draw(batch, debugs.get(i), 25, Gdx.graphics.getHeight() - (20*i));
		debugs.clear();
		batch.end();
	}

	public static void runExternalApp(ArrayList<String> commands){
		if (!appRunning){
			Thread thread = new Thread(() -> {
				try {
					music.pause();
					ProcessBuilder builder = new ProcessBuilder(commands);
					Process process = builder.start();
					appRunning = true;
					process.waitFor();
					appRunning = false;
					music.play();
				} catch (IOException e) {
					throw new RuntimeException(e);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			});

			thread.start();
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		//img.dispose();
	}
}
