package com.dounanshi.libgdx.util.destructable.mappreprocessor;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;
import com.dounanshi.libgdx.util.destructable.CollisionMap;

public class MapPreprocessor implements ApplicationListener {

    private static final String LOG_TAG = "MapPreprocessor";

    // Number of pixels to look around when calculating projection vector.
    // For more curvy maps, a higher number is better.
    private static final int PROJECTION_SMOOTH = 6;

    @Override
    public void create() {
        // IMPORTANT! This allows you to set pixels to the exact bits.
        Pixmap.setBlending(Blending.None);

        // Load level image file.
        FileHandle inFileHandle = chooseFile(false);
        if (inFileHandle == null) {
            Gdx.app.log("MapExporter", "Open cancelled");
            Gdx.app.exit();
            return;
        }

        // Create collision pixmap.
        Pixmap levelPixmap = new Pixmap(inFileHandle);
        Pixmap collisionPixmap = buildCollisionPixmap(levelPixmap);

        // Save output pixmap.
        FileHandle outFileHandle = chooseFile(true);
        if (outFileHandle == null) {
            Gdx.app.log(LOG_TAG, "Save cancelled");
            Gdx.app.exit();
            return;
        }
        PixmapIO.writeCIM(outFileHandle, collisionPixmap);
        Gdx.app.log(LOG_TAG, "Done saving!");
        Gdx.app.exit();
    }

    @Override
    public void dispose() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void render() {
    }

    @Override
    public void resize(int arg0, int arg1) {
    }

    @Override
    public void resume() {
    }

    /**
     * Helper method that let's the user choose a file from a GUI.
     * 
     * @param save
     *            will open file to save to if true.
     */
    private FileHandle chooseFile(boolean save) {
        JFileChooser fc = new JFileChooser();
        JPanel panel = new JPanel();
        int returnVal = save ? fc.showSaveDialog(panel) : fc.showOpenDialog(panel);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return new FileHandle(fc.getSelectedFile());
        }
        return null;
    }

    /**
     * Creates a collision {@link Pixmap} from a {@link Pixmap} of the level.
     */
    private Pixmap buildCollisionPixmap(Pixmap levelPixmap) {
        int w = levelPixmap.getWidth();
        int h = levelPixmap.getHeight();
        Pixmap collisionPixmap = new Pixmap(w, h, Format.RGBA8888);
        for (int x = 0; x < w; x++) {
            if (x % 500 == 0) {
                Gdx.app.log(LOG_TAG, "building collision map x: " + x);
            }
            for (int y = 0; y < h; y++) {
                int color = getColor(levelPixmap, x, y);
                collisionPixmap.drawPixel(x, y, color);
            }
        }
        return collisionPixmap;
    }

    /**
     * Returns a 32 bit color for the pixel at (x, y).
     * 
     * @param pixmap
     *            {@link Pixmap} representing the terrain.
     * @param x
     *            x coordinate of the pixel. 0 is the leftmost column.
     * @param y
     *            y coordinate of the pixel. 0 is the topmost row.
     * 
     * @return the color of the pixel.
     */
    private static int getColor(Pixmap pixmap, int x, int y) {
        if (!isSolid(pixmap.getPixel(x, y))) {
            return CollisionMap.encodeBlankPixelData();
        }
        if (!isEdgePixel(pixmap, x, y)) {
            return CollisionMap.encodeSolidPixelData(0, 0);
        }
        boolean hasHorz = false;
        boolean hasVert = false;
        int snx = 0;
        int sny = 0;
        for (int dx = -PROJECTION_SMOOTH; dx <= PROJECTION_SMOOTH; dx++) {
            for (int dy = -PROJECTION_SMOOTH; dy <= PROJECTION_SMOOTH; dy++) {
                int pixel = pixmap.getPixel(x + dx, y + dy);
                if (isSolid(pixel)) {
                    hasHorz = hasHorz || (dx != 0);
                    hasVert = hasVert || (dy != 0);
                    snx -= dx;
                    sny += dy;
                }
            }
        }
        if (snx == 0 && sny == 0) {
            if (hasHorz && hasVert) {
                // This should never happen.
                Gdx.app.log("getColor", "Wtf? hasHorz && hasVert at (" + x + ", " + y + ")");
                sny = 1;
            } else if (!hasHorz && !hasVert) {
                sny = 1;
            } else if (hasHorz) {
                sny = 1;
            } else if (hasVert) {
                snx = 1;
            }
        }
        return CollisionMap.encodeSolidPixelData(snx, sny);
    }

    private static boolean isEdgePixel(Pixmap pixmap, int x, int y) {
        return !isSolid(pixmap.getPixel(x - 1, y - 1)) || !isSolid(pixmap.getPixel(x - 1, y))
                || !isSolid(pixmap.getPixel(x - 1, y + 1)) || !isSolid(pixmap.getPixel(x, y - 1))
                || !isSolid(pixmap.getPixel(x, y + 1)) || !isSolid(pixmap.getPixel(x + 1, y - 1))
                || !isSolid(pixmap.getPixel(x + 1, y)) || !isSolid(pixmap.getPixel(x + 1, y + 1));
    }

    private static boolean isSolid(int pixel) {
        return (pixel & 0xff) != 0;
    }
}
