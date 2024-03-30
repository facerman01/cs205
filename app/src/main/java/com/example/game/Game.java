package com.example.game;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class Game {
    private final Object mutex = new Object();
//    private boolean showFps = false;
    private final Paint fpsText = new Paint();
    private final static int targetFps = 30;
    private final ElapsedTimer elapsedTimer = new ElapsedTimer();
    private final static long intervalFps = 1000L;
    private final static long intervalUps = 1000L;
    private final DeltaStepper fpsUpdater = new DeltaStepper(intervalFps, this::fpsUpdate);

    private final DeltaStepper upsUpdater = new DeltaStepper(intervalUps, this::upsUpdate);
    private final Predicate<Consumer<Canvas>> useCanvas;
    private int secondCount = 0;
    private int width = 0;
    private int height = 0;
    private boolean finished = false;
    private final Counter frameCounter = new Counter();
    private double avgFps = 0.0;


    //needs constructor
    public Game(final Predicate<Consumer<Canvas>> useCanvas) {
        this.useCanvas = useCanvas;
    }


    //resize
    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    //swipe methods(left right up down)


    //upsUpdate UPS -> updates per second
    private boolean upsUpdate(long deltaTime) {
        if (secondCount < 60) {
            ++secondCount;
        }
        if (secondCount == 60) {
            if (!finished) {
                finished = true;
//                try {
//                    runnable.run();
//                } catch (final Exception e) {
//                    e.printStackTrace();
//                }
//                spinnerPaint.setColor(Color.BLACK);
            }
        }
        return true;
    }

    private boolean fpsUpdate(long deltaTime) {
        final double fractionTime = intervalFps / (double)deltaTime;
        avgFps = frameCounter.getValue() * fractionTime;
        return false;
    }

    public void draw() {
        if (useCanvas.test(this::draw)) {
            frameCounter.increment();
        }
    }

    @SuppressLint("DefaultLocale")
    private void draw(Canvas canvas) {
        if (canvas == null) {
            return;
        }
//        final float radius = Math.min(width, height) * 0.40f;
//        final float centerWidth = width / 2.0f;
//        final float centerHeight = height / 2.0f;
        // Draw the background.
        {
            canvas.drawColor(Color.BLACK);
//            Paint paint = new Paint();
//            paint.setColor(Color.BLACK);
//            canvas.drawText("Hello", 100, 100, paint);
            synchronized (mutex) {
                //draw background...?
            }
        }

        // Draw the frame-rate counter.
        {
//            if (showFps) {
                canvas.drawText(
                        String.format("%.2f", avgFps),
                        10.0f, 30.0f,
                        fpsText
                );
//            }
        }
    }

    public long getSleepTime() {
        final double targetFrameTime = (1000.0 / targetFps);
        final long updateEndTime = System.currentTimeMillis();
        final long updateTime = updateEndTime - elapsedTimer.getUpdateStartTime();
        return Math.round(targetFrameTime - updateTime);
    }

    public void update() {
        final long deltaTime = elapsedTimer.progress();
        if (deltaTime <= 0) {
            return;
        }
        // Step updates.
        upsUpdater.update(deltaTime);
        fpsUpdater.update(deltaTime);
        // Immediate updates.
//        spinner += (deltaTime / (60.0f * 1000.0f)) * 360.0f;
    }
}
