package cs.mad.musictracker.exprbases

import androidx.room.*
import com.google.gson.annotations.SerializedName

data class SongList(@SerializedName("Songs") val result: List<Song>)

@Entity
data class Song (
    val track: String,
    val trackName: String,
    val album: String,
    val albumName: String,
    val albumImage: String,
    val artist: String
        )

@Dao
interface SongDao {
    @Query ("Get * from Songs")
    suspend fun getAll(): List<Song>
    @Insert
    suspend fun insertAll(songs: List<Song>)
    @Update
    suspend fun update(songs: List<Song>)
    @Delete
    suspend fun delete(songs: List<Song>)
    @Query ("delete from Songs")
    suspend fun deleteAll()
}

