package com.dounan.libgdx.util;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Utils {

    /**
     * Returns the x coordinate to center the target Actor horizontally with
     * respect to the reference Actor.
     */
    public static float centerHorz(Actor reference, Actor target) {
        float refW2 = reference.getWidth() * reference.getScaleX() * .5f;
        float targetW2 = target.getWidth() * target.getScaleX() * .5f;
        return reference.getX() + refW2 - targetW2;
    }

    /**
     * Returns the x coordinate to center the target horizontally with respect
     * to the reference.
     */
    public static float centerHorz(float refX, float refW, float targetW) {
        return refX + refW * .5f - targetW * .5f;
    }

    /**
     * Returns the y coordinate to center the target Actor vertically with
     * respect to the reference Actor.
     */
    public static float centerVert(Actor reference, Actor target) {
        float refH2 = reference.getHeight() * reference.getScaleY() * .5f;
        float targetH2 = target.getHeight() * target.getScaleY() * .5f;
        return reference.getY() + refH2 - targetH2;
    }

    /**
     * Returns the y coordinate to center the target vertically with respect to
     * the reference.
     */
    public static float centerVert(float refY, float refH, float targetH) {
        return refY + refH * .5f - targetH * .5f;
    }

    /**
     * Sets the origin of the image to the center and moves the image so that
     * the origin is at (0, 0).
     */
    public static void alignCenter(Image img) {
        float w2 = img.getWidth() * .5f;
        float h2 = img.getHeight() * .5f;
        img.setOrigin(w2, h2);
        img.setPosition(-w2, -h2);
    }

    /**
     * Returns the angle of the vector from a1 to a2 in radians.
     */
    public static float angle(Actor a1, Actor a2) {
        float dx = a2.getX() - a1.getX();
        float dy = a2.getY() - a1.getY();
        return MathUtils.atan2(dy, dx);
    }

    /**
     * Returns the length squared of the vector from point 1 to point 2.
     */
    public static float distSq(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return dx * dx + dy * dy;
    }

    /**
     * Shorthand way of creating a new Map<String, String>.
     * 
     * @param args
     *            Alternating key and value. Length of args must be even.
     * @return a new Map with the given key values.
     */
    public static Map<String, String> map(String... args) {
        if (args.length % 2 != 0) {
            throw new IllegalArgumentException("Utils.map arguments must be key value pairs");
        }
        HashMap<String, String> m = new HashMap<String, String>();
        for (int i = 0; i < args.length / 2; i++) {
            m.put(args[2 * i], args[2 * i + 1]);
        }
        return m;
    }

    /**
     * Calculates the amount to rotate the actor so that it is facing the
     * target.
     * 
     * @param maxRotation
     *            maximum amount to rotate (magnitude).
     * @return the amount to rotate the actor towards the target.
     */
    public static float rotation(Actor actor, Actor target, float maxRotation) {
        float x = actor.getX();
        float y = actor.getY();
        float tx = target.getX();
        float ty = target.getY();
        float targetRot = (MathUtils.radDeg * MathUtils.atan2(ty - y, tx - x) % 360 + 360) % 360;
        float rot = (actor.getRotation() % 360 + 360) % 360;
        float dr = targetRot - rot;
        if (Math.abs(dr) <= maxRotation) {
            return dr;
        } else if (dr > 180) {
            return -maxRotation;
        } else if (dr < -180) {
            return maxRotation;
        } else if (dr > 0) {
            return maxRotation;
        } else {
            return -maxRotation;
        }
    }

    /**
     * Converts a color in HSBA to RGBA.
     * 
     * The input color will contain HSBA data. The method will modify the input
     * color and set the proper RGBA values.
     */
    public static void hsbToRgb(Color color) {
        float hue = color.r;
        float saturation = color.g;
        float brightness = color.b;
        int r = 0, g = 0, b = 0;
        if (saturation == 0) {
            r = g = b = (int) (brightness * 255.0f + 0.5f);
        } else {
            float h = (hue - (float) Math.floor(hue)) * 6.0f;
            float f = h - (float) java.lang.Math.floor(h);
            float p = brightness * (1.0f - saturation);
            float q = brightness * (1.0f - saturation * f);
            float t = brightness * (1.0f - (saturation * (1.0f - f)));
            switch ((int) h) {
                case 0:
                    r = (int) (brightness * 255.0f + 0.5f);
                    g = (int) (t * 255.0f + 0.5f);
                    b = (int) (p * 255.0f + 0.5f);
                    break;
                case 1:
                    r = (int) (q * 255.0f + 0.5f);
                    g = (int) (brightness * 255.0f + 0.5f);
                    b = (int) (p * 255.0f + 0.5f);
                    break;
                case 2:
                    r = (int) (p * 255.0f + 0.5f);
                    g = (int) (brightness * 255.0f + 0.5f);
                    b = (int) (t * 255.0f + 0.5f);
                    break;
                case 3:
                    r = (int) (p * 255.0f + 0.5f);
                    g = (int) (q * 255.0f + 0.5f);
                    b = (int) (brightness * 255.0f + 0.5f);
                    break;
                case 4:
                    r = (int) (t * 255.0f + 0.5f);
                    g = (int) (p * 255.0f + 0.5f);
                    b = (int) (brightness * 255.0f + 0.5f);
                    break;
                case 5:
                    r = (int) (brightness * 255.0f + 0.5f);
                    g = (int) (p * 255.0f + 0.5f);
                    b = (int) (q * 255.0f + 0.5f);
                    break;
            }
        }
        color.r = ((float) r) / 255;
        color.g = ((float) g) / 255;
        color.b = ((float) b) / 255;
    }

    /**
     * Squeezes the backing interpolation into the window [humpStart, humpStart
     * + humpDuration).
     */
    public static class HumpInterpolation extends Interpolation {

        public float humpStart;
        private final float humpDuration;
        private final Interpolation backingInterpolation;

        /**
         * @param humpDuration
         *            [0, 1] of how long the backing interpolation should last.
         */
        public HumpInterpolation(Interpolation backingInterpolation, float humpDuration) {
            this.humpDuration = humpDuration;
            this.backingInterpolation = backingInterpolation;
        }

        @Override
        public float apply(float a) {
            float humpEnd = humpStart + humpDuration;
            float a2 = a + 1;
            if ((a >= humpStart && a < humpEnd) || (a2 >= humpStart && a2 < humpEnd)) {
                float t = (a - humpStart + 1) % 1;
                return backingInterpolation.apply(t / humpDuration);
            } else {
                return 0;
            }
        }
    }

    /**
     * Runs the interpolation backwards.
     */
    public static class ReverseInterpolation extends Interpolation {

        private final Interpolation backingInterpolation;

        public ReverseInterpolation(Interpolation backingInterpolation) {
            this.backingInterpolation = backingInterpolation;
        }

        @Override
        public float apply(float a) {
            return backingInterpolation.apply(1 - a);
        }
    }
}
