package com.example.rotatingcubeimplementation;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Cube {

    /** How many bytes per float. */
    private final int mBytesPerFloat = 4;

    public final FloatBuffer objectVertex;
    public final FloatBuffer objectTexture;
    public final FloatBuffer objectColor;

    int mTriangles = 12;

    private final float[] Points = {
            // In OpenGL counter-clockwise winding is default. This means that when we look at a triangle,
            // if the points are counter-clockwise we are looking at the "front". If not we are looking at
            // the back. OpenGL has an optimization where all back-facing triangles are culled, since they
            // usually represent the backside of an object and aren't visible anyways.

            // Front face
            -1.0f, 1.0f, 1.0f,      -1.0f, -1.0f, 1.0f,     1.0f, 1.0f, 1.0f,       -1.0f, -1.0f, 1.0f,     1.0f, -1.0f, 1.0f,      1.0f, 1.0f, 1.0f,
            // Right face
            1.0f, 1.0f, 1.0f,       1.0f, -1.0f, 1.0f,      1.0f, 1.0f, -1.0f,      1.0f, -1.0f, 1.0f,      1.0f, -1.0f, -1.0f,     1.0f, 1.0f, -1.0f,
            // Back face
            1.0f, 1.0f, -1.0f,      1.0f, -1.0f, -1.0f,     -1.0f, 1.0f, -1.0f,     1.0f, -1.0f, -1.0f,     -1.0f, -1.0f, -1.0f,    -1.0f, 1.0f, -1.0f,
            // Left face
            -1.0f, 1.0f, -1.0f,     -1.0f, -1.0f, -1.0f,    -1.0f, 1.0f, 1.0f,      -1.0f, -1.0f, -1.0f,    -1.0f, -1.0f, 1.0f,     -1.0f, 1.0f, 1.0f,
            // Top face
            -1.0f, 1.0f, -1.0f,     -1.0f, 1.0f, 1.0f,      1.0f, 1.0f, -1.0f,      -1.0f, 1.0f, 1.0f,      1.0f, 1.0f, 1.0f,       1.0f, 1.0f, -1.0f,
            // Bottom face
            1.0f, -1.0f, -1.0f,     1.0f, -1.0f, 1.0f,      -1.0f, -1.0f, -1.0f,    1.0f, -1.0f, 1.0f,      -1.0f, -1.0f, 1.0f,     -1.0f, -1.0f, -1.0f,
    };

    // R, G, B, A
    private final float[] Colors = {
            // Front face (red)
//            1.0f, 0.0f, 0.0f, 1.0f,     1.0f, 0.0f, 0.0f, 1.0f,     1.0f, 0.0f, 0.0f, 1.0f,     1.0f, 0.0f, 0.0f, 1.0f,     1.0f, 0.0f, 0.0f, 1.0f,     1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,     1.0f, 1.0f, 1.0f, 1.0f,     1.0f, 1.0f, 1.0f, 1.0f,     1.0f, 1.0f, 1.0f, 1.0f,     1.0f, 1.0f, 1.0f, 1.0f,     1.0f, 1.0f, 1.0f, 1.0f,
            // Right face (green)
//            0.0f, 1.0f, 0.0f, 1.0f,     0.0f, 1.0f, 0.0f, 1.0f,     0.0f, 1.0f, 0.0f, 1.0f,     0.0f, 1.0f, 0.0f, 1.0f,     0.0f, 1.0f, 0.0f, 1.0f,     0.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,     1.0f, 1.0f, 1.0f, 1.0f,     1.0f, 1.0f, 1.0f, 1.0f,     1.0f, 1.0f, 1.0f, 1.0f,     1.0f, 1.0f, 1.0f, 1.0f,     1.0f, 1.0f, 1.0f, 1.0f,
            // Back face (blue)
//            0.0f, 0.0f, 1.0f, 1.0f,     0.0f, 0.0f, 1.0f, 1.0f,     0.0f, 0.0f, 1.0f, 1.0f,     0.0f, 0.0f, 1.0f, 1.0f,     0.0f, 0.0f, 1.0f, 1.0f,     0.0f, 0.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,     1.0f, 1.0f, 1.0f, 1.0f,     1.0f, 1.0f, 1.0f, 1.0f,     1.0f, 1.0f, 1.0f, 1.0f,     1.0f, 1.0f, 1.0f, 1.0f,     1.0f, 1.0f, 1.0f, 1.0f,
            // Left face (yellow)
//            1.0f, 1.0f, 0.0f, 1.0f,     1.0f, 1.0f, 0.0f, 1.0f,     1.0f, 1.0f, 0.0f, 1.0f,     1.0f, 1.0f, 0.0f, 1.0f,     1.0f, 1.0f, 0.0f, 1.0f,     1.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,     1.0f, 1.0f, 1.0f, 1.0f,     1.0f, 1.0f, 1.0f, 1.0f,     1.0f, 1.0f, 1.0f, 1.0f,     1.0f, 1.0f, 1.0f, 1.0f,     1.0f, 1.0f, 1.0f, 1.0f,
            // Top face (cyan)
//            0.0f, 1.0f, 1.0f, 1.0f,     0.0f, 1.0f, 1.0f, 1.0f,     0.0f, 1.0f, 1.0f, 1.0f,     0.0f, 1.0f, 1.0f, 1.0f,     0.0f, 1.0f, 1.0f, 1.0f,     0.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,     1.0f, 1.0f, 1.0f, 1.0f,     1.0f, 1.0f, 1.0f, 1.0f,     1.0f, 1.0f, 1.0f, 1.0f,     1.0f, 1.0f, 1.0f, 1.0f,     1.0f, 1.0f, 1.0f, 1.0f,
            // Bottom face (magenta)
//            1.0f, 0.0f, 1.0f, 1.0f,     1.0f, 0.0f, 1.0f, 1.0f,     1.0f, 0.0f, 1.0f, 1.0f,     1.0f, 0.0f, 1.0f, 1.0f,     1.0f, 0.0f, 1.0f, 1.0f,     1.0f, 0.0f, 1.0f, 1.0f
            1.0f, 1.0f, 1.0f, 1.0f,     1.0f, 1.0f, 1.0f, 1.0f,     1.0f, 1.0f, 1.0f, 1.0f,     1.0f, 1.0f, 1.0f, 1.0f,     1.0f, 1.0f, 1.0f, 1.0f,     1.0f, 1.0f, 1.0f, 1.0f
    };

    public final float[] Textures = {
            // Front face
            0.0f, 0.0f,     0.0f, 1.0f,     1.0f, 0.0f,     0.0f, 1.0f,     1.0f, 1.0f,     1.0f, 0.0f,
            // Right face
            0.0f, 0.0f,     0.0f, 1.0f,     1.0f, 0.0f,     0.0f, 1.0f,     1.0f, 1.0f,     1.0f, 0.0f,
            // Back face
            0.0f, 0.0f,     0.0f, 1.0f,     1.0f, 0.0f,     0.0f, 1.0f,     1.0f, 1.0f,     1.0f, 0.0f,
            // Left face
            0.0f, 0.0f,     0.0f, 1.0f,     1.0f, 0.0f,     0.0f, 1.0f,     1.0f, 1.0f,     1.0f, 0.0f,
            // Top face
            0.0f, 0.0f,     0.0f, 1.0f,     1.0f, 0.0f,     0.0f, 1.0f,     1.0f, 1.0f,     1.0f, 0.0f,
            // Bottom face
            0.0f, 0.0f,     0.0f, 1.0f,     1.0f, 0.0f,     0.0f, 1.0f,     1.0f, 1.0f,     1.0f, 0.0f
    };


    public Cube() {
        objectVertex = ByteBuffer.allocateDirect(Points.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        objectVertex.put(Points).position(0);

        objectTexture = ByteBuffer.allocateDirect(Textures.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        objectTexture.put(Textures).position(0);

        objectColor = ByteBuffer.allocateDirect(Colors.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        objectColor.put(Colors).position(0);
    }
}
