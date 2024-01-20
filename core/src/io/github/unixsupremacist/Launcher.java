package io.github.unixsupremacist;

import com.badlogic.gdx.graphics.Texture;
import lombok.Getter;

@Getter
public class Launcher {
    Texture texture;
    Background bg;

    public Launcher(Texture texture){
        this(texture, null);
    }

    public Launcher(Texture texture, Background bg){
        this.texture = texture;
        this.bg = bg;
        this.texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Nearest);
    }

    public void run(){}

    public float getWidth(int IconSize){
        if (isTall(IconSize)){
            return texture.getWidth() * IconSize / texture.getHeight() ;
        } else {
            return IconSize;
        }
    }

    public float getHeight(int IconSize){
        if (isTall(IconSize)){
            return IconSize;
        } else {
            return texture.getHeight() * IconSize / texture.getWidth();
        }
    }

    private boolean isTall(int IconSize){
        float x = IconSize / texture.getWidth();
        float y = IconSize / texture.getHeight();
        if (x > y){
            return true;
        } else {
            return false;
        }
    }
}
