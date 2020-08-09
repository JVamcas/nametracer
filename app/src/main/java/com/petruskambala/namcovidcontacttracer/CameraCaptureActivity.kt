package com.owambo.jvamcas.stokkman.ui.camera

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import com.petruskambala.namcovidcontacttracer.R
import com.petruskambala.namcovidcontacttracer.databinding.ActivityCameraCaptureBinding
import com.petruskambala.namcovidcontacttracer.utils.Const
import kotlinx.android.synthetic.main.activity_camera_capture.*
import java.io.File

class CameraCaptureActivity : AppCompatActivity() {

    private lateinit var captureBinding: ActivityCameraCaptureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        captureBinding = DataBindingUtil.setContentView(this, R.layout.activity_camera_capture)

        setSupportActionBar(camera_toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        window.statusBarColor = resources.getColor(R.color.colorPrimary, null)

        captureBinding.apply {
            cameraSurface.setLifecycleOwner(this@CameraCaptureActivity)
            toggleView(true)

            captureFrame.setOnClickListener { cameraSurface.takePictureSnapshot() }
            discardFrame.setOnClickListener { toggleView(true) }
            val mResult = arrayOfNulls<PictureResult>(1)

            saveFrame.setOnClickListener {
                val  filePath = intent.getStringExtra(Const.ICON_PATH)
                val mFile = File(getExternalFilesDir(null),filePath)
                setResult(Activity.RESULT_OK,intent)

                mResult[0]?.toFile(mFile) { finish() }
            }
            cameraSurface.addCameraListener(object : CameraListener(){
                override fun onPictureTaken(result: PictureResult) {
                    super.onPictureTaken(result)
                    toggleView(false)
                    mResult[0] = result

                    result.toBitmap(cameraSurface.width,cameraSurface.height) {
                        captureFramePreview.setImageBitmap(it)
                    }
                }
            })
        }
    }
    private fun toggleView(takePicture: Boolean){
        with(captureBinding){
            this.takePicture = takePicture
            supportActionBar!!.setTitle(if (takePicture)R.string.take_picture else R.string.save_picture)
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}