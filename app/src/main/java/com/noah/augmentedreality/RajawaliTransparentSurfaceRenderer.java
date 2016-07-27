package com.noah.augmentedreality;

import android.content.Context;
import android.graphics.Color;

import java.io.ObjectInputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import rajawali.BaseObject3D;
import rajawali.SerializedObject3D;
import rajawali.animation.Animation3D;
import rajawali.lights.DirectionalLight;
import rajawali.materials.DiffuseMaterial;
import rajawali.renderer.RajawaliRenderer;

public class RajawaliTransparentSurfaceRenderer extends RajawaliRenderer {
    private Context c;
    private Animation3D mAnim;
    public BaseObject3D m2;

    public RajawaliTransparentSurfaceRenderer(Context context) {
		super(context);
        c = context;
		setFrameRate(60);
	}

	protected void initScene() {
		DirectionalLight light = new DirectionalLight(0, 0, -1);
		light.setPower(1);
		mCamera.setPosition(0, 0, 0);
        mCamera.setRotation(90,-270,0);
		
		try {
			ObjectInputStream ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.monkey_ser));
			SerializedObject3D serializedMonkey = (SerializedObject3D) ois.readObject();
			ois.close();

			BaseObject3D monkey = new BaseObject3D(serializedMonkey);
			DiffuseMaterial material = new DiffuseMaterial();
			material.setUseColor(true);
			monkey.setMaterial(material);
			monkey.addLight(light);
			monkey.setColor(0xffff8C00);
			monkey.setScale(2);
            monkey.setPosition(0,0,-16);
			addChild(monkey);

			BaseObject3D m = monkey.clone();
            m.setColor(Color.BLUE);
			m.setPosition(0,-16,0);
			addChild(m);

            m2 = monkey.clone();
            m2.setColor(Color.CYAN);
            m2.setPosition(15,0,0);
            addChild(m2);

            BaseObject3D m3 = monkey.clone();
            m3.setColor(Color.YELLOW);
            m3.setPosition(15,0,-10);
            addChild(m3);

//			mAnim = new RotateAnimation3D(Axis.Y, 360);
//			mAnim.setDuration(6000);
//			mAnim.setRepeatCount(Animation3D.INFINITE);
//			mAnim.setInterpolator(new AccelerateDecelerateInterpolator());
//			mAnim.setTransformable3D(monkey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// -- set the background color to be transparent
		//    you need to have called setGLBackgroundTransparent(true); in the activity
		//    for this to work.
		setBackgroundColor(0);
	}
	
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		super.onSurfaceCreated(gl, config);
		((CameraActivity) mContext).hideLoader();
//		mAnim.start();
	}


}
