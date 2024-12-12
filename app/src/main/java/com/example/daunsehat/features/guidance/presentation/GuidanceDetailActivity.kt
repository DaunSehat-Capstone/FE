package com.example.daunsehat.features.guidance.presentation

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.daunsehat.data.pref.UserPreference
import com.example.daunsehat.data.pref.dataStore
import com.example.daunsehat.data.remote.response.GuidanceResponse
import com.example.daunsehat.data.remote.retrofit.ApiConfig
import com.example.daunsehat.data.repository.GuidanceRepository
import com.example.daunsehat.data.repository.UserRepository
import com.example.daunsehat.databinding.ActivityGuidanceDetailBinding
import com.example.daunsehat.features.guidance.viewmodel.GuidanceViewModel
import com.example.daunsehat.features.guidance.viewmodel.GuidanceViewModelFactory

class GuidanceDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGuidanceDetailBinding
    private val viewModel: GuidanceViewModel by viewModels {
        GuidanceViewModelFactory(
            userRepository = UserRepository.getInstance(
                UserPreference.getInstance(applicationContext.dataStore),
                ApiConfig.getApiService()
            ),
            guidanceRepository = GuidanceRepository(ApiConfig.getApiService())
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuidanceDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val guidanceCategory = intent.getStringExtra(EXTRA_GUIDANCE_NAME) ?: return

        viewModel.getSession().observe(this) { user ->
            if (user.token.isNotEmpty()) {
                observeViewModel()
                viewModel.getArticlesByCategory("Bearer ${user.token}", guidanceCategory)
            }
        }

        binding.btnSeeMore.setOnClickListener {
            val intent = Intent(this, GuidanceMenuActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }

    }

    private fun observeViewModel() {
        viewModel.guidanceArticles.observe(this) { articles ->
            displayArticle(articles)
        }

        viewModel.errorMessage.observe(this) { error ->
            binding.txtGuidanceDescription.text = error
        }
    }

    private fun displayArticle(articles: List<GuidanceResponse>) {
        if (articles.isNotEmpty()) {
            val article = articles[0]

            binding.txtGuidanceTitle.text = article.title
            binding.txtGuidanceDescription.text = fromHtmlFormat(article.body)

        } else {
            binding.txtGuidanceDescription.text = "No articles available for this category."
        }
    }

    private fun fromHtmlFormat(htmlText: String): Spanned {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY)
        } else {
            @Suppress("DEPRECATION")
            Html.fromHtml(htmlText)
        }
    }

    companion object {
        const val EXTRA_GUIDANCE_NAME = "guidance_name"

        fun newIntent(context: android.content.Context, guidanceName: String): Intent {
            val intent = Intent(context, GuidanceDetailActivity::class.java)
            intent.putExtra(EXTRA_GUIDANCE_NAME, guidanceName)
            return intent
        }
    }
}
