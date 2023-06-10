package practice.shrishri1108.mymusicplayer.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import practice.shrishri1108.mymusicplayer.R;
import practice.shrishri1108.mymusicplayer.databinding.ActivityMusicPlayerBinding;

public class MusicPlayer extends AppCompatActivity {

    private ActivityMusicPlayerBinding bindings;
    private int currentPlayedSongPos;
    private List<File> audioList;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        bindings = DataBindingUtil.setContentView(MusicPlayer.this, R.layout.activity_music_player);

        audioList = (List<File>) getIntent().getSerializableExtra("audio");
        if (audioList == null) {
            audioList = new ArrayList<>();
        }
        currentPlayedSongPos = getIntent().getIntExtra("selected_pos", -1);


        init();

    }

    private void init() {
        initAudio();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        bindings.btnPlays.setOnClickListener(v -> playMyAudio());

        bindings.btnPrevious.setOnClickListener( v-> {
            if (currentPlayedSongPos>0 ) {
                currentPlayedSongPos--;
                mediaPlayer.pause();
                playMyAudio();
            }
        });

        bindings.btnNext.setOnClickListener( v-> {
            if (currentPlayedSongPos< audioList.size()-1 ) {
                currentPlayedSongPos++ ;
                mediaPlayer.pause();
                playMyAudio();
            }
        });

        playMyAudio();
    }

    private void initAudio() {

        if (audioList.size() > 0) {
            bindings.btnPlays.setEnabled(true);
            bindings.btnNext.setEnabled(true);
            bindings.btnPrevious.setEnabled(true);
        } else {
            Toast.makeText(this, "No Music Found. ", Toast.LENGTH_SHORT).show();
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(mp -> {
            if (currentPlayedSongPos< audioList.size()-1  ) {
                currentPlayedSongPos++ ;
                mediaPlayer.pause();
                playMyAudio();
            }
        });


    }



    @SuppressLint("UseCompatLoadingForDrawables")
    private void playMyAudio() {

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            bindings.btnPlays.setImageDrawable( getDrawable(R.drawable.play));
        } else {
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(audioList.get(currentPlayedSongPos).getAbsolutePath());
                mediaPlayer.prepare();
                mediaPlayer.start();
                bindings.btnPlays.setImageDrawable(getDrawable(R.drawable.pause));
            } catch (IOException e) {
                Toast.makeText(this, "Error in playing ", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }


        try {
            final MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(audioList.get(currentPlayedSongPos).getAbsolutePath());
            byte[] pict = mediaMetadataRetriever.getEmbeddedPicture();
            if (pict != null) {
                InputStream is = new ByteArrayInputStream(mediaMetadataRetriever.getEmbeddedPicture());
                Bitmap bitmap = BitmapFactory.decodeStream(is);
//                final Bitmap bitmap = BitmapFactory.decodeByteArray(pict, 0, pict.length);
                bindings.audioImage.setImageBitmap(bitmap);
            }
            else {

                bindings.audioImage.setImageResource( R.drawable.no_photo);
            }
            mediaMetadataRetriever.release();
        } catch ( Exception e) {
//            throw new RuntimeException(e);
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }
}