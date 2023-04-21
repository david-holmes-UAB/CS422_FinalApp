package cs.mad.musictracker.exprbases

import android.content.Intent
import android.util.Log
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.LoginActivity
import cs.mad.musictracker.MainActivity

class SpotifyAPICaller() {

    fun onActivityResult(requestCode: Int, resultCode:Int, data:Intent?) {
        if (requestCode == LoginActivity.REQUEST_CODE) {
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
                        val topSong = MainActivity.TopSong(
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
                        val topArtist = MainActivity.TopArtist(
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
            /*
            for (topSong in topSongs) {
                fullTop.add(topSong)
            }
             */
            spotifyAuthenticator.disconnect()
            /*Thread.sleep(1000)
            //otherBinding = FragmentHomeBinding.inflate(layoutInflater)
            binding = ActivityMainBinding.inflate(layoutInflater)*/

        }
    }

}
