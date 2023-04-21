package cs.mad.musictracker.exprbases

import androidx.room.*
import com.google.gson.annotations.SerializedName

data class ArtistList(@SerializedName("Artists") val result: List<Artist>)

@Entity
data class Artist (
    val artist: String,
    val artistName: String,
    val artistImage: String,
    val artistGenres: String
        )

@Dao
interface ArtistDao {
    @Query("Get * from Artists")
    suspend fun getAll(): List<Artist>
    @Insert
    suspend fun insertAll(artists: List<Artist>)
    @Update
    suspend fun update(artists: List<Artist>)
    @Delete
    suspend fun delete(artists: List<Artist>)
    @Query("delete from Artists")
    suspend fun deleteAll()
}