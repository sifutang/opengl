package com.example.opengldemo

import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity(){

    private lateinit var mGlSurfaceView: GLSurfaceView

    private var mRender: Render? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mGlSurfaceView = findViewById(R.id.gl_surface_view)
        mGlSurfaceView.setEGLContextClientVersion(2)
        mRender = Render(applicationContext)
        mGlSurfaceView.setRenderer(mRender)
    }
}