package io.github.unixsupremacist;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Retroarch {
    public static final String flatpakID = "org.libretro.RetroArch";
    public static final List<String> blacklist = new ArrayList<>();
    public static boolean isInstalled(){
        return Flatpak.getInstalled().containsKey(flatpakID);
    }

    public static void populateCores(){
        if(isInstalled()){
            addCore("NES", CMBMain.config.retroarchNESCore, "", CMBMain.config.retroarchNESPath);
            addCore("SNES", CMBMain.config.retroarchSNESCore, "", CMBMain.config.retroarchSNESPath);
            addCore("N64", CMBMain.config.retroarchN64Core, "", CMBMain.config.retroarchN64Path);
            addCore("GCN", CMBMain.config.retroarchGCNCore, ".nkit", CMBMain.config.retroarchGCNPath);
            addCore("Wii", CMBMain.config.retroarchWIICore, ".nkit", CMBMain.config.retroarchWIIPath);
            addCore("GB", CMBMain.config.retroarchGBCore, "", CMBMain.config.retroarchGBPath);
            addCore("GBC", CMBMain.config.retroarchGBCCore, "", CMBMain.config.retroarchGBCPath);
            addCore("GBA", CMBMain.config.retroarchGBACore, "", CMBMain.config.retroarchGBAPath);
            addCore("DS", CMBMain.config.retroarchDSCore, "", CMBMain.config.retroarchDSPath);
            addCore("SMD", CMBMain.config.retroarchSMDCore, "", CMBMain.config.retroarchSMDPath);
            addCore("SDC", CMBMain.config.retroarchSDCCore, "", CMBMain.config.retroarchSDCPath);
            addCore("PS1", CMBMain.config.retroarchPS1Core, "", CMBMain.config.retroarchPS1Path);
            addCore("PS2", CMBMain.config.retroarchPS2Core, "", CMBMain.config.retroarchPS2Path);
            addCore("PSP", CMBMain.config.retroarchPSPCore, "", CMBMain.config.retroarchPSPPath);
        }
    }

    public static void addCore(String name, String core, String extension, String path){
        var rcore = newRetrocore(core, "retroarch.png", extension, path);
        if (rcore != null) CMBMain.menu.put(name, rcore);
    }


    public static Map<String, Launcher> newRetrocore(String core, String texture, String extension, String dir){
        Map<String, Launcher> coremenu = new HashMap<>();
        FileHandle icon;
        ExternalLauncher launcher;

        FileHandle handle = Gdx.files.absolute(dir);
        if (handle.isDirectory() && handle.list().length != 0){
            for (FileHandle rom : handle.list()) {
                if (rom.extension().contains("m3u"))
                    try {
                        List<String> files = IOUtils.readLines(new BufferedReader(new FileReader(rom.path())));
                        blacklist.addAll(files);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
            }

            for (FileHandle rom : handle.list()){
                if(!blacklist.contains(rom.name()) && !rom.isDirectory()){
                    FileHandle iconPathSteamLogo = Gdx.files.absolute(CMBMain.StorageDirectory+"steamgrids/"+rom.nameWithoutExtension().replace(extension, "")+"_logo.png");
                    FileHandle iconPathSteamHero = Gdx.files.absolute(CMBMain.StorageDirectory+"steamgrids/"+rom.nameWithoutExtension().replace(extension, "")+"_hero.png");
                    if (((!iconPathSteamLogo.exists() || !iconPathSteamHero.exists()) && CMBMain.config.steamgridEnabled)){
                        int id = Steamgrid.steamgridSearch(rom.nameWithoutExtension().replace(extension, ""));

                        if(id != 0){
                            if (!iconPathSteamLogo.exists()){
                                var url = Steamgrid.steamgridGet(id, "logos");
                                if (!url.isEmpty()){
                                    try {
                                        FileUtils.copyURLToFile(new URL(url), new File(CMBMain.StorageDirectory+"steamgrids/"+rom.nameWithoutExtension().replace(extension, "")+"_logo.png"));
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }

                            if (!iconPathSteamHero.exists()){
                                var url = Steamgrid.steamgridGet(id, "heroes");
                                if (!url.isEmpty()){
                                    try {
                                        FileUtils.copyURLToFile(new URL(url), new File(CMBMain.StorageDirectory+"steamgrids/"+rom.nameWithoutExtension().replace(extension, "")+"_hero.png"));
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                        }
                    }

                    if (iconPathSteamLogo.exists())
                        icon = iconPathSteamLogo;
                    else
                        icon = Gdx.files.internal(texture);

                    ArrayList<String> commands = new ArrayList<>();
                    commands.add("org.libretro.RetroArch");
                    commands.add("-L");
                    commands.add("/home/unix/.var/app/org.libretro.RetroArch/config/retroarch/cores/"+core+"_libretro.so");
                    commands.add(rom.path());

                    if(iconPathSteamHero.exists())
                        launcher = new ExternalLauncher(icon, iconPathSteamHero, commands);
                    else
                        launcher = new ExternalLauncher(icon, commands);
                    coremenu.put(rom.nameWithoutExtension().replace(extension, ""), launcher);
                }
            }
            return coremenu;
        }
        return null;
    }
}
