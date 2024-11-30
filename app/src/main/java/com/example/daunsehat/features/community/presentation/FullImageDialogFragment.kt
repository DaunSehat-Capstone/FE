package com.example.daunsehat.features.community.presentation

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.daunsehat.databinding.DialogFullImageBinding

class FullImageDialogFragment : DialogFragment() {

    private var _binding: DialogFullImageBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_IMAGE_URI = "image_uri"

        fun newInstance(imageUri: String): FullImageDialogFragment {
            val fragment = FullImageDialogFragment()
            val args = Bundle()
            args.putString(ARG_IMAGE_URI, imageUri)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setCanceledOnTouchOutside(true) // Allow dialog to close when tapping outside
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFullImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the URI from arguments
        val imageUri = arguments?.getString(ARG_IMAGE_URI)

        // Set the image URI to the ImageView
        imageUri?.let {
            binding.ivFullImage.setImageURI(Uri.parse(it))
        }

        // Close the dialog on clicking the close button
        binding.ivCloseDialog.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
