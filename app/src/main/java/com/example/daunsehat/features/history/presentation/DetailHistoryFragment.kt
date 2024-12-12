package com.example.daunsehat.features.history.presentation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.daunsehat.databinding.FragmentDetailHistoryBinding
import com.example.daunsehat.features.authentication.login.presentation.LoginActivity
import com.example.daunsehat.features.history.presentation.viewmodel.HistoryViewModel
import com.example.daunsehat.utils.NetworkUtils
import com.example.daunsehat.utils.ViewModelFactory


class DetailHistoryFragment : Fragment() {
    private var _binding: FragmentDetailHistoryBinding? = null
    private val binding get() = _binding!!

    private var plantName: String? = null
    private var plantCondition: String? = null
    private var imagePlant: String? = null
    private var confidence: Int? = null
    private var treatment: String? = null

    private val viewModels by viewModels<HistoryViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeSession()

    }

    private fun observeSession() {
        viewModels.getSession().observe(viewLifecycleOwner) { user ->
            if (!user.isLogin) {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
            } else {
                if (NetworkUtils.isInternetAvailable(requireContext())) {
                    setupView()
                } else {
                    Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupView() {
        arguments?.let {
            plantName = it.getString("PLANT_NAME")
            plantCondition = it.getString("PLANT_CONDITION")
            imagePlant = it.getString("IMAGE_PLANT")
            confidence = it.getInt("CONFIDENCE")
            treatment = it.getString("TREATMENT")
        }

        plantName?.let { binding.tvPlantName.text = "Tanaman: $it" }
        plantCondition?.let { binding.tvDescription.text = "Prediksi: $it" }
        confidence?.let { binding.tvPercentage.text = "$it%" }
        treatment?.let { binding.tvTreatment.text = it }
        Glide.with(requireContext())
            .load(imagePlant)
            .into(binding.ivResultImage)

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(
            plantName: String,
            plantCondition: String,
            imagePlant: String,
            confidence: Int,
            treatment: String
        ): DetailHistoryFragment {
            val fragment = DetailHistoryFragment()
            val bundle = Bundle().apply {
                putString("PLANT_NAME", plantName)
                putString("PLANT_CONDITION", plantCondition)
                putString("IMAGE_PLANT", imagePlant)
                putInt("CONFIDENCE", confidence)
                putString("TREATMENT", treatment)
            }
            fragment.arguments = bundle
            return fragment
        }
    }
}