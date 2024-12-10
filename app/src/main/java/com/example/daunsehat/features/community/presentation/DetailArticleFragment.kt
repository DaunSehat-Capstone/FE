package com.example.daunsehat.features.community.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.daunsehat.R
import com.example.daunsehat.data.repository.ResultApi
import com.example.daunsehat.databinding.FragmentDetailArticleBinding
import com.example.daunsehat.features.community.presentation.viewmodel.DetailArticleViewModel
import com.example.daunsehat.utils.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
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

        val articleId = arguments?.getString(EXTRA_ARTICLE_ID)


        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (user.token.isNotEmpty()) {
                Log.d("DetailArticleFragmentxxx", "onViewCreated: ${user.token}")
                setupView(articleId)
            } else {
                Snackbar.make(
                    binding.root,
                    "Invalid session. Please login again.",
                    Snackbar.LENGTH_SHORT
                ).show()
                requireActivity().finish()
            }
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupView(articleId: String?) {
        showLoading(true)
        Log.d("DetailArticleFragmentxxx", "setupView: $articleId")
        if (articleId != null) {
            Log.d("DetailArticleFragmentxxx", "setupView1: $articleId")
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
                        Log.d("DetailArticleFragmentxxx", "Success: ${article.data}")
                    }
                    is ResultApi.Error -> {
                        showLoading(false)
                        Snackbar.make(binding.root, "Error: ${article.error}", Snackbar.LENGTH_SHORT).show()
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