package com.example.daunsehat.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.daunsehat.data.repository.UserRepository
import com.example.daunsehat.di.Injection
import com.example.daunsehat.features.authentication.login.presentation.viewmodel.LoginViewModel
import com.example.daunsehat.features.authentication.register.presentation.viewmodel.RegisterViewModel
import com.example.daunsehat.features.community.presentation.viewmodel.AddArticleViewModel
import com.example.daunsehat.features.community.presentation.viewmodel.CommunityViewModel
import com.example.daunsehat.features.community.presentation.viewmodel.DetailArticleViewModel
import com.example.daunsehat.features.detection.presentation.viewmodel.PredictViewModel
import com.example.daunsehat.features.history.presentation.viewmodel.HistoryViewModel
import com.example.daunsehat.features.main.viewmodel.MainViewModel
import com.example.daunsehat.features.profile.presentation.viewmodel.ProfileViewModel

class ViewModelFactory(
    private val repository: UserRepository
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
                modelClass.isAssignableFrom(CommunityViewModel::class.java) -> {
                    CommunityViewModel(repository) as T
                }
                modelClass.isAssignableFrom(AddArticleViewModel::class.java) -> {
                    AddArticleViewModel(repository) as T
                }
                modelClass.isAssignableFrom(DetailArticleViewModel::class.java) -> {
                    DetailArticleViewModel(repository) as T
                }
                modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                    RegisterViewModel(repository) as T
                }
                modelClass.isAssignableFrom(HistoryViewModel::class.java) -> {
                    HistoryViewModel(repository) as T
                }
                modelClass.isAssignableFrom(PredictViewModel::class.java) -> {
                    PredictViewModel(repository) as T
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
                        Injection.provideUserRepository(context)
                    )
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }

}
