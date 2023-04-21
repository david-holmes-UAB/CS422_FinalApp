package cs.mad.musictracker.bases

import androidx.room.Entity
import androidx.room.Insert
import androidx.room.Query
import com.google.gson.annotations.SerializedName

data class ArtistContainer(
    @SerializedName("Artists") val result: List<Artist>
)

@Entity
data class Artist(
    val artist: String,
    val artistName: String,
    val artistImage: String,
    val artistGenres: String
)

@Dao
interface ArtistDao {
    @Query("get * from Artists")
    suspend fun getAll(): List<Artist>
    @Insert
    suspend fun insertAll(artists: List<Artist>)
}
