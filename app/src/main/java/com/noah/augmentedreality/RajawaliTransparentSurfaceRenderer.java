package com.noah.augmentedreality;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLU;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import rajawali.BaseObject3D;
import rajawali.SerializedObject3D;
import rajawali.animation.Animation3D;
import rajawali.lights.DirectionalLight;
import rajawali.materials.DiffuseMaterial;
import rajawali.math.Number3D;
import rajawali.math.Quaternion;
import rajawali.parser.AParser;
import rajawali.parser.ObjParser;
import rajawali.renderer.RajawaliRenderer;
import rajawali.util.ObjectColorPicker;
import rajawali.util.OnObjectPickedListener;

public class RajawaliTransparentSurfaceRenderer extends RajawaliRenderer implements OnObjectPickedListener {
	public NBaseObject3D m1;
	public NBaseObject3D m2;
	public NBaseObject3D selectedObject;
	float[] Re = new float[16];
	private Context c;
    private Animation3D mAnim;
	private Quaternion q;
	private String selectedObjectName;
	private ObjectColorPicker mPicker;

	public RajawaliTransparentSurfaceRenderer(Context context) {
		super(context);
        c = context;
		setFrameRate(60);
	}

	protected void initScene() {
		DirectionalLight light = new DirectionalLight(0, 0, -1);
		light.setPower(1);
		Vec cameraPos = new Vec(-16, 0, 16);
		cameraPos.worldToDevice();
		mCamera.setPosition(cameraPos.x, cameraPos.y, cameraPos.z);
		mPicker = new ObjectColorPicker(this);
		mPicker.setOnObjectPickedListener(this);
		mCamera.setFarPlane(1000);

		try {
			ObjectInputStream ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.monkey_ser));
			SerializedObject3D serializedMonkey = (SerializedObject3D) ois.readObject();
			ois.close();

			NBaseObject3D monkey = new NBaseObject3D(serializedMonkey);
			DiffuseMaterial material = new DiffuseMaterial();
			material.setUseColor(true);
			monkey.setMaterial(material);
			monkey.addLight(light);
			monkey.setColor(Color.RED);
			monkey.setName("red");
			monkey.setPositionByW(new Vec(0, 0, -20));
			addChild(monkey);

			m1 = monkey.clone();
			m1.setColor(Color.BLUE);
			m1.setName("blue");
			m1.setPositionByW(new Vec(0, -20, 0));
			addChild(m1);

			m2 = monkey.clone();
			m2.setColor(Color.CYAN);
			m2.setName("cyan");
			m2.setPositionByW(new Vec(10, 0, 0));
			addChild(m2);

			NBaseObject3D m3 = monkey.clone();
			m3.setColor(Color.YELLOW);
			m3.setPositionByW(new Vec(20, 0, -10));
			m3.setName("yellow");
			addChild(m3);

			ObjParser objParser = new ObjParser(mContext.getResources(), mTextureManager, R.raw.axis_obj);
			objParser.parse();
			BaseObject3D mObject = objParser.getParsedObject();
			mObject.addLight(light);
			mObject.setColor(Color.RED);
			mObject.setPosition(0, 0, 0);
			addChild(mObject);

			mPicker.registerObject(monkey);
			mPicker.registerObject(m1);
			mPicker.registerObject(m2);
			mPicker.registerObject(m3);

		} catch (AParser.ParsingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		//for transparent
		setBackgroundColor(0);
	}

	@Override
	public void onDrawFrame(GL10 glUnused) {
		super.onDrawFrame(glUnused);

		((CameraActivity) mContext).headTracker.getLastHeadView(Re, 0);
//		android.opengl.Matrix.invertM(Re,0,Re,0);
//		q = getQuaternion(Re);
		((CameraActivity) mContext).runOnUiThread(new Runnable() {
			@Override
			public void run() {
//					((CameraActivity)mContext).orientationText.setText("xx:"+(float) Math.toDegrees(q.x)+"\n"+(float) Math.toDegrees(q.y)+"\n"+(float)Math.toDegrees(q.z));
				StringBuffer b = new StringBuffer();
				b.append(round3(Re[0]) + " " + round3(Re[1]) + " " + round3(Re[2]) + " " + round3(Re[3]) + "\n");
				b.append(round3(Re[4]) + " " + round3(Re[5]) + " " + round3(Re[6]) + " " + round3(Re[7]) + "\n");
				b.append(round3(Re[8]) + " " + round3(Re[9]) + " " + round3(Re[10]) + " " + round3(Re[11]) + "\n");
				b.append(round3(Re[12]) + " " + round3(Re[13]) + " " + round3(Re[14]) + " " + round3(Re[15]) + "\n");

				if (selectedObject != null) {
					Number3D c = selectedObject.getPosition();
					b.append(c.x + " " + c.y + " " + c.z + "\n");
					Vec v = selectedObject.getWPosition();
					b.append(v.x + " " + v.y + " " + v.z + "\n");
				}
				((CameraActivity) mContext).orientationText.setText(b.toString());
			}
		});
//		mCamera.setOrientation(new Quaternion(mCamera.getOrientation().x+0.1f,mCamera.getOrientation().y,mCamera.getOrientation().z,0));
//		mCamera.setRotation(-(float) Math.toDegrees(q.x)*2, -(float) Math.toDegrees(q.y)*2,-(float)Math.toDegrees(q.z)*2);

		mCamera.setRotationMatrix(Re);
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		super.onSurfaceCreated(gl, config);
		((CameraActivity) mContext).hideLoader();
	}


	@Override
	public void onObjectPicked(BaseObject3D object) {
		selectedObject = (NBaseObject3D) object;
		Log.e("cxz", "select:" + selectedObject.getName());
	}

	public void getObjectAt(float x, float y) {
		mPicker.getObjectAt(x, y);
	}

	public void moveObject(float x, float y) {
		if (selectedObject != null) {
			float zz = selectedObject.getWPosition().z;
			float yy = selectedObject.getWPosition().y;
			float CSF = Re[5] * 90;
			float[] n = getNewPosition(x, y, CSF);
			Log.e("cxz", "move_" + n[0] + " " + yy);
			selectedObject.setPositionByW(new Vec(n[0], n[1], 0));

//			Number3D n = GetWorldCoords(x,y);
//			Log.e("cxz","xxx"+n.x+" "+n.y+" "+n.z);
//			selectedObject.setPosition(n.x,n.y,0);
		}
	}


	private float[] getNewPosition(float x, float y, float CSF) {
		float X = 0, Y = 0;
		float MSO = (float) Math.atan(((y - Constants.PQ / 2)
				* Math.tan(Math.toRadians(Utils.ASE / 2))
				* 2) / Constants.PQ);
		float DF = (float) (Constants.CAMERA_HEIGHT * (Math.tan(Math.toRadians(CSF) - MSO)));
		Vec cameraPos = new Vec(mCamera.getX(), mCamera.getY(), mCamera.getZ());
		cameraPos.deviceToWorld();
		DF += cameraPos.x - 30;
		Log.e("cxz", "camera" + cameraPos.x);
		return new float[]{DF, Y};
	}

	public Number3D GetWorldCoords(float x, float y) {
		int[] viewport = new int[]{0, 0, mViewportWidth, mViewportHeight};
		float[] nearPos = new float[4];
		float[] farPos = new float[4];

		GLU.gluUnProject(x, mViewportHeight - y, 0,
				mCamera.getViewMatrix(), 0,
				mCamera.getProjectionMatrix(), 0,
				viewport, 0, nearPos, 0);
		GLU.gluUnProject(x, mViewportHeight - y, 1.f,
				mCamera.getViewMatrix(), 0,
				mCamera.getProjectionMatrix(), 0,
				viewport, 0, farPos, 0);

		Vector3 nearVec = new Vector3(nearPos[0] / nearPos[3], nearPos[1] / nearPos[3], nearPos[2] / nearPos[3]);
		Vector3 farVec = new Vector3(farPos[0] / farPos[3], farPos[1] / farPos[3], farPos[2] / farPos[3]);

		float factor = (float) ((Math.abs(selectedObject.getZ()) + nearVec.z) / (mCamera.getFarPlane() - mCamera.getNearPlane()));

		Vector3 diff = Vector3.subtract(farVec, nearVec);
		diff.multiply(factor);
		nearVec.add(diff);


		return new Number3D(nearVec.x, nearVec.y, 0);

	}


	float round3(float n) {
		n = Math.round(n * 1000);
		return n / 1000;
	}

	private Quaternion getQuaternion(float[] r) {
		float[] m = r;
		float t = m[0] + m[5] + m[10];
		float x;
		float y;
		float z;
		float w;
		float s;
		if (t >= 0.0F) {
			s = (float) Math.sqrt(t + 1.0F);
			w = 0.5F * s;
			s = 0.5F / s;
			x = (m[9] - m[6]) * s;
			y = (m[2] - m[8]) * s;
			z = (m[4] - m[1]) * s;
		} else {
			if ((m[0] > m[5]) && (m[0] > m[10])) {
				s = (float) Math.sqrt(1.0F + m[0] - m[5] - m[10]);
				x = s * 0.5F;
				s = 0.5F / s;
				y = (m[4] + m[1]) * s;
				z = (m[2] + m[8]) * s;
				w = (m[9] - m[6]) * s;
			} else {
				if (m[5] > m[10]) {
					s = (float) Math.sqrt(1.0F + m[5] - m[0] - m[10]);
					y = s * 0.5F;
					s = 0.5F / s;
					x = (m[4] + m[1]) * s;
					z = (m[9] + m[6]) * s;
					w = (m[2] - m[8]) * s;
				} else {
					s = (float) Math.sqrt(1.0F + m[10] - m[0] - m[5]);
					z = s * 0.5F;
					s = 0.5F / s;
					x = (m[2] + m[8]) * s;
					y = (m[9] + m[6]) * s;
					w = (m[4] - m[1]) * s;
				}
			}
		}
		return new Quaternion(w, x, y, z);
	}



}

