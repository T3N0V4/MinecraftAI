package com.jano.minecraftai.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import org.lwjgl.glfw.GLFW;

public final class MinecraftAIKeyUtil {

    public static int findFreeKey(
            MinecraftClient client,
            int[] candidates,
            KeyBinding ignoredBinding
    ) {

        if (
                client == null
                || client.options == null
        ) {
            return GLFW.GLFW_KEY_UNKNOWN;
        }

        for (
                int candidate :
                candidates
        ) {

            if (
                    !isKeyUsed(
                            client,
                            candidate,
                            ignoredBinding
                    )
            ) {
                return candidate;
            }
        }

        return GLFW.GLFW_KEY_UNKNOWN;
    }


    private static boolean isKeyUsed(
            MinecraftClient client,
            int keyCode,
            KeyBinding ignoredBinding
    ) {

        InputUtil.Key candidate =
                InputUtil.Type.KEYSYM
                        .createFromCode(
                                keyCode
                        );

        String candidateId =
                candidate
                        .getTranslationKey();

        for (
                KeyBinding binding :
                client.options.allKeys
        ) {

            if (
                    binding == ignoredBinding
                    || binding.isUnbound()
            ) {
                continue;
            }

            if (
                    candidateId.equals(
                            binding
                                    .getBoundKeyTranslationKey()
                    )
            ) {
                return true;
            }
        }

        return false;
    }


    private MinecraftAIKeyUtil() {
    }
}