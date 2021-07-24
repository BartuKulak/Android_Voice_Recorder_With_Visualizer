package com.serdar.androidstethescope;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SoundItemAdapter extends RecyclerView.Adapter<SoundItemViewHolder> {
    Context context;
    List<AudioModel> audioModelList;
    LayoutInflater layoutInflater;
    SoundItemViewHolder.OnAudioEventsListener listener;

    public SoundItemAdapter(Context context, List<AudioModel> audioModelList, SoundItemViewHolder.OnAudioEventsListener listener) {
        this.context = context;
        this.audioModelList = audioModelList;
        this.listener = listener;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public SoundItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.sound_list_item, parent, false);
        return new SoundItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SoundItemViewHolder holder, int position) {
        AudioModel audioModel = audioModelList.get(position);
        holder.setData(audioModel, listener);
    }

    @Override
    public int getItemCount() {
        if (audioModelList.isEmpty())
            return 0;
        else
            return audioModelList.size();
    }
}
