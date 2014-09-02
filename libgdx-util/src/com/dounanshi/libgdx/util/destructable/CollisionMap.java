package com.dounanshi.libgdx.util.destructable;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

public class CollisionMap implements Disposable {

    // Number of bits to determine solid or blank.
    private static final int SOLID_SIZE = 1;

    // Number of bits in surface normal vector.
    private static final int SN_SIZE = 11;

    // Solid or blank bit mask.
    private static final int SOLID_MASK = (int) Math.pow(2, SOLID_SIZE) - 1;

    // Surface normal vector component bit mask.
    private static final int SN_MASK = (int) Math.pow(2, SN_SIZE) - 1;

    // Number of bits to shift for snx and sny.
    private static final int SNX_SHIFT = SOLID_SIZE + SN_SIZE;
    private static final int SNY_SHIFT = SOLID_SIZE;

    // Surface normal vector offset to handle negative values.
    private static final int VECTOR_OFFSET = (int) Math.pow(2, SN_SIZE - 1);

    // Blank pixel that does not contain any data.
    private static final int EMPTY_PIXEL = 0;

    /**
     * Returns a 32 bit int that contains data about the pixel and the surface
     * normal (snx, sny) of the terrain at that pixel.
     * 
     * |snx| and |sny| cannot be greater than VECTOR_OFFSET - 1.
     * 
     * SOLID_SIZE least significant bit determines whether the surface is solid
     * or not. Next SN_SIZE bits used for sny. Next SN_SIZE bits used for snx.
     * Remaining bits to store arbitrary data.
     * 
     * @param snx
     *            x component of the surface vector.
     * @param sny
     *            y component of the surface vector.
     * 
     * @return 32 bit encoding of the surface data.
     */
    public static int encodeSolidPixelData(int snx, int sny) {
        snx += VECTOR_OFFSET;
        sny += VECTOR_OFFSET;
        int pixel = SOLID_MASK | ((snx & SN_MASK) << SNX_SHIFT) | ((sny & SN_MASK) << SNY_SHIFT);
        return pixel;
    }

    /**
     * First 9 bits can still be used to store arbitrary data.
     */
    public static int encodeBlankPixelData() {
        // For now, just return EMPTY_PIXEL.
        return EMPTY_PIXEL;
    }

    public final Pixmap collisionPixmap;
    private final int mapHeight;

    public CollisionMap(Pixmap collisionPixmap) {
        this.collisionPixmap = collisionPixmap;
        this.mapHeight = collisionPixmap.getHeight();
    }

    /**
     * Constructs a {@link CollisionMap} from an exported {@link Pixmap} cim
     * file.
     */
    public CollisionMap(FileHandle cimFile) {
        this(PixmapIO.readCIM(cimFile));
    }

    /**
     * Destructively modifies the position out of the collision map if the
     * position is hitting the collision map.
     * 
     * @param pos
     *            Position of the object to project (will be mutated).
     * @param vel
     *            Velocity of the object to project.
     * 
     * @return Surface normal vector of the part of the surface that the
     *         position was projected out of, or null if the position did not
     *         hit the collision map.
     */
    public Vector2 projectOut(Vector2 pos, Vector2 vel) {

        Vector2 surfaceNormal = surfaceNormal(pos.x, pos.y, vel);
        if (surfaceNormal == null) {
            return null;
        }

        Vector2 lastSurfaceNormal = surfaceNormal;
        Vector2 velProjection = vel.cpy().nor().scl(-1f);

        while (surfaceNormal != null) {
            if (surfaceNormal.x == 0 && surfaceNormal.y == 0) {
                // Project by velocity if the object is too deep in the
                // collision map.
                pos.add(velProjection);
            } else {
                // Otherwise project by the surface normal.
                Vector2 surfaceProjection = surfaceNormal.cpy().scl(0.1f);
                pos.add(surfaceProjection);
            }
            lastSurfaceNormal = surfaceNormal;
            surfaceNormal = surfaceNormal(pos.x, pos.y, vel);
        }
        return lastSurfaceNormal;
    }

    /**
     * Destructively modifies the velocity to bounce according to the surface
     * normal.
     * 
     * @param vel
     *            Velocity of the object to project.
     * @param normal
     *            Surface normal to bounce off of.
     * @param f
     *            Amount of horizontal velocity to keep (0 to 1).
     * @param b
     *            Amount of vertical velocity to keep (0 to 1).
     */
    public void bounce(Vector2 vel, Vector2 normal, float f, float b) {

        // Vertical component of the bounce velocity.
        Vector2 vert = normal.cpy().scl(-b * vel.dot(normal));
        // Make up for any surface normal errors and flip the vertical portion
        // of the velocity so that it is going in the same direction as the
        // normal.
        if (vert.dot(normal) < 0) {
            vert.scl(-1f);
        }

        // Horizontal component of the bounce velocity.
        Vector2 horz = new Vector2(normal.y, -normal.x);
        horz.scl(f * vel.dot(horz));

        // Combine vertical and horizontal components.
        vert.add(horz);

        // Update the velocity.
        vel.set(vert);
    }

    /**
     * Returns the surface normal of the surface at (levelX, levelY), or null if
     * there is no surface.
     * 
     * Level coordinate system - origin at bottom left. Pixmap coordinate system
     * - origin at top left.
     */
    public Vector2 surfaceNormal(float levelX, float levelY) {
        // Convert to pixmap coordinates.
        int py = mapHeight - (int) levelY;
        int collisionPixel = collisionPixmap.getPixel((int) levelX, py);
        if ((collisionPixel & SOLID_MASK) == 0) {
            return null;
        }
        int snx = ((collisionPixel >> SNX_SHIFT) & SN_MASK) - VECTOR_OFFSET;
        int sny = ((collisionPixel >> SNY_SHIFT) & SN_MASK) - VECTOR_OFFSET;
        return new Vector2(snx, sny).nor();
    }

    /**
     * Ensures that the surface normal makes sense with respect to the velocity.
     * 
     * See {@link #surfaceNormal(float, float)}.
     * 
     * @param vel
     *            velocity of the object.
     */
    public Vector2 surfaceNormal(float levelX, float levelY, Vector2 vel) {
        Vector2 n = surfaceNormal(levelX, levelY);
        if (n == null) {
            return n;
        }
        // Surface normal must be going against velocity.
        float cos = n.dot(vel) / (n.len() * vel.len());
        // Errors in computing the surface normal can sometimes result in
        // collisions
        // where the surface normal is not against velocity.
        // The acceptable error is around 0.35, or 20 degrees.
        if (cos > 0.35f) {
            // If surface normal is not going against velocity, just flip it.
            n.scl(-1);
        }
        return n;
    }

    /**
     * Creates a hole in the collision map.
     * 
     * Precondition: Pixmap.setBlending(Blending.None);
     */
    public void addHole(float levelX, float levelY, int radius) {
        // Convert to pixmap coordinates.
        int px = (int) levelX;
        int py = mapHeight - (int) levelY;
        collisionPixmap.setColor(EMPTY_PIXEL);
        collisionPixmap.fillCircle(px, py, radius);

        // Update projection normals.
        for (int dx = -radius - 2; dx <= radius + 2; dx++) {
            for (int dy = -radius - 2; dy <= radius + 2; dy++) {
                int px2 = px + dx;
                int py2 = py + dy;
                int ly = mapHeight - py2;
                Vector2 surfaceNormal = surfaceNormal(px2, ly);
                // Make sure not to override any existing surface normals.
                // Only set a new surface normal if the previous one was (0, 0).
                if (surfaceNormal != null && surfaceNormal.x == 0 && surfaceNormal.y == 0
                        && isEdge(px2, py2)) {
                    int color = encodeSolidPixelData(-dx, dy);
                    collisionPixmap.drawPixel(px2, py2, color);
                }
            }
        }
    }

    @Override
    public void dispose() {
        collisionPixmap.dispose();
    }

    /**
     * Returns true iff one of the 8 surrounding pixels is not solid.
     */
    private boolean isEdge(int px, int py) {
        return (collisionPixmap.getPixel(px - 1, py - 1) & SOLID_MASK) == 0
                || (collisionPixmap.getPixel(px - 1, py) & SOLID_MASK) == 0
                || (collisionPixmap.getPixel(px - 1, py + 1) & SOLID_MASK) == 0
                || (collisionPixmap.getPixel(px, py - 1) & SOLID_MASK) == 0
                || (collisionPixmap.getPixel(px, py + 1) & SOLID_MASK) == 0
                || (collisionPixmap.getPixel(px + 1, py - 1) & SOLID_MASK) == 0
                || (collisionPixmap.getPixel(px + 1, py) & SOLID_MASK) == 0
                || (collisionPixmap.getPixel(px + 1, py + 1) & SOLID_MASK) == 0;
    }
}
