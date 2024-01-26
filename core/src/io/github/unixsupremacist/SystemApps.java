package io.github.unixsupremacist;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemApps {


    public static Map<String, Launcher> newSysappCore(String texture){
        Map<String, Launcher> coremenu = new HashMap<>();
        FileHandle icon;
        ExternalLauncher launcher;
        Map<String, List<String>> sysApps = new HashMap<>();
        List<String> nullList = new ArrayList<>();
        sysApps.put("firefox", nullList);
        sysApps.put("lutris", nullList);
        List<String> steamList = new ArrayList<>();
        steamList.add("-steampal");
        steamList.add("-gamepadui");
        sysApps.put("steam", steamList);

        for (String app : sysApps.keySet()){
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
            commands.add(app);
            commands.addAll(sysApps.get(app));
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

    public void update(){
        //detect operating system and run system update
    }


    public void runCmd(ArrayList<String> commands){
        Thread thread = new Thread(() -> {
            try {
                ProcessBuilder builder = new ProcessBuilder(commands);
                Process process = builder.start();
                process.waitFor();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        thread.start();
    }

    public static List<String> runCmdOutput(ArrayList<String> commands){
        try {
            ProcessBuilder builder = new ProcessBuilder(commands);
            Process process = builder.start();
            process.waitFor();
            return IOUtils.readLines(process.getInputStream());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
