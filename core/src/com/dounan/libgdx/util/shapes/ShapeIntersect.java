package com.dounan.libgdx.util.shapes;

import com.badlogic.gdx.math.Intersector;

public class ShapeIntersect {

    public static boolean hit(Shape a, Shape b) {
        if (a == null || b == null) {
            return false;
        }
        if (a instanceof PointShape) {
            PointShape p = (PointShape) a;
            // point-point
            if (b instanceof PointShape) {
                return hitPointPoint(p, (PointShape) b);
            }
            // point-circle
            if (b instanceof CircleShape) {
                return hitPointCircle(p, (CircleShape) b);
            }
            // point-rectangle
            if (b instanceof RectangleShape) {
                return hitPointRectangle(p, ((RectangleShape) b));
            }
            // point-polygon
            if (b instanceof PolygonShape) {
                return hitPointPolygon(p, (PolygonShape) b);
            }
        }
        if (a instanceof CircleShape) {
            CircleShape c = (CircleShape) a;
            // circle-point
            if (b instanceof PointShape) {
                return hitPointCircle((PointShape) b, c);
            }
            // circle-circle
            if (b instanceof CircleShape) {
                return Intersector.overlaps(c.circle, ((CircleShape) b).circle);
            }
            // circle-rectangle
            if (b instanceof RectangleShape) {
                return Intersector.overlaps(c.circle, ((RectangleShape) b).rectangle);
            }
            // circle-polygon
            if (b instanceof PolygonShape) {
                return hitCirclePolygon(c, (PolygonShape) b);
            }
        }
        if (a instanceof RectangleShape) {
            RectangleShape r = (RectangleShape) a;
            // rectangle-point
            if (b instanceof PointShape) {
                return hitPointRectangle((PointShape) b, r);
            }
            // rectangle-circle
            if (b instanceof CircleShape) {
                return Intersector.overlaps(((CircleShape) b).circle, r.rectangle);
            }
            // rectangle-rectangle
            if (b instanceof RectangleShape) {
                return Intersector.overlaps(r.rectangle, ((RectangleShape) b).rectangle);
            }
            // rectangle-polygon
            if (b instanceof PolygonShape) {
                return hitRectanglePolygon(r, (PolygonShape) b);
            }
        }
        if (a instanceof PolygonShape) {
            PolygonShape poly = (PolygonShape) a;
            // polygon-point
            if (b instanceof PointShape) {
                return hitPointPolygon((PointShape) b, poly);
            }
            // polygon-circle
            if (b instanceof CircleShape) {
                return hitCirclePolygon((CircleShape) b, poly);
            }
            // polygon-rectangle
            if (b instanceof RectangleShape) {
                return hitRectanglePolygon((RectangleShape) b, poly);
            }
            // polygon-polygon
            if (b instanceof PolygonShape) {
                return Intersector.overlapConvexPolygons(poly.polygon, ((PolygonShape) b).polygon);
            }
        }
        return false;
    }

    public static boolean hitPointPoint(PointShape p1, PointShape p2) {
        return p1.x == p2.x && p1.y == p2.y;
    }

    public static boolean hitPointCircle(PointShape p, CircleShape c) {
        float dx = p.x - c.circle.x;
        float dy = p.y - c.circle.y;
        float r = c.circle.radius;
        return dx * dx + dy * dy < r * r;
    }

    public static boolean hitPointRectangle(PointShape p, RectangleShape r) {
        return p.x >= r.rectangle.x && p.x <= r.rectangle.x + r.rectangle.width
                && p.y >= r.rectangle.y && p.y <= r.rectangle.y + r.rectangle.height;
    }

    public static boolean hitPointPolygon(PointShape p, PolygonShape poly) {
        return poly.polygon.contains(p.x, p.y);
    }

    public static boolean hitCirclePolygon(CircleShape c, PolygonShape poly) {
        throw new UnsupportedOperationException("Cannot intersect circle and polygon");
    }

    public static boolean hitRectanglePolygon(RectangleShape r, PolygonShape poly) {
        throw new UnsupportedOperationException("Cannot intersect rectangle and polygon");
    }
}
