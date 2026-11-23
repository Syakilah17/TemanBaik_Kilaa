package com.example.temanbaik_kila

import android.app.Activity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class FormCampaignActivity : AppCompatActivity() {

    private val daftarKategori = listOf("Pendidikan", "Bencana Alam", "Pangan & Gizi", "Kesehatan")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_campaign)

        val btnBack = findViewById<TextView>(R.id.btn_back_campaign)
        val tvTitle = findViewById<TextView>(R.id.tv_form_campaign_title)
        val etJudul = findViewById<EditText>(R.id.et_campaign_judul)
        val spKategori = findViewById<Spinner>(R.id.sp_campaign_kategori)
        val etTarget = findViewById<EditText>(R.id.et_campaign_target)
        val etTerkumpul = findViewById<EditText>(R.id.et_campaign_terkumpul)
        val btnSimpan = findViewById<Button>(R.id.btn_simpan_campaign)

        // Setup Spinner Kategori
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, daftarKategori)
        spKategori.adapter = spinnerAdapter

        // Cek MODE_FORM secara eksplisit ("TAMBAH" vs "EDIT")
        val modeForm = intent.getStringExtra("MODE_FORM") ?: "TAMBAH"
        val campaignId = intent.getLongExtra("CAMPAIGN_ID", -1L)
        val isEditMode = (modeForm == "EDIT" && campaignId != -1L)

        if (isEditMode) {
            tvTitle.text = "Edit Program Donasi"
            btnSimpan.text = "Simpan Perubahan"
            etJudul.setText(intent.getStringExtra("CAMPAIGN_JUDUL") ?: "")
            etTarget.setText(intent.getLongExtra("CAMPAIGN_TARGET", 0L).toString())
            etTerkumpul.setText(intent.getLongExtra("CAMPAIGN_TERKUMPUL", 0L).toString())

            val kategoriLama = intent.getStringExtra("CAMPAIGN_KATEGORI") ?: "Pendidikan"
            val idx = daftarKategori.indexOfFirst { it.equals(kategoriLama, ignoreCase = true) }
            if (idx >= 0) spKategori.setSelection(idx)
        } else {
            tvTitle.text = "Buat Program Donasi"
            btnSimpan.text = "Publikasikan Program"
        }

        btnBack.setOnClickListener { finish() }

        btnSimpan.setOnClickListener {
            val judul = etJudul.text.toString().trim()
            val kategori = spKategori.selectedItem.toString()
            val target = etTarget.text.toString().toLongOrNull() ?: 0L
            val terkumpul = etTerkumpul.text.toString().toLongOrNull() ?: 0L

            if (judul.isEmpty() || target <= 0L) {
                Toast.makeText(this, "Judul dan target dana wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnSimpan.isEnabled = false
            btnSimpan.text = "Menyimpan..."

            lifecycleScope.launch {
                try {
                    if (isEditMode) {
                        val body = mapOf(
                            "judul" to judul,
                            "kategori" to kategori,
                            "terkumpul" to terkumpul,
                            "target" to target
                        )
                        val response = SupabaseClient.api.updateCampaign("eq.$campaignId", body)
                        if (response.isSuccessful) {
                            Toast.makeText(this@FormCampaignActivity, "Program berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                            setResult(Activity.RESULT_OK)
                            finish()
                        } else {
                            Toast.makeText(this@FormCampaignActivity, "Gagal update: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val request = CampaignInsertRequest(
                            judul = judul,
                            kategori = kategori,
                            terkumpul = terkumpul,
                            target = target
                        )
                        val response = SupabaseClient.api.createCampaign(request)
                        if (response.isSuccessful) {
                            Toast.makeText(this@FormCampaignActivity, "Program baru berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                            setResult(Activity.RESULT_OK)
                            finish()
                        } else {
                            Toast.makeText(this@FormCampaignActivity, "Gagal menambah: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@FormCampaignActivity, "Error koneksi: ${e.message}", Toast.LENGTH_SHORT).show()
                } finally {
                    btnSimpan.isEnabled = true
                    btnSimpan.text = if (isEditMode) "Simpan Perubahan" else "Publikasikan Program"
                }
            }
        }
    }
}