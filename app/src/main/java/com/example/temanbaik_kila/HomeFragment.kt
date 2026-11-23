package com.example.temanbaik_kila

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch
import java.util.Locale

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var donasiAdapter: DonasiAdapter
    private lateinit var tvNoResult: TextView
    private lateinit var rvDonasi: RecyclerView
    private var semuaDataDonasi: List<DonasiModel> = emptyList()

    private val formCampaignLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            loadCampaignsFromSupabase()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnDonasiSekarang = view.findViewById<Button>(R.id.btn_donasi_sekarang)
        val btnTambahProgram = view.findViewById<Button>(R.id.btn_tambah_program)
        val etSearch = view.findViewById<EditText>(R.id.et_search_donasi)
        tvNoResult = view.findViewById(R.id.tv_no_result)
        rvDonasi = view.findViewById(R.id.rv_donasi)

        val gridPendidikan = view.findViewById<MaterialCardView>(R.id.grid_pendidikan)
        val gridBencana = view.findViewById<MaterialCardView>(R.id.grid_bencana)
        val gridMakanan = view.findViewById<MaterialCardView>(R.id.grid_makanan)
        val gridSemua = view.findViewById<MaterialCardView>(R.id.grid_semua)

        donasiAdapter = DonasiAdapter(
            listDonasi = emptyList(),
            onItemClick = { item ->
                val intent = Intent(requireContext(), FormDonasiActivity::class.java)
                intent.putExtra("JUDUL_PROGRAM", item.judul)
                startActivity(intent)
            },
            onEditClick = { item ->
                val intent = Intent(requireContext(), FormCampaignActivity::class.java)
                intent.putExtra("MODE_FORM", "EDIT")
                intent.putExtra("CAMPAIGN_ID", item.id ?: -1L)
                intent.putExtra("CAMPAIGN_JUDUL", item.judul)
                intent.putExtra("CAMPAIGN_KATEGORI", item.kategori)
                intent.putExtra("CAMPAIGN_TERKUMPUL", item.terkumpulNominal)
                intent.putExtra("CAMPAIGN_TARGET", item.targetNominal)
                formCampaignLauncher.launch(intent)
            },
            onDeleteClick = { item ->
                konfirmasiHapusCampaign(item)
            }
        )
        rvDonasi.layoutManager = LinearLayoutManager(requireContext())
        rvDonasi.isNestedScrollingEnabled = false
        rvDonasi.adapter = donasiAdapter

        loadCampaignsFromSupabase()

        // Tombol + Tambah -> Paksa mode TAMBAH (POST baru)
        btnTambahProgram.setOnClickListener {
            val intent = Intent(requireContext(), FormCampaignActivity::class.java)
            intent.putExtra("MODE_FORM", "TAMBAH")
            formCampaignLauncher.launch(intent)
        }

        btnDonasiSekarang.setOnClickListener {
            startActivity(Intent(requireContext(), FormDonasiActivity::class.java))
        }

        gridPendidikan.setOnClickListener { filterList("pendidikan") }
        gridBencana.setOnClickListener { filterList("bencana") }
        gridMakanan.setOnClickListener { filterList("pangan") }
        gridSemua.setOnClickListener { filterList("") }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterList(s.toString().trim())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onResume() {
        super.onResume()
        loadCampaignsFromSupabase()
    }

    private fun loadCampaignsFromSupabase() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = SupabaseClient.api.getCampaigns()
                if (response.isSuccessful) {
                    val data = response.body() ?: emptyList()
                    semuaDataDonasi = data.map { c ->
                        val persentase = if (c.target > 0) ((c.terkumpul * 100) / c.target).toInt().coerceIn(0, 100) else 0
                        val gambarRes = when {
                            c.kategori.contains("Pendidikan", ignoreCase = true) -> R.drawable.sekolah
                            c.kategori.contains("Bencana", ignoreCase = true) -> R.drawable.banjir
                            c.kategori.contains("Pangan", ignoreCase = true) || c.kategori.contains("Makanan", ignoreCase = true) -> R.drawable.bantuan_makanan
                            else -> R.drawable.medis
                        }
                        DonasiModel(
                            id = c.id,
                            judul = c.judul,
                            kategori = c.kategori,
                            terkumpulNominal = c.terkumpul,
                            targetNominal = c.target,
                            terkumpul = "${formatRupiahSingkat(c.terkumpul)} dari ${formatRupiahSingkat(c.target)}",
                            progress = persentase,
                            gambar = gambarRes
                        )
                    }
                    donasiAdapter.updateData(semuaDataDonasi)
                    tvNoResult.visibility = if (semuaDataDonasi.isEmpty()) View.VISIBLE else View.GONE
                } else {
                    Toast.makeText(requireContext(), "Gagal memuat program: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error koneksi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun konfirmasiHapusCampaign(item: DonasiModel) {
        val campaignId = item.id ?: return
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Program")
            .setMessage("Yakin ingin menghapus program '${item.judul}'?")
            .setPositiveButton("Hapus") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        val response = SupabaseClient.api.deleteCampaign("eq.$campaignId")
                        if (response.isSuccessful) {
                            Toast.makeText(requireContext(), "Program berhasil dihapus!", Toast.LENGTH_SHORT).show()
                            loadCampaignsFromSupabase()
                        } else {
                            Toast.makeText(requireContext(), "Gagal hapus: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun filterList(query: String) {
        val hasil = if (query.isEmpty()) {
            semuaDataDonasi
        } else {
            semuaDataDonasi.filter {
                it.judul.contains(query, ignoreCase = true) ||
                        it.kategori.contains(query, ignoreCase = true)
            }
        }
        donasiAdapter.updateData(hasil)
        tvNoResult.visibility = if (hasil.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun formatRupiahSingkat(nominal: Long): String {
        return when {
            nominal >= 1_000_000 -> {
                val juta = nominal / 1_000_000.0
                if (juta % 1.0 == 0.0) "Rp${juta.toInt()}jt" else String.format(Locale.US, "Rp%.1fjt", juta)
            }
            nominal >= 1_000 -> "Rp${nominal / 1_000}rb"
            else -> "Rp$nominal"
        }
    }
}