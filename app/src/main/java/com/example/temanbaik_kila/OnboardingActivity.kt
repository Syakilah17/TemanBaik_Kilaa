package com.example.temanbaik_kila

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

class OnboardingActivity : AppCompatActivity() {

    data class OnboardItem(val imageRes: Int, val title: String, val desc: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        val vpOnboarding = findViewById<ViewPager2>(R.id.vp_onboarding)
        val tvIndicator = findViewById<TextView>(R.id.tv_indicator)
        val btnNext = findViewById<Button>(R.id.btn_next)
        val tvSkip = findViewById<TextView>(R.id.tv_skip)

        val slides = listOf(
            OnboardItem(
                R.drawable.sekolah,
                "Wujudkan Mimpi Pendidikan",
                "Bantu anak-anak Indonesia mendapatkan akses pendidikan yang layak melalui donasi mudah dan transparan."
            ),
            OnboardItem(
                R.drawable.banjir,
                "Tanggap Darurat Bencana",
                "Ulurkan tangan dengan cepat untuk saudara kita yang terdampak bencana alam di berbagai daerah."
            ),
            OnboardItem(
                R.drawable.bantuan_makanan,
                "Berbagi Kebaikan Setiap Hari",
                "Satu kebaikan kecil darimu sangat berarti untuk meringankan beban sesama bersama Teman Baik."
            )
        )

        vpOnboarding.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_onboarding, parent, false)
                return object : RecyclerView.ViewHolder(view) {}
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val item = slides[position]
                holder.itemView.findViewById<ImageView>(R.id.img_onboard).setImageResource(item.imageRes)
                holder.itemView.findViewById<TextView>(R.id.tv_onboard_title).text = item.title
                holder.itemView.findViewById<TextView>(R.id.tv_onboard_desc).text = item.desc
            }

            override fun getItemCount(): Int = slides.size
        }

        vpOnboarding.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tvIndicator.text = "${position + 1} / ${slides.size}"
                btnNext.text = if (position == slides.size - 1) "Mulai Sekarang" else "Lanjut"
            }
        })

        val goToLogin = {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        tvSkip.setOnClickListener { goToLogin() }

        btnNext.setOnClickListener {
            if (vpOnboarding.currentItem < slides.size - 1) {
                vpOnboarding.currentItem += 1
            } else {
                goToLogin()
            }
        }
    }
}