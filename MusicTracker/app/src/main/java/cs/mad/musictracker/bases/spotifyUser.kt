package cs.mad.musictracker.bases

import androidx.room.Dao
import androidx.room.Entity

data class spotifyUser (
    val id: String,
    val displayName: String,
    val email: String?,
    val country: String?
    )

// Object data classes; structure from Damon
data class Artist (
    val id: String,
    val name: String
)
data class Album (
    val id: String,
    val name: String,
    val songs: List<Song>
        )
data class Song (
    val intID: String,
    val title: String,
    val length: Int,
    val artist: Artist,
    val album: Album
        )

data class listenData (
    val yrHrs: Int,
    val topSongs: List<Song>,
    val topArtists: List<Artist>
        )

@Dao
interface userDao {
    // TODO
}