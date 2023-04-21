package cs.mad.musictracker.exprbases

import androidx.room.Dao
import androidx.room.Entity
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
    
}

