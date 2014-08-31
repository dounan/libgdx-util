package com.dounan.libgdx.util.destructable;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

public class LevelRenderer implements Disposable {

    // Size of the texture tiles to break the level into.
    private static final int REGION_W = 512;
    private static final int REGION_H = 512;

    private final int numRegionCols;
    private final int numRegionRows;

    // 2d array of map regions with y-up (row 0 is the bottom most row).
    private Pixmap[] levelRegionPixmaps;
    private Sprite[] levelRegions;

    /**
     * Constructor.
     * 
     * @param levelPixmap
     *            {@link Pixmap} image of the level.
     */
    public LevelRenderer(Pixmap levelPixmap) {

        int w = levelPixmap.getWidth();
        int h = levelPixmap.getHeight();

        // Break the level into texture tiles.
        numRegionCols = MathUtils.ceil(((float) w) / REGION_W);
        numRegionRows = MathUtils.ceil(((float) h) / REGION_H);
        int numRegions = numRegionCols * numRegionRows;
        levelRegionPixmaps = new Pixmap[numRegions];
        levelRegions = new Sprite[numRegions];

        // Since the textures are POT dimensions, there will be some unused
        // pixels
        // in the x and y directions. Due to the y-up coord system, we must make
        // sure these pixels are at the top for the camera to show the map
        // properly.
        // (without showing the unfilled pixels).
        for (int r = 0; r < numRegionRows; r++) {
            int spriteY = r * REGION_H;
            // The y-coord in the source to draw from.
            int pixmapY = h - spriteY - REGION_H;
            // The start y-coord in the target to draw to.
            int yOffset = 0;
            // The number of rows of pixels to copy over from the source pixmap.
            int pixmapHeight = REGION_H;
            if (pixmapY < 0) {
                yOffset = -pixmapY;
                pixmapHeight += pixmapY;
                pixmapY = 0;
            }

            for (int c = 0; c < numRegionCols; c++) {
                int pixmapX = c * REGION_W;
                // OpenGL has y-coordinates going up, so row 0 is the bottom of
                // the map.
                Pixmap regionPixmap = new Pixmap(REGION_W, REGION_H, levelPixmap.getFormat());
                regionPixmap.drawPixmap(levelPixmap, 0, yOffset, pixmapX, pixmapY, REGION_W,
                        pixmapHeight);

                Sprite regionSprite = new Sprite(new Texture(regionPixmap, true));
                regionSprite.getTexture().setFilter(TextureFilter.MipMapNearestLinear,
                        TextureFilter.MipMapNearestLinear);
                regionSprite.setPosition(pixmapX, spriteY);

                int regionIdx = regionIdx(r, c);
                levelRegionPixmaps[regionIdx] = regionPixmap;
                levelRegions[regionIdx] = regionSprite;
            }
        }
    }

    /**
     * Renders the level to the {@link SpriteBatch}.
     * 
     * @param spriteBatch
     *            where to draw the level to.
     * @param view
     *            visible part of the level.
     */
    public void render(SpriteBatch spriteBatch, Rectangle view) {
        int startR = (int) view.y / REGION_H;
        if (startR < 0) {
            startR = 0;
        }
        int startC = (int) view.x / REGION_W;
        if (startC < 0) {
            startC = 0;
        }
        int endR = (int) (view.y + view.height) / REGION_H;
        if (endR >= numRegionRows) {
            endR = numRegionRows - 1;
        }
        int endC = (int) (view.x + view.width) / REGION_W;
        if (endC >= numRegionCols) {
            endC = numRegionCols - 1;
        }
        for (int r = startR; r <= endR; r++) {
            for (int c = startC; c <= endC; c++) {
                levelRegions[regionIdx(r, c)].draw(spriteBatch);
            }
        }
    }

    /**
     * Creates a hole in the level (makes that area transparent).
     */
    public void addHole(float levelX, float levelY, int radius) {
        int lx = (int) levelX;
        int ly = (int) levelY;
        int startR = (ly - radius) / REGION_H;
        if (startR < 0) {
            startR = 0;
        }
        int startC = (lx - radius) / REGION_W;
        if (startC < 0) {
            startC = 0;
        }
        int endR = (ly + radius) / REGION_H;
        if (endR >= numRegionRows) {
            endR = numRegionRows - 1;
        }
        int endC = (lx + radius) / REGION_W;
        if (endC >= numRegionCols) {
            endC = numRegionCols - 1;
        }
        for (int r = startR; r <= endR; r++) {
            for (int c = startC; c <= endC; c++) {
                int regionIdx = regionIdx(r, c);
                Pixmap regionPixmap = levelRegionPixmaps[regionIdx];
                int regionOffsetX = c * REGION_W;
                int regionOffsetY = r * REGION_H;
                int px = lx - regionOffsetX;
                int py = regionPixmap.getHeight() - (ly - regionOffsetY);
                regionPixmap.setColor(0);
                regionPixmap.fillCircle(px, py, radius);
                Texture regionTexture = levelRegions[regionIdx].getTexture();
                regionTexture.load(new PixmapTextureData(regionPixmap, null, true, false));
            }
        }
    }

    @Override
    public void dispose() {
        for (Sprite sprite : levelRegions) {
            sprite.getTexture().dispose();
        }
    }

    /**
     * Returns the index into the levelRegions array for the region at (r, c).
     */
    private int regionIdx(int r, int c) {
        return r * numRegionCols + c;
    }
}
