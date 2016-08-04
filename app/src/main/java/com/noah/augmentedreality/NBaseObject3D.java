package com.noah.augmentedreality;

import rajawali.BaseObject3D;
import rajawali.SerializedObject3D;

/**
 * Created by HuyLV-CT on 8/1/2016.
 */
public class NBaseObject3D extends BaseObject3D {

    public NBaseObject3D(SerializedObject3D serializedMonkey) {
        super(serializedMonkey);
    }

    public NBaseObject3D() {
        super();
    }

    public void setPosition(float x, float y, float z) {
//        super.setPosition(-y, z, -x);
        super.setPosition(x, y, z);
    }

    public void setPositionByW(Vec v) {
        v.worldToDevice();
        super.setPosition(v.x, v.y, v.z);
    }

    public Vec getWPosition() {
        Vec devicePos = new Vec(getX(), getY(), getZ());
        devicePos.deviceToWorld();
        return devicePos;
    }

    public NBaseObject3D clone() {
        NBaseObject3D clone = new NBaseObject3D();
        cloneTo(clone, true);
        clone.setRotation(getRotation());
        clone.setScale(getScale());
        return clone;
    }
}
