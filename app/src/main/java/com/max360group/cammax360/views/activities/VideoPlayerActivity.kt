package com.max360group.cammax360.views.activities

import android.graphics.Color
import android.net.Uri
import androidx.navigation.fragment.NavHostFragment
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.max360group.cammax360.R
import kotlinx.android.synthetic.main.activity_player_view.*
import kotlinx.android.synthetic.main.fragment_photo_detail.*
import kotlinx.android.synthetic.main.fragment_photo_detail.playerView

class VideoPlayerActivity : BaseAppCompactActivity() {

    companion object{
        const val INTENT_VIDEO_DATA="Video"
    }

    lateinit  var player: SimpleExoPlayer

    private var mVideo=""

    override val layoutId: Int
        get() = R.layout.activity_player_view

    override val isMakeStatusBarTransparent: Boolean
        get() = true

    override fun init() {
        //Get intent
        mVideo= intent?.getStringExtra(INTENT_VIDEO_DATA).toString()

        //Initialize player
        player = ExoPlayerFactory.newSimpleInstance(this)

        val mediaDataSourceFactory = DefaultDataSourceFactory(
            this,
            Util.getUserAgent(this, "mediaPlayerSample")
        )

        val mediaSource = ProgressiveMediaSource.Factory(mediaDataSourceFactory)
            .createMediaSource(Uri.parse(mVideo))

        with(player) {
            prepare(mediaSource, false, false)
            playWhenReady = true
        }


        playerView.setShutterBackgroundColor(Color.TRANSPARENT)
        playerView.player = player
        playerView.requestFocus()

        //Set click listener
        ivCancel.setOnClickListener {
            finish()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        player.stop()
    }

    override fun onPause() {
        super.onPause()
        player.stop()
    }

    override val navHostFragment: NavHostFragment?
        get() =null
}