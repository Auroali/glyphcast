package com.auroali.glyphcast.common.spells;

public record SpellStats(double efficiency, int cooldown, double fireAffinity, double lightAffinity, double iceAffinity,
                         double earthAffinity) {
    public static final SpellStats PARCHMENT = new Builder().addEarthAffinity(0.25).addFireAffinity(0.25).addLightAffinity(0.25).addIceAffinity(0.25).addCooldown(40).addEfficiency(0.35).build();

    public double averageAffinity() {
        return (fireAffinity + lightAffinity + iceAffinity + earthAffinity) / 4;
    }

    public static class Builder {
        private double efficiency;
        private double fireAffinity;
        private double lightAffinity;
        private double iceAffinity;
        private double earthAffinity;
        private int cooldown;

        public Builder addIceAffinity(double iceAffinity) {
            this.iceAffinity += iceAffinity;
            return this;
        }

        public Builder addFireAffinity(double fireAffinity) {
            this.fireAffinity += fireAffinity;
            return this;
        }

        public Builder addLightAffinity(double lightAffinity) {
            this.lightAffinity += lightAffinity;
            return this;
        }

        public Builder addEarthAffinity(double earthAffinity) {
            this.earthAffinity += earthAffinity;
            return this;
        }

        public Builder addEfficiency(double efficiency) {
            this.efficiency += efficiency;
            return this;
        }

        public Builder addCooldown(int cooldown) {
            this.cooldown += cooldown;
            return this;
        }

        public SpellStats build() {
            return new SpellStats(efficiency, cooldown, fireAffinity, lightAffinity, iceAffinity, earthAffinity);
        }
    }
}
