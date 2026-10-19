package com.example.temanbaik_kila

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PaymentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val btnConfirm = findViewById<Button>(R.id.btn_confirm_payment)
        val tvUbah = findViewById<TextView>(R.id.tv_ubah_donasi)

        btnConfirm.setOnClickListener {
            Toast.makeText(this, "Donasi berhasil dikirim! Terima kasih.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        tvUbah.setOnClickListener {
            finish()
        }
    }
}