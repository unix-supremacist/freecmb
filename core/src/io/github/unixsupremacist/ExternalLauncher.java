package io.github.unixsupremacist;

import com.badlogic.gdx.files.FileHandle;

import java.util.ArrayList;

public class ExternalLauncher extends Launcher{
    ArrayList<String> commands;

    public ExternalLauncher(FileHandle texture, String command){
        this(texture, null, command);
    }

    public ExternalLauncher(FileHandle texture, FileHandle background, String command){
        super(texture, background);
        ArrayList<String> commands = new ArrayList<>();
        commands.add(command);
        this.commands = commands;
    }

    public ExternalLauncher(FileHandle texture, ArrayList<String> commands){
        this(texture, null, commands);
    }

    public ExternalLauncher(FileHandle texture, FileHandle background, ArrayList<String> commands){
        super(texture, background);
        this.commands = commands;
    }

    @Override
    public void run(){
        CMBMain.runExternalApp(commands);
    }
}
