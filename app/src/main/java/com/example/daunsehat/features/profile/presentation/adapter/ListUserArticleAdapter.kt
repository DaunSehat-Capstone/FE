package com.example.daunsehat.features.profile.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.daunsehat.R
import com.example.daunsehat.data.remote.response.ListArticleItem
import com.example.daunsehat.databinding.UserArticleItemBinding
import com.example.daunsehat.features.community.presentation.DetailArticleFragment
import java.text.SimpleDateFormat
import java.util.Locale

class ListUserArticleAdapter (
    private val listener: OnDeleteArticleListener
): RecyclerView.Adapter<ListUserArticleAdapter.ListViewHolder>() {

    private val listArticle : MutableList<ListArticleItem> = mutableListOf()

    interface OnDeleteArticleListener {
        fun onDeleteArticle(articleId: String)
    }

    fun removeArticleById(articleId: String) {
        val index = listArticle.indexOfFirst { it.articleId.toString() == articleId }
        if (index != -1) {
            listArticle.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val itemsListArticleBinding = UserArticleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(itemsListArticleBinding, listener)
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

    class ListViewHolder(
        private val binding: UserArticleItemBinding,
        private val listener: OnDeleteArticleListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(article: ListArticleItem) {
            with(binding) {
                ivMenu.visibility = View.VISIBLE
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

                ivMenu.setOnClickListener {
                    article.articleId?.let { id ->
                        showDeleteMenu(ivMenu, id.toString(), listener)
                    }
                }
            }
        }

        @SuppressLint("InflateParams")
        private fun showDeleteMenu(
            anchor: View,
            articleId: String,
            listener: OnDeleteArticleListener
        ) {
            val popupView = LayoutInflater.from(itemView.context).inflate(R.layout.menu_item_delete, null)
            val popupWindow = PopupWindow(popupView, 400, 100, true)
            popupWindow.isFocusable = true

            popupView.setOnClickListener {
                popupWindow.dismiss()
                listener.onDeleteArticle(articleId)
            }
            popupWindow.showAsDropDown(anchor, -380, 0)
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