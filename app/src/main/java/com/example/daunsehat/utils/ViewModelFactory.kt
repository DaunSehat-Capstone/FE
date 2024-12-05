package com.example.daunsehat.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.daunsehat.data.repository.UserRepository
import com.example.daunsehat.di.Injection
import com.example.daunsehat.features.authentication.login.presentation.viewmodel.LoginViewModel
import com.example.daunsehat.features.history.data.repository.HistoryRepository
import com.example.daunsehat.features.main.viewmodel.MainViewModel
import com.example.daunsehat.features.profile.presentation.viewmodel.ProfileViewModel

class ViewModelFactory(
    private val repository: UserRepository,
    provideHistoryRepository: HistoryRepository,
    ) : ViewModelProvider.NewInstanceFactory() {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when {
                modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                    MainViewModel(repository) as T
                }
                modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                    LoginViewModel(repository) as T
                }
                modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                    ProfileViewModel(repository) as T
                }
                else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
            }
        }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(
                        Injection.provideUserRepository(context),
                        Injection.provideHistoryRepository(context)
                    )
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }

}