package com.example.opengldemo

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import com.example.opengldemo.utils.ShaderHelper
import com.example.opengldemo.utils.TextResourceReader
import com.example.opengldemo.utils.Util
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class YUVRender(context: Context) : GLSurfaceView.Renderer {

    companion object {
        private const val TAG = "YUVRender"

        private val VERTEXT = floatArrayOf(
            -1f, -1f, 0.0f, 1.0f,
             1f, -1f, 1.0f, 1.0f,
            -1f,  1f, 0.0f, 0.0f,
             1f,  1f, 1.0f, 0.0f
        )
    }

    private var context: Context? = context

    private var program = -1

    private var positionLoc = -1
    private var texCoordLoc = -1
    private var yTextureLoc = -1
    private var uvTextureLoc = -1
    private var matrixLoc = -1

    private var vertexBuffer: FloatBuffer? = null

    private var matrix = FloatArray(16)

    private val imageWidth = 800
    private val imageHeight = 480
    private var yTextureId = -1
    private var uvTextureId = -1
    private lateinit var imageBytes: ByteArray
    private lateinit var yBuffer: ByteBuffer
    private lateinit var uvBuffer: ByteBuffer

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        GLES20.glUseProgram(program)
        vertexBuffer!!.position(0)
        GLES20.glEnableVertexAttribArray(positionLoc)
        GLES20.glVertexAttribPointer(
            positionLoc, 2, GLES20.GL_FLOAT, false, 16, vertexBuffer
        )

        vertexBuffer!!.position(2)
        GLES20.glEnableVertexAttribArray(texCoordLoc)
        GLES20.glVertexAttribPointer(
            texCoordLoc, 2, GLES20.GL_FLOAT, false, 16, vertexBuffer
        )

        GLES20.glUniformMatrix4fv(matrixLoc, 1, false, matrix, 0)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, yTextureId)
        GLES20.glUniform1i(yTextureLoc, 0)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, uvTextureId)
        GLES20.glUniform1i(uvTextureLoc, 1)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        GLES20.glDisableVertexAttribArray(positionLoc)
        GLES20.glDisableVertexAttribArray(texCoordLoc)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        Log.d(TAG, "onSurfaceChanged: width = $width, height = $height")
        GLES20.glViewport(0, 0, width, height)

        Matrix.setIdentityM(matrix, 0)
        val sx = 1f * imageWidth / width
        val sy = 1f * imageHeight / height
        Matrix.scaleM(matrix, 0, sx, sy, 1f)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        Log.d(TAG, "onSurfaceCreated: ")
        program = ShaderHelper.buildProgram(
            TextResourceReader.readTextFileFromResource(context!!, R.raw.vertex_yuv),
            TextResourceReader.readTextFileFromResource(context!!, R.raw.fragment_yuv)
        )
        positionLoc = GLES20.glGetAttribLocation(program, "aPosition")
        texCoordLoc = GLES20.glGetAttribLocation(program, "aTexCoord")
        yTextureLoc = GLES20.glGetUniformLocation(program, "yTexture")
        uvTextureLoc = GLES20.glGetUniformLocation(program, "uvTexture")
        matrixLoc = GLES20.glGetUniformLocation(program, "matrix")

        vertexBuffer = ByteBuffer
            .allocateDirect(VERTEXT.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexBuffer?.put(VERTEXT)

        // read image data
        imageBytes = Util.read("test.yuv", context!!)
        Log.d(TAG, "onSurfaceCreated: image bytes length = ${imageBytes.size}")

        yBuffer = ByteBuffer.allocateDirect(imageWidth * imageHeight)
            .order(ByteOrder.nativeOrder())
        yBuffer.put(imageBytes, 0, imageWidth * imageHeight)
        yBuffer.position(0)

        uvBuffer = ByteBuffer.allocateDirect(imageWidth * imageHeight / 2)
            .order(ByteOrder.nativeOrder())
        uvBuffer.put(imageBytes, imageWidth * imageHeight, imageWidth * imageHeight / 2)
        uvBuffer.position(0)

        val textureObjectIds = IntArray(2)
        GLES20.glGenTextures(2, textureObjectIds, 0)

        // y texture
        yTextureId = textureObjectIds[0]
        textureLuminance(yBuffer, imageWidth, imageHeight, yTextureId)

        // uv texture
        uvTextureId = textureObjectIds[1]
        textureLuminanceAlpha(uvBuffer, imageWidth / 2, imageHeight / 2, uvTextureId)
        Log.d(TAG, "onSurfaceCreated: yTextureId = $yTextureId, uvTextureId = $uvTextureId")
    }

    private fun textureLuminance(imageData: ByteBuffer, width: Int, height: Int, textureId: Int) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexImage2D(
            GLES20.GL_TEXTURE_2D, 0,
            GLES20.GL_LUMINANCE, width, height, 0,
            GLES20.GL_LUMINANCE,
            GLES20.GL_UNSIGNED_BYTE, imageData
        )
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
    }

    private fun textureLuminanceAlpha(imageData: ByteBuffer, width: Int, height: Int, textureId: Int) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexImage2D(
            GLES20.GL_TEXTURE_2D, 0,
            GLES20.GL_LUMINANCE_ALPHA, width, height, 0,
            GLES20.GL_LUMINANCE_ALPHA,
            GLES20.GL_UNSIGNED_BYTE, imageData
        )
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
    }
}