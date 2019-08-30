package bge.igame.player;

import java.util.HashMap;

public class PlayerOptions {
    public final String optionName;
    public final CPOptionValues possibleValues;
    public final HashMap<String, PlayerOptions> subOptions = new HashMap<>();

    public PlayerOptions(String optionName, CPOptionValues possibleValues) {
        this.optionName = optionName;
        this.possibleValues = possibleValues;
    }

    public PlayerOptions addSubOption(String parentName, PlayerOptions option) {
        subOptions.put(parentName, option);
        return this;
    }

    public interface CPOptionValues {
        String getKey();
    }

    public static class CPOptionStringArray implements CPOptionValues {
        private final String key;
        public final String[] array;

        public CPOptionStringArray(String key, String[] array) {
            this.key = key;
            this.array = array;
        }

        @Override
        public String getKey() {
            return key;
        }
    }

    public static class CPOptionIntRange implements CPOptionValues {
        private final String key;
        public final int minValue;
        public final int maxValue;

        public CPOptionIntRange(String key, int minValue, int maxValue) {
            this.key = key;
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        @Override
        public String getKey() {
            return key;
        }
    }
}
