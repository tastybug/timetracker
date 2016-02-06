package com.tastybug.timetracker.model.rounding;

public class RoundingFactory {

    public enum Strategy {
        NO_ROUNDING(new NoRounding()),
        FULL_MINUTE_DOWN(new FullMinuteDown()),
        FULL_MINUTE_UP(new FullMinuteUp()),
        TEN_MINUTES_UP(null),
        THIRTY_MINUTES_UP(null),
        SIXTY_MINUTES_UP(null);

        private RoundingStrategy strategy;

        Strategy(RoundingStrategy strategy) {
            this.strategy = strategy;
        }

        public RoundingStrategy getStrategy() {
            return strategy;
        }
    }

    public RoundingFactory() {}

    public RoundingStrategy getStrategy(Strategy strategy) {
        return strategy.getStrategy();
    }
}
