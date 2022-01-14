package com.example.college_students_communication_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.college_students_communication_app.Adapters.GroupMessageAdapter;
import com.example.college_students_communication_app.contracts.ChatDataReaderContract;
import com.example.college_students_communication_app.contracts.ChatReaderDbHelper;
import com.example.college_students_communication_app.databinding.ActivityGroupChatBinding;
import com.example.college_students_communication_app.models.Chat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GroupChatActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityGroupChatBinding binding;

    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private String messageSenderID;
    private String groupCode;
    ChatReaderDbHelper dbHelper;
    int count;

    private final List<Chat> chats = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private GroupMessageAdapter groupMessageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mAuth = FirebaseAuth.getInstance();
        messageSenderID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();

        dbHelper = new ChatReaderDbHelper(GroupChatActivity.this);

        groupCode = getIntent().getExtras().get("groupCode").toString();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        actionBar.setTitle(groupCode);

        groupMessageAdapter = new GroupMessageAdapter(chats);
        linearLayoutManager = new LinearLayoutManager(this);
        binding.groupChatMessages.setLayoutManager(linearLayoutManager);
        binding.groupChatMessages.setAdapter(groupMessageAdapter);

        binding.sendMessageButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.sendMessageButton){
            sendChatMessage();
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        chats.clear();
        chats.addAll(getChatsFromSqlLite());
        RootRef.child("Chats").child(groupCode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    saveChatToSqlite(chat);

                    RootRef.child("Chats").child(groupCode).removeValue();

                    chats.clear();
                    chats.addAll(getChatsFromSqlLite());

                    groupMessageAdapter.notifyDataSetChanged();

                    binding.groupChatMessages.smoothScrollToPosition(binding.groupChatMessages.getAdapter().getItemCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        RootRef.child("Chats").child(groupCode).removeEventListener(ValueEventListener);
    }

    private void sendChatMessage()
    {
        String chatText = binding.inputMessage.getText().toString();
        long time = System.currentTimeMillis();

        if (!TextUtils.isEmpty(chatText))
        {
            Chat chat = new Chat(chatText, messageSenderID, time, groupCode);

            RootRef.child("Chats").child(groupCode).setValue(chat).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if (!task.isSuccessful())
                    {
                        Toast.makeText(GroupChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                    binding.inputMessage.setText("");
                }
            });
        }
    }

    public void saveChatToSqlite(Chat chat){

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long newRowId = db.insert(ChatDataReaderContract.ChatDataEntry.TABLE_NAME, null, chat.getChatValues());

    }

    public List<Chat> getChatsFromSqlLite(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                ChatDataReaderContract.ChatDataEntry.COLUMN_NAME_MESSAGE,
                ChatDataReaderContract.ChatDataEntry.COLUMN_NAME_SENDER,
                ChatDataReaderContract.ChatDataEntry.COLUMN_NAME_GROUP_CODE,
                ChatDataReaderContract.ChatDataEntry.COLUMN_NAME_TIME
        };

        String selection = ChatDataReaderContract.ChatDataEntry.COLUMN_NAME_GROUP_CODE + " = ?";
        String[] selectionArgs = { groupCode };

        String sortOrder = ChatDataReaderContract.ChatDataEntry.COLUMN_NAME_TIME + " ASC";

        Cursor cursor = db.query(
                ChatDataReaderContract.ChatDataEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        List<Chat> chats = new ArrayList<>();
        while(cursor.moveToNext()) {
            String message = cursor.getString(cursor.getColumnIndexOrThrow(ChatDataReaderContract.ChatDataEntry.COLUMN_NAME_MESSAGE));
            String sender = cursor.getString(cursor.getColumnIndexOrThrow(ChatDataReaderContract.ChatDataEntry.COLUMN_NAME_SENDER));
            String groupCode = cursor.getString(cursor.getColumnIndexOrThrow(ChatDataReaderContract.ChatDataEntry.COLUMN_NAME_GROUP_CODE));
            long time = cursor.getLong(cursor.getColumnIndexOrThrow(ChatDataReaderContract.ChatDataEntry.COLUMN_NAME_TIME));
            Chat chat = new Chat(message, sender, time, groupCode);
            chats.add(chat);
        }
        cursor.close();

        return chats;
    }
}