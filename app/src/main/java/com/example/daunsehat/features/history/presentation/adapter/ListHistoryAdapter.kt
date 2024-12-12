package com.example.daunsehat.features.history.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.daunsehat.R
import com.example.daunsehat.data.remote.response.HistoryPredictResponseItem
import com.example.daunsehat.databinding.HistoryItemBinding
import com.example.daunsehat.features.history.presentation.DetailHistoryFragment
import kotlin.math.roundToInt

class ListHistoryAdapter : RecyclerView.Adapter<ListHistoryAdapter.ListViewHolder>() {

        private val listHistory : MutableList<HistoryPredictResponseItem> = mutableListOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
            val itemsListHistoryBinding = HistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ListViewHolder(itemsListHistoryBinding)
        }

        @SuppressLint("NotifyDataSetChanged")
        fun setList(list: List<HistoryPredictResponseItem>) {
            listHistory.clear()
            listHistory.addAll(list)
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int = listHistory.size

        override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
            holder.bind(listHistory[position])
        }

        class ListViewHolder(private val binding: HistoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
            @SuppressLint("SetTextI18n")
            fun bind(history: HistoryPredictResponseItem) {
                with(binding) {
                    val confidencePercentage = history.confidence?.toDouble()?.roundToInt()
                    tvPlantName.text = history.plant
                    tvPercentage.text = "$confidencePercentage%"
                    tvDiseaseName.text = history.plantCondition
                    Glide.with(itemView.context)
                        .load(history.imagePlant)
                        .into(binding.ivDiseaseImage)

                    itemView.setOnClickListener {
                        val context = itemView.context
                        val fragment = confidencePercentage?.let {
                            DetailHistoryFragment.newInstance(
                                history.plant ?: "",
                                history.plantCondition ?: "",
                                history.imagePlant ?: "",
                                it,
                                history.treatment ?: ""
                            )
                        }

                        if (context is AppCompatActivity) {
                            if (fragment != null) {
                                context.supportFragmentManager.beginTransaction()
                                    .replace(R.id.fragment_container, fragment)
                                    .addToBackStack(null)
                                    .commit()
                            }
                        }
                    }
                }
            }
        }
}