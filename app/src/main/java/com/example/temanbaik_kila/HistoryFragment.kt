package com.example.temanbaik_kila

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class HistoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Memanggil tampilan layout fragment_history
        return inflater.inflate(R.layout.fragment_history, container, false)
    }
}