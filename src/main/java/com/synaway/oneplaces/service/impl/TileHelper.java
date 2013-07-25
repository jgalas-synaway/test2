package com.synaway.oneplaces.service.impl;

/**
 * Helper class with methods to calculate a bounding box from (zoom, x, y).
 * 
 */
public class TileHelper {
    public static BoundingBox tile2boundingBox(final int x, final int y, final int zoom) {
        BoundingBox bb = new BoundingBox();
        bb.setNorth(tile2lat(y, zoom));
        bb.setSouth(tile2lat(y + 1, zoom));
        bb.setWest(tile2lon(x, zoom));
        bb.setEast(tile2lon(x + 1, zoom));
        return bb;
    }

    public static double tile2lon(int x, int z) {
        return x / Math.pow(2.0, z) * 360.0 - 180;
    }

    public static double tile2lat(int y, int z) {
        double n = Math.PI - (2.0 * Math.PI * y) / Math.pow(2.0, z);
        return Math.toDegrees(Math.atan(Math.sinh(n)));
    }
}
