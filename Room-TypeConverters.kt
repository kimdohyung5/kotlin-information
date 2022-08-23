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


