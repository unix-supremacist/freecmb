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

public class Flatpak {
    ArrayList<String> defaultCommands = new ArrayList<>();
    static Map<String, String> installedFlatpaks;

    public Flatpak(){
        defaultCommands.add("flatpak");
        defaultCommands.add("--assumeyes");
        defaultCommands.add("--noninteractive");
    }

    public static Map<String, String> getInstalled(){
        if (installedFlatpaks == null){
            ArrayList<String> commands = new ArrayList<>();
            commands.add("flatpak");
            commands.add("list");
            commands.add("--app");
            commands.add("--columns");
            commands.add("application");
            List<String> ids =  runCmdOutput(commands);

            commands = new ArrayList<>();
            commands.add("flatpak");
            commands.add("list");
            commands.add("--app");
            commands.add("--columns");
            commands.add("name");

            List<String> names =  runCmdOutput(commands);
            Map<String, String> output = new HashMap<>();
            for (int i = 0; i < ids.size(); i++) output.put(ids.get(i), names.get(i));
            installedFlatpaks = output;
        }
        return installedFlatpaks;
    }

    public static Map<String, Launcher> newFlatpakCore(String texture){
        getInstalled();
        Map<String, Launcher> coremenu = new HashMap<>();
        FileHandle icon;
        ExternalLauncher launcher;
        for (String flatpak : installedFlatpaks.keySet()){
            FileHandle iconPathSteamLogo = Gdx.files.absolute(CMBMain.StorageDirectory+"steamgrids/"+flatpak+"_logo.png");
            FileHandle iconPathSteamHero = Gdx.files.absolute(CMBMain.StorageDirectory+"steamgrids/"+flatpak+"_hero.png");
            FileHandle iconPath = Gdx.files.absolute(CMBMain.StorageDirectory+"flatpak/"+flatpak+".png");
            if (((!iconPathSteamLogo.exists() || !iconPathSteamHero.exists()) && CMBMain.config.steamgridEnabled)){
                int id = Steamgrid.steamgridSearch(installedFlatpaks.get(flatpak));

                if(id != 0){
                    if (!iconPathSteamLogo.exists()){
                        var url = Steamgrid.steamgridGet(id, "logos");
                        try {
                            FileUtils.copyURLToFile(new URL(url), new File(CMBMain.StorageDirectory+"steamgrids/"+flatpak+"_logo.png"));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    if (!iconPathSteamHero.exists()){
                        var url = Steamgrid.steamgridGet(id, "heroes");
                        try {
                            FileUtils.copyURLToFile(new URL(url), new File(CMBMain.StorageDirectory+"steamgrids/"+flatpak+"_hero.png"));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }

            if (iconPathSteamLogo.exists()){
                icon = iconPathSteamLogo;
            } else if (iconPath.exists()){
                icon = iconPath;
            } else {
                icon = Gdx.files.internal(texture);
            }

            ArrayList<String> commands = new ArrayList<>();
            commands.add("flatpak");
            commands.add("run");
            commands.add(flatpak);
            System.out.println(commands);
            if(iconPathSteamHero.exists()){
                launcher = new ExternalLauncher(icon, iconPathSteamHero, commands);
            } else {
                launcher = new ExternalLauncher(icon, commands);
            }
            coremenu.put(installedFlatpaks.get(flatpak), launcher);
        }
        return coremenu;
    }

    public void update(){
        ArrayList<String> commands = new ArrayList<>();
        commands.addAll(defaultCommands);
        commands.add("update");

        runCmd(commands);
    }

    public void installFlatpak(String flatpak){
        ArrayList<String> commands = new ArrayList<>();
        commands.addAll(defaultCommands);
        commands.add("--or-update");
        commands.add("install");
        commands.add(flatpak);

        runCmd(commands);
    }

    public void uninstallFlatpak(String flatpak){
        ArrayList<String> commands = new ArrayList<>();
        commands.addAll(defaultCommands);
        commands.add("--delete-data");
        commands.add("uninstall");
        commands.add(flatpak);

        runCmd(commands);
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