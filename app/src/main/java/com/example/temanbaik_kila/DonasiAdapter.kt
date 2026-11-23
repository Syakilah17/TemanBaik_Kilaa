package com.example.temanbaik_kila

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class DonasiModel(
    val id: Long? = null,
    val judul: String,
    val kategori: String = "Umum",
    val terkumpulNominal: Long = 0L,
    val targetNominal: Long = 0L,
    val terkumpul: String,
    val progress: Int,
    val gambar: Int
)

class DonasiAdapter(
    private var listDonasi: List<DonasiModel>,
    private val onItemClick: (DonasiModel) -> Unit,
    private val onEditClick: (DonasiModel) -> Unit,
    private val onDeleteClick: (DonasiModel) -> Unit
) : RecyclerView.Adapter<DonasiAdapter.DonasiViewHolder>() {

    class DonasiViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgDonasi: ImageView = view.findViewById(R.id.img_item_donasi)
        val tvTitle: TextView = view.findViewById(R.id.tv_item_title)
        val pbDonasi: ProgressBar = view.findViewById(R.id.pb_item_donasi)
        val tvTerkumpul: TextView = view.findViewById(R.id.tv_item_terkumpul)
        val btnEdit: ImageButton = view.findViewById(R.id.btn_edit_program)
        val btnDelete: ImageButton = view.findViewById(R.id.btn_delete_program)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonasiViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_donasi, parent, false)
        return DonasiViewHolder(view)
    }

    override fun onBindViewHolder(holder: DonasiViewHolder, position: Int) {
        val item = listDonasi[position]
        holder.imgDonasi.setImageResource(item.gambar)
        holder.tvTitle.text = item.judul
        holder.pbDonasi.progress = item.progress
        holder.tvTerkumpul.text = item.terkumpul

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }

        holder.btnEdit.setOnClickListener {
            onEditClick(item)
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(item)
        }
    }

    override fun getItemCount(): Int = listDonasi.size

    fun updateData(newList: List<DonasiModel>) {
        listDonasi = newList
        notifyDataSetChanged()
    }
}