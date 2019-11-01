package osmandroid.iptv.home;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.rtmp.RtmpDataSourceFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.List;

import spencerstudios.com.jetdblib.JetDB;

public class PlayerActivity extends AppCompatActivity implements SimpleExoPlayer.EventListener{

    private static final String TAG = "TAG";
    PlayerView exoPlayerView;
    SimpleExoPlayer exoPlayer;
    //http://183.82.112.212:8082/hls3/mqtv/index.m3u8
    //rtmp://stream.logosys.in:1935/siasat/siasat
    //rtmp://103.250.39.13:1935/dw3/4tvnews.flv
    String videoPath = "rtmp://stream.logosys.in:1935/siasat/siasat.flv";

    ImageButton next,prev,play,pause,rewd,frwd,toggle,vr,shuffle;
    Button retry;
    int pos = 0;
    List<SimpleM3UParser.M3U_Entry> channelList;


    int onclicktype = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        exoPlayerView = findViewById(R.id.video_view);
        exoPlayer = ExoPlayerFactory.newSimpleInstance(this);

        channelList = new ArrayList<>();

        videoPath = getIntent().getStringExtra("video_path");
        pos = getIntent().getIntExtra("pos",0);

        loadVideo(videoPath);



        try {
            channelList = JetDB.getListOfObjects(this, SimpleM3UParser.M3U_Entry.class, "channelList");

        }catch (Exception ignored){}



        findViewsWithIDs();



    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {

            case Player.STATE_BUFFERING:
                Log.d(TAG, "onPlayerStateChanged: STATE_BUFFERING");
                break;
            case Player.STATE_ENDED:
                Log.d(TAG, "onPlayerStateChanged: STATE_ENDED");
                break;
            case Player.STATE_IDLE:
                Log.d(TAG, "onPlayerStateChanged: STATE_IDLE");
                break;
            case Player.STATE_READY:
                Log.d(TAG, "onPlayerStateChanged: STATE_READY");
                break;
            default:
                Log.d(TAG, "onPlayerStateChanged: default");
                break;
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        switch (error.type) {
            case ExoPlaybackException.TYPE_SOURCE:
                Log.d(TAG, "TYPE_SOURCE: " + error.getSourceException().getMessage());

                switch (onclicktype)
                {
                    case 0:
                        retry.setVisibility(View.VISIBLE);
                        retry.setEnabled(true);
                        break;
                    case 1:
                        switchNext();
                        break;
                    case 2:
                        switchPrevious();
                        break;
                }


            case ExoPlaybackException.TYPE_RENDERER:
                Log.e(TAG, "TYPE_RENDERER: " + error.toString());
                break;

            case ExoPlaybackException.TYPE_UNEXPECTED:
                Log.e(TAG, "TYPE_UNEXPECTED: " + error.toString());
                break;
            case ExoPlaybackException.TYPE_OUT_OF_MEMORY:
                Log.e(TAG, "TYPE_OUT_OF_MEMORY: " + error.toString());
                break;
            case ExoPlaybackException.TYPE_REMOTE:
                Log.e(TAG, "TYPE_REMOTE: " + error.toString());
                break;
        }
    }


    void loadVideo(String videoPath)
    {
        Log.d(TAG, "loadVideo: URL: "+videoPath);
        try
        {
            Uri videoUri = Uri.parse(videoPath);

            DataSource.Factory dataSourceFactory =
                    getDataSourceFactory(videoPath);
            MediaSource mediaSource = getMediaSource(videoUri,dataSourceFactory);

            exoPlayer.prepare(mediaSource);
            exoPlayerView.setPlayer(exoPlayer);
            exoPlayer.setPlayWhenReady(true);


            exoPlayer.addListener(this);

        }catch (Exception e)
        {
            Log.d(TAG, "loadVideo Error: "+e.toString());
            retry.setVisibility(View.VISIBLE);
            retry.setEnabled(true);
        }


    }


    DataSource.Factory getDataSourceFactory(String url)
    {
        String str = url.substring(0,4);

        switch (str)
        {
            case "rtmp":
                return new RtmpDataSourceFactory();

            case "http":
            default:
                    return new DefaultHttpDataSourceFactory(Util.getUserAgent(this, "app-name"));

        }
    }

    MediaSource getMediaSource(Uri uri,DataSource.Factory dataSourceFactory)
    {
        String str = uri.toString().substring(0,4);
        switch (str)
        {
            case "http":
                return new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);

            case "rtmp":
            default:
                return new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
        }
    }


    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //Activity Lifecycle functions

    @Override
    protected void onResume() {
        super.onResume();
        exoPlayer.setPlayWhenReady(true);
    }

    @Override
    protected void onPause() {
        exoPlayer.setPlayWhenReady(false);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        releasePlayer();
        super.onDestroy();
    }


    void findViewsWithIDs()
    {
        play = findViewById(R.id.exo_play);
        pause = findViewById(R.id.exo_pause);
        shuffle = findViewById(R.id.exo_shuffle);
        toggle = findViewById(R.id.exo_repeat_toggle);
        prev = findViewById(R.id.exo_prev);
        rewd = findViewById(R.id.exo_rew);
        frwd = findViewById(R.id.exo_ffwd);
        next = findViewById(R.id.exo_next);
        vr = findViewById(R.id.exo_vr);
        retry = findViewById(R.id.exo_retry);

        retry.setVisibility(View.INVISIBLE);
        retry.setEnabled(false);



/*
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(PlayerActivity.this,"play",Toast.LENGTH_SHORT).show();
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
*/


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclicktype=1;
                switchNext();
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclicktype=2;
                switchPrevious();
            }
        });

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclicktype=0;
                loadVideo(channelList.get(pos).getUrl());
                retry.setVisibility(View.INVISIBLE);
                retry.setEnabled(false);
            }
        });



        rewd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        frwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        vr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



    }


    public void switchNext()
    {
        exoPlayer.stop();
        if(pos == channelList.size()-1)
            pos=0;
        else pos++;
        loadVideo(channelList.get(pos).getUrl());
        Log.d(TAG, "onClick: "+channelList.get(pos).getName());
    }

    public void switchPrevious()
    {
        exoPlayer.stop();
        if(pos == 0)
            pos=channelList.size()-1;
        else pos--;
        loadVideo(channelList.get(pos).getUrl());
        Log.d(TAG, "onClick: "+channelList.get(pos).getName());
    }
}


