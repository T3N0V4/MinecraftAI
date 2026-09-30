package com.jano.minecraftai.tools;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ToolCache {

    private static class Entry {

        private final ToolResult result;
        private final long expiresAt;


        private Entry(
                ToolResult result,
                long expiresAt
        ) {

            this.result =
                    result;

            this.expiresAt =
                    expiresAt;
        }
    }


    private final Map<String, Entry> entries =
            new ConcurrentHashMap<>();


    public ToolResult get(
            String key
    ) {

        Entry entry =
                entries.get(
                        key
                );

        if (
                entry == null
        ) {

            return null;
        }


        long now =
                System.currentTimeMillis();


        if (
                now >= entry.expiresAt
        ) {

            entries.remove(
                    key
            );

            return null;
        }


        return entry.result;
    }


    public void put(
            String key,
            ToolResult result,
            long durationMs
    ) {

        if (
                key == null
                || result == null
                || durationMs <= 0
        ) {

            return;
        }


        entries.put(
                key,
                new Entry(
                        result,
                        System.currentTimeMillis()
                                + durationMs
                )
        );
    }


    public void clear() {

        entries.clear();
    }
}