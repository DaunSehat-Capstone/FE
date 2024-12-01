package com.example.daunsehat.features.homepage.presentation

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.daunsehat.R
import com.example.daunsehat.TipItem
import com.example.daunsehat.TipsAdapter
import com.example.daunsehat.databinding.FragmentHomePageBinding
import com.example.daunsehat.guidance.GuidanceDetailActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class HomePageFragment : Fragment() {
    private var _binding: FragmentHomePageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomePageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calendar = Calendar.getInstance()
        val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        val monthYear = monthYearFormat.format(calendar.time)

        val txtMonthYear: TextView = view.findViewById(R.id.txt_month_year)
        txtMonthYear.text = monthYear

        val currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        for (i in 1..31) {
            val dateTextViewId = resources.getIdentifier("date_$i", "id", requireActivity().packageName)
            val dateTextView = view.findViewById<TextView>(dateTextViewId)
            dateTextView?.let {
                if (i == currentDayOfMonth) {
                    it.setBackgroundResource(R.drawable.bg_highlight)
                }
            }
        }

        val tipsRecyclerView: RecyclerView = view.findViewById(R.id.recycler_tips)
        val tipsList = listOf(
            TipItem(R.drawable.dummy_img_tip, "Biji-bijian"),
            TipItem(R.drawable.dummy_img_tip, "Kacang-kacangan"),
            TipItem(R.drawable.dummy_img_tip, "Buah-buahan"),
            TipItem(R.drawable.dummy_img_tip, "Sayur-mayur")
        )

        val tipsAdapter = TipsAdapter(tipsList) { tipItem ->
            val intent = Intent(requireContext(), GuidanceDetailActivity::class.java)
            intent.putExtra("tip_name", tipItem.name)
            startActivity(intent)
        }

        tipsRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        tipsRecyclerView.adapter = tipsAdapter
    }

}