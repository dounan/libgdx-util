package com.dounan.libgdx.util.shapes;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class PointShape implements Shape {

    public float x;
    public float y;

    @Override
    public void write(Json json) {
        json.writeValue("x", x);
        json.writeValue("y", y);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        x = json.readValue(Float.class, jsonData.get("x"));
        y = json.readValue(Float.class, jsonData.get("y"));
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public float getMinX() {
        return x;
    }

    @Override
    public float getMaxX() {
        return x;
    }

    @Override
    public float getMinY() {
        return y;
    }

    @Override
    public float getMaxY() {
        return y;
    }
}
