package com.dounanshi.libgdx.util.sounds;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

/**
 * Plays one sound from a set at random.
 */
public class SoundSet implements Sound {

    public static final int RANDOM = 0;
    public static final int SEQUENTIAL = 1;

    private Array<SoundData> soundDatas;
    private int playback;
    // Used for sequential playback.
    private int nextSoundIdx;

    public SoundSet() {
        soundDatas = new Array<SoundData>();
    }

    public void add(Sound sound, float volume) {
        soundDatas.add(new SoundData(sound, volume));
    }

    /**
     * Sets how a sound is chosen from the set. Default is RANDOM.
     */
    public void setPlayback(int playback) {
        this.playback = playback;
    }

    @Override
    public long play() {
        SoundData soundData = nextSoundData();
        if (soundData == null) {
            return -1;
        }
        return soundData.sound.play(soundData.volume);
    }

    @Override
    public long play(float volume) {
        SoundData soundData = nextSoundData();
        if (soundData == null) {
            return -1;
        }
        return soundData.sound.play(volume * soundData.volume);
    }

    @Override
    public long play(float volume, float pitch, float pan) {
        SoundData soundData = nextSoundData();
        if (soundData == null) {
            return -1;
        }
        return soundData.sound.play(volume * soundData.volume, pitch, pan);
    }

    @Override
    public long loop() {
        SoundData soundData = nextSoundData();
        if (soundData == null) {
            return -1;
        }
        return soundData.sound.loop(soundData.volume);
    }

    @Override
    public long loop(float volume) {
        SoundData soundData = nextSoundData();
        if (soundData == null) {
            return -1;
        }
        return soundData.sound.loop(volume * soundData.volume);
    }

    @Override
    public long loop(float volume, float pitch, float pan) {
        SoundData soundData = nextSoundData();
        if (soundData == null) {
            return -1;
        }
        return soundData.sound.loop(volume * soundData.volume, pitch, pan);
    }

    @Override
    public void stop() {
        for (SoundData soundData : soundDatas) {
            soundData.sound.stop();
        }
    }

    @Override
    public void pause() {
        for (SoundData soundData : soundDatas) {
            soundData.sound.pause();
        }
    }

    @Override
    public void resume() {
        for (SoundData soundData : soundDatas) {
            soundData.sound.resume();
        }
    }

    @Override
    public void dispose() {
        for (SoundData soundData : soundDatas) {
            soundData.sound.dispose();
        }
    }

    @Override
    public void stop(long soundId) {
        for (SoundData soundData : soundDatas) {
            soundData.sound.stop(soundId);
        }
    }

    @Override
    public void pause(long soundId) {
        for (SoundData soundData : soundDatas) {
            soundData.sound.pause(soundId);
        }
    }

    @Override
    public void resume(long soundId) {
        for (SoundData soundData : soundDatas) {
            soundData.sound.resume(soundId);
        }
    }

    @Override
    public void setLooping(long soundId, boolean looping) {
        for (SoundData soundData : soundDatas) {
            soundData.sound.setLooping(soundId, looping);
        }
    }

    @Override
    public void setPitch(long soundId, float pitch) {
        for (SoundData soundData : soundDatas) {
            soundData.sound.setPitch(soundId, pitch);
        }
    }

    @Override
    public void setVolume(long soundId, float volume) {
        for (SoundData soundData : soundDatas) {
            soundData.sound.setVolume(soundId, volume * soundData.volume);
        }
    }

    @Override
    public void setPan(long soundId, float pan, float volume) {
        for (SoundData soundData : soundDatas) {
            soundData.sound.setPan(soundId, pan, volume * soundData.volume);
        }
    }

    @Override
    public void setPriority(long soundId, int priority) {
        for (SoundData soundData : soundDatas) {
            soundData.sound.setPriority(soundId, priority);
        }
    }

    private SoundData nextSoundData() {
        if (soundDatas.size == 0) {
            return null;
        }
        int idx;
        if (playback == RANDOM) {
            idx = MathUtils.random(soundDatas.size - 1);
        } else if (playback == SEQUENTIAL) {
            idx = nextSoundIdx;
            nextSoundIdx = (nextSoundIdx + 1) % soundDatas.size;
        } else {
            throw new IllegalStateException("Unknown playback: " + playback);
        }
        return soundDatas.get(idx);
    }

    private static class SoundData {

        private final Sound sound;
        private final float volume;

        private SoundData(Sound sound, float volume) {
            this.sound = sound;
            this.volume = volume;
        }
    }
}
