package com.example.daunsehat.features.history.presentation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.daunsehat.data.repository.ResultApi
import com.example.daunsehat.databinding.FragmentHistoryBinding
import com.example.daunsehat.features.authentication.login.presentation.LoginActivity
import com.example.daunsehat.features.history.presentation.adapter.ListHistoryAdapter
import com.example.daunsehat.features.history.presentation.viewmodel.HistoryViewModel
import com.example.daunsehat.utils.NetworkUtils
import com.example.daunsehat.utils.ViewModelFactory

class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<HistoryViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    private val adapter = ListHistoryAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeSession()

    }

    private fun observeSession() {
        viewModel.getSession().observe(viewLifecycleOwner, { user ->
            if (!user.isLogin) {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
            } else {
                if (NetworkUtils.isInternetAvailable(requireContext())) {
                    binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
                    binding.rvHistory.adapter = adapter

                    setupView()
                } else {
                    Toast.makeText(requireContext(), "No Internet Connection", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })
    }

    private fun setupView() {
        viewModel.getHistoryPredict.observe(viewLifecycleOwner, { history ->
            when (history) {
                is ResultApi.Loading -> {
                    showLoading(true)
                }
                is ResultApi.Success -> {
                    showLoading(false)
                    adapter.setList(history.data)
                }
                is ResultApi.Error -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), "Error: ${history.error}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}