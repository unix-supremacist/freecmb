package io.github.unixsupremacist;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Retroarch {
    public static final String flatpakID = "org.libretro.RetroArch";
    public static boolean isInstalled(){
        return Flatpak.getInstalled().containsKey(flatpakID);
    }

    public static void populateCores(){
        if(isInstalled()){
            CMBMain.menu.put("GCN", newRetrocore( "dolphin", "retroarch.png", ".nkit", "/run/media/unix/NVME/Games/Roms/Neoset/gcn/"));
            CMBMain.menu.put("Wii", newRetrocore( "dolphin", "retroarch.png", ".nkit", "/run/media/unix/NVME/Games/Roms/Neoset/wii/phy/"));
            CMBMain.menu.put("SDC", newRetrocore( "flycast", "retroarch.png", "", "/run/media/unix/NVME/Games/Roms/Neoset/sdc/"));
        }
    }

    public static Map<String, Launcher> newRetrocore(String core, String texture, String extension, String dir){
        Map<String, Launcher> coremenu = new HashMap<>();
        FileHandle icon;
        ExternalLauncher launcher;

        FileHandle handle = Gdx.files.absolute(dir);
        if (handle.isDirectory()){
            for (FileHandle rom : handle.list()){
                FileHandle iconPathSteamLogo = Gdx.files.absolute(CMBMain.StorageDirectory+"steamgrids/"+rom.nameWithoutExtension().replace(extension, "")+"_logo.png");
                FileHandle iconPathSteamHero = Gdx.files.absolute(CMBMain.StorageDirectory+"steamgrids/"+rom.nameWithoutExtension().replace(extension, "")+"_hero.png");
                if ((!iconPathSteamLogo.exists() || !iconPathSteamHero.exists())){
                    int id = Steamgrid.steamgridSearch(rom.nameWithoutExtension().replace(extension, ""));

                    if(id != 0){
                        if (!iconPathSteamLogo.exists()){
                            var url = Steamgrid.steamgridGet(id, "logos");
                            try {
                                FileUtils.copyURLToFile(new URL(url), new File(CMBMain.StorageDirectory+"steamgrids/"+rom.nameWithoutExtension().replace(extension, "")+"_logo.png"));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        if (!iconPathSteamHero.exists()){
                            var url = Steamgrid.steamgridGet(id, "heroes");
                            try {
                                FileUtils.copyURLToFile(new URL(url), new File(CMBMain.StorageDirectory+"steamgrids/"+rom.nameWithoutExtension().replace(extension, "")+"_hero.png"));
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
                commands.add("org.libretro.RetroArch");
                commands.add("-L");
                commands.add("/home/unix/.var/app/org.libretro.RetroArch/config/retroarch/cores/"+core+"_libretro.so");
                commands.add(rom.path());

                if(iconPathSteamHero.exists()){
                    launcher = new ExternalLauncher(icon, iconPathSteamHero, commands);
                } else {
                    launcher = new ExternalLauncher(icon, commands);
                }
                coremenu.put(rom.nameWithoutExtension().replace(extension, ""), launcher);
            }
        }
        return coremenu;
    }
}
