package com.urise.webapp.util;

import com.google.gson.*;

import java.lang.reflect.Type;

public class JsonSectionAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {

    private static final String CLASSNAME = "CLASSNAME";
    private static final String INSTANCE = "INSTANCE";

    @Override
    public T deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        JsonPrimitive primitive = (JsonPrimitive) object.get(CLASSNAME);
        String className = primitive.getAsString();

        try{
            Class clazz = Class.forName(className);
            return jsonDeserializationContext.deserialize(object.get(INSTANCE), clazz);
        }
        catch (ClassNotFoundException e){
            throw new JsonParseException(e.getMessage());
        }
    }

    @Override
    public JsonElement serialize(T t, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject retValue = new JsonObject();
        retValue.addProperty(CLASSNAME, t.getClass().getName());
        JsonElement element = jsonSerializationContext.serialize(t);
        retValue.add(INSTANCE, element);
        return retValue;
    }
}
