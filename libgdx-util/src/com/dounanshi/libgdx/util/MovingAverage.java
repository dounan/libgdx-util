package com.dounanshi.libgdx.util;

import java.util.Arrays;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;

public class MovingAverage implements Serializable {

    public float avg;
    private int windowSize;
    private float[] samples;
    private int nextSampleIdx;

    public MovingAverage(int maxWindowSize) {
        windowSize = 0;
        samples = new float[maxWindowSize];
    }

    /**
     * For deserialization.
     */
    @SuppressWarnings("unused")
    private MovingAverage() {
    }

    public void sample(float value) {
        float prevVal = samples[nextSampleIdx];
        samples[nextSampleIdx] = value;
        if (windowSize < samples.length) {
            avg *= ((float) windowSize) / (windowSize + 1);
            windowSize++;
        }
        avg += value / windowSize - prevVal / windowSize;
        nextSampleIdx = (nextSampleIdx + 1) % samples.length;
    }

    @Override
    public void write(Json json) {
        json.writeValue("avg", avg);
        json.writeValue("windowSize", windowSize);
        json.writeValue("samples", samples);
        json.writeValue("nextSampleIdx", nextSampleIdx);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        avg = json.readValue(Float.class, jsonData.get("avg"));
        windowSize = json.readValue(Integer.class, jsonData.get("windowSize"));
        samples = json.readValue(float[].class, jsonData.get("samples"));
        nextSampleIdx = json.readValue(Integer.class, jsonData.get("nextSampleIdx"));
    }

    public void reset() {
        windowSize = 0;
        avg = 0;
        Arrays.fill(samples, 0);
        nextSampleIdx = 0;
    }
}
