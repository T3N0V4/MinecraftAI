package com.jano.minecraftai.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import java.nio.file.Files;
import java.nio.file.Path;

public final class MinecraftAIClientConfig {

    private static final Gson GSON =
            new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

    private static final Path CONFIG_PATH =
            FabricLoader
                    .getInstance()
                    .getConfigDir()
                    .resolve(
                            "minecraftai-client.json"
                    );

    private static Data data =
            new Data();

    static {
        load();
    }


    public static boolean isVoiceEnabled() {
        return data.voiceEnabled;
    }


    public static void setVoiceEnabled(
            boolean enabled
    ) {

        data.voiceEnabled =
                enabled;

        save();
    }


    public static boolean toggleVoice() {

        setVoiceEnabled(
                !data.voiceEnabled
        );

        return data.voiceEnabled;
    }


    private static void load() {

        if (
                !Files.exists(
                        CONFIG_PATH
                )
        ) {

            save();

            return;
        }

        try (
                Reader reader =
                        Files.newBufferedReader(
                                CONFIG_PATH
                        )
        ) {

            Data loaded =
                    GSON.fromJson(
                            reader,
                            Data.class
                    );

            if (
                    loaded != null
            ) {

                data =
                        loaded;
            }

        } catch (
                Exception e
        ) {

            System.out.println(
                    "[MinecraftAI] No pude leer la config del cliente."
            );

            e.printStackTrace();
        }
    }


    private static void save() {

        try {

            Files.createDirectories(
                    CONFIG_PATH
                            .getParent()
            );

            try (
                    Writer writer =
                            Files.newBufferedWriter(
                                    CONFIG_PATH
                            )
            ) {

                GSON.toJson(
                        data,
                        writer
                );
            }

        } catch (
                IOException e
        ) {

            System.out.println(
                    "[MinecraftAI] No pude guardar la config del cliente."
            );

            e.printStackTrace();
        }
    }


    private static class Data {

        boolean voiceEnabled =
                true;
    }


    private MinecraftAIClientConfig() {
    }
}