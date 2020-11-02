package com.rayferric.dungeonfinder.welzl;

public class Segment implements Simplex {
    public Segment(Vector3f from, Vector3f to) {
        this.from = from;
        this.to = to;
    }

    public Segment(Vector3f[] array) {
        from = array[0];
        to = array[1];
    }

    @Override
    public Sphere getBoundingSphere() {
        Vector3f origin = from.add(to).mul(0.5F);
        float radius = origin.distance(from);
        return new Sphere(origin, radius);
    }

    public Vector3f getFrom() {
        return from;
    }

    public void setFrom(Vector3f from) {
        this.from = from;
    }

    public Vector3f getTo() {
        return to;
    }

    public void setTo(Vector3f to) {
        this.to = to;
    }

    private Vector3f from, to;
}
