package cs.mad.musictracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.LoginActivity.REQUEST_CODE

import android.util.Log;
import cs.mad.musictracker.bases.SpotifyAuthenticator
import cs.mad.musictracker.bases.SpotifyConnector
//import kotlin.collections.joinToString


class MainActivity : AppCompatActivity() {

    //private lateinit var binding: ActivityMainBinding

    private val clientId = "671cc4720b4f420397ecd146b7eb09b1"
    private val redirectUri = "https://com.spotify.android.spotifysdkkotlindemo/callback"
    private val clientSecret = "828c4238ce8c4d6ea15eb48b632d00f0"

    private lateinit var spotifyConnector: SpotifyConnector
    private lateinit var spotifyAuthenticator: SpotifyAuthenticator

    data class TopSong(
        val track: String,
        val trackName: String,
        val album: String,
        val albumName: String,
        val albumImage: String,
        val artist: String
    )

   data class TopArtist(
       val artist: String,
       val artistName: String,
       val artistImage: String,
       val artistGenres: String
   ) {
       override fun toString(): String {
           return "Artist Name: $artistName\nArtist Image: $artistImage\nArtist Genres: $artistGenres\n"
       }
   }

    val topSongs = mutableListOf<TopSong>()
    val topArtists = mutableListOf<TopArtist>()


    override fun onCreate(savedInstanceState: Bundle?) {

        /*
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        // Home should display a normal welcome screen (populated by user data?)
        // Users (nav_dashboard) should display aa list of linked profiles with an option to add another (maybe?)
        // Search (nav_notifications) lets a user search an artist
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
             */
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //spotifyConnector = SpotifyConnector(this, "671cc4720b4f420397ecd146b7eb09b1", "https://com.spotify.android.spotifysdkkotlindemo/callback")

        spotifyAuthenticator = SpotifyAuthenticator(clientId, redirectUri)

        // Authenticate the user
        spotifyAuthenticator.authenticate(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Handle the authorization response
        if (requestCode == REQUEST_CODE) {
            Log.i("inside onActivityResult", "WE ARE HERE")
            Log.i("request code", requestCode.toString())
            val response = AuthorizationClient.getResponse(resultCode, data)
            spotifyAuthenticator.handleAuthResponse(response)

            while (spotifyAuthenticator.getAccessToken() == null) {
                Thread.sleep(1000)
            }

            var playlists = listOf<String>()
            var playlistIds = listOf<String>()
            var topTracks = listOf<String>()
            val accessToken = spotifyAuthenticator.getAccessToken()
            println("Access token: $accessToken")

            // API CALLS
            // GET PLAYLISTS
            spotifyAuthenticator.getUserPlaylists(accessToken) { response ->
                if (response != null) {
                    //Log.d("MainActivity2", "Playlists: $response")
                    playlists = spotifyAuthenticator.getPlaylists(response.toString())
                    printPlaylists(playlists)

                    playlistIds = spotifyAuthenticator.getPlaylistIds(response.toString())
                    printPlaylists(playlistIds)

                } else {
                    Log.d("MainActivity", "Failed to get playlists")
                }
                //makeAPICalls(accessToken)
            }

            while (playlistIds.isEmpty()) {
                println("inside while")
                Thread.sleep(1000)
            }

            // GET PLAYLIST IDs
            for (i in 0 until playlistIds.size) {
                spotifyAuthenticator.getPlaylistTracks(accessToken, playlistIds[i]) { response ->
                    if (response != null) {
                        val songs = spotifyAuthenticator.parseSongsResponse(response)
                        for (song in songs) {
                            Log.d("MainActivity4", song.toString())
                        }
                    } else {
                        Log.d("MainActivity", "Failed to get playlist tracks")
                    }
                }
            }

            // GET TOP TRACKS
            spotifyAuthenticator.getTopTracks(accessToken) { response ->
                // Handle the JSON response
                if (response != null) {

                    val items = response.getJSONArray("items")

                    for (i in 0 until items.length()) {
                        val track = items.getJSONObject(i)
                        val trackName = track.getString("name")
                        val album = track.getJSONObject("album")
                        val albumName = album.getString("name")
                        val albumImage =
                            album.getJSONArray("images").getJSONObject(0).getString("url")
                        //  may want to check the length of the "images" array before accessing its first element to avoid a potential IndexOutOfBoundsException.
                        val artist =
                            track.getJSONArray("artists").getJSONObject(0).getString("name")

                        // Create a TopSong object and add it to the list
                        val topSong = TopSong(
                            track = track.toString(),
                            trackName = trackName,
                            album = album.toString(),
                            albumName = albumName,
                            albumImage = albumImage,
                            artist = artist
                        )
                        topSongs.add(topSong)
                    }

                    for (topSong in topSongs) {
                        Log.d("MainActivity4", topSong.toString())
                    }
                } else {
                    println("Error: Unable to get top tracks")
                }
            }

            spotifyAuthenticator.getTopArtists(accessToken) { response ->
                // Handle the JSON response
                if (response != null) {
                    val items = response.getJSONArray("items")
                    for (i in 0 until items.length()) {
                        val artist = items.getJSONObject(i)
                        val artistName = artist.getString("name")
                        val artistImage = artist.getJSONArray("images").getJSONObject(0).getString("url")
                        val genresArray = artist.optJSONArray("genres")
                        val artistGenres = if (genresArray != null && genresArray.length() > 0) {
                            genresArray.getString(0)
                        } else {
                            ""
                        }

                        // Create a TopArtist object and add it to the list
                        val topArtist = TopArtist(
                            artist = artist.toString(),
                            artistName = artistName,
                            artistImage = artistImage,
                            artistGenres = artistGenres
                        )
                        topArtists.add(topArtist)
                    }

                    for (topArtist in topArtists) {
                        Log.d("MainActivity565658", topArtist.artistImage.toString())
                    }
                } else {
                    println("Error: Unable to get top artists")
                }
            }
            spotifyAuthenticator.disconnect()
        }
    }

    fun printPlaylists(playlists: List<String>) {
        for (i in 0 until playlists.size) {
            println(playlists[i])
        }
    }
}
// Below functions are first provided steps from Spotify
/*
override fun onStart() {
    super.onStart()
    spotifyConnector.connectToSpotify("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL")
}

override fun onStop() {
    super.onStop()
    spotifyConnector.disconnect()

}
*/
