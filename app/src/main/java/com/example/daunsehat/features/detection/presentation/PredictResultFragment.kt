package com.example.daunsehat.features.detection.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.daunsehat.R
import com.example.daunsehat.data.remote.response.Prediction
import com.example.daunsehat.data.repository.ResultApi
import com.example.daunsehat.features.main.MainActivity
import com.example.daunsehat.databinding.FragmentPredictResultBinding
import com.example.daunsehat.features.authentication.login.presentation.LoginActivity
import com.example.daunsehat.features.detection.presentation.viewmodel.PredictViewModel
import com.example.daunsehat.features.history.presentation.HistoryFragment
import com.example.daunsehat.features.homepage.presentation.HomePageFragment
import com.example.daunsehat.utils.NetworkUtils
import com.example.daunsehat.utils.ViewModelFactory
import com.example.daunsehat.utils.uriToFile

class PredictResultFragment : Fragment() {
    private var _binding: FragmentPredictResultBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<PredictViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    private var currentImageUri: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPredictResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val predictPhoto = arguments?.getString("PREDICT_IMAGE_URI")
        if (predictPhoto != null) {
            currentImageUri = predictPhoto
        }

        observeSession()
    }

    private fun observeSession() {
        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (!user.isLogin) {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
            } else {
                if (NetworkUtils.isInternetAvailable(requireContext())) {
                    setupView()
                    setupAction()
                } else {
                    Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupView() {
        val uri = Uri.parse(currentImageUri)
        val imageFile = uriToFile(uri!!, requireContext())
        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (!user.isLogin) {
                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            } else {
                viewModel.predictPlant(imageFile).observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is ResultApi.Loading -> {
                            showLoading(true)
                        }
                        is ResultApi.Success -> {
                            showLoading(false)
                            val predict = result.data.prediction
                            if (predict != null) {
                                updateUI(predict)
                            } else {
                                binding.tvError.visibility = View.VISIBLE
                                binding.tvError.text = "Prediction data not found"
                            }
                        }
                        is ResultApi.Error -> {
                            showLoading(false)
                            binding.tvError.visibility = View.VISIBLE
                            Toast.makeText(requireContext(), "Error: ${result.error}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun updateUI(predict: Prediction) {

        with(binding) {
            tvIntro.visibility = View.VISIBLE
            cardDetection.visibility = View.VISIBLE
            llContent.visibility = View.VISIBLE
            buttonLayout.visibility = View.VISIBLE

            tvPlantName.text = "Tanaman: ${predict.plant ?: "Tanaman tidak ditemukan"}"
            tvDescription.text = "Prediksi: ${predict.plantCondition ?: "Tidak ada deskripsi"}"
            tvPercentage.text = "${predict.confidence?.toDouble()?.toInt() ?: 0}%"
            tvTreatment.text = predict.treatment ?: "Pengobatan tidak tersedia"
            Glide.with(requireContext())
                .load(predict.imagePlant)
                .into(ivResultImage)
        }
    }

    private fun setupAction() {
        binding.btnDetectAgain.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomePageFragment())
                .commit()
        }

        binding.btnFinish.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomePageFragment())
                .commit()
        }

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
}