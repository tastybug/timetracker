package com.tastybug.timetracker.model.rounding;

import com.tastybug.timetracker.R;

public class RoundingFactory {

    public enum Strategy {
        NO_ROUNDING(new NoRounding(), R.string.rounding_no_rounding_label),
        FULL_MINUTE_DOWN(new FullMinuteDown(), R.string.rounding_minutes_down),
        FULL_MINUTE_UP(XMinutesUp.fullMinutesUp(), R.string.rounding_minutes_up),
        TEN_MINUTES_UP(XMinutesUp.tenMinutesUp(), R.string.rounding_ten_minutes_up),
        THIRTY_MINUTES_UP(XMinutesUp.thirtyMinutesUp(), R.string.rounding_thirty_minutes_up),
        SIXTY_MINUTES_UP(XMinutesUp.fullHoursUp(), R.string.rounding_sixty_minutes_up);

        private RoundingStrategy strategy;
        private int descriptionStringResource;

        Strategy(RoundingStrategy strategy, int descriptionStringResource) {
            this.strategy = strategy;
            this.descriptionStringResource = descriptionStringResource;
        }

        public RoundingStrategy getStrategy() {
            return strategy;
        }

        public int getDescriptionStringResource() {
            return descriptionStringResource;
        }
    }

    public RoundingFactory() {
    }

    public RoundingStrategy getStrategy(Strategy strategy) {
        return strategy.getStrategy();
    }
}
