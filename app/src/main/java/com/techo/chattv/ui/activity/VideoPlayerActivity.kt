package com.techo.chattv.ui.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.View.OnTouchListener
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.techo.chattv.databinding.ActvityVideoPlayerBinding
import com.techo.chattv.model.Channel
import com.techo.chattv.utils.IPTvRealm
import com.techo.chattv.viewmodel.FavoriteViewModel
import com.techo.chattv.utils.SoundProgressChangeListner
import com.techo.chattv.utils.SoundView
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.Util
import com.google.android.gms.ads.*
import com.techo.chattv.utils.NativeAds
import kotlin.math.abs


class VideoPlayerActivity : AppCompatActivity(), Player.EventListener {

    private lateinit var binding: ActvityVideoPlayerBinding
    private var downy = 0f
    private var endheight = 0f
    private var diffheight = 0f
    private var currentprogress = 0
    private var currentbrightprogress = 0
    private var currentseek = 0
    private var lastx = 0f
    private var putx = 0f
    private var puty = 0f
    private var trackx = 0f
    private var lastprogress = 0
    private var lasttime: Long = 0
    private var selected = 0
    private var isshow = false
    private var scaleFactor = 1.0f
    private var first = true
    private var second = true
    private var third = true
    private var isdonebyus = false
    private var ontouchpos = 0
    private var isplaybackground = false
    private var isorientionchange = false
    private var resizemode = 0
    private var isscalegesture = false
    private var savebright = -1.0f
    private var islock = false
    private var playbackspeed = 5
    var position: Int = 0
    var progress: Long = 0
    var stopTime: Long = 0
    private var startAutoPlay = false
    private var startWindow = 0
    private var startPosition: Long = 0
    private val aspectmode = arrayOf("FIT", "FILL", "ZOOM", "FIXED HEIGHT", "FIXED WIDTH")
    private val resource = intArrayOf(
        com.techo.chattv.R.drawable.ic_zoom_stretch,
        com.techo.chattv.R.drawable.ic_baseline_crop_3_2_24,
        com.techo.chattv.R.drawable.ic_crop_white_24dp,
        com.techo.chattv.R.drawable.ic_zoom_inside,
        com.techo.chattv.R.drawable.ic_zoom_original
    )
    private var handler: Handler? = null
    private var hidehandler: Handler? = null

    private lateinit var playerView: PlayerView
    private var player: SimpleExoPlayer? = null
    private var continuelay: LinearLayout? = null
    private var bottomview: ConstraintLayout? = null
    private var toolbar: ConstraintLayout? = null
    private var dragseek: SeekBar? = null
    private var aspecttext: TextView? = null
    private var mediaItems: List<MediaItem>? = null
    private var channel: Channel? = null

    private lateinit var ipTvRealm: IPTvRealm
    private var favoriteViewModel: FavoriteViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActvityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        favoriteViewModel = ViewModelProvider(this)[FavoriteViewModel::class.java]
        ipTvRealm = IPTvRealm()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

        init()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.continuetextid.text = "Continue from where you stopped."
        } else {
            binding.continuetextid.text = "Continue from\nwhere you stopped."
        }
    }

    override fun onBackPressed() {
        val orientation = this@VideoPlayerActivity.resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        }
        finish()
    }

    private fun init() {
        NativeAds().loadNativeBannerFBAd(this,binding.bannerFrameLay)
        position = intent.getIntExtra("position", 0)
        channel = intent.getParcelableExtra("list")
        checkFavorite(channel?.channelName!!)
        binding.videotitle.text = channel!!.channelName
        continuelay = binding.continuelay
        bottomview = binding.bottomview
        toolbar = binding.toolbar
        dragseek = binding.dragseek
        playerView = binding.player
        if (savebright >= 0) {
            val lp = window.attributes
            lp.screenBrightness = savebright
            window.attributes = lp
        }
        val speedView = binding.speedview
        val unlockbtn = binding.unlockbtn
        val playpausebutton = binding.imageButton
        val touchview = binding.toucher
        val seeklay = binding.seeklay
        val seektime = binding.seektime
        val seekdelay = binding.seekdelay
        val aspectbtn = binding.aspectbtn

        player = SimpleExoPlayer.Builder(this).build()
        playerView.player = player
        intializePlayer()
        playpausebutton.setOnClickListener {
            if (player!!.isPlaying) {
                player!!.pause()
            } else {
                player!!.play()
            }
        }
        binding.rotateview.setOnClickListener {
            rotate()
        }
        binding.backarrow.setOnClickListener { onBackPressed() }
        val runnable: Runnable = object : Runnable {
            override fun run() {
                if (player == null) return
                if (!isdonebyus) dragseek?.progress = player!!.currentPosition.toInt()
                Handler().postDelayed({ this.run() }, 1000)
            }
        }
        val hiderunnable = Runnable {
            if (player == null) return@Runnable
            if (player!!.isPlaying) {
                hideSystemUI()
                bottomview!!.visibility = View.GONE
                toolbar!!.visibility = View.GONE
            }
        }
        aspectbtn.setImageResource(resource[resizemode % 5])
        aspecttext = binding.aspecttext
        aspectbtn.setOnClickListener {
            resizemode++
            aspecttext!!.visibility = View.VISIBLE
            if (resizemode % 5 != 2) {
                playerView.scaleX = 1.0f
                playerView.scaleY = 1.0f
            } else {
                playerView.scaleX = scaleFactor
                playerView.scaleY = scaleFactor
            }
            aspecttext!!.text = aspectmode[resizemode % 5]
            aspectbtn.setImageResource(resource[resizemode % 5])
            playerView.resizeMode = resizemode % 5
            Handler().postDelayed({ aspecttext!!.visibility = View.GONE }, 300)
            Handler().removeCallbacks(hiderunnable)
            Handler().postDelayed(hiderunnable, 4000)
        }
        player!!.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    playpausebutton.setImageResource(com.techo.chattv.R.drawable.ic_baseline_pause_24)
                    binding.endprogress.text = milltominute(player!!.duration)
                    dragseek?.max = player!!.duration.toInt()
                    Handler().postDelayed(runnable, 0)
                } else {
                    playpausebutton.setImageResource(com.techo.chattv.R.drawable.ic_baseline_play_arrow_24)
                    Handler().removeCallbacks(runnable)
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {}
        })
        player!!.setSeekParameters(SeekParameters.CLOSEST_SYNC)
        dragseek?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (b) {
                    player!!.seekTo(i.toLong())
                    seektime.text = milltominute(i.toLong())
                    seekdelay.text = "[" + milltominute((i - ontouchpos).toLong()) + "]"
                }
                binding.currentprogress.text = milltominute(i.toLong())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                ontouchpos = seekBar.progress
                seeklay.visibility = View.VISIBLE
                player!!.pause()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                seeklay.visibility = View.GONE
                player!!.play()
            }
        })
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        val soundView: SoundView = binding.volumeview
        val volumecontainerView = binding.volumecontainer
        val muteview = binding.muteview
        val brightView: SoundView = binding.brightview
        muteview.setOnClickListener {
            if (soundView.progress === 0) {
                muteview.setBackgroundResource(com.techo.chattv.R.drawable.roundbg)
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 7, 0)
                soundView.setProgress(7)
            } else {
                muteview.setBackgroundResource(com.techo.chattv.R.drawable.colorroundbg)
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)
                soundView.setProgress(0)
            }
        }
        brightView.setOnsoundProgressChangeListner(object : SoundProgressChangeListner {
            override fun onchange(progress: Int) {
                binding.brightprogresstext.text = progress.toString() + ""
            }
        })
        soundView.setOnsoundProgressChangeListner(object : SoundProgressChangeListner {
            override fun onchange(progress: Int) {
//                Log.e("change", progress.toString() + "")
                binding.progresstext.text = progress.toString() + ""
                if (progress == 0) {
                    binding.volumeicon.setImageResource(com.techo.chattv.R.drawable.ic_baseline_volume_off_24)
                } else {
                    binding.volumeicon.setImageResource(com.techo.chattv.R.drawable.ic_baseline_volume_up_24)
                    muteview.setBackgroundResource(com.techo.chattv.R.drawable.roundbg)
                }
            }
        })
        val scaleGestureDetector =
            ScaleGestureDetector(this, MyOnScaleGestureListener())
        touchview.setOnTouchListener(OnTouchListener { view, motionEvent ->
//            Log.e("pointer", motionEvent.pointerCount.toString() + "," + motionEvent.action)
            if (!islock) scaleGestureDetector.onTouchEvent(motionEvent)
            if (motionEvent.action == MotionEvent.ACTION_POINTER_2_DOWN && motionEvent.pointerCount == 2) {
                isscalegesture = true
            }
            if (motionEvent.action == MotionEvent.ACTION_DOWN && !isscalegesture) {
                lastx = motionEvent.x
                downy = motionEvent.y
                putx = motionEvent.x
                puty = motionEvent.y
                lasttime = System.currentTimeMillis()
                endheight =
                    downy - resources.getDimensionPixelSize(com.techo.chattv.R.dimen.widthmeasure)
                diffheight = endheight - downy
                currentprogress = soundView.progress
                currentbrightprogress = brightView.progress
                first = true
                second = true
                third = true
                selected = 0
                isdonebyus = false
                trackx = motionEvent.x
                currentseek = dragseek?.progress!!
            } else if (motionEvent.action == MotionEvent.ACTION_MOVE && !isscalegesture) {
                if (islock) return@OnTouchListener false
                val xdistance = motionEvent.x - lastx
                val ydistance = motionEvent.y - downy
                if (first && abs(xdistance) == 0f && abs(ydistance) == 0f) {
                } else if (second && abs(xdistance) < abs(ydistance) || selected == 1) {
                    if (selected == 0) {
                        selected = 1
                        first = false
                        second = true
                        third = false
                        if (motionEvent.x > view.width / 2.0f) {
                            volumecontainerView.visibility = View.VISIBLE
                        } else {
                            binding.brightcontainer.visibility = View.VISIBLE
                        }
                    }
                    val tempwidth = endheight - motionEvent.y
                    val progress: Float = tempwidth * soundView.maxprogess / diffheight
                    val jprogress = soundView.maxprogess - progress
                    if (volumecontainerView.visibility == View.VISIBLE) {
                        val prog = currentprogress + jprogress
                        when {
                            prog > soundView.maxprogess -> soundView.setProgress(soundView.maxprogess)
                            prog < 0 -> soundView.setProgress(0)
                            else -> {
                                audioManager.setStreamVolume(
                                    AudioManager.STREAM_MUSIC,
                                    prog.toInt(),
                                    0
                                )
                                soundView.setProgress(prog.toInt())
                            }
                        }
                    } else {
                        val prog = currentbrightprogress + jprogress
                        when {
                            prog > brightView.maxprogess -> brightView.setProgress(brightView.maxprogess)
                            prog < 0 -> brightView.setProgress(0)
                            else -> {
                                val brightness = prog / 16.0f
                                val lp = window.attributes
                                lp.screenBrightness = brightness
                                window.attributes = lp
                                brightView.setProgress(prog.toInt())
                                brightView.progress = prog.toInt()
                            }
                        }
                    }
                } else if (third || selected == 2) {
                    if (selected == 0) {
                        if (player!!.isPlaying) {
                            isdonebyus = true
                            player!!.pause()
                        }
                        second = false
                        first = false
                        third = true
                        selected = 2
                        playpausebutton.visibility = View.GONE
                        bottomview!!.visibility = View.VISIBLE
                        toolbar!!.visibility = View.VISIBLE
                        seeklay.visibility = View.VISIBLE
                    }
                    val progress = (60000 * (motionEvent.x - trackx) / touchview.width).toInt()
                    if (lastprogress != progress) {
                        player!!.seekTo((currentseek + progress).toLong())
                        dragseek?.progress = currentseek + progress
                        seektime.text = milltominute(dragseek?.progress!!.toLong())
                        seekdelay.text = "[" + milltominute(progress.toLong()) + "]"
                    }
                    lastprogress = progress
                }
                lastx = motionEvent.x
                downy = motionEvent.y
            } else if (motionEvent.pointerCount == 1 && motionEvent.action == MotionEvent.ACTION_UP) {
                if (isscalegesture) {
                    isscalegesture = false
                } else {
                    seeklay.visibility = View.GONE
                    if (isdonebyus) player!!.play()
                    isdonebyus = false
                    if (islock) {
                        if (unlockbtn.visibility == View.GONE) {
                            unlockbtn.visibility = View.VISIBLE
                            Handler().postDelayed({ unlockbtn.visibility = View.GONE }, 2000)
                        }
                    } else {
                        if (motionEvent.x == putx && motionEvent.y == puty && System.currentTimeMillis() - lasttime <= 1000) {
                            speedView.visibility = View.GONE
                            if (isshow) {
                                hideSystemUI()
                                bottomview!!.visibility = View.GONE
                                toolbar!!.visibility = View.GONE
                            } else {
                                showSystemUI()
                                playpausebutton.visibility = View.VISIBLE
                                bottomview!!.visibility = View.VISIBLE
                                toolbar!!.visibility = View.VISIBLE
                                Handler().postDelayed(hiderunnable, 4000)
                            }
                        } else {
                            Handler().postDelayed({
                                hideSystemUI()
                                volumecontainerView.visibility = View.INVISIBLE
                                binding.brightcontainer.visibility = View.INVISIBLE
                                bottomview!!.visibility = View.GONE
                                toolbar!!.visibility = View.GONE
                                playpausebutton.visibility = View.VISIBLE
                            }, 300)
                        }
                    }
                }
            } else if (isscalegesture) {
            }
            false
        })
        binding.lockbtn.setOnClickListener {
            islock = true
            hideSystemUI()
            bottomview!!.visibility = View.GONE
            toolbar!!.visibility = View.GONE
            unlockbtn.visibility = View.VISIBLE
            Handler().postDelayed({ unlockbtn.visibility = View.GONE }, 2000)
        }
        unlockbtn.setOnClickListener {
            islock = false
            unlockbtn.visibility = View.GONE
            showSystemUI()
            bottomview!!.visibility = View.VISIBLE
            toolbar!!.visibility = View.VISIBLE
            Handler().postDelayed({
                hideSystemUI()
                bottomview!!.visibility = View.GONE
                toolbar!!.visibility = View.GONE
            }, 4000)
        }
        soundView.setMaxprogress(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC))
        soundView.progress = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        var tempbright = window.attributes.screenBrightness
        if (tempbright < 0) tempbright = 0.5f
        val mybright = (15 * tempbright).toInt()
        brightView.setMaxprogress(soundView.maxprogess)
        brightView.progress = mybright
        intializePlayer()
        val speedbtn = binding.speedBtnTv
        speedbtn.setOnClickListener { speedView.visibility = View.VISIBLE }
        val speedseekbar = binding.speedseekbar
        val speedtextview = binding.speedtextview
        speedseekbar.progress = playbackspeed
        speedbtn.text = (0.5f + playbackspeed / 10.0f).toString() + "X"
        speedtextview.text = (0.5f + playbackspeed / 10.0f).toString() + "X"
        speedseekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                playbackspeed = i
                val param = PlaybackParameters(0.5f + playbackspeed / 10.0f)
                player!!.playbackParameters = param
                speedbtn.text = (0.5f + playbackspeed / 10.0f).toString() + "X"
                speedtextview.text = (0.5f + playbackspeed / 10.0f).toString() + "X"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                Handler().postDelayed({ speedView.visibility = View.GONE }, 3000)
            }
        })
        startPlayer()

        binding.exoFavoriteButton.setOnClickListener {
            if (player != null && player!!.currentMediaItem != null) {
                if (ipTvRealm.isFavorite(channel!!.channelName)) {
                    ipTvRealm.deleteFavorite(channel!!)
                    binding.exoFavoriteIcon.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            com.techo.chattv.R.drawable.ic_fav_unfeel, null
                        )
                    )
                } else {
                    ipTvRealm.setFavorite(channel!!)
                    binding.exoFavoriteIcon.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            com.techo.chattv.R.drawable.ic_fav_feel, null
                        )
                    )
                }
                favoriteViewModel!!.updateFavorite()
            }
        }

    }

    private fun intializePlayer() {
        val intent = intent
        mediaItems = createMediaItems(intent)
        val defaultTrackSelector = DefaultTrackSelector(this)
        defaultTrackSelector.setParameters(
            defaultTrackSelector.buildUponParameters().setMaxVideoSize(200, 200)
        )

        player!!.playWhenReady = startAutoPlay
        playerView.player = player
        val haveStartPosition = startWindow != C.INDEX_UNSET
        if (haveStartPosition) {
            player!!.seekTo(startWindow, startPosition)
        }
        player!!.addListener(this)
        player!!.setMediaItem(mediaItems!![0])
        player!!.prepare()
        player!!.playWhenReady = true
    }

    private fun rotate() {
        val orientation = this@VideoPlayerActivity.resources.configuration.orientation
        isorientionchange = true
        requestedOrientation = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        }
    }


    inner class MyOnScaleGestureListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            if (scaleFactor > 4 || scaleFactor < 0.25) {
                return true
            } else {
                playerView.scaleX = scaleFactor
                playerView.scaleY = scaleFactor
                val per = 1000 * scaleFactor / 4.5f
                aspecttext!!.text = "$per%"
            }
            return true
        }

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            aspecttext!!.visibility = View.VISIBLE
            scaleFactor = playerView.scaleX
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {
            aspecttext!!.visibility = View.GONE
            scaleFactor = playerView.scaleX
        }
    }

    private fun milltominute(milliseconds: Long): String {
        stopTime = milliseconds
        var milliseconds = milliseconds
        var v = false
        if (milliseconds < 0) {
            v = true
            milliseconds = abs(milliseconds)
        }
        val seconds = (milliseconds / 1000).toInt() % 60
        val minutes = (milliseconds / (1000 * 60) % 60).toInt()
        val hours = (milliseconds / (1000 * 60 * 60) % 24).toInt()
        var time = ""
        time = if (hours == 0) {
            String.format("%02d:%02d", minutes, seconds)
        } else {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }

        return if (v) "-$time" else time
    }

    override fun onPause() {
        super.onPause()
        if (!isplaybackground) pausePlayer()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
//        outState.putInt("currentitem", position)
        outState.putFloat("windowbright", window.attributes.screenBrightness)
        if (isorientionchange) outState.putLong("currentitemseek", player!!.currentPosition)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
//        Log.e("restorecall", position.toString() + "")
    }

    private fun pausePlayer() {
        if (player == null) return
        player!!.playWhenReady = false
        player!!.playbackState
    }

    private fun startPlayer() {
        player!!.play()
        hideSystemUI()
    }

    override fun onDestroy() {
        hideSystemUI()
        if (player != null) {
            player!!.stop()
            player!!.setVideoSurface(null)
            player!!.release()
            progress = player!!.currentPosition
        }
        super.onDestroy()
    }

    override fun onDetachedFromWindow() {
//        Log.e("detach", "onDetachedFromWindow")
        //
        super.onDetachedFromWindow()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
    }

    private fun hideSystemUI() {
        isshow = false
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_IMMERSIVE
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // Hide the nav bar and status bar
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
        if (continuelay != null) {
            val layoutParams = continuelay!!.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.setMargins(
                layoutParams.leftMargin,
                layoutParams.topMargin,
                layoutParams.rightMargin,
                resources.getDimensionPixelSize(com.techo.chattv.R.dimen.bottomspacesmall)
            )
            continuelay!!.layoutParams = layoutParams
        }
    }

    private fun showSystemUI() {
//        isshow = true
//        val decorView = window.decorView
//        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
//        if (continuelay != null) {
//            val layoutParams = continuelay!!.layoutParams as ConstraintLayout.LayoutParams
//            layoutParams.setMargins(
//                layoutParams.leftMargin,
//                layoutParams.topMargin,
//                layoutParams.rightMargin,
//                resources.getDimensionPixelSize(com.globle.iptvvideoplayer.R.dimen.bottomspacelarge)
//            )
//            continuelay!!.layoutParams = layoutParams
//        }
    }

    private fun createMediaItems(intent: Intent): List<MediaItem> {
        val mediaItems: MutableList<MediaItem> = ArrayList()
        mediaItems.add(setMediaItem(channel!!))
        return mediaItems
    }

    private fun setMediaItem(ch: Channel): MediaItem {
        return MediaItem.Builder()
            .setUri(ch.channelUrl)
            .setTag(ch.channelName)
            .setDrmLicenseUri(ch.channelDrmKey)
            .setDrmUuid(Util.getDrmUuid(C.WIDEVINE_UUID.toString()))
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(ch.channelName)
                    .setMediaUri(Uri.parse(ch.channelUrl))
                    .setArtworkUri(Uri.parse(ch.channelImg))
                    .setArtist(ch.channelGroup).build()
            )
            .build()
    }

    private fun checkFavorite(name: String) {
        Log.e("checkFavorite", name)
        if (ipTvRealm.isFavorite(name)) {
            binding.exoFavoriteIcon.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    com.techo.chattv.R.drawable.ic_fav_feel,
                    null
                )
            )
        } else {
            binding.exoFavoriteIcon.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    com.techo.chattv.R.drawable.ic_fav_unfeel,
                    null
                )
            )
        }
    }
}