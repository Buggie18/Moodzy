package com.example.moodzy;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private String[] moodTitles;
    private TypedArray moodImages;
    public int selectedPosition = -1;

    public void resetSelection() {
        selectedPosition = -1;
        notifyDataSetChanged(); // Notify the adapter to refresh the views
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkBox;
        ImageView imageView;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
            this.imageView = (ImageView) itemView.findViewById(R.id.moodImage);
            this.textView = (TextView) itemView.findViewById(R.id.moodText);
        }
    }

    public RecyclerViewAdapter(Context context, String[] moodTitles, TypedArray moodImages) {
        this.context = context;
        this.moodTitles = moodTitles;
        this.moodImages = moodImages;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mood, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        holder.textView.setText(moodTitles[position]);
        holder.imageView.setImageResource(moodImages.getResourceId(position, 0));
        holder.checkBox.setChecked(position == selectedPosition);

        holder.checkBox.setOnClickListener(v -> {
            if (holder.checkBox.isChecked()) {
                if (selectedPosition != position) {
                    selectedPosition = position;
                    notifyDataSetChanged();
                }
            } else {
                selectedPosition = -1;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return moodTitles.length;
    }
}
