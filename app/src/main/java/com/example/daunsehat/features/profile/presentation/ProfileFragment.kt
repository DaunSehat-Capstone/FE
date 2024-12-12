package com.example.daunsehat.features.profile.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.daunsehat.R
import com.example.daunsehat.data.repository.ResultApi
import com.example.daunsehat.databinding.FragmentProfileBinding
import com.example.daunsehat.features.authentication.login.presentation.LoginActivity
import com.example.daunsehat.features.community.presentation.AddArticleActivity
import com.example.daunsehat.features.profile.presentation.adapter.ListUserArticleAdapter
import com.example.daunsehat.features.profile.presentation.viewmodel.ProfileViewModel
import com.example.daunsehat.utils.ImageLoader
import com.example.daunsehat.utils.NetworkUtils
import com.example.daunsehat.utils.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

class ProfileFragment : Fragment(), ListUserArticleAdapter.OnDeleteArticleListener {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private val adapter by lazy {
        ListUserArticleAdapter(this)
    }

    private val addArticleLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ProfileFragment())
                    .commit()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeSession()

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

    private fun setupAction() {
        binding.ivMenu.setOnClickListener {
            showLogoutMenu(it)
        }

        binding.btnEditProfile.setOnClickListener {

            val popupMenu = PopupMenu(requireContext(), it)
            popupMenu.menuInflater.inflate(R.menu.profile_edit_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_edit_profile -> {
                        val bottomsheet = BottomSheetEditProfileFragment()
                        bottomsheet.show(childFragmentManager, "EditProfileFragment")
                        true
                    }
                    R.id.menu_edit_photo -> {
                        val bottomsheet = BottomSheetEditPhotoProfileFragment()
                        bottomsheet.show(childFragmentManager, "EditPhotoProfileFragment")
                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()
        }

        binding.fabCreatePost.setOnClickListener {
            val intent = Intent(requireContext(), AddArticleActivity::class.java)
            addArticleLauncher.launch(intent)
        }
    }

    private fun setupViewProfile() {
        viewModel.getProfile().observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultApi.Loading -> showLoading(true)
                is ResultApi.Success -> {
                    showLoading(false)
                    val profile = result.data.user
                    profile?.let {
                        binding.tvUsername.text = it.name
                        binding.tvEmail.text = it.email

                        it.imageUrl?.let { imageUrl ->
                            ImageLoader.downloadImage(requireContext(), imageUrl) { bitmap ->
                                if (isAdded && view != null) {
                                    bitmap?.let { downloadedBitmap ->
                                        binding.ivPhotoProfile.setImageBitmap(downloadedBitmap)
                                    } ?: run {
                                        binding.ivPhotoProfile.setImageResource(R.drawable.ic_photo_profile)
                                    }
                                }
                            }
                        }
                    }
                }

                is ResultApi.Error -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), "Error: ${result.error}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupViewUserArticle() {
        viewModel.getUserArticle.observe(viewLifecycleOwner) { result ->
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
                    Toast.makeText(requireContext(), "Error: ${result.error}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun setupView() {
        setupViewProfile()
        setupViewUserArticle()
    }

    private fun showNoDataMessage(show: Boolean) {
        binding.tvNoData.visibility = if (show) View.VISIBLE else View.GONE
        binding.rvUserArticles.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun showLogoutMenu(anchor: View) {
        val popupView = LayoutInflater.from(requireContext()).inflate(R.layout.menu_item_logout, null)

        val popupWindow = PopupWindow(popupView, 400, 100, true)
        popupWindow.isFocusable = true

        popupView.setOnClickListener {
            popupWindow.dismiss()
            performLogout()
        }
        popupWindow.showAsDropDown(anchor, -380, 0)
    }

    private fun performLogout() {
        // onboarding
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", AppCompatActivity.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("onboarding_completed", false).apply()

        viewModel.logout()
        val intent = Intent(requireContext(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        requireActivity().finish()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDeleteArticle(articleId: String) {
        viewModel.deleteUserArticle(articleId).observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultApi.Loading -> showLoading(true)
                is ResultApi.Success -> {
                    Toast.makeText(requireContext(), result.data.message, Toast.LENGTH_SHORT).show()
                    adapter.removeArticleById(articleId)
                    showLoading(false)
                }
                is ResultApi.Error -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), "Error: ${result.error}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}