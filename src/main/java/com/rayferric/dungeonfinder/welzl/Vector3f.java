package com.rayferric.dungeonfinder.welzl;

import java.util.Objects;

public class Vector3f {
    public static final int BYTES = 12;

    public static final Vector3f ZERO = new Vector3f(0);
    public static final Vector3f LEFT = new Vector3f(-1, 0, 0);
    public static final Vector3f RIGHT = new Vector3f(1, 0, 0);
    public static final Vector3f DOWN = new Vector3f(0, -1, 0);
    public static final Vector3f UP = new Vector3f(0, 1, 0);
    public static final Vector3f FORWARD = new Vector3f(0, 0, -1);
    public static final Vector3f BACKWARD = new Vector3f(0, 0, 1);

    public Vector3f() {
        x = y = z = 0;
    }

    public Vector3f(float all) {
        x = y = z = all;
    }

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3f(double x, double y, double z) {
        this.x = (float)x;
        this.y = (float)y;
        this.z = (float)z;
    }

    public Vector3f(Vector3f other) {
        x = other.x;
        y = other.y;
        z = other.z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector3f other = (Vector3f)o;
        return x == other.x && y == other.y && z == other.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return String.format("Vector3f{x=%s, y=%s, z=%s}", x, y, z);
    }

    public float[] toArray() {
        return new float[] { x, y, z };
    }

    public Vector3f add(Vector3f rhs) {
        return new Vector3f(x + rhs.x, y + rhs.y, z + rhs.z);
    }

    public Vector3f sub(Vector3f rhs) {
        return new Vector3f(x - rhs.x, y - rhs.y, z - rhs.z);
    }

    public Vector3f mul(Vector3f rhs) {
        return new Vector3f(x * rhs.x, y * rhs.y, z * rhs.z);
    }

    public Vector3f mul(float rhs) {
        return new Vector3f(x * rhs, y * rhs, z * rhs);
    }

    public Vector3f div(Vector3f rhs) {
        return new Vector3f(x / rhs.x, y / rhs.y, z / rhs.z);
    }

    public Vector3f div(float rhs) {
        return new Vector3f(x / rhs, y / rhs, z / rhs);
    }

    public float dot(Vector3f rhs) {
        return x * rhs.x + y * rhs.y + z * rhs.z;
    }

    public Vector3f cross(Vector3f rhs) {
        return new Vector3f(
                (y * rhs.z) - (rhs.y * z),
                (z * rhs.x) - (rhs.z * x),
                (x * rhs.y) - (rhs.x * y)
        );
    }

    public Vector3f lerp(Vector3f rhs, float weight) {
        return new Vector3f(
                Mathf.lerp(x, rhs.x, weight),
                Mathf.lerp(y, rhs.y, weight),
                Mathf.lerp(z, rhs.z, weight)
        );
    }

    public float length() {
        return Mathf.sqrt(dot(this));
    }

    public float distance(Vector3f rhs) {
        return sub(rhs).length();
    }

    public Vector3f normalize() {
        float length = length();
        return length == 0 ? new Vector3f(0) : this.div(length);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    private float x, y, z;
}
