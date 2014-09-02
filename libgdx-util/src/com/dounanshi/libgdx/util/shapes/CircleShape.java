package com.dounanshi.libgdx.util.shapes;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Wraps a libgdx Circle.
 */
public class CircleShape implements Shape {

    public Circle circle;

    public CircleShape() {
        circle = new Circle();
    }

    @Override
    public void write(Json json) {
        json.writeValue("x", circle.x);
        json.writeValue("y", circle.y);
        json.writeValue("r", circle.radius);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        circle.x = json.readValue(Float.class, jsonData.get("x"));
        circle.y = json.readValue(Float.class, jsonData.get("y"));
        circle.radius = json.readValue(Float.class, jsonData.get("r"));
    }

    @Override
    public float getX() {
        return circle.x;
    }

    @Override
    public float getY() {
        return circle.y;
    }

    @Override
    public float getMinX() {
        return circle.x - circle.radius;
    }

    @Override
    public float getMaxX() {
        return circle.x + circle.radius;
    }

    @Override
    public float getMinY() {
        return circle.y - circle.radius;
    }

    @Override
    public float getMaxY() {
        return circle.y + circle.radius;
    }
}
