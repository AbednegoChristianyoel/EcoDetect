package com.example.ecodetect.ui.daftardaurulang

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecodetect.R
import com.example.ecodetect.adapter.DaurUlangAdapter
import com.example.ecodetect.databinding.ActivityDaurUlangBinding
import com.example.ecodetect.viewmodel.ViewModelFactory

class DaurUlangActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDaurUlangBinding
    private val daurUlangViewModel: DaurUlangViewModel by viewModels {
        ViewModelFactory()
    }
    private lateinit var daurUlangAdapter: DaurUlangAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDaurUlangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.title_daur_ulang)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val jenis = intent.getStringExtra("result")
        val tipe = intent.getStringExtra("tipe")

        binding.tvJenisSampah.text = jenis
        binding.tvTipeSampah.text = tipe

        daurUlangAdapter = DaurUlangAdapter(emptyList(), this)
        binding.rvDaurUlang.layoutManager = LinearLayoutManager(this)

        if (jenis != null) {
            fetchData(jenis)
        }
    }
    private fun fetchData(jenis: String) {
        try {
            val recommendations = daurUlangViewModel.getRecommendationsByType(jenis, this)
            recommendations?.let { response ->
                if (response.isNotEmpty()) {
                    daurUlangAdapter = DaurUlangAdapter(response[0]?.metode ?: emptyList(), this)
                    binding.rvDaurUlang.adapter = daurUlangAdapter
                } else {
                    binding.tvTidakDitemukan.visibility = View.VISIBLE
                }
            }
        } catch (e: Exception) {
            Log.e("fetchData", "Error fetching data", e)
        }
    }
    @Suppress("DEPRECATION")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}