아래의 build.gradle이 필요 
// Kotlin serialization
implementation 'org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2'

// serialization방법: ( gson, moshi, kotlinx.serialization )

	
json serialization구현하기
import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class OrmConverter {
    @TypeConverter
    fun fromList(value: List<String>) = Json.encodeToString(value)

    @TypeConverter
    fun toList(value: String) = Json.decodeFromString<List<String>>(value)
}

class Converters {
    @TypeConverter
    fun toBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray( byteArray, 0, byteArray.size)
    }
    @TypeConverter
    fun fromBitmap(bm: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.PNG, 95, outputStream)
        return outputStream.toByteArray()
    }
}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }
    
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}


////////////////// gson으로 Converting하기.

@ProvidedTypeConverter
class StringListTypeConverter(private val gson: Gson) {

    @TypeConverter
    fun listToJson(value: List<String>): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun jsonToList(value: String): List<String> {
        return gson.fromJson(value, Array<String>::class.java).toList()
    }
}
@ProvidedTypeConverter
class AddressTypeConverter(private val gson: Gson) {

    @TypeConverter
    fun listToJson(value: Address): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun jsonToList(value: String): Address {
        return gson.fromJson(value, Address::class.java)
    }
}

룸에 정의하기
@Database(entities = [UserEntity::class], version = 1, exportSchema = false)
@TypeConverters(
    value = [
        StringListTypeConverter::class,
        AddressTypeConverter::class
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DB_NAME = "sample.db"

    @Singleton
    @Provides
    fun provideGson(): Gson {
        return Gson()
    }

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context, gson: Gson): AppDatabase {
        return Room
            .databaseBuilder(context, AppDatabase::class.java, DB_NAME)
            .addTypeConverter(StringListTypeConverter(gson)) // 'List<String>' converter
            .addTypeConverter(AddressTypeConverter(gson)) // 'Address' converter
            .build()
    }

    @Singleton
    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }
}
