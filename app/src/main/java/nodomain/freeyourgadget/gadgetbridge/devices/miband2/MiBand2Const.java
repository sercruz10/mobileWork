package nodomain.freeyourgadget.gadgetbridge.devices.miband2;

import nodomain.freeyourgadget.gadgetbridge.model.ActivityKind;

public class MiBand2Const {
    // observed the following values so far:
    // 00 01 02 09 0a 0b 0c 10 11

    // 0 = same activity kind as before
    // 1 = light activity walking?
    // 3 = definitely non-wear
    // 9 = probably light sleep, definitely some kind of sleep
    // 10 = ignore, except for hr (if valid)
    // 11 = probably deep sleep
    // 12 = definitely wake up
    // 17 = definitely not sleep related

    public static final int TYPE_UNSET = -1;
    public static final int TYPE_NO_CHANGE = 0;
    public static final int TYPE_ACTIVITY = 1;
    public static final int TYPE_RUNNING = 2;
    public static final int TYPE_NONWEAR = 3;
    public static final int TYPE_RIDE_BIKE = 4;
    public static final int TYPE_CHARGING = 6;
    public static final int TYPE_LIGHT_SLEEP = 9;
    public static final int TYPE_IGNORE = 10;
    public static final int TYPE_DEEP_SLEEP = 11;
    public static final int TYPE_WAKE_UP = 12;

    public static int toActivityKind(int rawType) {
        switch (rawType) {
            case TYPE_DEEP_SLEEP:
                return ActivityKind.TYPE_DEEP_SLEEP;
            case TYPE_LIGHT_SLEEP:
                return ActivityKind.TYPE_LIGHT_SLEEP;
            case TYPE_ACTIVITY:
            case TYPE_RUNNING:
            case TYPE_WAKE_UP:
                return ActivityKind.TYPE_ACTIVITY;
            case TYPE_NONWEAR:
                return ActivityKind.TYPE_NOT_WORN;
            case TYPE_CHARGING:
                return ActivityKind.TYPE_NOT_WORN; //I believe it's a safe assumption
            case TYPE_RIDE_BIKE:
                return ActivityKind.TYPE_CYCLING;
            default:
            case TYPE_UNSET: // fall through
                return ActivityKind.TYPE_UNKNOWN;
        }
    }

    public static int toRawActivityType(int activityKind) {
        switch (activityKind) {
            case ActivityKind.TYPE_ACTIVITY:
                return TYPE_ACTIVITY;
            case ActivityKind.TYPE_DEEP_SLEEP:
                return TYPE_DEEP_SLEEP;
            case ActivityKind.TYPE_LIGHT_SLEEP:
                return TYPE_LIGHT_SLEEP;
            case ActivityKind.TYPE_NOT_WORN:
                return TYPE_NONWEAR;
            case ActivityKind.TYPE_UNKNOWN: // fall through
            default:
                return TYPE_UNSET;
        }
    }

}
