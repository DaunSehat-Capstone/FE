package com.example.daunsehat.features.community.presentation.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.daunsehat.R
import com.example.daunsehat.data.remote.response.ListArticleItem
import com.example.daunsehat.databinding.UserArticleItemBinding
import com.example.daunsehat.features.community.presentation.DetailArticleFragment
import java.text.SimpleDateFormat
import java.util.Locale

class ListArticleAdapter : RecyclerView.Adapter<ListArticleAdapter.ListViewHolder>() {

    private val listArticle : MutableList<ListArticleItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val itemsListArticleBinding = UserArticleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(itemsListArticleBinding)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<ListArticleItem>) {
        listArticle.clear()
        listArticle.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = listArticle.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listArticle[position])
    }

    class ListViewHolder(private val binding: UserArticleItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(article: ListArticleItem) {
            with(binding) {
                tvUsername.text = article.name
                tvArticleTitle.text = article.titleArticle
                tvDate.text = article.timestamp?.let { formatDate(it) }
                tvArticleContent.text = article.bodyArticle
                Glide.with(itemView.context)
                    .load(article.imageUrl)
                    .placeholder(R.drawable.ic_photo_profile)
                    .into(binding.ivProfilePicture)

                if (article.imageArticle == null) {
                    cardArticleImage.visibility = View.GONE
                } else {
                    cardArticleImage.visibility = View.VISIBLE
                    Glide.with(itemView.context)
                        .load(article.imageArticle)
                        .into(binding.ivArticleImage)
                }

                itemView.setOnClickListener {
                    val context = itemView.context
                    val fragment = article.articleId?.let { it1 ->
                        DetailArticleFragment.newInstance(
                            it1
                        )
                    }
                    if (context is AppCompatActivity) {
                        if (fragment != null) {
                            context.supportFragmentManager.beginTransaction()
                                .add(R.id.fragment_container, fragment)
                                .hide(context.supportFragmentManager.findFragmentById(R.id.fragment_container)!!)
                                .addToBackStack(null)
                                .commit()
                        }
                    }
                }
            }
        }

        private fun formatDate(dateString: String): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

            return try {
                val date = inputFormat.parse(dateString)
                outputFormat.format(date!!)
            } catch (e: Exception) {
                dateString
            }
        }
    }
}