package com.dounan.libgdx.util.shapes;

import com.badlogic.gdx.utils.Json.Serializable;

/**
 * Common interface for Shapes.
 */
public interface Shape extends Serializable {
    public float getX();

    public float getY();

    public float getMinX();

    public float getMaxX();

    public float getMinY();

    public float getMaxY();
}
