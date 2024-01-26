package io.github.unixsupremacist;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Webapps {
    static String browser = "firefox";
    public static Map<String, Launcher> newWebappCore(String texture){
        Map<String, Launcher> coremenu = new HashMap<>();
        FileHandle icon;
        ExternalLauncher launcher;
        Map<String, String> webApps = new HashMap<>();
        webApps.put("Youtube", "https://youtube.com/tv");
        webApps.put("Pluto TV", "https://pluto.tv/");

        for (String app : webApps.keySet()){
            FileHandle iconPathSteamLogo = Gdx.files.absolute(CMBMain.StorageDirectory+"steamgrids/"+app+"_logo.png");
            FileHandle iconPathSteamHero = Gdx.files.absolute(CMBMain.StorageDirectory+"steamgrids/"+app+"_hero.png");
            if (((!iconPathSteamLogo.exists() || !iconPathSteamHero.exists()) && CMBMain.config.steamgridEnabled)){
                int id = Steamgrid.steamgridSearch(app);

                if(id != 0){
                    if (!iconPathSteamLogo.exists()){
                        var url = Steamgrid.steamgridGet(id, "logos");
                        try {
                            FileUtils.copyURLToFile(new URL(url), new File(CMBMain.StorageDirectory+"steamgrids/"+app+"_logo.png"));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    if (!iconPathSteamHero.exists()){
                        var url = Steamgrid.steamgridGet(id, "heroes");
                        try {
                            FileUtils.copyURLToFile(new URL(url), new File(CMBMain.StorageDirectory+"steamgrids/"+app+"_hero.png"));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }

            if (iconPathSteamLogo.exists()){
                icon = iconPathSteamLogo;
            } else {
                icon = Gdx.files.internal(texture);
            }

            ArrayList<String> commands = new ArrayList<>();
            commands.add(browser);
            commands.add("-kiosk");
            commands.add(webApps.get(app));
            System.out.println(commands);
            if(iconPathSteamHero.exists()){
                launcher = new ExternalLauncher(icon, iconPathSteamHero, commands);
            } else {
                launcher = new ExternalLauncher(icon, commands);
            }
            coremenu.put(app, launcher);
        }
        return coremenu;
    }
}
