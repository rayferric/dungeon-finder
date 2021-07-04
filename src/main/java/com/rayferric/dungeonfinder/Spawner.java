package com.rayferric.dungeonfinder;

import com.conversantmedia.util.collection.geometry.Point3d;
import com.conversantmedia.util.collection.geometry.Rect3d;
import com.conversantmedia.util.collection.spatial.HyperPoint;
import com.conversantmedia.util.collection.spatial.HyperRect;
import com.conversantmedia.util.collection.spatial.RectBuilder;
import com.rayferric.dungeonfinder.util.MobType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Spawner {
    public static final class Builder implements RectBuilder<Spawner> {
        @Override
        public HyperRect<?> getBBox(@NotNull Spawner spawner) {
            double x = spawner.pos.getCoord(0);
            double y = spawner.pos.getCoord(1);
            double z = spawner.pos.getCoord(2);
            return new Rect3d(x, y, z, x, y, z);
        }

        @Override
        public HyperRect<?> getMbr(@NotNull HyperPoint p1, @NotNull HyperPoint p2) {
            double x1 = p1.getCoord(0);
            double y1 = p1.getCoord(1);
            double z1 = p1.getCoord(2);
            double x2 = p2.getCoord(0);
            double y2 = p2.getCoord(1);
            double z2 = p2.getCoord(2);
            return new Rect3d(x1, y1, z1, x2, y2, z2);
        }
    }

    public Spawner(@NotNull Point3d pos, @NotNull MobType type) {
        this.pos = pos;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Spawner spawner = (Spawner)o;
        return Objects.equals(pos, spawner.pos) &&
                type == spawner.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pos, type);
    }

    public Point3d getPos() {
        return pos;
    }

    public MobType getType() {
        return type;
    }

    private final Point3d pos;
    private final MobType type;
}
