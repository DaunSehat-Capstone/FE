package com.example.daunsehat.features.community.presentation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.daunsehat.R
import com.example.daunsehat.data.repository.ResultApi
import com.example.daunsehat.databinding.FragmentDetailArticleBinding
import com.example.daunsehat.features.authentication.login.presentation.LoginActivity
import com.example.daunsehat.features.community.presentation.viewmodel.DetailArticleViewModel
import com.example.daunsehat.utils.NetworkUtils
import com.example.daunsehat.utils.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale

class DetailArticleFragment : Fragment() {

    private var _binding: FragmentDetailArticleBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<DetailArticleViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeSession()
    }

    private fun observeSession() {
        val articleId = arguments?.getString(EXTRA_ARTICLE_ID)
        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (!user.isLogin) {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
            } else {
                if (NetworkUtils.isInternetAvailable(requireContext())) {
                    setupView(articleId)
                    setupAction()
                } else {
                    Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupView(articleId: String?) {
        showLoading(true)
        if (articleId != null) {
            viewModel.getDetailArticle(articleId).observe(viewLifecycleOwner) { article ->
                when (article) {
                    is ResultApi.Loading -> showLoading(true)
                    is ResultApi.Success -> {
                        showLoading(false)
                        binding.tvUsername.text = article.data.name
                        binding.tvArticleTitle.text = article.data.titleArticle
                        binding.tvArticleContent.text = article.data.bodyArticle
                        binding.tvDate.text = article.data.timestamp?.let { formatDate(it) }
                        Glide.with(this)
                            .load(article.data.imageUrl)
                            .placeholder(R.drawable.ic_photo_profile)
                            .into(binding.ivProfilePicture)

                        if (article.data.imageArticle == null) {
                            binding.cardArticleImage.visibility = View.GONE
                        } else {
                            binding.cardArticleImage.visibility = View.VISIBLE
                            Glide.with(this)
                                .load(article.data.imageArticle)
                                .into(binding.ivArticleImage)
                        }
                    }
                    is ResultApi.Error -> {
                        showLoading(false)
                        Toast.makeText(requireContext(), "Error: ${article.error}", Toast.LENGTH_SHORT).show()
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

    private fun setupAction() {
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val EXTRA_ARTICLE_ID = "extra_article_id"

        fun newInstance(articleId: Int): DetailArticleFragment {
            val fragment = DetailArticleFragment()
            val args = Bundle()
            args.putString(EXTRA_ARTICLE_ID, articleId.toString())
            fragment.arguments = args
            return fragment
        }
    }
}