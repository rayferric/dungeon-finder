package com.rayferric.dungeonfinder.welzl;

import java.util.ArrayList;
import java.util.List;

public class Welzl {
    /**
     * Finds smallest enclosing sphere of a point cloud using the Welzl's algorithm.<br>
     *
     * @param points point cloud to be processed
     *
     * @return the bounding sphere
     */
    public static Sphere run(List<Vector3f> points) {
        // Construct an empty support set and start recursive search within the point cloud:
        return search(points, new ArrayList<>());
    }

    /**
     * Executes a single iteration of the Welzl's algorithm.<br>
     *
     * @param points  remaining points
     * @param support boundary points
     *
     * @return the possible bounding sphere
     */
    private static Sphere search(List<Vector3f> points, List<Vector3f> support) {
        // If we've ran out of points or filled the support set, we can finally use the trivial method:
        if(points.size() == 0 || support.size() == 4)
            return trivial(support.toArray(new Vector3f[0]));

        // Pop a point and compute a bounding sphere without it.
        Vector3f point = points.remove(points.size() - 1);
        Sphere sphere = search(points, support);

        // If the popped point is inside, we can simply drop it:
        if(sphere.contains(point)) return sphere;

        // Otherwise, the popped point must lie on the boundary, therefore we add it to the support set:
        support.add(point);
        return search(points, support);
    }

    /**
     * Computes smallest enclosing sphere of a point cloud of size smaller than or equal to 4.<br>
     *
     * @param points point cloud to be processed
     *
     * @return the bounding sphere
     *
     * @throws IllegalArgumentException if the point cloud is too large for trivial condition
     */
    private static Sphere trivial(Vector3f[] points) {
        if(points.length > 4)
            throw new IllegalArgumentException("Point cloud size must be smaller than or equal to 4.");

        if(points.length <= 0) return new Sphere(new Vector3f(0), 0);

        if(points.length == 1) return new Sphere(points[0], 0);

        if(points.length == 2) return new Segment(points).getBoundingSphere();

        if(points.length == 3) {
            // Check if two points suffice:
            for(int i = 0; i < 3; i++) {
                for(int j = i + 1; j < 3; j++) {
                    Sphere sphere = new Segment(points[i], points[j]).getBoundingSphere();
                    if(sphere.containsAll(points)) return sphere;
                }
            }
            // They do not:
            return new Triangle(points).getBoundingSphere();
        }

        // Check if two points suffice:
        for(int i = 0; i < 4; i++) {
            for(int j = i + 1; j < 4; j++) {
                Sphere sphere = new Segment(points[i], points[j]).getBoundingSphere();
                if(sphere.containsAll(points)) return sphere;
            }
        }
        // Check if three points suffice:
        for(int i = 0; i < 4; i++) {
            for(int j = i + 1; j < 4; j++) {
                for(int k = j + 1; k < 4; k++) {
                    Sphere sphere = new Triangle(points[i], points[j], points[k]).getBoundingSphere();
                    if(sphere.containsAll(points)) return sphere;
                }
            }
        }
        // They do not:
        return new Tetrahedron(points).getBoundingSphere();
    }
}
