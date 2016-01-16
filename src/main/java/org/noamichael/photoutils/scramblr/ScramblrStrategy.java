package org.noamichael.photoutils.scramblr;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author micha_000
 */
@FunctionalInterface
public interface ScramblrStrategy {

    void scramble(ScrambleContext scrambleContext);

    default List<StrategeySettingDescriptor> getSettingDescriptors() {
        return Collections.emptyList();
    }

    class StrategeySettingDescriptor {

        private String settingName;
        private SettingValueType settingValueType;

        public StrategeySettingDescriptor() {
        }

        public StrategeySettingDescriptor(String settingName, SettingValueType settingValueType) {
            this.settingName = settingName;
            this.settingValueType = settingValueType;
        }

        /**
         * @return the settingName
         */
        public String getSettingName() {
            return settingName;
        }

        /**
         * @param settingName the settingName to set
         */
        public void setSettingName(String settingName) {
            this.settingName = settingName;
        }

        /**
         * @return the settingValueType
         */
        public SettingValueType getSettingValueType() {
            return settingValueType;
        }

        /**
         * @param settingValueType the settingValueType to set
         */
        public void setSettingValueType(SettingValueType settingValueType) {
            this.settingValueType = settingValueType;
        }

        public enum SettingValueType {
            STRING, ENUM, NUMBER;
        }
    }
}
