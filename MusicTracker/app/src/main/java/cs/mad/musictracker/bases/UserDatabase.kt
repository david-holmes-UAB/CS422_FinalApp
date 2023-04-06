package cs.mad.musictracker.bases

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [spotifyUser::class], version = 1)
abstract class UserDatabase: RoomDatabase() {
    abstract val userDao: userDao
}
