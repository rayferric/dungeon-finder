package com.rayferric.dungeonfinder.welzl;

public class Mathf {
    public static float sqrt(float x) {
        return (float)Math.sqrt(x);
    }

    public static float pow(float x, float y) {
        return (float)Math.pow(x, y);
    }

    public static float sin(float x) {
        return (float)Math.sin(Math.toRadians(x));
    }

    public static float cos(float x) {
        return (float)Math.cos(Math.toRadians(x));
    }

    public static float tan(float x) {
        return (float)Math.tan(Math.toRadians(x));
    }

    public static float asin(float x) {
        return (float)Math.toDegrees(Math.asin(x));
    }

    public static float acos(float x) {
        return (float)Math.toDegrees(Math.acos(x));
    }

    public static float atan(float x) {
        return (float)Math.toDegrees(Math.atan(x));
    }

    public static float atan2(float x, float y) {
        return (float)Math.toDegrees(Math.atan2(x, y));
    }

    public static float min(float x, float y) {
        return Math.min(x, y);
    }

    public static float max(float x, float y) {
        return Math.max(x, y);
    }

    public static float clamp(float x, float min, float max) {
        return Math.min(Math.max(x, min), max);
    }

    public static int floor(float x) {
        return (int)Math.floor(x);
    }

    public static int ceil(float x) {
        return (int)Math.ceil(x);
    }

    public static int round(float x) {
        return Math.round(x);
    }

    public static float lerp(float x, float y, float weight) {
        return x + (y - x) * clamp(weight, 0, 1);
    }
}
