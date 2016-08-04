package com.noah.augmentedreality;

import rajawali.ATransformable3D;

/**
 * Created by HuyLV-CT on 8/3/2016.
 */
public abstract class NATransformable3D extends ATransformable3D {

    public Vec getNPosition() {
        Vec v = new Vec(getX(), getY(), getZ());
        v.worldToDevice();
        return v;
    }

    public void setNPosition(Vec v) {
        v.deviceToWorld();
        super.setPosition(v.x, v.y, v.z);
    }
}
