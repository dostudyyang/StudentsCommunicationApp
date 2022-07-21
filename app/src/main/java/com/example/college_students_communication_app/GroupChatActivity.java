package com.example.college_students_communication_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.rest.RestOptions;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.example.college_students_communication_app.Adapters.GroupMessageAdapter;
import com.example.college_students_communication_app.contracts.ChatDataReaderContract;
import com.example.college_students_communication_app.contracts.ChatReaderDbHelper;
import com.example.college_students_communication_app.databinding.ActivityGroupChatBinding;
import com.example.college_students_communication_app.ml.BertTransformer;
import com.example.college_students_communication_app.models.Chat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

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
    private BertTransformer bertTransformer;
    private Handler handler;

    private final List<Chat> chats = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private GroupMessageAdapter groupMessageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        try {
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.configure(getApplicationContext());

            Log.i("MyAmplifyApp", "Initialized Amplify.");
        } catch (AmplifyException error) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify.", error);
        }

        mAuth = FirebaseAuth.getInstance();
        messageSenderID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();

        dbHelper = new ChatReaderDbHelper(GroupChatActivity.this);

        groupCode = getIntent().getExtras().get("groupCode").toString();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(groupCode);

        groupMessageAdapter = new GroupMessageAdapter(chats);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        binding.groupChatMessages.setLayoutManager(linearLayoutManager);
        binding.groupChatMessages.setAdapter(groupMessageAdapter);

        binding.sendMessageButton.setOnClickListener(this);

        HandlerThread handlerThread = new HandlerThread("QAClient");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        bertTransformer = new BertTransformer(getApplicationContext());
    }

    @Override
    public void onClick(View view) {
        if (view == binding.sendMessageButton){
            sendChatMessage();
        }
    }

    ValueEventListener chatValueEventListener = new ValueEventListener() {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            if (dataSnapshot.exists()){
                Chat chat = dataSnapshot.getValue(Chat.class);

                saveChatToSqlite(chat);

                RootRef.child("Chats").child(groupCode).removeValue();

                updateChatsView();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }

    };

    @Override
    protected void onStart()
    {
        super.onStart();

        chats.clear();
        chats.addAll(getChatsFromSqlLite());
        RootRef.child("Chats").child(groupCode).addValueEventListener(chatValueEventListener);

        handler.post(
                () -> {
                    bertTransformer.loadDictionary();
                });
    }

    @Override
    protected void onStop() {
        super.onStop();

        RootRef.child("Chats").child(groupCode).removeEventListener(chatValueEventListener);
    }

    private void sendChatMessage()
    {
        String chatText = binding.inputMessage.getText().toString();
        long time = System.currentTimeMillis();

        if (!TextUtils.isEmpty(chatText))
        {
            //handler.removeCallbacksAndMessages(null);
            String currentUserID = mAuth.getCurrentUser().getUid();
            Chat chat = new Chat(chatText, currentUserID, time, groupCode, 1);

            RootRef.child("Chats").child(groupCode).setValue(chat).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if (!task.isSuccessful())
                    {
                        Toast.makeText(GroupChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        binding.inputMessage.setText("");
                    }
                    binding.inputMessage.setText("");
                }
            });

            binding.inputMessage.setText("");
        }
    }

    public void saveChatToSqlite(Chat chat){

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long newRowId = db.insert(ChatDataReaderContract.ChatDataEntry.TABLE_NAME, null, chat.getChatValues());
        predict(chat, db,newRowId);
    }

    public void updateChatsView(){
        chats.clear();
        chats.addAll(getChatsFromSqlLite());
        groupMessageAdapter.notifyDataSetChanged();
        binding.groupChatMessages.smoothScrollToPosition(binding.groupChatMessages.getAdapter().getItemCount());
    }

    public List<Chat> getChatsFromSqlLite(){
        SQLiteDatabase dbW = dbHelper.getWritableDatabase();
        //dbW.execSQL(ChatDataReaderContract.SQL_DELETE_CHAT);
        dbW.execSQL(ChatDataReaderContract.SQL_CREATE_CHAT);

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                ChatDataReaderContract.ChatDataEntry.COLUMN_NAME_MESSAGE,
                ChatDataReaderContract.ChatDataEntry.COLUMN_NAME_SENDER,
                ChatDataReaderContract.ChatDataEntry.COLUMN_NAME_GROUP_CODE,
                ChatDataReaderContract.ChatDataEntry.COLUMN_NAME_TIME,
                ChatDataReaderContract.ChatDataEntry.COLUMN_NAME_LABEL
        };

        String selection = ChatDataReaderContract.ChatDataEntry.COLUMN_NAME_GROUP_CODE + " = ? and "+ChatDataReaderContract.ChatDataEntry.COLUMN_NAME_LABEL + " = ?";
        String[] selectionArgs = { groupCode, "1" };

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
            int label = cursor.getInt(cursor.getColumnIndexOrThrow(ChatDataReaderContract.ChatDataEntry.COLUMN_NAME_LABEL));
            Chat chat = new Chat(message, sender, time, groupCode, label);
            Log.i("MyAmplifyApp", chat.message + " - label:" + chat.label);
            chats.add(chat);
        }
        cursor.close();

        return chats;
    }

    private void predict(Chat chat, SQLiteDatabase db, long rowId){

        String chatFeatures = bertTransformer.getFeatures(chat.getMessage());

        try {
            JSONObject json = new JSONObject();
            String body = json.put("data", chatFeatures).toString().replaceAll("\"", "\\\"");

            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    getApplicationContext(),
                    "us-west-2:f864c27f-6ff3-461a-a031-02c2884a1af0", // Identity pool ID
                    Regions.US_WEST_2 // Region
            );

            RestOptions options = RestOptions.builder()
                    .addPath("/predictchatsrelevance")
                    .addBody(body.getBytes())
                    .build();

            Amplify.API.post(options,
                    response -> {
                        Log.i("MyAmplifyApp", "POST succeeded: " + response.getData().asString());
                        String label = response.getData().asString();
                        if (label.equals("\"F\"")){
                            Log.i("MyAmplifyApp", chat.message + " - label:" + chat.label+"; before");
                            chat.setLabel(0);
                            db.update(ChatDataReaderContract.ChatDataEntry.TABLE_NAME, chat.getChatValues(), ChatDataReaderContract.ChatDataEntry._ID +" = ?", new String[]{String.valueOf(rowId)});
                            Log.i("MyAmplifyApp", chat.message + " - label:" + chat.label+"; updated");
                        }
                    },
                    error -> Log.e("MyAmplifyApp", "POST failed.", error)
            );
        }
        catch (Exception ex){
            Log.e("MyAmplifyApp: Exception", "POST failed."+ ex.getMessage());
            ex.printStackTrace();
        }
    }
}