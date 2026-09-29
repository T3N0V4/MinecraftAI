package com.jano.minecraftai;

import net.fabricmc.api.ModInitializer;

public class MinecraftAIMod implements ModInitializer {

    public static final String MOD_ID = "minecraftai";

    @Override
    public void onInitialize() {
        System.out.println("[MinecraftAI] Mod iniciado correctamente.");

        AICommand.register();
    }
}