
// AppModule.kt
// DataStore
// 아래와 같이 DataStore를 생성한다. 
@Singleton
@Provides
fun providePreferencesDataStore(application: Application): DataStore<Preferences> =
    PreferenceDataStoreFactory.create(
        produceFile = { application.preferencesDataStoreFile(DATASTORE_NAME) }
    )
    
    
활용은 아래와 같이 한다
class Repository {
    private object ObjectKey {
        val SORT_MODE = stringPreferencesKey("sort_mode")
    }

    override suspend fun saveSortMode(mode: String) {
        dataStore.edit { prefs ->
            prefs[SORT_MODE] = mode
        }
    }

    override suspend fun getSortMode(): Flow<String> {
        return dataStore.data
            .catch { exception ->
                if(exception is IOException) {
                    exception.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.map { prefs ->
                prefs[SORT_MODE] ?: Sort.ACCURACY.value
            }
    }
}
위를 감싼다. Wrapping시킨다.
class MyViewModel : ViewModel() {
    // DataStore
    fun saveSortMode(value: String) = viewModelScope.launch {
        repository.saveSortMode(value)
    }

    suspend fun getSortMode() = withContext(Dispatchers.IO) {
        repository.getSortMode().first()
    }
}

2. Manager형식으로 데이터를 관리한다. (향후에 이렇게 사용을 하자..)

private const val TAG = "PreferencesManager"
enum class SortOrder{ BY_NAME, BY_DATE }
data class FilterPreferences(val sortOrder: SortOrder, val hideCompleted: Boolean)
private val Context.dataStore by preferencesDataStore("user_preferences")
@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext context: Context){
    val dataStore = context.dataStore    
    val preferencesFlow = dataStore.data
        .catch { exception ->
            if( exception is IOException ) {
                Log.e(TAG, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val sortOrder = SortOrder.valueOf(
                preferences[PreferencesKeys.SORT_ORDER] ?: SortOrder.BY_DATE.name
            )
            val hideCompleted = preferences[PreferencesKeys.HIDE_COMPLETED] ?: false
            
            FilterPreferences(sortOrder, hideCompleted)
        }
    
    suspend fun updateSortOrder(sortOrder: SortOrder) {
        dataStore.edit{ preferences ->
            preferences[PreferencesKeys.SORT_ORDER] = sortOrder.name
        }
    }    
    suspend fun updateHideCompleted(hideCompleted: Boolean) {
        dataStore.edit{ preferences ->
            preferences[PreferencesKeys.HIDE_COMPLETED] = hideCompleted
        }
    }
    private object PreferencesKeys {
        val SORT_ORDER = stringPreferencesKey("sort_order")
        val HIDE_COMPLETED = booleanPreferencesKey("hide_completed")
    }
}    
    
3. 가장 알기 쉬운 방식 
class DataStoreModule(private val context : Context) {
    private val Context.dataStore  by preferencesDataStore(name = "dataStore")
    private val stringKey = stringPreferencesKey("textKey")
	
    val text : Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {preferences ->
            preferences[stringKey] ?: ""
        }        
    
    suspend fun setText(text : String){
        context.dataStore.edit { preferences ->
            preferences[stringKey] = text
        }
    }
}
3.1 참조하기 
    val dataStoreModule = DataStoreModule( appContext )	
3.2 읽기     
    val text = dataStoreModule.text.first()
3.3 쓰기 
    CoroutineScope(Dispatchers.Main).launch {
    val text = "Sample"
    dataStoreModule.setText(text)
 }
    
4. 알기쉬운 방법 2

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "on_boarding_pref")
class DataStoreRepository(context: Context) {

    private object PreferencesKey {
        val onBoardingKey = booleanPreferencesKey(name = "on_boarding_completed")
    }

    private val dataStore = context.dataStore

    suspend fun saveOnBoardingState(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.onBoardingKey] = completed
        }
    }

    fun readOnBoardingState(): Flow<Boolean> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val onBoardingState = preferences[PreferencesKey.onBoardingKey] ?: false
                onBoardingState
            }
    }
}

5. 위에서 4번 방법이 가장 깔끔한것 같다.. 

