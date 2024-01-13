package de.shurablack.model.player;

import java.util.function.Consumer;

public class EqualizerSettings {

    private final Consumer<Float> preset;
    private final float percentage;

    public EqualizerSettings(Consumer<Float> preset, float percentage) {
        this.preset = preset;
        this.percentage = percentage;
    }

    public Consumer<Float> getPreset() {
        return preset;
    }

    public float getPercentage() {
        return percentage;
    }
}
