package com.example.temanbaik_kila

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etNama = findViewById<EditText>(R.id.et_reg_nama)
        val etEmail = findViewById<EditText>(R.id.et_reg_email)
        val etPassword = findViewById<EditText>(R.id.et_reg_password)
        val btnSubmit = findViewById<Button>(R.id.btn_register_submit)
        val tvToLogin = findViewById<TextView>(R.id.tv_to_login)

        btnSubmit.setOnClickListener {
            val nama = etNama.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (nama.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua kolom wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnSubmit.isEnabled = false
            btnSubmit.text = "Mendaftarkan..."

            lifecycleScope.launch {
                try {
                    val userBaru = UserInsertRequest(nama = nama, email = email, password = password)
                    val response = SupabaseClient.api.registerUser(userBaru)

                    if (response.isSuccessful) {
                        Toast.makeText(this@RegisterActivity, "Registrasi berhasil! Silakan login.", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish()
                    } else {
                        val err = response.errorBody()?.string() ?: "Kosong"
                        Log.e("SUPABASE_REG", "Code: ${response.code()} | Body: $err")
                        Toast.makeText(this@RegisterActivity, "Gagal ${response.code()}: $err", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Log.e("SUPABASE_REG", "Exception: ${e.message}", e)
                    Toast.makeText(this@RegisterActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                } finally {
                    btnSubmit.isEnabled = true
                    btnSubmit.text = "Daftar Sekarang"
                }
            }
        }

        tvToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}