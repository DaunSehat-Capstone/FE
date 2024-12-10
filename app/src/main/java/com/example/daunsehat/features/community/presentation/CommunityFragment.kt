package com.example.daunsehat.features.community.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.daunsehat.R
import com.example.daunsehat.data.repository.ResultApi
import com.example.daunsehat.databinding.FragmentCommunityBinding
import com.example.daunsehat.features.authentication.login.presentation.LoginActivity
import com.example.daunsehat.features.community.presentation.adapter.ListArticleAdapter
import com.example.daunsehat.features.community.presentation.viewmodel.CommunityViewModel
import com.example.daunsehat.features.community.presentation.viewmodel.SharedViewModel
import com.example.daunsehat.utils.NetworkUtils
import com.example.daunsehat.utils.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

class CommunityFragment : Fragment() {
    private var _binding: FragmentCommunityBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<CommunityViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    private val adapter = ListArticleAdapter()

    private val addArticleLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, CommunityFragment())
                    .commit()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommunityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeSession()

        binding.btnSearch.setOnClickListener {
            val query = binding.etSearch.text.toString().trim()
            searchArticles(query)
        }

    }

    private fun observeSession() {
        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (!user.isLogin) {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
            } else {
                if (NetworkUtils.isInternetAvailable(requireContext())) {
                    binding.rvUserArticles.layoutManager = LinearLayoutManager(requireContext())
                    binding.rvUserArticles.adapter = adapter

                    setupView()
                    setupAction()
                } else {
                    Toast.makeText(requireContext(), "No Internet Connection", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }

    private fun searchArticles(query: String) {
        if (query.isEmpty()) {
            setupView()
            return
        }
        viewModel.getSearchArticle(query).observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultApi.Loading -> showLoading(true)
                is ResultApi.Success -> {
                    showLoading(false)
                    if (result.data.isEmpty()) {
                        showNoDataMessage(true)
                    } else {
                        showNoDataMessage(false)
                        adapter.setList(result.data)
                    }
                }
                is ResultApi.Error -> {
                    showLoading(false)
                    Snackbar.make(binding.root, "Error: ${result.error}", Snackbar.LENGTH_SHORT).show()
                    Log.d("CommunityFragment", "Error: ${result.error}")
                }
            }
        }
    }

    private fun setupAction() {
        binding.fabCreatePost.setOnClickListener {
            val intent = Intent(requireContext(), AddArticleActivity::class.java)
            addArticleLauncher.launch(intent)
        }
    }

    private fun setupView() {
        viewModel.getAllArticle.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultApi.Loading -> showLoading(true)
                is ResultApi.Success -> {
                    showLoading(false)
                    Log.d("CommunityFragmentxxx", "Articles loaded: ${result.data.size}")
                    if (result.data.isEmpty()) {
                        showNoDataMessage(true)
                    } else {
                        showNoDataMessage(false)
                        adapter.setList(result.data)
                    }
                }
                is ResultApi.Error -> {
                    showLoading(false)
                    Snackbar.make(binding.root, "Error: ${result.error}", Snackbar.LENGTH_SHORT).show()
                    Log.d("CommunityFragment", "Errorxxx: ${result.error}")
                }
            }
        }

    }
    private fun showNoDataMessage(show: Boolean) {
        binding.tvNoData.visibility = if (show) View.VISIBLE else View.GONE
        binding.rvUserArticles.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onResume() {
        super.onResume()
        setupView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}