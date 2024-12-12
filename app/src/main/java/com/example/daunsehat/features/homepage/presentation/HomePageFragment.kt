package com.example.daunsehat.features.homepage.presentation

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.daunsehat.R
import com.example.daunsehat.TipItem
import com.example.daunsehat.TipsAdapter
import com.example.daunsehat.databinding.FragmentHomePageBinding
import com.example.daunsehat.features.reminder.presentation.ReminderReceiver
import com.example.daunsehat.guidance.GuidanceDetailActivity
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomePageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                    Log.d("HomePageFragment", "Alarms successfully set")
                } else {
                    binding.switchReminder.isChecked = false
                    Log.e("HomePageFragment", "Permissions missing. Switch turned OFF.")
                }
            } else {
                Log.d("HomePageFragment", "Switch turned OFF. Cancelling alarms...")
                cancelAlarms()
                sharedPreferences.edit().putBoolean("reminder_enabled", false).apply()
            }
            updateNextReminderTime()
        }

        updateNextReminderTime()

        val tipsRecyclerView: RecyclerView = view.findViewById(R.id.recycler_tips)
        val tipsList = listOf(
            TipItem(R.drawable.img_plant, "Biji - bijian"),
            TipItem(R.drawable.img_plant, "Kacang - kacangan"),
            TipItem(R.drawable.img_plant, "Buah - buahan"),
            TipItem(R.drawable.img_plant, "Sayur - mayur")
        )

        val tipsAdapter = TipsAdapter(tipsList) { tipItem ->
            val intent = Intent(requireContext(), GuidanceDetailActivity::class.java)
            intent.putExtra("tip_name", tipItem.name)
            startActivity(intent)
        }

        tipsRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        tipsRecyclerView.adapter = tipsAdapter

        binding.btnShowAll.setOnClickListener {
//            val transaction = parentFragmentManager.beginTransaction()
//            transaction.replace(R.id.fragment_container, TipsMenuFragment())
//            transaction.addToBackStack(null)
//            transaction.commit()
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

        Log.d("HomePageFragment", "Setting morning alarm at: ${calendar.time}")

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
            Log.d("HomePageFragment", "Morning Alarm set with setExactAndAllowWhileIdle")
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                morningIntent
            )
            Log.d("HomePageFragment", "Morning Alarm set with setExact")
        }
    }

    private fun setAfternoonAlarm() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 19)
            set(Calendar.MINUTE, 25)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        Log.d("HomePageFragment", "Setting afternoon alarm at: ${calendar.time}")

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
            Log.d("HomePageFragment", "Morning Alarm set with setExactAndAllowWhileIdle")
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                afternoonIntent
            )
            Log.d("HomePageFragment", "Morning Alarm set with setExact")
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
                setTextColor(resources.getColor(R.color.white, null))
                setPadding(8, 8, 8, 8)
            }
            calendarGrid.addView(dayTextView)
        }

        for (i in 0..6) {
            val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
            val dateTextView = TextView(requireContext()).apply {
                text = currentDay.toString()
                textSize = 14f
                setTextColor(resources.getColor(R.color.white, null))
                setPadding(16, 16, 16, 16)
                gravity = android.view.Gravity.CENTER

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
                Log.e("HomePageFragment", "Exact Alarm permission NOT granted!")
                Toast.makeText(context, "Please enable exact alarm permission", Toast.LENGTH_SHORT).show()
                return false
            } else {
                Log.d("HomePageFragment", "Exact Alarm permission is GRANTED")
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

            if (hasNotificationPermission) {
                Log.d("HomePageFragment", "POST_NOTIFICATIONS permission is GRANTED")
            } else {
                Log.e("HomePageFragment", "POST_NOTIFICATIONS permission NOT granted!")
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
}