package com.tastybug.timetracker.model.rounding;

public class RoundingFactory {

    public enum Strategy {
        NO_ROUNDING(new NoRounding()),
        FULL_MINUTE_DOWN(new FullMinuteDown()),
        FULL_MINUTE_UP(XMinutesUp.fullMinutesUp()),
        TEN_MINUTES_UP(XMinutesUp.tenMinutesUp()),
        THIRTY_MINUTES_UP(XMinutesUp.thirtyMinutesUp()),
        SIXTY_MINUTES_UP(XMinutesUp.fullHoursUp());

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
