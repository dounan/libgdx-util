package com.dounanshi.libgdx.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class ActorSerializer {

    public static void write(String name, Actor actor, Json json) {
        json.writeObjectStart(name);
        json.writeValue("x", actor.getX());
        json.writeValue("y", actor.getY());
        json.writeValue("width", actor.getWidth());
        json.writeValue("height", actor.getHeight());
        json.writeValue("originX", actor.getOriginX());
        json.writeValue("originY", actor.getOriginY());
        json.writeValue("scaleX", actor.getScaleX());
        json.writeValue("scaleY", actor.getScaleY());
        json.writeValue("rotation", actor.getRotation());
        json.writeValue("color", actor.getColor());
        json.writeObjectEnd();
    }

    public static void read(Actor actor, Json json, JsonValue jsonData) {
        float x = json.readValue(Float.class, jsonData.get("x"));
        float y = json.readValue(Float.class, jsonData.get("y"));
        actor.setPosition(x, y);
        float w = json.readValue(Float.class, jsonData.get("width"));
        float h = json.readValue(Float.class, jsonData.get("height"));
        actor.setSize(w, h);
        float originX = json.readValue(Float.class, jsonData.get("originX"));
        float originY = json.readValue(Float.class, jsonData.get("originY"));
        actor.setOrigin(originX, originY);
        float scaleX = json.readValue(Float.class, jsonData.get("scaleX"));
        float scaleY = json.readValue(Float.class, jsonData.get("scaleY"));
        actor.setScale(scaleX, scaleY);
        float rotation = json.readValue(Float.class, jsonData.get("rotation"));
        actor.setRotation(rotation);
        Color color = json.readValue(Color.class, jsonData.get("color"));
        actor.setColor(color);
    }
}
