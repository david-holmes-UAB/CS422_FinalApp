package cs.mad.musictracker.ui.home

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cs.mad.musictracker.databinding.FragmentHomeBinding
import cs.mad.musictracker.MainActivity.TopSong
import cs.mad.musictracker.bases.TopSongAdapter
import kotlin.concurrent.thread

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val topSong1 = TopSong(
            track = "lollmao",
            trackName = "Father Stretch My Hands Pt. 1",
            album = "lifeofpablo",
            albumName = "The Life of Pablo",
            albumImage = "https://i.scdn.co/image/ab67616d0000b2732a7db835b912dc5014bd37f4",
            artist = "Kanye West"
        )
        val topSong2 = TopSong(
            track = "lollmao",
            trackName = "Everlong",
            album = "colorandtheshape",
            albumName = "The Color And The Shape",
            albumImage = "https://i.scdn.co/image/ab67616d0000b2730389027010b78a5e7dce426b",
            artist = "Foo Fighters"
        )
        val topSong3 = TopSong(
            track = "lollmao",
            trackName = "Private Landing (feat. Justin Bieber & Future)",
            album = "lovesick",
            albumName = "Love Sick",
            albumImage = "https://i.scdn.co/image/ab67616d0000b273feeff698e6090e6b02f21ec0",
            artist = "Don Toliver, Justin Bieber, Future"
        )
        val topSongs: List<TopSong> = mutableListOf(topSong1, topSong2, topSong3)
        _binding!!.topFiveView.adapter = TopSongAdapter(topSongs)
        println("This should print:" + topSongs.size)

        /*val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}