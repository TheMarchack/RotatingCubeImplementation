package com.example.rotatingcubeimplementation;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class OpenGLView extends GLSurfaceView {

    // Use this to show variable in bottom textView:
    // MainActivity.getInstance().setText(String.valueOf(variable));

    // Variables for touch interaction
    private float touchX = 0;
    private float touchY = 0;
    private float lastTouchDistance;
    private float touchDir;
    private float sizeCoef = 1;
    private boolean ignoreOnce = false; // Ignore movement measurement once after releasing second finger
    private boolean movementDetected = false; // Don't calculate touch coordinates if movement detected before

    OpenGLRenderer renderer;

    public OpenGLView(Context context) {
        super(context);
        init();
    }

    public OpenGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setEGLContextClientVersion(2);
        setPreserveEGLContextOnPause(true);
//        prepareMap();
        setRenderer(renderer = new OpenGLRenderer( this));
    }

//    private void prepareMap() {
//        Map img = new Map();
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int points = event.getPointerCount();
        final int action = event.getAction();
        float touchDistance;
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: { // One finger down
                touchX = event.getX();
                touchY = event.getY();
            }
            break;
            case MotionEvent.ACTION_POINTER_DOWN: { // Other finger down
                movementDetected = true;
                touchDistance = getTouchedDistance(event);
                lastTouchDistance = touchDistance;
            }
            break;
            case MotionEvent.ACTION_MOVE: { // Finger(s) move
                movementDetected = true;
                if (points == 1) {
                    // Calculate movement
                    if (ignoreOnce) {
                        ignoreOnce = false;
                    } else {
                        renderer.xMovement = (touchX - event.getX()) / 5f * sizeCoef;
                        renderer.yMovement = (touchY - event.getY()) / 5f * sizeCoef;
                    }
                    // Get new reading
                    touchX = event.getX(0);
                    touchY = event.getY(0);
                } else if (points == 2) {
                    touchDistance = getTouchedDistance(event);
                    if (touchDistance < lastTouchDistance) {
                        touchDir = 1;
                    } else if (touchDistance >= lastTouchDistance) {
                        touchDir = -1;
                    }
                    sizeCoef = Math.max(0.25f, Math.min(1f, sizeCoef + 0.03f * touchDir));
                    renderer.calculateProjection(renderer.viewportWidth, renderer.viewportHeight, sizeCoef);
                    lastTouchDistance = touchDistance;
                    // update firstTouch coordinates
                    touchX = event.getX(0);
                    touchY = event.getY(0);
                }
            }
            break;
            case MotionEvent.ACTION_POINTER_UP: { // Other finger up
                ignoreOnce = true;
            }
            break;
            case MotionEvent.ACTION_UP: {
                if (!movementDetected) { // Last finger up
                    try {
                        float[] intersect = intersectionPoint(castRay(touchX, touchY), renderer.eye, renderer.radius);

//                        MainActivity.getInstance().setText("Point: " + String.valueOf(Math.round(intersect[0] * 100) / 100f) +
//                                                                ", " + String.valueOf(Math.round(intersect[1] * 100) / 100f) +
//                                                                ", " + String.valueOf(Math.round(intersect[2] * 100) / 100f));

                        float[] polar = getPolar(intersect[0], intersect[1], intersect[2]);

                        float angleY = -renderer.yAngle;
                        float angleX = (renderer.xAngle % 360);

                        float angleYrad = (float) (angleY * Math.PI / 180);
                        float angleXrad = (float) (angleX * Math.PI / 180);

                        polar[0] += angleXrad;
                        polar[1] -= angleYrad;

//                        MainActivity.getInstance().setText("X: " + String.valueOf(Math.round(angleXrad * 100) / 100f) +
//                                ", Y: " + String.valueOf(Math.round(angleYrad * 100) / 100f));
//
                        MainActivity.getInstance().setText("p, t = " + String.valueOf(Math.round(polar[0] * 100) / 100f) +
                                                                ", " + String.valueOf(Math.round(polar[1] * 100) / 100f));

                        renderer.drawPointOnBitmap(polar[0], polar[1]);


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                movementDetected = false;
            }
            break;
            default:
                throw new IllegalStateException("Unexpected value: " + (action & MotionEvent.ACTION_MASK));
        }
        return true;
    }


    private float[] intersectionPoint(float[] vector, float[] origin, float radius) {
        float[] intersection = new float[3];
        float a, b, c, t;

        a = vector[0] * vector[0] + vector[1] * vector[1] + vector[2] * vector[2];
        b = origin[2] * vector[2] * 2;
        c = origin[2]*origin[2] - radius*radius;

        t = minRoot(a, b, c);
        intersection[0] = origin[0] + vector[0] * t;
        intersection[1] = origin[1] + vector[1] * t;
        intersection[2] = origin[2] + vector[2] * t;
        return intersection;
    }


    private float minRoot(float a, float b, float c) {
        float root1, root2, sqrtD;

        sqrtD = (float) Math.sqrt((b*b) - (4*a*c));
        root1 = (-b + sqrtD) / (2*a);
        root2 = (-b - sqrtD) / (2*a);

        return Math.min(root1, root2);
    }


    private float[] getPolar(float x, float y, float z) {
        float[] polarCoords = new float[2];
        float h = (float) Math.sqrt(x*x + z*z);
        float r = renderer.radius;
        polarCoords[0] = (float) Math.asin(x/h);
        polarCoords[1] = (float) Math.asin(y/r);
        return polarCoords;
    }


    private float[] castRay(float posX, float posY) throws InterruptedException {
        float[] pointPosition = new float[4];
        pointPosition[0] = (2.0f * posX) / renderer.viewportWidth - 1.0f;
        pointPosition[1] = (2.0f * posY) / renderer.viewportHeight - 1.0f;
        pointPosition[2] = -1.0f;
        pointPosition[3] = 1.0f;
        float[] touchRay = multiplyMat4ByVec4(renderer.mInverseProjectionMatrix, pointPosition);
        touchRay[2] = -1.0f;
        touchRay[3] = 0.0f;
        return multiplyMat4ByVec4(renderer.mInverseViewMatrix, touchRay);
    }


    private float[] multiplyMat4ByVec4(float[] matrix4, float[] vector4) {
        float[] returnMatrix = new float[4];
        returnMatrix[0] = (matrix4[0] * vector4[0]) + (matrix4[1] * vector4[1]) + (matrix4[2] * vector4[2]) + (matrix4[3] * vector4[3]);
        returnMatrix[1] = (matrix4[4] * vector4[0]) + (matrix4[5] * vector4[1]) + (matrix4[6] * vector4[2]) + (matrix4[7] * vector4[3]);
        returnMatrix[2] = (matrix4[8] * vector4[0]) + (matrix4[9] * vector4[1]) + (matrix4[10] * vector4[2]) + (matrix4[11] * vector4[3]);
        returnMatrix[3] = (matrix4[12] * vector4[0]) + (matrix4[13] * vector4[1]) + (matrix4[14] * vector4[2]) + (matrix4[15] * vector4[3]);
        return returnMatrix;
    }


    private float getTouchedDistance(MotionEvent event) {
        float x1 = event.getX(0);
        float y1 = event.getY(0);
        float x2 = event.getX(1);
        float y2 = event.getY(1);
        return (float) Math.sqrt(Math.pow(Math.abs(x2 - x1), 2) + Math.pow(Math.abs(y2 - y1), 2));
    }
}
