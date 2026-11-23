package com.example.temanbaik_kila

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class HistoryFragment : Fragment(R.layout.fragment_history) {

    private lateinit var adapter: HistoryCustomAdapter
    private var riwayatList: List<HistoryModel> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lvHistory = view.findViewById<ListView>(R.id.lv_history_donasi)

        adapter = HistoryCustomAdapter(requireContext(), emptyList())
        lvHistory.adapter = adapter

        // READ: Muat riwayat donasi dari Supabase
        loadDonationsFromSupabase()

        // Klik item riwayat untuk UPDATE atau DELETE
        lvHistory.setOnItemClickListener { _, _, position, _ ->
            val selected = riwayatList[position]
            tampilkanOpsiRiwayat(selected)
        }
    }

    override fun onResume() {
        super.onResume()
        loadDonationsFromSupabase()
    }

    // READ dari Supabase
    private fun loadDonationsFromSupabase() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = SupabaseClient.api.getDonations()
                if (response.isSuccessful) {
                    val data = response.body() ?: emptyList()
                    riwayatList = data.map { d ->
                        val iconRes = when {
                            d.judul_program.contains("Pendidikan", ignoreCase = true) -> R.drawable.sekolah
                            d.judul_program.contains("Banjir", ignoreCase = true) || d.judul_program.contains("Bencana", ignoreCase = true) -> R.drawable.banjir
                            d.judul_program.contains("Makanan", ignoreCase = true) || d.judul_program.contains("Pangan", ignoreCase = true) -> R.drawable.bantuan_makanan
                            else -> R.drawable.medis
                        }
                        HistoryModel(
                            id = d.id,
                            namaDonatur = d.nama_donatur,
                            judulProgram = d.judul_program,
                            nominal = d.nominal,
                            detailNominalTanggal = "Donatur: ${d.nama_donatur} • ${formatRupiah(d.total_bayar)}",
                            status = d.status,
                            iconRes = iconRes
                        )
                    }
                    adapter.updateData(riwayatList)
                } else {
                    Toast.makeText(requireContext(), "Gagal memuat riwayat: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error koneksi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Menu Pilihan Update / Delete
    private fun tampilkanOpsiRiwayat(item: HistoryModel) {
        val opsi = arrayOf("Edit Transaksi Donasi", "Hapus Riwayat Donasi")
        AlertDialog.Builder(requireContext())
            .setTitle(item.judulProgram)
            .setItems(opsi) { _, which ->
                when (which) {
                    0 -> tampilkanDialogEditDonasi(item)
                    1 -> konfirmasiHapusDonasi(item)
                }
            }
            .show()
    }

    // Dialog UPDATE
    private fun tampilkanDialogEditDonasi(item: HistoryModel) {
        val donationId = item.id ?: return
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 30, 50, 10)
        }

        val etDonatur = EditText(requireContext()).apply {
            hint = "Nama Donatur"
            setText(item.namaDonatur)
        }
        val etNominal = EditText(requireContext()).apply {
            hint = "Nominal Donasi (angka)"
            inputType = InputType.TYPE_CLASS_NUMBER
            setText(item.nominal.toString())
        }
        val etStatus = EditText(requireContext()).apply {
            hint = "Status (Berhasil / Selesai / Pending)"
            setText(item.status)
        }

        layout.addView(etDonatur)
        layout.addView(etNominal)
        layout.addView(etStatus)

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Transaksi Donasi")
            .setView(layout)
            .setPositiveButton("Simpan") { _, _ ->
                val donaturBaru = etDonatur.text.toString().trim().ifEmpty { "Kila" }
                val nominalBaru = etNominal.text.toString().toLongOrNull() ?: item.nominal
                val totalBayarBaru = nominalBaru + 7L
                val statusBaru = etStatus.text.toString().trim().ifEmpty { "Berhasil" }

                updateDonation(donationId, donaturBaru, nominalBaru, totalBayarBaru, statusBaru)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    // UPDATE ke Supabase
    private fun updateDonation(id: Long, namaDonatur: String, nominal: Long, totalBayar: Long, status: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val body = mapOf(
                    "nama_donatur" to namaDonatur,
                    "nominal" to nominal,
                    "total_bayar" to totalBayar,
                    "status" to status
                )
                val response = SupabaseClient.api.updateDonation("eq.$id", body)
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Riwayat donasi diperbarui!", Toast.LENGTH_SHORT).show()
                    loadDonationsFromSupabase()
                } else {
                    val err = response.errorBody()?.string() ?: ""
                    Toast.makeText(requireContext(), "Gagal update (${response.code()}): $err", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // DELETE dari Supabase
    private fun konfirmasiHapusDonasi(item: HistoryModel) {
        val donationId = item.id
        if (donationId == null) {
            Toast.makeText(requireContext(), "ID donasi tidak ditemukan!", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Riwayat")
            .setMessage("Yakin ingin menghapus riwayat donasi '${item.judulProgram}'?")
            .setPositiveButton("Hapus") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        val response = SupabaseClient.api.deleteDonation("eq.$donationId")
                        if (response.isSuccessful) {
                            riwayatList = riwayatList.filter { it.id != donationId }
                            adapter.updateData(riwayatList)
                            Toast.makeText(requireContext(), "Riwayat berhasil dihapus!", Toast.LENGTH_SHORT).show()
                            loadDonationsFromSupabase()
                        } else {
                            val err = response.errorBody()?.string() ?: ""
                            Toast.makeText(requireContext(), "Gagal hapus (${response.code()}): $err", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun formatRupiah(nominal: Long): String {
        val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
        return "Rp ${formatter.format(nominal)}"
    }
}