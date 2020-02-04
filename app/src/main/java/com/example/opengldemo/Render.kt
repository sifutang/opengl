package com.example.opengldemo

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import com.example.opengldemo.utils.ShaderHelper
import com.example.opengldemo.utils.TextResourceReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class Render(context: Context) : GLSurfaceView.Renderer {

    companion object {
        private const val TAG = "Render"
        private const val A_POSITION = "aPosition"
        private const val A_COLOR = "aColor"
        private const val U_PROJECT_MATRIX = "uProjectMatrix"
        private const val U_VIEW_MATRIX = "uViewMatrix"
        private const val U_MODEL_MATRIX = "uModelMatrix"

        private val VERTEXT = floatArrayOf(
            -0.5f, -0.5f, 1.0f, 0.0f, 0.0f,
             0.5f, -0.5f, 0.0f, 1.0f, 0.0f,
             0.0f,  0.5f, 0.0f, 0.0f, 1.0f
        )
    }

    private var mContext: Context? = context
    private var mFloatBuffer: FloatBuffer? = null

    private var mModelMatrix = floatArrayOf(
        0F, 0F, 0F, 0F,
        0F, 0F, 0F, 0F,
        0F, 0F, 0F, 0F,
        0F, 0F, 0F, 0F
    )
    private var mViewMatrix = floatArrayOf(
        0F, 0F, 0F, 0F,
        0F, 0F, 0F, 0F,
        0F, 0F, 0F, 0F,
        0F, 0F, 0F, 0F
    )
    private var mProjectMatrix = floatArrayOf(
        0F, 0F, 0F, 0F,
        0F, 0F, 0F, 0F,
        0F, 0F, 0F, 0F,
        0F, 0F, 0F, 0F
    )

    private var mProgram = -1
    private var mAPositionLoc = -1
    private var mAColor = -1;
    private var mUProjectMatrixLoc = -1
    private var mUViewMatrixLoc = -1
    private var mUModelMatrixLoc = -1

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        GLES20.glUseProgram(mProgram)
        mFloatBuffer!!.position(0)
        GLES20.glEnableVertexAttribArray(mAPositionLoc)
        GLES20.glVertexAttribPointer(
            mAPositionLoc, 2, GLES20.GL_FLOAT, false, 20, mFloatBuffer
        )

        mFloatBuffer!!.position(2)
        GLES20.glEnableVertexAttribArray(mAColor)
        GLES20.glVertexAttribPointer(
            mAColor, 3, GLES20.GL_FLOAT, false, 20, mFloatBuffer
        )

        GLES20.glUniformMatrix4fv(mUProjectMatrixLoc, 1, false,  mProjectMatrix, 0)
        GLES20.glUniformMatrix4fv(mUViewMatrixLoc, 1, false, mViewMatrix, 0)
        GLES20.glUniformMatrix4fv(mUModelMatrixLoc, 1, false, mModelMatrix, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6)
        GLES20.glDisableVertexAttribArray(mAPositionLoc)
        GLES20.glDisableVertexAttribArray(mAColor)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        Log.d(TAG, "onSurfaceChanged: width = $width, height = $height")
        GLES20.glViewport(0, 0, width, height)

        Matrix.setIdentityM(mModelMatrix, 0)

        Matrix.setLookAtM(mViewMatrix, 0,
            0F, 0F, 7F,
            0F, 0F, 0F,
            0F, 1F, 0F)

        val ratio = 1F * width / height
        Matrix.frustumM(mProjectMatrix, 0,
            -ratio, ratio, -1F, 1F, 3F, 7F)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        Log.d(TAG, "onSurfaceCreated: ")
        mProgram = ShaderHelper.buildProgram(
            TextResourceReader.readTextFileFromResource(mContext!!, R.raw.vertext),
            TextResourceReader.readTextFileFromResource(mContext!!, R.raw.fragment)
        )
        mAPositionLoc = GLES20.glGetAttribLocation(mProgram, A_POSITION)
        mAColor = GLES20.glGetAttribLocation(mProgram, A_COLOR)
        mUProjectMatrixLoc = GLES20.glGetUniformLocation(mProgram, U_PROJECT_MATRIX)
        mUViewMatrixLoc = GLES20.glGetUniformLocation(mProgram, U_VIEW_MATRIX)
        mUModelMatrixLoc = GLES20.glGetUniformLocation(mProgram, U_MODEL_MATRIX)

        mFloatBuffer = ByteBuffer
            .allocateDirect(VERTEXT.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        mFloatBuffer?.put(VERTEXT)
    }
}