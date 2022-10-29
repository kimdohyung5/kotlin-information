sealed class Resource<T>(val data:T? = null,  val message:String? = null) {
    class Success<T>(data:T?): Resource<T>(data)
    class Error<T>(message:String, data:T? = null): Resource<T>(data, message)
    class Loading<T>(data:T? = null): Resource<T>(data)
}


//////////////////////////////////////////////////////////////////////////
// 이건 여분으로 관련된 부분이다.
sealed class Result<out R> {

    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
        }
    }
}

/**
 * `true` if [Result] is of type [Success] & holds non-null [Success.data].
 */
val Result<*>.succeeded
    get() = this is Success && data != null
//////////////////////////////////////////////////////////////////////////
