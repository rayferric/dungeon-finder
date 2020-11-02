package com.rayferric.dungeonfinder.welzl;

public class Triangle implements Simplex {
    public Triangle(Vector3f a, Vector3f b, Vector3f c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public Triangle(Vector3f[] array) {
        a = array[0];
        b = array[1];
        c = array[2];
    }

    @Override
    public Sphere getBoundingSphere() {
        Vector3f ab = b.sub(a);
        Vector3f ac = c.sub(a);

        Vector3f abCrossAc = ab.cross(ac);

        float abDotAb = ab.dot(ab);
        float acDotAc = ac.dot(ac);

        Vector3f t1 = abCrossAc.cross(ab).mul(acDotAc);
        Vector3f t2 = ac.cross(abCrossAc).mul(abDotAb);

        float invDenom = 0.5F / abCrossAc.dot(abCrossAc);

        Vector3f toOrigin = t1.add(t2).mul(invDenom);

        Vector3f origin = a.add(toOrigin);
        float radius = toOrigin.length();

        return new Sphere(origin, radius);
    }

    public Vector3f getA() {
        return a;
    }

    public void setA(Vector3f a) {
        this.a = a;
    }

    public Vector3f getB() {
        return b;
    }

    public void setB(Vector3f b) {
        this.b = b;
    }

    public Vector3f getC() {
        return c;
    }

    public void setC(Vector3f c) {
        this.c = c;
    }

    private Vector3f a, b, c;
}
