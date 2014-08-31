package com.dounan.libgdx.util.shapes;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class PolygonShape implements Shape {

    /**
     * Creates a new rectangular PolygonShape with the same properties as the
     * Image (position, size, origin, rotation, scale).
     */
    public static PolygonShape create(Image img) {
        float w = img.getWidth();
        float h = img.getHeight();
        PolygonShape shape = new PolygonShape(new float[] { 0, 0, w, 0, w, h, 0, h });
        shape.polygon.setOrigin(img.getOriginX(), img.getOriginY());
        shape.polygon.setPosition(img.getX(), img.getY());
        shape.polygon.setRotation(img.getRotation());
        shape.polygon.setScale(img.getScaleX(), img.getScaleY());
        return shape;
    }

    public Polygon polygon;

    // Empty constructor for deserialization.
    public PolygonShape() {
        this(null);
    }

    public PolygonShape(float[] vertices) {
        if (vertices == null) {
            polygon = new Polygon();
        } else {
            polygon = new Polygon(vertices);
        }
    }

    @Override
    public void write(Json json) {
        float[] vertices = polygon.getVertices();
        Array<Float> vertArr = new Array<Float>(vertices.length);
        for (int i = 0; i < vertices.length; i++) {
            vertArr.add(vertices[i]);
        }
        json.writeValue("vertices", vertArr);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void read(Json json, JsonValue jsonData) {
        Array<Float> vertArr = json.readValue(Array.class, jsonData.get("vertices"));
        float[] vertices = new float[vertArr.size];
        for (int i = 0; i < vertArr.size; i++) {
            vertices[i] = vertArr.get(i);
        }
        polygon = new Polygon(vertices);
    }

    @Override
    public float getX() {
        return polygon.getX();
    }

    @Override
    public float getY() {
        return polygon.getY();
    }

    @Override
    public float getMinX() {
        return polygon.getBoundingRectangle().x;
    }

    @Override
    public float getMaxX() {
        Rectangle bounds = polygon.getBoundingRectangle();
        return bounds.x + bounds.width;
    }

    @Override
    public float getMinY() {
        return polygon.getBoundingRectangle().y;
    }

    @Override
    public float getMaxY() {
        Rectangle bounds = polygon.getBoundingRectangle();
        return bounds.y + bounds.height;
    }
}
