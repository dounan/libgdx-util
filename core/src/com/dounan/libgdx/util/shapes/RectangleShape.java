package com.dounan.libgdx.util.shapes;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Wraps a libgdx Rectangle.
 */
public class RectangleShape implements Shape {

    public Rectangle rectangle;

    public RectangleShape() {
        rectangle = new Rectangle();
    }

    @Override
    public void write(Json json) {
        json.writeValue("x", rectangle.x);
        json.writeValue("y", rectangle.y);
        json.writeValue("w", rectangle.width);
        json.writeValue("h", rectangle.height);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        rectangle.x = json.readValue(Float.class, jsonData.get("x"));
        rectangle.y = json.readValue(Float.class, jsonData.get("y"));
        rectangle.width = json.readValue(Float.class, jsonData.get("w"));
        rectangle.height = json.readValue(Float.class, jsonData.get("h"));
    }

    @Override
    public float getX() {
        return rectangle.x;
    }

    @Override
    public float getY() {
        return rectangle.y;
    }

    @Override
    public float getMinX() {
        return rectangle.x;
    }

    @Override
    public float getMaxX() {
        return rectangle.x + rectangle.width;
    }

    @Override
    public float getMinY() {
        return rectangle.y;
    }

    @Override
    public float getMaxY() {
        return rectangle.y + rectangle.height;
    }
}
