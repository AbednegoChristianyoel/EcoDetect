package com.example.ecodetect.ui.main

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ecodetect.R
import com.example.ecodetect.databinding.ActivityMainBinding
import com.example.ecodetect.ui.hasil.HasilActivity
import com.example.ecodetect.utils.getImageUri
import com.example.ecodetect.utils.reduceFileImage
import com.example.ecodetect.utils.uriToFile
import com.example.ecodetect.viewmodel.ViewModelFactory
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var getFile: File? = null
    private var imgPrediction: Bitmap? = null
    private var currentImageUri: Uri? = null


    private lateinit var progressDialog: ProgressDialog
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory()
    }
    private val mainScope = MainScope()


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.no_permission),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.title_eco_detect)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.btnCamerax.setOnClickListener { startCamera() }
        binding.btnGallery.setOnClickListener { startGallery() }
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle(getString(R.string.memuat))
        progressDialog.setMessage(getString(R.string.mohon_menunggu))
        progressDialog.setCancelable(false)

        binding.btnScan.setOnClickListener {
            getFile?.takeIf { it.exists() } ?: run {
                Toast.makeText(this, getString(R.string.empty_file), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            progressDialog.show()

            mainScope.launch {
                val bitmapImage = imgPrediction
                bitmapImage?.let {
                    mainViewModel.classifyImage(it, this@MainActivity).observe(this@MainActivity) { result ->
                        progressDialog.dismiss()
                        if (result != null) {
                            val intent = Intent(this@MainActivity, HasilActivity::class.java)
                            intent.putExtra(RESULT, result)
                            startActivity(intent)
                            Toast.makeText(
                                this@MainActivity,
                                getString(R.string.scan_berhasil),
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                getString(R.string.scan_tidak_berhasil),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = getString(R.string.intent_type)
        val chooser = Intent.createChooser(intent, getString(R.string.choose_image))
        launcherIntentGallery.launch(chooser)
    }

    // Inside onDestroy
    override fun onDestroy() {
        super.onDestroy()
        mainScope.cancel()
    }

    private fun fileToBitmap(file: File): Bitmap? {
        return BitmapFactory.decodeFile(file.absolutePath)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@MainActivity)
            getFile = myFile

            imgPrediction = fileToBitmap(reduceFileImage(myFile))
            binding.previewImageView.setImageURI(selectedImg)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            currentImageUri?.let {
                Log.d("Image URI", "showImage: $it")
                binding.previewImageView.setImageURI(it)
                val myFile = uriToFile(it, this)
                getFile = myFile
                imgPrediction = fileToBitmap(reduceFileImage(myFile))

            }
        }
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
        const val RESULT = "result"
    }
}