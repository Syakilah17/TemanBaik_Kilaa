package com.example.temanbaik_kila

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

data class HistoryModel(
    val id: Long? = null,
    val namaDonatur: String = "Kila",
    val judulProgram: String,
    val nominal: Long = 0L,
    val detailNominalTanggal: String,
    val status: String,
    val iconRes: Int
)

class HistoryCustomAdapter(
    private val context: Context,
    private var dataList: List<HistoryModel>
) : BaseAdapter() {

    override fun getCount(): Int = dataList.size

    override fun getItem(position: Int): Any = dataList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_history_custom, parent, false)

        val item = dataList[position]

        val imgIcon = view.findViewById<ImageView>(R.id.img_history_icon)
        val tvJudul = view.findViewById<TextView>(R.id.tv_history_judul)
        val tvTanggal = view.findViewById<TextView>(R.id.tv_history_tanggal)
        val tvStatus = view.findViewById<TextView>(R.id.tv_history_status)

        imgIcon.setImageResource(item.iconRes)
        tvJudul.text = item.judulProgram
        tvTanggal.text = item.detailNominalTanggal
        tvStatus.text = item.status

        return view
    }

    fun updateData(newList: List<HistoryModel>) {
        dataList = newList
        notifyDataSetChanged()
    }
}