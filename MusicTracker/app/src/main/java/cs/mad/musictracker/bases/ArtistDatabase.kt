package cs.mad.musictracker.bases

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Artist::class], version = 1)
abstract class ArtistDatabase: RoomDatabase() {
    abstract val artistDao: ArtistDao
}
