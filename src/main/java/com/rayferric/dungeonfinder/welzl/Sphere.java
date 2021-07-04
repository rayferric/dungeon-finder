package com.rayferric.dungeonfinder.welzl;

import java.util.Objects;

public class Sphere {
    public Sphere(Vector3f origin, float radius) {
        this.origin = origin;
        this.radius = radius;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sphere other = (Sphere)o;
        return Objects.equals(origin, other.origin) && radius == other.radius;
    }

    @Override
    public int hashCode() {
        return Objects.hash(origin, radius);
    }

    @Override
    public String toString() {
        return String.format("Sphere{origin=%s, radius=%s}", origin, radius);
    }

    public boolean contains(Vector3f point) {
        return origin.distance(point) <= radius;
    }

    public boolean containsAll(Vector3f[] points) {
        for (Vector3f point : points) {
            if (!contains(point)) return false;
        }
        return true;
    }

    public Vector3f getOrigin() {
        return origin;
    }

    public void setOrigin(Vector3f origin) {
        this.origin = origin;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    private Vector3f origin;
    private float radius;
}
