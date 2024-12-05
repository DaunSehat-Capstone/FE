package com.example.daunsehat.data.repository

sealed  class ResultApi <out R> private constructor() {
    data class Success<out T>(val data: T) : ResultApi<T>()
    data class Error(val error: String) : ResultApi<Nothing>()
    data object Loading : ResultApi<Nothing>()
}