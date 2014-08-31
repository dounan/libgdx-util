package com.dounan.libgdx.util;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Box2dWorldSerializer {

    public static final String SERIALIZATION_VERSION = "0.1";

    private static final Array<Body> BODIES = new Array<Body>();

    public static void write(String name, World world, Json json) {
        json.writeObjectStart(name);
        json.writeValue("serializationVersion", SERIALIZATION_VERSION);
        json.writeArrayStart("bodies");
        world.getBodies(BODIES);
        for (Body body : BODIES) {
            writeBody(body, json);
        }
        json.writeArrayEnd();
        // TODO(dounanshi): write joints
        json.writeObjectEnd();
    }

    public static void read(World world, Json json, JsonValue jsonData) {
        for (JsonValue bd = jsonData.get("bodies").child(); bd != null; bd = bd.next()) {
            readBody(world, json, bd);
        }
        // TODO(dounanshi): read joints
    }

    private static void writeBody(Body body, Json json) {
        Vector2 bodyPos = body.getPosition();
        Vector2 bodyLinVel = body.getLinearVelocity();
        json.writeObjectStart();
        json.writeValue("type", body.getType());
        json.writeValue("x", bodyPos.x);
        json.writeValue("y", bodyPos.y);
        json.writeValue("angle", body.getAngle());
        json.writeValue("vx", bodyLinVel.x);
        json.writeValue("vy", bodyLinVel.y);
        json.writeValue("angularVelocity", body.getAngularVelocity());
        json.writeValue("linearDamping", body.getLinearDamping());
        json.writeValue("angularDamping", body.getAngularDamping());
        json.writeValue("allowSleep", body.isSleepingAllowed());
        json.writeValue("awake", body.isAwake());
        json.writeValue("fixedRotation", body.isFixedRotation());
        json.writeValue("bullet", body.isBullet());
        json.writeValue("active", body.isActive());
        json.writeValue("gravityScale", body.getGravityScale());
        json.writeArrayStart("fixtures");
        for (Fixture fixture : body.getFixtureList()) {
            json.writeObjectStart();
            writeFixture(fixture, json);
            json.writeObjectEnd();
        }
        json.writeArrayEnd();
        json.writeValue("massData", body.getMassData());
        // Use null knownType to write the class for proper deserialization.
        json.writeValue("userData", body.getUserData(), null);
        json.writeObjectEnd();
    }

    private static void readBody(World world, Json json, JsonValue jsonData) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = json.readValue(BodyType.class, jsonData.get("type"));
        bodyDef.position.x = json.readValue(Float.class, jsonData.get("x"));
        bodyDef.position.y = json.readValue(Float.class, jsonData.get("y"));
        bodyDef.angle = json.readValue(Float.class, jsonData.get("angle"));
        bodyDef.linearVelocity.x = json.readValue(Float.class, jsonData.get("vx"));
        bodyDef.linearVelocity.y = json.readValue(Float.class, jsonData.get("vy"));
        bodyDef.angularVelocity = json.readValue(Float.class, jsonData.get("angularVelocity"));
        bodyDef.linearDamping = json.readValue(Float.class, jsonData.get("linearDamping"));
        bodyDef.angularDamping = json.readValue(Float.class, jsonData.get("angularDamping"));
        bodyDef.allowSleep = json.readValue(Boolean.class, jsonData.get("allowSleep"));
        bodyDef.awake = json.readValue(Boolean.class, jsonData.get("awake"));
        bodyDef.fixedRotation = json.readValue(Boolean.class, jsonData.get("fixedRotation"));
        bodyDef.bullet = json.readValue(Boolean.class, jsonData.get("bullet"));
        bodyDef.active = json.readValue(Boolean.class, jsonData.get("active"));
        bodyDef.gravityScale = json.readValue(Float.class, jsonData.get("gravityScale"));
        Body body = world.createBody(bodyDef);
        for (JsonValue fd = jsonData.get("fixtures").child(); fd != null; fd = fd.next()) {
            readFixture(body, json, fd);
        }
        body.setMassData(json.readValue(MassData.class, jsonData.get("massData")));
        body.setUserData(json.readValue(Object.class, jsonData.get("userData")));
    }

    private static void writeFixture(Fixture fixture, Json json) {
        Filter filter = fixture.getFilterData();
        json.writeObjectStart("shape");
        writeShape(fixture.getShape(), json);
        json.writeObjectEnd();
        json.writeValue("friction", fixture.getFriction());
        json.writeValue("restitution", fixture.getRestitution());
        json.writeValue("density", fixture.getDensity());
        json.writeValue("sensor", fixture.isSensor());
        json.writeValue("categoryBits", filter.categoryBits);
        json.writeValue("maskBits", filter.maskBits);
        json.writeValue("groupIndex", filter.groupIndex);
    }

    private static void readFixture(Body body, Json json, JsonValue jsonData) {
        FixtureDef fixDef = new FixtureDef();
        fixDef.shape = readShape(json, jsonData.get("shape"));
        fixDef.friction = json.readValue(Float.class, jsonData.get("friction"));
        fixDef.restitution = json.readValue(Float.class, jsonData.get("restitution"));
        fixDef.density = json.readValue(Float.class, jsonData.get("density"));
        fixDef.isSensor = json.readValue(Boolean.class, jsonData.get("sensor"));
        fixDef.filter.categoryBits = json.readValue(Short.class, jsonData.get("categoryBits"));
        fixDef.filter.maskBits = json.readValue(Short.class, jsonData.get("maskBits"));
        fixDef.filter.groupIndex = json.readValue(Short.class, jsonData.get("groupIndex"));
        body.createFixture(fixDef);
        fixDef.shape.dispose();
    }

    private static void writeShape(Shape shape, Json json) {
        Vector2 tmpVec;
        Shape.Type type = shape.getType();
        json.writeValue("type", type);
        switch (type) {
            case Circle:
                CircleShape circleShape = (CircleShape) shape;
                json.writeValue("position", circleShape.getPosition());
                json.writeValue("radius", circleShape.getRadius());
                break;
            case Polygon:
                PolygonShape polygonShape = (PolygonShape) shape;
                json.writeArrayStart("vertices");
                tmpVec = new Vector2();
                for (int i = 0; i < polygonShape.getVertexCount(); i++) {
                    polygonShape.getVertex(i, tmpVec);
                    json.writeValue(tmpVec.x);
                    json.writeValue(tmpVec.y);
                }
                json.writeArrayEnd();
                break;
            case Edge:
                EdgeShape edgeShape = (EdgeShape) shape;
                tmpVec = new Vector2();
                edgeShape.getVertex1(tmpVec);
                json.writeValue("v1x", tmpVec.x);
                json.writeValue("v1y", tmpVec.y);
                edgeShape.getVertex2(tmpVec);
                json.writeValue("v2x", tmpVec.x);
                json.writeValue("v2y", tmpVec.y);
                break;
            case Chain:
                ChainShape chainShape = (ChainShape) shape;
                json.writeArrayStart("vertices");
                tmpVec = new Vector2();
                for (int i = 0; i < chainShape.getVertexCount(); i++) {
                    chainShape.getVertex(i, tmpVec);
                    json.writeValue(tmpVec.x);
                    json.writeValue(tmpVec.y);
                }
                json.writeArrayEnd();
                break;
            default:
                throw new UnsupportedOperationException("Cannot write shape of type " + type);
        }
    }

    private static Shape readShape(Json json, JsonValue jsonData) {
        Shape.Type type = json.readValue(Shape.Type.class, jsonData.get("type"));
        switch (type) {
            case Circle:
                CircleShape circleShape = new CircleShape();
                circleShape.setPosition(json.readValue(Vector2.class, jsonData.get("position")));
                circleShape.setRadius(json.readValue(Float.class, jsonData.get("radius")));
                return circleShape;
            case Polygon:
                PolygonShape polygonShape = new PolygonShape();
                polygonShape.set(json.readValue(float[].class, jsonData.get("vertices")));
                return polygonShape;
            case Edge:
                EdgeShape edgeShape = new EdgeShape();
                edgeShape.set(json.readValue(Float.class, jsonData.get("v1x")),
                        json.readValue(Float.class, jsonData.get("v1y")),
                        json.readValue(Float.class, jsonData.get("v2x")),
                        json.readValue(Float.class, jsonData.get("v2y")));
                return edgeShape;
            case Chain:
                ChainShape chainShape = new ChainShape();
                chainShape.createChain(json.readValue(float[].class, jsonData.get("vertices")));
                return chainShape;
            default:
                throw new UnsupportedOperationException("Cannot read shape of type " + type);
        }
    }
}
