package com.example.ecodetect.ui.hasil

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.ecodetect.R
import com.example.ecodetect.data.DataDeskripsiSampah
import com.example.ecodetect.data.DataJenisSampah
import com.example.ecodetect.data.response.PredictionResult
import com.example.ecodetect.databinding.ActivityHasilBinding
import com.example.ecodetect.ui.daftardaurulang.DaurUlangActivity

class HasilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHasilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHasilBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.title_hasil_scan)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val result = intent.getParcelableExtra<PredictionResult>("result")
/*      val filePath = intent.getStringExtra("bitmap")
        val bitmap: Bitmap? = BitmapFactory.decodeFile(filePath)
        binding.hasilImageView.setImageBitmap(bitmap)  */
        if (result != null) {
            binding.tvJenisSampah.text = result.className
            binding.tvTipeSampah.text = result.type
            binding.tvAkurasi.text = result.accuracy

            val image = DataJenisSampah.listJenisSampah.find { it.jenis == result.className }
            if (image != null) {
                binding.hasilImageView.setImageResource(image.iconResId)
            }

            val deskripsi = DataDeskripsiSampah.listDeskripsiSampah.find { it.jenis == result.className }
            if (deskripsi != null) {
                binding.tvDeskripsiJenisSampah.text = deskripsi.deskripsi
            }
        }

        binding.btnCaraDaurUlang.setOnClickListener{
            if (result != null) {
                val intent = Intent(this, DaurUlangActivity::class.java)
                intent.putExtra(JENIS_EXTRA, result.className)
                intent.putExtra(TIPE_EXTRA, result.type)
                startActivity(intent)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val JENIS_EXTRA = "result"
        const val TIPE_EXTRA = "tipe"

    }
}