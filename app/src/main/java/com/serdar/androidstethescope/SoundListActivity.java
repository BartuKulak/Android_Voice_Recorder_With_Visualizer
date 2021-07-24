package com.serdar.androidstethescope;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

public class SoundListActivity extends AppCompatActivity {
    String filePath;
    File directory;
    File[] files;
    List<AudioModel> audioModelList;
    SoundItemAdapter adapter;
    RecyclerView soundList;

    ImageButton playButton;
    SeekBar seekAudio;
    TextView audioName;
    TextView duration;

    boolean isPlaying = false;

    MediaPlayer mediaPlayer;
    AudioModel playingModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_list);
        soundList = findViewById(R.id.soundlist);
        playButton = findViewById(R.id.playbutton);
        seekAudio = findViewById(R.id.seekBar);
        audioName = findViewById(R.id.audioname);
        duration = findViewById(R.id.duration);

        filePath = getFilesDir().getAbsolutePath() + "/";
        directory = new File(filePath);
        files = directory.listFiles(pathname -> pathname.getPath().endsWith(".mp3"));
        audioModelList = new ArrayList<>();
        if (files.length > 0) {
            for (File audioFile : files) {
                AudioModel audioModel = new AudioModel(audioFile.getPath(), audioFile.getName(), getDuration(audioFile), getDurationMs(audioFile));
                audioModelList.add(audioModel);
            }
        }

        playButton.setOnClickListener(v -> playAudioFile(playingModel));

        adapter = new SoundItemAdapter(SoundListActivity.this, audioModelList, new SoundItemViewHolder.OnAudioEventsListener() {
            @Override
            public void onAudioClick(AudioModel audioModel) {
                playAudioFile(audioModel);
            }

            @Override
            public void onAudioDeleted(AudioModel audioModel) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SoundListActivity.this)
                        .setMessage(audioModel.getName() + " will be deleted. Are you sure?" )
                        .setPositiveButton("Ok", (dialog, which) -> {
                            if (playingModel == audioModel)
                                playingModel = null;
                            if (isPlaying)
                                playAudioFile(audioModel);

                            new File(audioModel.getPath()).delete();
                            audioModelList.remove(audioModel);
                            adapter.notifyDataSetChanged();
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {

                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();

            }

            @Override
            public void onAudioShare(AudioModel audioModel) {
                shrareAudioFile(audioModel);
            }
        });
        soundList.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(SoundListActivity.this);
        soundList.setLayoutManager(layoutManager);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void playAudioFile(AudioModel audioModel) {
        if (isPlaying) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            isPlaying = false;
            seekAudio.setProgress(0);
            playButton.setImageResource(R.drawable.ic_play);
        } else {
            try {
                playingModel = audioModel;
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(audioModel.getPath());
                mediaPlayer.prepare();
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(mp -> {
                    isPlaying = false;
                    playButton.setImageResource(R.drawable.ic_play);
                });
                playButton.setImageResource(R.drawable.ic_pause_black);
                audioName.setText(audioModel.getName());
                duration.setText(audioModel.getDuration());
                seekAudio.setMax(audioModel.durationMs / 1000);
                isPlaying = true;
                Handler mHandler = new Handler();
                SoundListActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isPlaying) {
                            if (mediaPlayer != null) {
                                int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                                seekAudio.setProgress(mCurrentPosition);
                            }
                            mHandler.postDelayed(this, 1000);
                        }
                    }
                });

                seekAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser && mediaPlayer != null && isPlaying) {
                            mediaPlayer.seekTo(progress * 1000);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
            } catch (Exception ex) {

            }
        }
    }

    private String getDuration(File audioFile) {
        int millSecond = getDurationMs(audioFile);

        long milis = (millSecond % 1000) / 100;
        long seconds = (millSecond / 1000) % 60;
        long minutes = (millSecond / (1000 * 60)) % 60;
        long hours = (millSecond / (1000 * 60 * 60));

        StringBuilder formattedString = new StringBuilder();
        Formatter formatter = new Formatter(formattedString, Locale.getDefault());

        if (hours > 0)
            formatter.format("%02d:%02d:%02d.%01d", hours, minutes, seconds, milis);
        else
            formatter.format("%02d:%02d.%01d", minutes, seconds, milis);

        return formattedString.toString();
    }

    private int getDurationMs(File audioFile) {
        Uri uri = Uri.parse(audioFile.getAbsolutePath());
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(SoundListActivity.this, uri);
        String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return Integer.parseInt(duration);
    }

    private void shrareAudioFile(AudioModel audioModel) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        File file = new File(audioModel.getPath());
        Uri uri = FileProvider.getUriForFile(SoundListActivity.this, SoundListActivity.this.getApplicationContext().getPackageName() + ".provider", file);
        sharingIntent.setType("audio/mpeg");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(sharingIntent, "Paylaş"));
    }
}