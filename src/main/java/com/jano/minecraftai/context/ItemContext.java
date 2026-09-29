package com.jano.minecraftai.context;

public class ItemContext {

    public String id;
    public String name;
    public int count;

    public int durability;
    public int maxDurability;

    public ItemContext(
            String id,
            String name,
            int count,
            int durability,
            int maxDurability
    ) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.durability = durability;
        this.maxDurability = maxDurability;
    }

    @Override
    public String toString() {

        if (id == null) {
            return "Vacío";
        }

        String text =
                name
                + " x"
                + count
                + " ("
                + id
                + ")";

        if (maxDurability > 0) {

            text +=
                    " durabilidad "
                    + durability
                    + "/"
                    + maxDurability;
        }

        return text;
    }
}