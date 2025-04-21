package com.example.airsimapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CommandAdapter extends RecyclerView.Adapter<CommandAdapter.CommandViewHolder> {
    private ArrayList<AutopilotCommand> commandList;
    private OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(int position);
    }


    public CommandAdapter(ArrayList<AutopilotCommand> commandList, OnItemClickListener listener) {
        this.commandList = commandList;
        this.listener = listener;
    }

    public static class CommandViewHolder extends RecyclerView.ViewHolder {
        TextView commandText;
        ImageView commandIcon;

        public CommandViewHolder(View itemView, OnItemClickListener listener) {

            super(itemView);
            commandText = itemView.findViewById(R.id.commandTextView);
            commandIcon = itemView.findViewById(R.id.commandImageView);
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position);
                }
            });
        }
    }

    public CommandAdapter(ArrayList<AutopilotCommand> commandList) {
        this.commandList = commandList;
    }

    @NonNull
    @Override
    public CommandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_command, parent, false);
        return new CommandViewHolder(view, listener);


    }

    @Override
    public void onBindViewHolder(@NonNull CommandViewHolder holder, int position) {
        AutopilotCommand command = commandList.get(position);

        holder.commandText.setText(command.getId()); // Or customize this

        switch (command.getId()) {
            case "Heading&Speed":
                holder.commandIcon.setImageResource(R.drawable.heading);
                break;
            case "GPS":
                holder.commandIcon.setImageResource(R.drawable.gps);
                break;
            case "LoiterPattern":
                holder.commandIcon.setImageResource(R.drawable.loiter);
                break;
            default:
                holder.commandIcon.setImageResource(R.drawable.gps);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return commandList.size();
    }

    public void updateCommands(ArrayList<AutopilotCommand> newCommands) {
        commandList = newCommands;
        notifyDataSetChanged();
    }
}

