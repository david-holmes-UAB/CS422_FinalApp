package cs.mad.musictracker.bases

import android.content.Context
import android.util.Log
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Track

class SpotifyConnector(private val context: Context, private val clientId: String, private val redirectUri: String) {

    private var spotifyAppRemote: SpotifyAppRemote? = null

    fun connectToSpotify(playlistURI: String) {
        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(context, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                spotifyAppRemote = appRemote
                Log.d("SpotifyConnector", "Connected to Spotify!")
                appRemote.playerApi.play(playlistURI)
                appRemote.playerApi.subscribeToPlayerState().setEventCallback {
                    val track: Track = it.track
                    Log.d("SpotifyConnector", track.name + " by " + track.artist.name)
                }
            }

            override fun onFailure(throwable: Throwable) {
                Log.e("SpotifyConnector", "Connection failed: " + throwable.message, throwable)
            }
        })
    }

    fun disconnect() {
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
            Log.d("SpotifyConnector", "Disconnected from Spotify!")
        }
    }
}