package fr.bl00k.lafac.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fr.bl00k.lafac.activity.ChatActivity;
import fr.bl00k.lafac.model.Message;

public class ChatMessageAdapter extends RecyclerView.Adapter {
    public ChatMessageAdapter(List<Message> messageList, String stringExtra, ChatActivity chatActivity) {
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
