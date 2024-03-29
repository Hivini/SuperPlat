package com.hivini.engine;

import com.hivini.platformer.GameManager;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class GameContainer implements Runnable {

    private Thread thread;
    private Window window;
    private Renderer renderer;
    private Input input;
    private AbstractGame game;

    private int width = 320, height = 240;
    private float scale = 3f;
    private String title = "Platformer Engine";

    private boolean render = false;
    private boolean running = false;
    private final double UPDATE_CAP = 1.0 / 60.0;
    private boolean showCheckText = false;
    private boolean showDiedText = false;

    public GameContainer(AbstractGame game) {
        this.game = game;
    }

    public void start() {
        window = new Window(this);
        renderer = new Renderer(this);
        thread = new Thread(this);
        input = new Input(this);
        thread.run();
    }

    public void stop() {

    }

    @Override
    public void run() {
        running = true;

        boolean render;
        double firstTime = 0;
        double lastTime = System.nanoTime() / 1000000000.0;
        double passedTime = 0;
        double unprocessedTime = 0;

        double frameTime = 0;
        int frames = 0;
        int fps = 0;

        game.init(this);

        while (running) {
            render = false; // Change this to show all the frames
            firstTime = System.nanoTime() / 1000000000.0;
            passedTime = firstTime - lastTime;
            lastTime = firstTime;

            unprocessedTime += passedTime;
            frameTime += passedTime;

            while (unprocessedTime >= UPDATE_CAP) {
                unprocessedTime -= UPDATE_CAP;
                render = true;

                game.update(this, (float)UPDATE_CAP);

                input.update();

                if (frameTime >= 1.0) {
                    frameTime = 0;
                    fps = frames;
                    frames = 0;
                }
            }

            if (render) {
                renderer.clear();
                game.render(this, renderer);
                renderer.process();
                renderer.setCameraX(0);
                renderer.setCameraY(0);
                renderer.drawText("FPS:" + fps, 0, 0, 0xfff4d03f);
                if (showCheckText)
                    renderer.drawText("Checkpoint Flagged!", getWidth() / 2 - 60, getHeight() / 2 - 20, 0xff00ff00);
                if (showDiedText)
                    renderer.drawText("You lost one live, be careful!", getWidth() / 2 - 90, getHeight() / 2 - 20, 0xffffff00);
                renderer.drawText("Lives: " + GameManager.getPlayer().getLives(), getWidth()- 50, 0, 0xffffffff);
                renderer.drawText("Score: " + GameManager.score, getWidth()- 70, 10, 0xffffffff);
                window.update();
                frames++;
            } else {
                // This is gonna low the usage percentage of the CPU
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            dispose();

        }
    }

    private void dispose() {

    }

    public void drawCheckPoint(boolean enabler) {
        showCheckText = enabler;
    }

    public void drawDiedText(boolean enabler) {
        showDiedText = enabler;
    }

    // Getters and Setters below

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Window getWindow() {
        return window;
    }

    public Input getInput() {
        return input;
    }

    public Renderer getRenderer() {
        return renderer;
    }
}
