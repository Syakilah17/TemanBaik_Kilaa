package com.example.temanbaik_kila

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment

class EventFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_event, container, false)

        val btnKirim = view.findViewById<Button>(R.id.btn_kirim_pendaftaran)
        btnKirim?.setOnClickListener {
            Toast.makeText(context, "Pendaftaran relawan berhasil dikirim!", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}