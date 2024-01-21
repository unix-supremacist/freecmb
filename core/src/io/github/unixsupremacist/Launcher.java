package io.github.unixsupremacist;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import lombok.Getter;

@Getter
public class Launcher {
    FileHandle texture;
    FileHandle bg;

    public Launcher(FileHandle texture){
        this(texture, null);
    }

    public Launcher(FileHandle texture, FileHandle bg){
        this.texture = texture;
        this.bg = bg;
        //this.texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Nearest);
    }

    public void run(){}

    public float getWidth(int IconSize, int width, int height){
        if (isTall(IconSize, width, height)){
            return width * IconSize / height;
        } else {
            return IconSize;
        }
    }

    public float getHeight(int IconSize, int width, int height){
        if (isTall(IconSize, width, height)){
            return IconSize;
        } else {
            return height * IconSize / width;
        }
    }

    private boolean isTall(int IconSize, int width, int height){
        float x = IconSize / width;
        float y = IconSize / height;
        if (x > y){
            return true;
        } else {
            return false;
        }
    }
}
