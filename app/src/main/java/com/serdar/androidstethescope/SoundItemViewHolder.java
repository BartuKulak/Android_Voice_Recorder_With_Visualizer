package com.serdar.androidstethescope;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SoundItemViewHolder extends RecyclerView.ViewHolder {
    TextView audioName;
    TextView duration;
    ImageButton deleteButton;
    ImageButton shareButton;

    public SoundItemViewHolder(@NonNull View itemView) {
        super(itemView);
        audioName = itemView.findViewById(R.id.audioname);
        duration = itemView.findViewById(R.id.audioduration);
        deleteButton = itemView.findViewById(R.id.deletebutton);
        shareButton = itemView.findViewById(R.id.sharebutton);
    }

    public void setData(AudioModel audioModel, OnAudioEventsListener listener) {
        audioName.setText(audioModel.getName());
        duration.setText(audioModel.getDuration());
        deleteButton.setOnClickListener(v -> {
            listener.onAudioDeleted(audioModel);
        });

        shareButton.setOnClickListener(v -> listener.onAudioShare(audioModel));

        itemView.setOnClickListener(v -> {
            listener.onAudioClick(audioModel);
        });
    }

    public interface OnAudioEventsListener {
        void onAudioClick(AudioModel audioModel);

        void onAudioDeleted(AudioModel audioModel);

        void onAudioShare(AudioModel audioModel);
    }
}
