package io.github.unixsupremacist;

import java.util.ArrayList;
import java.util.List;

public class Config {
    String steamgridKey = "";
    boolean steamgridEnabled = false;
    List<Integer> dbcache = new ArrayList<>();
    String retroarchNESPath = "/run/media/unix/NVME/Games/Roms/Neoset/nes/";
    String retroarchSNESPath = "/run/media/unix/NVME/Games/Roms/Neoset/snes/";
    String retroarchN64Path = "/run/media/unix/NVME/Games/Roms/Neoset/n64/";
    String retroarchGCNPath = "/run/media/unix/NVME/Games/Roms/Neoset/gcn/";
    String retroarchWIIPath = "/run/media/unix/NVME/Games/Roms/Neoset/wii/phy/";
    String retroarchGBPath = "/run/media/unix/NVME/Games/Roms/Neoset/gb/";
    String retroarchGBCPath = "/run/media/unix/NVME/Games/Roms/Neoset/gbc/";
    String retroarchGBAPath = "/run/media/unix/NVME/Games/Roms/Neoset/gba/";
    String retroarchDSPath = "/run/media/unix/NVME/Games/Roms/Neoset/ds/";

    String retroarchSMDPath = "/run/media/unix/NVME/Games/Roms/Neoset/smd/";
    String retroarchSDCPath = "/run/media/unix/NVME/Games/Roms/Neoset/sdc/";
    String retroarchPS1Path = "/run/media/unix/NVME/Games/Roms/Neoset/ps1/";
    String retroarchPS2Path = "/run/media/unix/NVME/Games/Roms/Neoset/ps2/";
    String retroarchPSPPath = "/run/media/unix/NVME/Games/Roms/Neoset/psp/";
    String retroarchNESCore = "fceumm";
    String retroarchSNESCore = "snes9x";
    String retroarchN64Core = "paralleln64";
    String retroarchGCNCore = "dolphin";
    String retroarchWIICore = "dolphin";
    String retroarchGBCore = "gambatte";
    String retroarchGBCCore = "gambatte";
    String retroarchGBACore = "mgba";
    String retroarchDSCore = "melonds";

    String retroarchSMDCore = "picodrive";
    String retroarchSDCCore = "flycast";
    String retroarchPS1Core = "swanstation";
    String retroarchPS2Core = "lrps2";
    String retroarchPSPCore = "ppsspp";


}
