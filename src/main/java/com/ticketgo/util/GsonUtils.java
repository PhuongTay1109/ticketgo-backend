package com.ticketgo.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public final class GsonUtils {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private static final Gson GSON =
            new GsonBuilder()
                    .serializeNulls()
                    .registerTypeAdapter(
                            byte[].class,
                            new TypeAdapter<byte[]>() {
                                @Override
                                public void write(JsonWriter out, byte[] value) throws IOException {
                                    out.value(Base64.getEncoder().encodeToString(value));
                                }

                                @Override
                                public byte[] read(JsonReader in) throws IOException {
                                    return Base64.getDecoder().decode(in.nextString());
                                }
                            })
                    .registerTypeAdapter(
                            LocalDateTime.class,
                            new TypeAdapter<LocalDateTime>() {
                                @Override
                                public void write(JsonWriter out, LocalDateTime value) throws IOException {
                                    if (value == null) {
                                        out.nullValue();
                                    } else {
                                        out.value(value.format(DATE_TIME_FORMATTER));
                                    }
                                }

                                @Override
                                public LocalDateTime read(JsonReader in) throws IOException {
                                    if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                                        in.nextNull();
                                        return null;
                                    }
                                    return LocalDateTime.parse(in.nextString(), DATE_TIME_FORMATTER);
                                }
                            }
                    )
                    .create();

    public static <T> T fromJson(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

    public static <T> T fromJson(String json, Type type) {
        return GSON.fromJson(json, type);
    }

    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    private GsonUtils() {}
}