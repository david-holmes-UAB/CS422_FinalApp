package cs.mad.musictracker.bases

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import cs.mad.musictracker.MainActivity.TopSong
import cs.mad.musictracker.databinding.SongObjBinding
import com.bumptech.glide.Glide

class TopSongAdapter(topSongs: List<TopSong>) : RecyclerView.Adapter<TopSongAdapter.ViewHolder>() {

    private val topSongs = topSongs.toMutableList()

    class ViewHolder(val binding: SongObjBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(SongObjBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = topSongs[position]
        // (list (obj (pic, title), ...)) list[x].pic => input, list[x].title => input
        holder.binding.songCover.drawable = song.albumImage

    }

    override fun getItemCount(): Int {
        return songs.size
    }
}