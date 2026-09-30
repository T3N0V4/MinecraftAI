package com.jano.minecraftai.client;

public final class MinecraftAIAnimation {

    public enum Curve {
        LINEAR,
        EASE_OUT_CUBIC,
        EASE_IN_OUT_CUBIC,
        EASE_OUT_QUINT,
        EASE_OUT_BACK
    }


    public static final class Tween {

        private double from;
        private double to;

        private long startTimeNanos;
        private long durationNanos;

        private Curve curve =
                Curve.EASE_OUT_CUBIC;

        private boolean running =
                false;


        public Tween() {
        }


        public Tween(
                double initialValue
        ) {

            this.from =
                    initialValue;

            this.to =
                    initialValue;
        }


        public void start(
                double from,
                double to,
                long durationMs
        ) {

            start(
                    from,
                    to,
                    durationMs,
                    Curve.EASE_OUT_CUBIC
            );
        }


        public void start(
                double from,
                double to,
                long durationMs,
                Curve curve
        ) {

            this.from =
                    from;

            this.to =
                    to;

            this.durationNanos =
                    Math.max(
                            1L,
                            durationMs
                    )
                            * 1_000_000L;

            this.curve =
                    curve == null
                            ? Curve.EASE_OUT_CUBIC
                            : curve;

            this.startTimeNanos =
                    System.nanoTime();

            this.running =
                    true;
        }


        public double get() {

            if (
                    !running
            ) {
                return to;
            }

            double progress =
                    (
                            System.nanoTime()
                                    - startTimeNanos
                    )
                            / (double) durationNanos;

            if (
                    progress >= 1.0
            ) {

                running =
                        false;

                return to;
            }

            progress =
                    clamp01(
                            progress
                    );

            double eased =
                    ease(
                            progress,
                            curve
                    );

            return lerp(
                    from,
                    to,
                    eased
            );
        }


        public float getFloat() {

            return (float) get();
        }


        public int getInt() {

            return (int) Math.round(
                    get()
            );
        }


        public boolean isRunning() {

            get();

            return running;
        }


        public void snap(
                double value
        ) {

            from =
                    value;

            to =
                    value;

            running =
                    false;
        }


        public double getTarget() {

            return to;
        }
    }


    public static double ease(
            double progress,
            Curve curve
    ) {

        double t =
                clamp01(
                        progress
                );

        return switch (
                curve
        ) {

            case LINEAR ->
                    t;

            case EASE_OUT_CUBIC ->
                    1.0
                            - Math.pow(
                                    1.0 - t,
                                    3.0
                            );

            case EASE_IN_OUT_CUBIC ->
                    t < 0.5
                            ? 4.0
                                    * t
                                    * t
                                    * t
                            : 1.0
                                    - Math.pow(
                                            -2.0 * t
                                                    + 2.0,
                                            3.0
                                    )
                                    / 2.0;

            case EASE_OUT_QUINT ->
                    1.0
                            - Math.pow(
                                    1.0 - t,
                                    5.0
                            );

            case EASE_OUT_BACK -> {

                double c1 =
                        1.70158;

                double c3 =
                        c1 + 1.0;

                yield 1.0
                        + c3
                                * Math.pow(
                                        t - 1.0,
                                        3.0
                                )
                        + c1
                                * Math.pow(
                                        t - 1.0,
                                        2.0
                                );
            }
        };
    }


    public static double lerp(
            double from,
            double to,
            double progress
    ) {

        return from
                + (
                        to - from
                )
                * progress;
    }


    public static float lerp(
            float from,
            float to,
            float progress
    ) {

        return from
                + (
                        to - from
                )
                * progress;
    }


    public static int lerpInt(
            int from,
            int to,
            double progress
    ) {

        return (int) Math.round(
                lerp(
                        from,
                        to,
                        progress
                )
        );
    }


    public static double clamp01(
            double value
    ) {

        return Math.max(
                0.0,
                Math.min(
                        1.0,
                        value
                )
        );
    }


    public static int alpha(
            int color,
            double alpha
    ) {

        int a =
                (int) Math.round(
                        clamp01(
                                alpha
                        )
                                * 255.0
                );

        return (
                color
                        & 0x00FFFFFF
        )
                | (
                        a << 24
                );
    }


    private MinecraftAIAnimation() {
    }
}