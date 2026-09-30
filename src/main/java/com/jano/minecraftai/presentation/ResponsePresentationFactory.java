package com.jano.minecraftai.presentation;

import java.util.List;

public final class ResponsePresentationFactory {

    private ResponsePresentationFactory() {
    }

    public static ResponsePresentation block(
            String name,
            String id,
            String mod,
            int x,
            int y,
            int z
    ) {
        String voiceText =
                "Es " + name + " de " + mod + ".";

        String displayText =
                name + "\n" +
                "ID: " + id + "\n" +
                "Mod: " + mod + "\n" +
                "Posición: " + x + ", " + y + ", " + z;

        return new ResponsePresentation(
                voiceText,
                displayText,
                PresentationType.BLOCK_INFO
        );
    }


    public static ResponsePresentation entity(
            String name,
            String id,
            double distance,
            boolean hostile,
            double health,
            double maxHealth
    ) {
        String voiceText;

        if (hostile) {
            voiceText =
                    "Es un " + name +
                    " hostil a " +
                    formatNumber(distance) +
                    " bloques.";
        } else {
            voiceText =
                    "Es un " + name +
                    " a " +
                    formatNumber(distance) +
                    " bloques.";
        }

        String displayText =
                name + "\n" +
                "ID: " + id + "\n" +
                "Distancia: " + formatNumber(distance) + " bloques\n" +
                "Hostil: " + (hostile ? "Sí" : "No") + "\n" +
                "Vida: " +
                formatNumber(health) +
                "/" +
                formatNumber(maxHealth);

        return new ResponsePresentation(
                voiceText,
                displayText,
                PresentationType.ENTITY_INFO
        );
    }


    public static ResponsePresentation inventory(
            List<String> items
    ) {
        int count = items == null ? 0 : items.size();

        String voiceText;

        if (count == 0) {
            voiceText = "Tu inventario está vacío.";
        } else if (count == 1) {
            voiceText = "Tenés un objeto en el inventario.";
        } else {
            voiceText =
                    "Tenés " +
                    count +
                    " objetos distintos en el inventario.";
        }

        StringBuilder display = new StringBuilder();
        display.append("Inventario");

        if (count == 0) {
            display.append("\nVacío");
        } else {
            for (String item : items) {
                display.append("\n• ").append(item);
            }
        }

        return new ResponsePresentation(
                voiceText,
                display.toString(),
                PresentationType.INVENTORY
        );
    }


    public static ResponsePresentation recipe(
            String result,
            List<String> ingredients
    ) {
        int count =
                ingredients == null
                        ? 0
                        : ingredients.size();

        String voiceText =
                "La receta de " +
                result +
                " usa " +
                count +
                (count == 1
                        ? " ingrediente."
                        : " ingredientes.");

        StringBuilder display = new StringBuilder();
        display.append("Receta: ").append(result);

        if (count == 0) {
            display.append("\nSin ingredientes disponibles.");
        } else {
            display.append("\nIngredientes:");

            for (String ingredient : ingredients) {
                display
                        .append("\n• ")
                        .append(ingredient);
            }
        }

        return new ResponsePresentation(
                voiceText,
                display.toString(),
                PresentationType.RECIPE
        );
    }


    private static String formatNumber(double value) {
        if (value == Math.rint(value)) {
            return Long.toString((long) value);
        }

        return String.format(
                java.util.Locale.US,
                "%.1f",
                value
        );
    }
}