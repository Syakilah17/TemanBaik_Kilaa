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
import com.google.android.material.card.MaterialCardView

class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Binding UI
        val btnDonasiSekarang = view.findViewById<Button>(R.id.btn_donasi_sekarang)
        val etSearch = view.findViewById<EditText>(R.id.et_search_donasi)
        val tvNoResult = view.findViewById<TextView>(R.id.tv_no_result)

        val cardPendidikan = view.findViewById<MaterialCardView>(R.id.card_pendidikan)
        val cardBanjir = view.findViewById<MaterialCardView>(R.id.card_banjir)
        val cardMakanan = view.findViewById<MaterialCardView>(R.id.card_makanan)

        val tvTitlePendidikan = view.findViewById<TextView>(R.id.tv_title_pendidikan)
        val tvTitleBanjir = view.findViewById<TextView>(R.id.tv_title_banjir)
        val tvTitleMakanan = view.findViewById<TextView>(R.id.tv_title_makanan)

        // 1. PINDAH HALAMAN (Arahkan ke Form Donasi / Detail untuk QRIS)
        val openDonasi = View.OnClickListener {
            val intent = Intent(requireContext(), FormDonasiActivity::class.java)
            startActivity(intent)
        }

        btnDonasiSekarang?.setOnClickListener(openDonasi)
        cardPendidikan?.setOnClickListener(openDonasi)
        cardBanjir?.setOnClickListener(openDonasi)
        cardMakanan?.setOnClickListener(openDonasi)

        // 2. LOGIKA FITUR SEARCH BAR FILTER REAL-TIME
        etSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim().lowercase()

                val titlePendidikan = tvTitlePendidikan?.text?.toString()?.lowercase() ?: ""
                val titleBanjir = tvTitleBanjir?.text?.toString()?.lowercase() ?: ""
                val titleMakanan = tvTitleMakanan?.text?.toString()?.lowercase() ?: ""

                var visibleCount = 0

                // Filter Card Pendidikan
                if (titlePendidikan.contains(query)) {
                    cardPendidikan?.visibility = View.VISIBLE
                    visibleCount++
                } else {
                    cardPendidikan?.visibility = View.GONE
                }

                // Filter Card Banjir
                if (titleBanjir.contains(query)) {
                    cardBanjir?.visibility = View.VISIBLE
                    visibleCount++
                } else {
                    cardBanjir?.visibility = View.GONE
                }

                // Filter Card Makanan
                if (titleMakanan.contains(query)) {
                    cardMakanan?.visibility = View.VISIBLE
                    visibleCount++
                } else {
                    cardMakanan?.visibility = View.GONE
                }

                // Jika tidak ada hasil yang cocok
                if (visibleCount == 0) {
                    tvNoResult?.visibility = View.VISIBLE
                } else {
                    tvNoResult?.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}