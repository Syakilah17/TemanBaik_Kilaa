package com.example.temanbaik_kila

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class FormDonasiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_donasi)

        val etNominal = findViewById<EditText>(R.id.et_nominal_donasi)
        val tvTotalBayar = findViewById<TextView>(R.id.tv_total_bayar)
        val btnLanjutBayar = findViewById<Button>(R.id.btn_lanjut_bayar)

        // FITUR 2: LOGIKA CALCULATOR ESTIMASI TOTAL DONASI REAL-TIME
        etNominal?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val inputStr = s.toString().trim()
                val nominal = inputStr.toLongOrNull() ?: 0L

                if (nominal > 0) {
                    val kodeUnik = 7L // Menambahkan kode unik Rp7
                    val total = nominal + kodeUnik
                    tvTotalBayar?.text = "Total Pembayaran: Rp $total (Termasuk kode unik Rp7)"
                } else {
                    tvTotalBayar?.text = "Total Pembayaran: Rp 0"
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        btnLanjutBayar?.setOnClickListener {
            val intent = Intent(this, PaymentActivity::class.java)
            startActivity(intent)
        }
    }
}