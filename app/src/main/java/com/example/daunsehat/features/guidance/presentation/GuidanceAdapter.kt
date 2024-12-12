package com.example.daunsehat.features.guidance.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.daunsehat.R
import com.example.daunsehat.data.pref.GuidanceItem

class GuidanceAdapter(
    private val guidanceList: List<GuidanceItem>,
    private val onClick: (GuidanceItem) -> Unit,
    private val useMenuLayout: Boolean
) :
    RecyclerView.Adapter<GuidanceAdapter.GuidanceViewHolder>() {

    inner class GuidanceViewHolder(view: View, private val viewType: Int) : RecyclerView.ViewHolder(view) {
        private val imgGuidance: ImageView
        private val txtGuidanceName: TextView

        init {
            if (viewType == VIEW_TYPE_HOME) {
                imgGuidance = view.findViewById(R.id.img_guidance)
                txtGuidanceName = view.findViewById(R.id.txt_guidance_name)
            } else {
                imgGuidance = view.findViewById(R.id.img_guidance_menu)
                txtGuidanceName = view.findViewById(R.id.txt_guidance_menu_name)
            }
        }

        fun bind(guidance: GuidanceItem) {
            imgGuidance.setImageResource(guidance.imageRes)
            txtGuidanceName.text = guidance.name
            itemView.setOnClickListener { onClick(guidance) }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (useMenuLayout) VIEW_TYPE_MENU else VIEW_TYPE_HOME
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuidanceViewHolder {
        val layout = if (viewType == VIEW_TYPE_MENU) {
            R.layout.item_guidance_menu
        } else {
            R.layout.item_guidance
        }

        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return GuidanceViewHolder(view, viewType)
    }

    override fun onBindViewHolder(holder: GuidanceViewHolder, position: Int) {
        holder.bind(guidanceList[position])
    }

    override fun getItemCount(): Int = guidanceList.size

    companion object {
        private const val VIEW_TYPE_HOME = 0
        private const val VIEW_TYPE_MENU = 1
    }
}
