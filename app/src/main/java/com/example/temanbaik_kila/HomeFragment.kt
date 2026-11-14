package com.example.temanbaik_kila

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var donasiAdapter: DonasiAdapter
    private val semuaDataDonasi = listOf(
        DonasiModel("Bantuan Pendidikan", "Rp4.5jt dari Rp10jt", 45, R.drawable.sekolah),
        DonasiModel("Korban Banjir", "Rp7jt dari Rp10jt", 70, R.drawable.banjir),
        DonasiModel("Bantuan Makanan", "Rp1jt dari Rp5jt", 20, R.drawable.bantuan_makanan),
        DonasiModel("Bantuan Medis Darurat", "Rp3.2jt dari Rp8jt", 40, R.drawable.medis)
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnDonasiSekarang = view.findViewById<Button>(R.id.btn_donasi_sekarang)
        val etSearch = view.findViewById<EditText>(R.id.et_search_donasi)
        val tvNoResult = view.findViewById<TextView>(R.id.tv_no_result)
        val rvDonasi = view.findViewById<RecyclerView>(R.id.rv_donasi)

        val gridPendidikan = view.findViewById<MaterialCardView>(R.id.grid_pendidikan)
        val gridBencana = view.findViewById<MaterialCardView>(R.id.grid_bencana)
        val gridMakanan = view.findViewById<MaterialCardView>(R.id.grid_makanan)
        val gridSemua = view.findViewById<MaterialCardView>(R.id.grid_semua)

        // Setup RecyclerView
        donasiAdapter = DonasiAdapter(semuaDataDonasi) {
            startActivity(Intent(requireContext(), FormDonasiActivity::class.java))
        }
        rvDonasi.layoutManager = LinearLayoutManager(requireContext())
        rvDonasi.adapter = donasiAdapter

        // Tombol Banner
        btnDonasiSekarang.setOnClickListener {
            startActivity(Intent(requireContext(), FormDonasiActivity::class.java))
        }

        // Klik Grid Kategori untuk filter cepat
        gridPendidikan.setOnClickListener { filterList("pendidikan", tvNoResult) }
        gridBencana.setOnClickListener { filterList("banjir", tvNoResult) }
        gridMakanan.setOnClickListener { filterList("makanan", tvNoResult) }
        gridSemua.setOnClickListener { filterList("", tvNoResult) }

        // Search Bar Real-Time dengan RecyclerView
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterList(s.toString().trim(), tvNoResult)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterList(query: String, tvNoResult: TextView) {
        val hasil = if (query.isEmpty()) {
            semuaDataDonasi
        } else {
            semuaDataDonasi.filter { it.judul.lowercase().contains(query.lowercase()) }
        }
        donasiAdapter.updateData(hasil)
        tvNoResult.visibility = if (hasil.isEmpty()) View.VISIBLE else View.GONE
    }
}