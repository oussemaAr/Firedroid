package tn.odc.firedroid.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tn.odc.firedroid.R;
import tn.odc.firedroid.model.Message;
import tn.odc.firedroid.utils.Utils;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder> {

    /**
     * Message Sent constant value
     */
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    /**
     * Message Received constant value
     */
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private List<Message> conversation;
    private Context context;

    public MessagesAdapter(Context context, List<Message> conversation) {
        this.conversation = conversation;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            v = LayoutInflater.from(context).inflate(R.layout.item_message_sent, parent, false);
        } else {
            v = LayoutInflater.from(context).inflate(R.layout.item_message_received, parent, false);
        }
        return new MyViewHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(conversation.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        if (conversation.get(position).userUid.equals(Utils.localUid)) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else
            return VIEW_TYPE_MESSAGE_RECEIVED;
    }

    @Override
    public int getItemCount() {
        return conversation.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView messageText, timeText, nameText;
        private int viewType;

        MyViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;

            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
            if (viewType== VIEW_TYPE_MESSAGE_RECEIVED)
                nameText = itemView.findViewById(R.id.sender_name);
        }


        void bind(Message message) {
            messageText.setText(message.messageBody);
            timeText.setText(Utils.formatDateTime(message.sentAt));
            if (viewType == VIEW_TYPE_MESSAGE_RECEIVED)
                nameText.setText(message.userName);
        }
    }
}
