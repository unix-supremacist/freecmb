package io.github.unixsupremacist;

import com.badlogic.gdx.graphics.Texture;

public class Exiter extends Launcher{
    public Exiter(Texture texture) {
        super(texture);
    }

    @Override
    public void run(){
        System.exit(0);
    }
}
