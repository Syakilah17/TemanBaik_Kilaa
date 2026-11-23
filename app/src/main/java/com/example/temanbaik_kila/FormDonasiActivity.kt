package com.example.temanbaik_kila

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class FormDonasiActivity : AppCompatActivity() {

    private var nominalAktif = 0L
    private var totalBayarAktif = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_donasi)

        val judulProgram = intent.getStringExtra("JUDUL_PROGRAM") ?: "Program Donasi Umum"

        val etNominal = findViewById<EditText>(R.id.et_nominal_donasi)
        val tvTotalBayar = findViewById<TextView>(R.id.tv_total_bayar)
        val btnLanjutBayar = findViewById<Button>(R.id.btn_lanjut_bayar)

        // Calculator Estimasi Total Donasi Real-Time
        etNominal?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val inputStr = s.toString().trim()
                val nominal = inputStr.toLongOrNull() ?: 0L

                if (nominal > 0) {
                    val kodeUnik = 7L
                    val total = nominal + kodeUnik
                    nominalAktif = nominal
                    totalBayarAktif = total
                    tvTotalBayar?.text = "Total Pembayaran: Rp $total (Termasuk kode unik Rp7)"
                } else {
                    nominalAktif = 0L
                    totalBayarAktif = 0L
                    tvTotalBayar?.text = "Total Pembayaran: Rp 0"
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // CREATE: Simpan transaksi donasi ke Supabase lalu lanjut ke PaymentActivity
        btnLanjutBayar?.setOnClickListener {
            if (nominalAktif <= 0L) {
                Toast.makeText(this, "Masukkan nominal donasi terlebih dahulu!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnLanjutBayar.isEnabled = false
            btnLanjutBayar.text = "Memproses..."

            lifecycleScope.launch {
                try {
                    val donasiBaru = DonationInsertRequest(
                        nama_donatur = "Kila",
                        judul_program = judulProgram,
                        nominal = nominalAktif,
                        total_bayar = totalBayarAktif,
                        status = "Berhasil"
                    )
                    val response = SupabaseClient.api.createDonation(donasiBaru)

                    if (response.isSuccessful) {
                        Toast.makeText(this@FormDonasiActivity, "Donasi berhasil dicatat!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@FormDonasiActivity, PaymentActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@FormDonasiActivity, "Gagal menyimpan donasi: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@FormDonasiActivity, "Error koneksi: ${e.message}", Toast.LENGTH_SHORT).show()
                } finally {
                    btnLanjutBayar.isEnabled = true
                    btnLanjutBayar.text = "Lanjut Bayar"
                }
            }
        }
    }
}