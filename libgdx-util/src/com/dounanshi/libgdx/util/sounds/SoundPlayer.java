package com.dounanshi.libgdx.util.sounds;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class SoundPlayer {

    private static final int MIN_SOUND_DELAY_MS = 50;

    private final Map<Sound, Long> lastPlayedMap;

    public SoundPlayer() {
        lastPlayedMap = new HashMap<Sound, Long>();
    }

    /**
     * Plays the sound iff the sound is allowed to play.
     * 
     * Returns the soundId or null if the sound was not played.
     */
    public Long playSound(Sound sound, float volume) {
        return playSound(sound, volume, 1, 0);
    }

    /**
     * Plays the sound iff the sound is allowed to play.
     * 
     * Returns the soundId or null if the sound was not played.
     */
    public Long playSound(Sound sound, float volume, float pitch, float pan) {
        if (sound == null || !canPlaySound(sound)) {
            return null;
        }
        lastPlayedMap.put(sound, System.currentTimeMillis());
        return sound.play(volume, pitch, pan);
    }

    /**
     * Plays the music iff the music is allowed to play.
     */
    public void playMusic(Music music) {
        if (music == null || !canPlayMusic(music)) {
            return;
        }
        music.play();
    }

    protected boolean canPlaySound(Sound sound) {
        Long lastPlayedTime = lastPlayedMap.get(sound);
        return lastPlayedTime == null
                || System.currentTimeMillis() - lastPlayedTime > MIN_SOUND_DELAY_MS;
    }

    protected boolean canPlayMusic(Music music) {
        return !music.isPlaying();
    }
}
