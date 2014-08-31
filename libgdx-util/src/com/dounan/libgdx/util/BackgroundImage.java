package com.dounan.libgdx.util;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class BackgroundImage extends Image {

    public boolean blendingEnabled;

    public BackgroundImage(TextureRegion textureRegion) {
        super(textureRegion);
        blendingEnabled = false;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        boolean prevBlendingEnabled = batch.isBlendingEnabled();
        if (!blendingEnabled) {
            batch.disableBlending();
        }
        super.draw(batch, parentAlpha);
        if (!blendingEnabled && prevBlendingEnabled) {
            batch.enableBlending();
        }
    }
}
