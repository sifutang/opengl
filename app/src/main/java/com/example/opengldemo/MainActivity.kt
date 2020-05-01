package com.example.opengldemo

import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.example.opengldemo.utils.Util
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity(){

    private lateinit var mGlSurfaceView: GLSurfaceView
    private lateinit var mImageView: ImageView

//    private var mRender: Render? = null
    private var mRender: YUVRender? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mGlSurfaceView = findViewById(R.id.gl_surface_view)
        mGlSurfaceView.setEGLContextClientVersion(2)
        mRender = YUVRender(applicationContext)
        mGlSurfaceView.setRenderer(mRender)

//        mImageView = findViewById(R.id.image_view)
//        mImageView.visibility = View.VISIBLE
//        val image = Util.read("test.yuv", this)
//        val yuvImage = YuvImage(image, ImageFormat.NV21, 800, 480, null)
//        val byteArrayOutputStream = ByteArrayOutputStream()
//        yuvImage.compressToJpeg(Rect(0, 0, 800, 480), 100, byteArrayOutputStream)
//        val byteArray = byteArrayOutputStream.toByteArray()
//        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
//        mImageView.setImageBitmap(bitmap)
    }
}