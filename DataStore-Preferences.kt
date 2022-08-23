
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

    
    
    
