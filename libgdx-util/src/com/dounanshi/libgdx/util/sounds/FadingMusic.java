package com.dounanshi.libgdx.util.sounds;

import com.badlogic.gdx.audio.Music;

public class FadingMusic implements Music {

    private static final int STOP = 0;
    private static final int PLAY = 1;
    private static final int PAUSE = 2;

    private final Music backingMusic;
    private final float fadeInRate;
    private final float fadeOutRate;
    private float curVolRatio;
    private float userVol;
    private int state;

    /**
     * Fades the music when changing volume.
     * 
     * @param backingMusic
     *            actual music track.
     * @param fadeRate
     *            how much volume can change in one second.
     */
    public FadingMusic(Music backingMusic, float fadeRate) {
        this(backingMusic, fadeRate, fadeRate);
    }

    /**
     * Fades the music when changing volume.
     * 
     * @param backingMusic
     *            actual music track.
     * @param fadeInRate
     *            how much volume can increase in one second.
     * @param fadeOutRate
     *            how much volume can decrease in one second.
     */
    public FadingMusic(Music backingMusic, float fadeInRate, float fadeOutRate) {
        this.backingMusic = backingMusic;
        this.fadeInRate = fadeInRate;
        this.fadeOutRate = fadeOutRate;
        curVolRatio = 0;
        userVol = 1;
        state = STOP;
        backingMusic.setVolume(curVolRatio);
    }

    @Override
    public void play() {
        if (!backingMusic.isPlaying()) {
            backingMusic.play();
        }
        state = PLAY;
    }

    @Override
    public void pause() {
        state = PAUSE;
    }

    public void forcePause() {
        state = PAUSE;
        curVolRatio = 0;
        this.backingMusic.setVolume(0);
        this.backingMusic.pause();
    }

    @Override
    public void stop() {
        state = STOP;
    }

    @Override
    public boolean isPlaying() {
        return state == PLAY;
    }

    @Override
    public void setLooping(boolean isLooping) {
        backingMusic.setLooping(isLooping);
    }

    @Override
    public void setVolume(float volume) {
        userVol = volume;
    }

    @Override
    public void setPan(float pan, float volume) {
        backingMusic.setPan(pan, volume);
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {
        backingMusic.setOnCompletionListener(listener);
    }

    @Override
    public float getPosition() {
        return backingMusic.getPosition();
    }

    @Override
    public boolean isLooping() {
        return backingMusic.isLooping();
    }

    @Override
    public float getVolume() {
        return backingMusic.getVolume();
    }

    @Override
    public void dispose() {
        backingMusic.dispose();
    }

    public void act(float delta) {
        float curDestVol = state == PLAY ? 1 : 0;
        if (curDestVol < curVolRatio) {
            curVolRatio -= fadeOutRate * delta;
            if (curVolRatio < curDestVol) {
                curVolRatio = curDestVol;
            }
        } else if (curDestVol > curVolRatio) {
            curVolRatio += fadeInRate * delta;
            if (curVolRatio > curDestVol) {
                curVolRatio = curDestVol;
            }
        }
        backingMusic.setVolume(curVolRatio * userVol);
        if (curVolRatio == curDestVol) {
            if (state == PAUSE) {
                backingMusic.pause();
            } else if (state == STOP) {
                backingMusic.stop();
            }
        }
    }
}
