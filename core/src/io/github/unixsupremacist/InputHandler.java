package io.github.unixsupremacist;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;

import java.util.ArrayList;

public class InputHandler implements InputProcessor, ControllerListener {
    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.LEFT)
            CMBMain.x--;
        if (keycode == Input.Keys.RIGHT)
            CMBMain.x++;
        if (keycode == Input.Keys.UP)
            CMBMain.y--;
        if (keycode == Input.Keys.DOWN)
            CMBMain.y++;

        if (keycode == Input.Keys.SPACE)
            CMBMain.menu.get(CMBMain.submenus.get(CMBMain.x)).get(CMBMain.getOptions().get(CMBMain.y)).run();
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    @Override
    public void connected(Controller controller) {

    }

    @Override
    public void disconnected(Controller controller) {

    }

    @Override
    public boolean buttonDown(Controller controller, int keycode) {
        if (keycode == controller.getMapping().buttonDpadLeft)
            CMBMain.x--;
        if (keycode == controller.getMapping().buttonDpadRight)
            CMBMain.x++;
        if (keycode == controller.getMapping().buttonDpadUp)
            CMBMain.y--;
        if (keycode == controller.getMapping().buttonDpadDown)
            CMBMain.y++;

        if (keycode == controller.getMapping().buttonA)
            CMBMain.menu.get(CMBMain.submenus.get(CMBMain.x)).get(CMBMain.getOptions().get(CMBMain.y)).run();
        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int i) {
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int i, float v) {
        return false;
    }
}
