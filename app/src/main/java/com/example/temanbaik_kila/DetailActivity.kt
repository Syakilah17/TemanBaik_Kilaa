package com.example.temanbaik_kila

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val tvJudulDonasi = findViewById<TextView>(R.id.tv_detail_title)
        val btnShare = findViewById<Button>(R.id.btn_share_donasi)

        btnShare?.setOnClickListener {
            val judul = tvJudulDonasi?.text?.toString() ?: "Program Donasi"
            val message = "Mari bantu sesama melalui program: *$judul* di aplikasi Teman Baik! Salurkan donasimu sekarang."

            // Intent Implisit untuk Share ke Aplikasi Lain
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
            }
            startActivity(Intent.createChooser(shareIntent, "Bagikan Program Donasi Melalui:"))
        }
    }
}