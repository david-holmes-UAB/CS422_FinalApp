package cs.mad.musictracker.bases

import androidx.room.Dao
import androidx.room.Entity

data class spotifyUser (
    val id: String,
    val displayName: String,
    val email: String?,
    val country: String?
    )

@Dao
interface userDao {
    // TODO
}