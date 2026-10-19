package com.example.temanbaik_kila

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val btnDonasi = findViewById<Button>(R.id.btn_donasi_detail)
        btnDonasi.setOnClickListener {
            val intent = Intent(this, FormDonasiActivity::class.java)
            startActivity(intent)
        }
    }
}