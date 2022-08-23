아래의 build.gradle이 필요 
// Kotlin serialization
implementation 'org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2'

	
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


