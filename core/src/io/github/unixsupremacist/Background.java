package io.github.unixsupremacist;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Background {
    private Texture texture;
    private float relX, relY;

    public Background(Texture texture) {
        this.relX = 0.5f;
        this.relY = 0.5f;
        this.texture = texture;
        this.texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }


    public void setX(float x) {
        this.relX = x / Gdx.graphics.getWidth();
    }

    public void setY(float y) {
        this.relY = y / Gdx.graphics.getHeight();
    }

    public float getX() {
        return relX * (Gdx.graphics.getWidth() - getWidth());
    }

    public float getY() {
        return relY * Gdx.graphics.getHeight() - getHeight() / 2;
    }

    public float getWidth(){
        if (isTall())
            return (float) (texture.getWidth() * Gdx.graphics.getHeight()) / texture.getHeight() ;
        else
            return Gdx.graphics.getWidth();
    }

    public float getHeight(){
        if (isTall())
            return Gdx.graphics.getHeight();
        else
            return (float) (texture.getHeight() * Gdx.graphics.getWidth()) / texture.getWidth();
    }

    private boolean isTall(){
        float x = (float) Gdx.graphics.getWidth() / texture.getWidth();
        float y = (float) Gdx.graphics.getHeight() / texture.getHeight();
        if (x > y)
            return false;
        else
            return true;
    }

    public void snapToScreenspace(){
        if (relX < 0.0f) relX = 0.0f;
        if (relX > 1.0f) relX = 1.0f;
    }

    public void dispose(){
        texture.dispose();
    }
}
