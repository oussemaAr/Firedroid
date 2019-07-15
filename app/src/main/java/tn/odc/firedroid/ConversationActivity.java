package tn.odc.firedroid;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.smartreply.FirebaseSmartReply;
import com.google.firebase.ml.naturallanguage.smartreply.FirebaseTextMessage;
import com.google.firebase.ml.naturallanguage.smartreply.SmartReplySuggestion;
import com.google.firebase.ml.naturallanguage.smartreply.SmartReplySuggestionResult;

import java.util.ArrayList;

import tn.odc.firedroid.adapters.MessagesAdapter;
import tn.odc.firedroid.model.Message;
import tn.odc.firedroid.utils.Utils;

public class ConversationActivity extends AppCompatActivity {

    private static final String TAG = "ConversationActivity";

    private DatabaseReference mReference;
    private RecyclerView recyclerView;
    private EditText mMessage;
    private ArrayList<FirebaseTextMessage> conversationArray = new ArrayList<>();

    private ArrayList<Message> messageArrayList = new ArrayList<>();
    private MessagesAdapter messagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mReference = FirebaseDatabase.getInstance().getReference("group");
        mMessage = findViewById(R.id.input_message);
        recyclerView = findViewById(R.id.recycler_view);
        messagesAdapter = new MessagesAdapter(this, messageArrayList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messagesAdapter);
        retreiveData();


    }

    private void retreiveData() {
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageArrayList.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Log.e(TAG, "onDataChange: " + child.getValue(Message.class).userUid);
                    Message value = child.getValue(Message.class);
                    messageArrayList.add(value);
                    if (value.userUid.equals(Utils.localUid)) {
                        conversationArray.add(0,FirebaseTextMessage.createForLocalUser(value.messageBody, value.sentAt));
                    } else {
                        conversationArray.add(0,FirebaseTextMessage.createForRemoteUser(value.messageBody, value.sentAt, "local"));
                    }
                }
                createSmartReply();
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Retrieve smart reply from MLKit
     */
    private void createSmartReply() {
//        FirebaseSmartReply smartReply = FirebaseNaturalLanguage.getInstance().getSmartReply();
//        smartReply.suggestReplies(conversationArray)
//                .addOnSuccessListener(new OnSuccessListener<SmartReplySuggestionResult>() {
//                    @Override
//                    public void onSuccess(SmartReplySuggestionResult result) {
//                        if (result.getStatus() == SmartReplySuggestionResult.STATUS_SUCCESS) {
//                            for (SmartReplySuggestion suggestion : result.getSuggestions()) {
//                                Log.e(TAG, "onSuccess: "+suggestion.getText() );
//                            }
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });
    }

    /**
     * Send Message to database
     */
    public void sendMessage(View view) {
        Message message = new Message();
        message.messageBody = mMessage.getText().toString();
        message.userName = Utils.pseudo;
        message.sentAt = System.currentTimeMillis();
        message.userUid = Utils.localUid;

        mReference.push().setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(ConversationActivity.this, "Message Send", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
