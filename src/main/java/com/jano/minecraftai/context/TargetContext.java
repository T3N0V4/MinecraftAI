package com.jano.minecraftai.context;

import java.util.LinkedHashMap;
import java.util.Map;

public class TargetContext {

    public String type;

    public String id;
    public String name;

    public Integer x;
    public Integer y;
    public Integer z;

    public double distance;

    // Para bloques
    public Map<String, String> properties =
            new LinkedHashMap<>();

    // Para entidades
    public Float health;
    public Float maxHealth;

    public TargetContext(
            String type,
            String id,
            String name,
            Integer x,
            Integer y,
            Integer z,
            double distance
    ) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.distance = distance;
    }

    public static TargetContext none() {

        return new TargetContext(
                "none",
                null,
                null,
                null,
                null,
                null,
                0
        );
    }

    @Override
    public String toString() {

        if ("none".equals(type)) {
            return "Nada";
        }

        StringBuilder text =
                new StringBuilder();

        text.append(name)
                .append(" (")
                .append(id)
                .append(")");

        text.append(" [")
                .append(type)
                .append("]");

        text.append(" a ")
                .append(
                        String.format(
                                "%.1f",
                                distance
                        )
                )
                .append(" bloques");

        if (x != null) {
            text.append(" en ")
                    .append(x)
                    .append(", ")
                    .append(y)
                    .append(", ")
                    .append(z);
        }

        if (!properties.isEmpty()) {
            text.append(" ")
                    .append(properties);
        }

        if (health != null) {
            text.append(" vida ")
                    .append(health)
                    .append("/")
                    .append(maxHealth);
        }

        return text.toString();
    }
}