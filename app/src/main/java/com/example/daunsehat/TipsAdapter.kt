package com.example.daunsehat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TipsAdapter(private val tipsList: List<TipItem>, private val onClick: (TipItem) -> Unit) :
    RecyclerView.Adapter<TipsAdapter.TipViewHolder>() {

    inner class TipViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imgTip: ImageView = view.findViewById(R.id.img_tip)
        private val txtTipName: TextView = view.findViewById(R.id.txt_tip_name)

        fun bind(tip: TipItem) {
            imgTip.setImageResource(tip.imageRes)
            txtTipName.text = tip.name
            itemView.setOnClickListener { onClick(tip) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tip, parent, false)
        return TipViewHolder(view)
    }

    override fun onBindViewHolder(holder: TipViewHolder, position: Int) {
        holder.bind(tipsList[position])
    }

    override fun getItemCount(): Int = tipsList.size
}
