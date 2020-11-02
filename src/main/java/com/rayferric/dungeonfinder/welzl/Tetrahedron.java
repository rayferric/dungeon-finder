package com.rayferric.dungeonfinder.welzl;

public class Tetrahedron implements Simplex {
    public Tetrahedron(Vector3f a, Vector3f b, Vector3f c, Vector3f d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public Tetrahedron(Vector3f[] array) {
        a = array[0];
        b = array[1];
        c = array[2];
        d = array[3];
    }

    @Override
    public Sphere getBoundingSphere() {
        Vector3f ab = b.sub(a);
        Vector3f ac = c.sub(a);
        Vector3f ad = d.sub(a);

        Vector3f acCrossAd = ac.cross(ad);
        Vector3f adCrossAb = ad.cross(ab);
        Vector3f abCrossAc = ab.cross(ac);

        float abDotAb = ab.dot(ab);
        float acDotAc = ac.dot(ac);
        float adDotAd = ad.dot(ad);

        Vector3f t1 = acCrossAd.mul(abDotAb);
        Vector3f t2 = adCrossAb.mul(acDotAc);
        Vector3f t3 = abCrossAc.mul(adDotAd);

        float invDenom = 0.5F / ab.dot(acCrossAd);

        Vector3f toOrigin = t1.add(t2).add(t3).mul(invDenom);

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

    public Vector3f getD() {
        return d;
    }

    public void setD(Vector3f d) {
        this.d = d;
    }

    private Vector3f a, b, c, d;
}
