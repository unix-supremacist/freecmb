package io.github.unixsupremacist;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ScreenUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.badlogic.gdx.math.MathUtils.random;

public class CMBMain extends ApplicationAdapter {
	SpriteBatch batch;
	private OrthographicCamera camera;
	static int x = 0;
	static int y = 0;
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
	public static String StorageDirectory;
	public static String flatpakDirectory = "/var/lib/flatpak/exports/";
	Map<String, String> installedFlatpaks;
	@Getter
	static Robot robot;
	@Getter
	static boolean appRunning;
	@Getter @Setter static String mapping = "renpy";
	Background iconbg;
	List<Texture> icons;
	int lastX = 1;
	int lastY = 1;
	public static Config config;
	static Gson gson;
	private ShaderProgram defaultShader, blurShader;
	private FrameBuffer blurTargetX, blurTargetY;


	public void playBGM(){
		if (music != null)
			music.dispose();
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
			FileHandle out = Gdx.files.absolute(StorageDirectory+"flatpak/"+name+".png");
			FileHandle path = Gdx.files.absolute(flatpakDirectory+"share/icons/hicolor/scalable/apps/"+name+".svg");
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
					path = Gdx.files.absolute(flatpakDirectory+"share/icons/hicolor/512x512/apps/"+name+".png");
					if (path.exists()){
						path.copyTo(out);
					} else {
						path = Gdx.files.absolute(flatpakDirectory+"share/icons/hicolor/256x256/apps/"+name+".png");
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
		menu.put("SysApps", SystemApps.newSysappCore("retroarch.png"));
		menu.put("WebApps", Webapps.newWebappCore("retroarch.png"));

		Map<String, Launcher> sys = new HashMap<>();
		sys.put("Exit", new Exiter(Gdx.files.internal("retroarch.png")));
		menu.put("System", sys);
		for (String submenu : menu.keySet())
			submenus.add(submenu);
	}

	public static void writeConfig(Config config){
		FileHandle configPath = Gdx.files.absolute(StorageDirectory+"config.json");
		String json = gson.toJson(config);
		try {
			FileUtils.writeStringToFile(new File(configPath.path()), json);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Config handleConfig(){
		gson = new GsonBuilder().setPrettyPrinting().create();
		Config config;
		FileHandle configPath = Gdx.files.absolute(StorageDirectory+"config.json");
		if (configPath.exists()){
            try {
                String file = FileUtils.readFileToString(new File(configPath.path()));
				config = gson.fromJson(file, Config.class);
				writeConfig(config);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
		} else {
			config = new Config();
			writeConfig(config);
		}
		return config;
	}

	private ShaderProgram buildShader(String vertexPath, String fragmentPath) {
		String vert = Gdx.files.internal(vertexPath).readString();
		String frag = Gdx.files.internal(fragmentPath).readString();
		ShaderProgram program = new ShaderProgram(vert, frag);
		if (!program.isCompiled()) {
			throw new GdxRuntimeException(program.getLog());
		}
		return program;
	}

	@Override
	public void create () {
		StorageDirectory = Gdx.files.getExternalStoragePath()+"cmbtest/";
		config = handleConfig();
		Gdx.input.setInputProcessor(input);
		Controllers.addListener(input);
		camera = new OrthographicCamera();
		background = new Background(new Texture(Gdx.files.absolute(StorageDirectory+"bg.jpg")));
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
		defaultShader = batch.getShader();
		blurShader = buildShader("shaders/blur.vert", "shaders/blur.frag");
		font = new BitmapFont();
    }

	private void setupCamera(){
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.update();
		ScreenUtils.clear(0, 0, 0, 1);
		batch.setProjectionMatrix(camera.combined);
		debugs.add(String.valueOf(Gdx.graphics.getFramesPerSecond()));
		if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) music.stop();
		if (trackName != null) debugs.add(trackName);
	}

	public void controllerToMouse(){
		Controller controller = Controllers.getCurrent();
		if (controller != null){
			Point location = MouseInfo.getPointerInfo().getLocation();
			var axisLeftX = controller.getAxis(controller.getMapping().axisLeftX);
			var axisLeftY = controller.getAxis(controller.getMapping().axisLeftY);

			if (axisLeftX < -0.1f || axisLeftX > 0.1f || axisLeftY < -0.1f || axisLeftY > 0.1f)
				robot.mouseMove((int) (location.x+(500 * axisLeftX * Gdx.graphics.getDeltaTime())), (int) (location.y+(500 * axisLeftY * Gdx.graphics.getDeltaTime())));
		}
	}

	public void wrapMenu(){
		if (x >= menu.size())
			x = 0;
		else if (x < 0)
			x = menu.size()-1;
		if (y >= menu.get(submenus.get(x)).size())
			y = 0;
		else if (y < 0)
			y = menu.get(submenus.get(x)).size()-1;
	}

	public void fixBg(){
		if (blurTargetX != null) blurTargetX.dispose();
		if (blurTargetY != null) blurTargetY.dispose();
		if(iconbg != null) iconbg.dispose();
		blurTargetX = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		blurTargetY = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		if (menu.get(submenus.get(x)).get(getOptions().get(y)).bg != null){
			Texture texture = new Texture(menu.get(submenus.get(x)).get(getOptions().get(y)).bg);
			iconbg = drawBg(new Background(texture));
			texture.dispose();
		} else
			iconbg = drawBg(background);
	}

	public void handleChanges(){
		if (lastX != x){
			options.clear();
			for (String option : menu.get(submenus.get(x)).keySet())
				options.add(option);
			fixBg();
			if (icons != null){
				for (Texture icon : icons)
					if(icon != null)
						icon.dispose();
				icons.clear();
				icons = null;
			}
			lastX = x;
		}

		if (lastY != y){
			fixBg();
			lastY = y;
		}

		if (icons == null){
			icons = new ArrayList<>();
			for (int i = 0; i < menu.get(submenus.get(x)).size(); i++)
				if(i > y-4 && i < y+7){
					Texture texture = new Texture(menu.get(submenus.get(x)).get(options.get(i)).getTexture());
					texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Nearest);
					icons.add(texture);
				} else icons.add(null);
		}
	}

	public Background drawBg(Background bg){
		for(int i = 0; i < 12; i++){
			batch.setShader(blurShader);

			blurTargetX.begin();
			batch.begin();
			blurShader.setUniformf("dir", 0.7f, 0);
			blurShader.setUniformf("radius", 1);
			blurShader.setUniformf("resolution", Gdx.graphics.getWidth());

			if (i == 0){
				batch.setColor(0.6f, 0.6f, 0.6f, 1f);
				batch.draw(bg.getTexture(), bg.getX(), bg.getY(), bg.getWidth(), bg.getHeight());
				batch.setColor(1, 1, 1, 1);
			} else {
				batch.draw(blurTargetY.getColorBufferTexture(), 0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, 1, 1);
			}

			batch.end();
			blurTargetX.end();

			blurTargetY.begin();
			batch.begin();
			blurShader.setUniformf("dir", 0, 0.7f);
			blurShader.setUniformf("radius", 1);
			blurShader.setUniformf("resolution", Gdx.graphics.getHeight());
			batch.draw(blurTargetX.getColorBufferTexture(), 0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, 1, 1);
			batch.end();
			blurTargetY.end();

			batch.setShader(defaultShader);
		}
		return new Background(blurTargetY.getColorBufferTexture());
	}

	public void draw(){
		var selsize = 128;
		var gaps = selsize+(selsize/4);
		var smallgap = selsize+(selsize/16);
		var start = Gdx.graphics.getWidth() / 4 - gaps * 2;

		for (int i = 0; i < submenus.size(); i++){
			if (i == x) {
				batch.draw(defaultIcon, start + gaps * i - gaps * x, ((Gdx.graphics.getHeight() + gaps) / 2) + gaps, selsize, selsize);
			} else {
				float unselsize;
				double pow;
				if(i > x)
					pow = Math.pow(1.1, i-x);
				else
					pow = Math.pow(1.2, x-i);
				batch.setColor(1, 1, 1, (float) (0.9 / pow));
				unselsize = (float) (selsize / pow);
				float sizeDiff = (selsize-unselsize)/2;
				batch.draw(defaultIcon, start + (float) sizeDiff + gaps * i - gaps * x, ((Gdx.graphics.getHeight() + gaps) / 2) + gaps + sizeDiff, unselsize, unselsize);
				batch.setColor(1, 1, 1, 1);
			}
		}

		for (int i = 0; i < menu.get(submenus.get(x)).size(); i++){
			if (icons.get(i) == null) {
				if (i > y-4 && i < y+7){
					Texture texture = new Texture(menu.get(submenus.get(x)).get(options.get(i)).getTexture());
					texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Nearest);
					icons.set(i, texture);
				}
			} else {
				Launcher launcher = menu.get(submenus.get(x)).get(options.get(i));
				int screenHeight = Gdx.graphics.getHeight();
				int height = icons.get(i).getHeight();
				int width = icons.get(i).getWidth();
				float unselsize, sizeDiff, unselHeight, unselWidth;
				float selHeight = launcher.getHeight(selsize, width, height);
				float selWidth = launcher.getWidth(selsize, width, height);

				if (i == y){
					batch.draw(icons.get(i), start, ((screenHeight + smallgap + selsize - selHeight) / 2) - smallgap*i + smallgap*y, selWidth, selHeight);
					font.draw(batch, options.get(i), start + gaps, ((screenHeight + smallgap) / 2) - smallgap*i + smallgap*y + smallgap/2);
				} else {
					double pow, alpha;
					if(i > y)
						pow = Math.pow(1.3, i-y);
					else
						pow = Math.pow(1.7, y-i);
					alpha = 0.9 / pow;
					unselsize = (float) (selsize / pow);
					sizeDiff = (selsize-unselsize)/2;
					unselHeight = launcher.getHeight((int) unselsize, width, height);
					unselWidth = launcher.getWidth((int) unselsize, width, height);
					batch.setColor(1, 1, 1, (float) alpha);
					font.setColor(1, 1, 1, (float) alpha);
					if (i > y){
						batch.draw(icons.get(i), start + sizeDiff, ((Gdx.graphics.getHeight() + smallgap + unselsize - unselHeight) / 2) + sizeDiff - smallgap*i + smallgap*y, unselWidth, unselHeight);
						font.draw(batch, options.get(i), start + gaps, ((screenHeight + smallgap) / 2) - smallgap*i + smallgap*y + smallgap/2);
					} else {
						batch.draw(icons.get(i), start + sizeDiff, ((Gdx.graphics.getHeight() + smallgap + unselsize - unselHeight) / 2) + sizeDiff - smallgap*i + smallgap*y + gaps, unselWidth, unselHeight);
						font.draw(batch, options.get(i), start + gaps, ((screenHeight + smallgap) / 2) - smallgap*i + smallgap*y + gaps + smallgap/2);
					}
					batch.setColor(1, 1, 1, 1);
					font.setColor(1, 1, 1, 1);
				}
			}
		}

		for (int i = 0; i < debugs.size(); i++)
			font.draw(batch, debugs.get(i), 25, Gdx.graphics.getHeight() - (20*i));
		debugs.clear();
	}


	@Override
	public void render () {
		if (!appRunning)controllerToMouse();

		if (music == null) playBGM();
		else if (!music.isPlaying() && !appRunning) playBGM();

		setupCamera();



		wrapMenu();
		handleChanges();

		batch.begin();

		batch.draw(iconbg.getTexture(), 0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, 1, 1);

		draw();
		batch.end();
	}

	public static void runExternalApp(ArrayList<String> commands){
		if (!appRunning){
			Thread thread = new Thread(() -> {
				try {
					if (music != null) music.pause();
					ProcessBuilder builder = new ProcessBuilder(commands);
					Process process = builder.start();
					appRunning = true;
					process.waitFor();
					appRunning = false;
					if (music != null) music.play();
				} catch (IOException | InterruptedException e) {
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
