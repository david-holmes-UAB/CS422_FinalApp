package cs.mad.musictracker.exprbases

import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import com.spotify.sdk.android.auth.LoginActivity.REQUEST_CODE
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import android.app.Activity
import android.util.Log

class SpotifyAuthenticator(private val clientId: String, private val redirectUri: String) {

    data class Song(val name: String, val artists: List<String>, val albumName: String, val albumImage: String)
    data class topSong(val name: String, val artist: String, val album: String?)

    private var spotifyAppRemote: SpotifyAppRemote? = null
    private var accessToken: String? = null

    fun authenticate(activity: Activity) {
        val authRequest = AuthorizationRequest.Builder(
            clientId,
            AuthorizationResponse.Type.CODE,
            redirectUri
        )
            .setScopes(arrayOf("user-read-email", "user-read-private", "user-top-read"))
            .build()

        AuthorizationClient.openLoginActivity(activity, REQUEST_CODE, authRequest)
    }

    fun handleAuthResponse(response: AuthorizationResponse) {
        //var accessToken: String? = null
        when (response.type) {
            AuthorizationResponse.Type.CODE -> {
                val authCode = response.code
                val clientSecret = "828c4238ce8c4d6ea15eb48b632d00f0"

                val requestBody = FormBody.Builder()
                    .add("grant_type", "authorization_code")
                    .add("code", authCode)
                    .add("redirect_uri", redirectUri)
                    .add("client_id", clientId)
                    .add("client_secret", clientSecret)
                    .build()

                val request = Request.Builder()
                    .url("https://accounts.spotify.com/api/token")
                    .post(requestBody)
                    .build()

                OkHttpClient().newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        // Handle network error
                        Log.i("inside onFailure", "network error")
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val responseJson = JSONObject(response.body?.string()!!)
                        accessToken = responseJson.getString("access_token")
                        Log.d("SpotifyAuthenticator", "Access token: $accessToken")
                        // Now you can use the access token to make API calls
                    }
                })
            }
            AuthorizationResponse.Type.ERROR -> {
                // Handle error
                Log.i("AuthorizationResponse.Type.ERROR", "ERROR")
            }
            else -> {
                // Handle other cases
                Log.i("ELSE","something happened here")
            }
        }
    }

    fun getAccessToken(): String? {
        return accessToken
    }

    fun setSpotifyAppRemote(appRemote: SpotifyAppRemote) {
        spotifyAppRemote = appRemote
    }

    fun disconnect() {
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
        }
    }

    // with callback
    fun getUserPlaylists(accessToken: String?, callback: (JSONObject?) -> Unit) {
        if (accessToken == null) {
            Log.d("MainActivity", "Access token is null")
            callback(null)
            return
        }

        val request = Request.Builder()
            .url("https://api.spotify.com/v1/me/playlists")
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle network error
                Log.i("getUserPlaylists", "failure")
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseJson = JSONObject(response.body?.string()!!)
                Log.d("MainActivity3", "Playlists: $responseJson")
                callback(responseJson)
            }
        })
    }

    // outputting a list
    fun getPlaylists(jsonString: String): List<String> {
        val playlists = mutableListOf<String>()

        val jsonObject = JSONObject(jsonString)
        val items = jsonObject.getJSONArray("items")

        for (i in 0 until items.length()) {
            val playlistObject = items.getJSONObject(i)
            val name = playlistObject.getString("name")
            playlists.add(name)
        }
        return playlists
    }

    // outputting a list
    fun getPlaylistIds(jsonString: String): List<String> {
        val playlistIds = mutableListOf<String>()

        val jsonObject = JSONObject(jsonString)
        val items = jsonObject.getJSONArray("items")

        for (i in 0 until items.length()) {
            val playlistObject = items.getJSONObject(i)
            val id = playlistObject.getString("id")
            playlistIds.add(id)
        }
        return playlistIds
    }

    // with callback
    fun getPlaylistTracks(accessToken: String?, playlistId: String, callback: (JSONObject?) -> Unit) {
        if (accessToken == null) {
            Log.d("MainActivity", "Access token is null")
            callback(null)
            return
        }

        val request = Request.Builder()
            .url("https://api.spotify.com/v1/playlists/$playlistId/tracks")
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle network error
                Log.i("getPlaylistTracks", "failure")
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseJson = JSONObject(response.body?.string()!!)
                Log.d("MainActivity", "Tracks: $responseJson")
                callback(responseJson)
            }
        })
    }

    // returns a list of songs from a playlist
    fun parseSongsResponse(response: JSONObject): List<Song> {
        val songs = mutableListOf<Song>()

        val items = response.getJSONArray("items")
        for (i in 0 until items.length()) {
            val track = items.getJSONObject(i).getJSONObject("track")
            val name = track.getString("name")

            val album = track.getJSONObject("album")
            val albumName = album.getString("name")
            val albumImage = album.getJSONArray("images").getJSONObject(0).getString("url")

            val artists = mutableListOf<String>()
            val artistsJsonArray = track.getJSONArray("artists")
            for (j in 0 until artistsJsonArray.length()) {
                val artistName = artistsJsonArray.getJSONObject(j).getString("name")
                artists.add(artistName)
            }

            val song = Song(name, artists, albumName, albumImage)
            songs.add(song)
        }

        return songs
    }

    fun getTopTracks(accessToken: String?, callback: (JSONObject?) -> Unit) {
        if (accessToken == null) {
            Log.d("MainActivity", "Access token is null")
            callback(null)
            return
        }

        val request = Request.Builder()
            .url("https://api.spotify.com/v1/me/top/tracks?time_range=short_term&limit=10")
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle network error
                Log.i("getTopTracks", "failure")
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseJson = JSONObject(response.body?.string()!!)
                Log.d("GET TOP TRACKS", "top tracks: $responseJson")
                callback(responseJson)
            }
        })
    }
    fun getTopArtists(accessToken: String?, callback: (JSONObject?) -> Unit) {
        if (accessToken == null) {
            Log.d("MainActivity", "Access token is null")
            callback(null)
            return
        }

        val request = Request.Builder()
            .url("https://api.spotify.com/v1/me/top/artists?time_range=short_term&limit=10")
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle network error
                Log.i("getTopArtists", "failure")
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseJson = JSONObject(response.body?.string()!!)
                Log.d("GET TOP ARTISTS", "top artists: $responseJson")
                callback(responseJson)
            }
        })
    }
}