package com.noah.augmentedreality;

/**
 * Created by HuyLV-CT on 8/1/2016.
 */
public class Vec {
    public float x, y, z;

    public Vec() {
        x = y = z = 0;
    }

    public Vec(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void worldToDevice() {
        float newX = -y;
        float newY = z;
        float newZ = -x;
        x = newX;
        y = newY;
        z = newZ;
    }

    public void deviceToWorld() {
        float newX = -z;
        float newY = -x;
        float newZ = y;
        x = newX;
        y = newY;
        z = newZ;
    }

}
