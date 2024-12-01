package com.example.daunsehat.onboarding

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.example.daunsehat.R

class OnboardingAdapter(
    private val onboardingItems: List<OnboardingItem>
) : RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    inner class OnboardingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imgOnboarding: ImageView = view.findViewById(R.id.img_onboarding)
        private val txtTitle: TextView = view.findViewById(R.id.txt_title)
        private val txtDescription: TextView = view.findViewById(R.id.txt_description)

        fun bind(item: OnboardingItem) {
            Glide.with(itemView.context).clear(imgOnboarding)

            Glide.with(itemView.context)
                .asGif()
                .load(item.imageRes)
                .into(object : com.bumptech.glide.request.target.CustomTarget<GifDrawable>() {
                    override fun onResourceReady(
                        resource: GifDrawable,
                        transition: com.bumptech.glide.request.transition.Transition<in GifDrawable>?
                    ) {
                        imgOnboarding.setImageDrawable(resource)
                        resource.setLoopCount(1)
                        resource.start()
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        imgOnboarding.setImageDrawable(placeholder)
                    }
                })

            txtTitle.text = item.title
            txtDescription.text = item.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_onboarding, parent, false)
        return OnboardingViewHolder(view)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(onboardingItems[position])
    }

    override fun getItemCount(): Int = onboardingItems.size
}
