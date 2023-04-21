package cs.mad.musictracker.bases

import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.content.Context

import androidx.recyclerview.widget.RecyclerView
import cs.mad.musictracker.MainActivity
import cs.mad.musictracker.MainActivity.TopSong

class TopSongAdapter(topSongs: List<TopSong>) : RecyclerView.Adapter<TopSongAdapter.ViewHolder>{
    private val topSongs = topSongs.toMutableList()

    class ViewHolder () {}


}