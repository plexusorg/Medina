package dev.plex.medina.util.adapter;

import com.google.gson.*;
import dev.plex.medina.Medina;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ZonedDateTimeAdapter implements JsonSerializer<ZonedDateTime>, JsonDeserializer<ZonedDateTime>
{
    private static final String TIMEZONE = Medina.getPlugin().config.getString("timezone");

    @Override
    public JsonElement serialize(ZonedDateTime src, Type typeOfSrc, JsonSerializationContext context)
    {
        return new JsonPrimitive(src.toInstant().toEpochMilli());
    }

    @Override
    public ZonedDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        Instant instant = Instant.ofEpochMilli(json.getAsJsonPrimitive().getAsLong());
        return ZonedDateTime.ofInstant(instant, ZoneId.of(TIMEZONE));
    }
}
