package org.airella.airella.data

sealed class Result<out T : Any, out R : Any> {

    data class Success<out T : Any>(val data: T) : Result<T, Nothing>()
    data class Error<out R : Any>(val data: R) : Result<Nothing, R>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$data]"
        }
    }
}