package com.jano.minecraftai.client;

public final class MinecraftAIAnimation {

    public enum Curve {
        EASE_OUT_CUBIC,
        EASE_IN_OUT_CUBIC,
        EASE_OUT_QUINT
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


        public boolean isRunning() {

            get();

            return running;
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