package com.rayferric.dungeonfinder;

import com.conversantmedia.util.collection.geometry.Point3d;

import java.util.Objects;

public class BlockPos implements Comparable<BlockPos> {
    public BlockPos() {
        x = y = z = 0;
    }

    public BlockPos(long x, long y, long z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BlockPos(double x, double y, double z) {
        this.x = (long)Math.floor(x);
        this.y = (long)Math.floor(y);
        this.z = (long)Math.floor(z);
    }

    public BlockPos(Point3d point) {
        x = (long)Math.floor(point.getCoord(0));
        y = (long)Math.floor(point.getCoord(1));
        z = (long)Math.floor(point.getCoord(2));
    }

    public BlockPos(BlockPos other) {
        x = other.x;
        y = other.y;
        z = other.z;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)return true;
        if(obj == null || getClass() != obj.getClass())return false;
        BlockPos other = (BlockPos)obj;
        return x == other.x && y == other.y && z == other.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return String.format("BlockPos{x=%d, y=%d, z=%d}", x, y, z);
    }

    @Override
    public int compareTo(BlockPos other) {
        return hashCode() - other.hashCode();
    }

    public BlockPos add(BlockPos other) {
        return new BlockPos(x + other.getX(), y + other.getY(), z + other.getZ());
    }

    public BlockPos sub(BlockPos other) {
        return new BlockPos(x - other.getX(), y - other.getY(), z - other.getZ());
    }

    public BlockPos div(long value) {
        return new BlockPos(x / value, y / value, z / value);
    }

    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public double distance(BlockPos other) {
        return sub(other).length();
    }

    public long getX() {
        return x;
    }

    public void setX(long x) {
        this.x = x;
    }

    public long getY() {
        return y;
    }

    public void setY(long y) {
        this.y = y;
    }

    public long getZ() {
        return z;
    }

    public void setZ(long z) {
        this.z = z;
    }

    private long x, y, z;
}