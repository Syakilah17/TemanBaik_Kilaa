package com.example.temanbaik_kila

import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment

class HistoryFragment : Fragment(R.layout.fragment_history) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lvHistory = view.findViewById<ListView>(R.id.lv_history_donasi)

        val riwayatList = listOf(
            HistoryModel("Bantuan Pendidikan", "14 Nov 2026 • Rp 50.007", "Berhasil", R.drawable.sekolah),
            HistoryModel("Korban Banjir", "10 Nov 2026 • Rp 100.007", "Berhasil", R.drawable.banjir),
            HistoryModel("Bantuan Makanan", "05 Nov 2026 • Rp 25.007", "Berhasil", R.drawable.bantuan_makanan),
            HistoryModel("Bantuan Medis Darurat", "01 Nov 2026 • Rp 75.007", "Selesai", R.drawable.medis)
        )

        val adapter = HistoryCustomAdapter(requireContext(), riwayatList)
        lvHistory.adapter = adapter

        lvHistory.setOnItemClickListener { _, _, position, _ ->
            val selected = riwayatList[position]
            Toast.makeText(requireContext(), "Donasi: ${selected.judulProgram} (${selected.status})", Toast.LENGTH_SHORT).show()
        }
    }
}