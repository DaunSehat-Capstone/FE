package com.example.daunsehat.features.homepage.presentation

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.daunsehat.R
import com.example.daunsehat.data.pref.GuidanceItem
import com.example.daunsehat.data.remote.response.ProfileResponse
import com.example.daunsehat.data.repository.ResultApi
import com.example.daunsehat.databinding.FragmentHomePageBinding
import com.example.daunsehat.features.guidance.adapter.GuidanceAdapter
import com.example.daunsehat.features.guidance.presentation.GuidanceDetailActivity
import com.example.daunsehat.features.guidance.presentation.GuidanceMenuActivity
import com.example.daunsehat.features.profile.presentation.viewmodel.ProfileViewModel
import com.example.daunsehat.features.reminder.presentation.ReminderReceiver
import com.example.daunsehat.utils.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class HomePageFragment : Fragment() {
    private var _binding: FragmentHomePageBinding? = null
    private val binding get() = _binding!!
    private lateinit var alarmManager: AlarmManager
    private lateinit var morningIntent: PendingIntent
    private lateinit var afternoonIntent: PendingIntent
    private val prefsName = "reminder_prefs"
    private val profileViewModel: ProfileViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomePageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchUserProfile()
        checkNotificationPermission()
        generateWeeklyCalendar()

        val calendar = Calendar.getInstance()
        val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        binding.txtMonthYear.text = monthYearFormat.format(calendar.time)

        val currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        for (i in 1..31) {
            val dateTextViewId = resources.getIdentifier("date_$i", "id", requireActivity().packageName)
            val dateTextView = view.findViewById<TextView>(dateTextViewId)
            dateTextView?.let {
                if (i == currentDayOfMonth) {
                    it.setBackgroundResource(R.drawable.bg_highlight)
                } else {
                    it.background = null
                }
            }
        }

        checkExactAlarmPermission()

        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val sharedPreferences = requireContext().getSharedPreferences(prefsName, Context.MODE_PRIVATE)

        val isReminderEnabled = sharedPreferences.getBoolean("reminder_enabled", false)
        binding.switchReminder.isChecked = isReminderEnabled

        binding.switchReminder.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (checkExactAlarmPermission()) {
                    setMorningAlarm()
                    setAfternoonAlarm()
                    sharedPreferences.edit().putBoolean("reminder_enabled", true).apply()
                } else {
                    binding.switchReminder.isChecked = false
                }
            } else {
                cancelAlarms()
                sharedPreferences.edit().putBoolean("reminder_enabled", false).apply()
            }
            updateNextReminderTime()
        }

        updateNextReminderTime()

        val guidanceRecyclerView: RecyclerView = view.findViewById(R.id.recycler_guidance)
        val guidanceList = listOf(
            GuidanceItem(R.drawable.img_plant, "Biji-bijian"),
            GuidanceItem(R.drawable.img_plant, "Kacang-kacangan"),
            GuidanceItem(R.drawable.img_plant, "Buah-buahan"),
            GuidanceItem(R.drawable.img_plant, "Sayur-mayur"),
            GuidanceItem(R.drawable.img_plant, "Industri"),
            GuidanceItem(R.drawable.img_plant, "Rempah"),
            GuidanceItem(R.drawable.img_plant, "Umbi-umbian"),
            GuidanceItem(R.drawable.img_plant, "Hias")
        )

        val guidanceAdapter = GuidanceAdapter(guidanceList, { guidanceItem ->
            val intent = Intent(requireContext(), GuidanceDetailActivity::class.java)
            intent.putExtra("guidance_name", guidanceItem.name)
            startActivity(intent)
        }, useMenuLayout = false)

        guidanceRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        guidanceRecyclerView.adapter = guidanceAdapter

        binding.btnShowAll.setOnClickListener {
            val intent = Intent(requireContext(), GuidanceMenuActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setMorningAlarm() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }


        val intent = Intent(requireContext(), ReminderReceiver::class.java)
        morningIntent = PendingIntent.getBroadcast(
            requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                morningIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                morningIntent
            )
        }
    }

    private fun setAfternoonAlarm() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 16)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        val intent = Intent(requireContext(), ReminderReceiver::class.java)
        afternoonIntent = PendingIntent.getBroadcast(
            requireContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                afternoonIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                afternoonIntent
            )
        }
    }

    private fun cancelAlarms() {
        val intent = Intent(requireContext(), ReminderReceiver::class.java)
        morningIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE)
        afternoonIntent = PendingIntent.getBroadcast(requireContext(), 1, intent, PendingIntent.FLAG_IMMUTABLE)

        alarmManager.cancel(morningIntent)
        alarmManager.cancel(afternoonIntent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateNextReminderTime() {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)

        val nextAlarmTime = if (currentHour < 8) {
            "08:00"
        } else if (currentHour < 16) {
            "16:00"
        } else {
            "08:00"
        }

        binding.txtAlarmTime.text = nextAlarmTime
    }

    private fun generateWeeklyCalendar() {
        val calendarGrid = binding.calendarGrid
        calendarGrid.removeAllViews()

        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_MONTH)

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        calendar.get(Calendar.DAY_OF_MONTH)

        calendar.add(Calendar.DAY_OF_WEEK, 6)
        calendar.get(Calendar.DAY_OF_MONTH)

        calendar.add(Calendar.DAY_OF_WEEK, -6)

        val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")
        for (day in daysOfWeek) {
            val dayTextView = TextView(requireContext()).apply {
                text = day
                textSize = 14f
                gravity = android.view.Gravity.CENTER
                setTextColor(resources.getColor(R.color.white, null))
                setPadding(16, 16, 16, 16)
            }
            calendarGrid.addView(dayTextView)
        }

        for (i in 0..6) {
            val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
            val dateTextView = TextView(requireContext()).apply {
                text = currentDay.toString()
                textSize = 14f
                gravity = android.view.Gravity.CENTER
                setTextColor(resources.getColor(R.color.white, null))
                setPadding(16, 16, 16, 16)

                if (currentDay == today) {
                    setBackgroundResource(R.drawable.bg_highlight)
                }
            }
            calendarGrid.addView(dateTextView)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    private fun checkExactAlarmPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val hasExactAlarmPermission = alarmManager.canScheduleExactAlarms()

            if (!hasExactAlarmPermission) {
                Toast.makeText(context, "Please enable exact alarm permission", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasNotificationPermission = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasNotificationPermission) {
                requestNotificationPermission()
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                102
            )
        }
    }

    private fun fetchUserProfile() {
        profileViewModel.getProfile().observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultApi.Loading -> {
                }
                is ResultApi.Success -> {
                    updateUI(result.data)
                }
                is ResultApi.Error -> {
                }
            }
        }
    }

    private fun updateUI(profile: ProfileResponse) {
        val userName = profile.user?.name ?: "User"
        binding.txtGreeting.text = "Hello, $userName!"
    }
}