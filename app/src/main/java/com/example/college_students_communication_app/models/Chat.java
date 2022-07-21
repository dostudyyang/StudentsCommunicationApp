package com.example.college_students_communication_app.models;

import android.content.ContentValues;

import com.example.college_students_communication_app.contracts.ChatDataReaderContract;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Chat {

    public String message, senderId, groupCode;
    public int label;
    public long time;

    public Chat(){

    }

    public Chat(String message, String senderId, long time, String groupCode, int label) {
        this.message = message;
        this.senderId = senderId;
        this.time = time;
        this.groupCode = groupCode;
        this.label = label;
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }


    @Exclude
    public ContentValues getChatValues(){

        ContentValues chatValues = new ContentValues();
        chatValues.put(ChatDataReaderContract.ChatDataEntry.COLUMN_NAME_MESSAGE, message);
        chatValues.put(ChatDataReaderContract.ChatDataEntry.COLUMN_NAME_SENDER, senderId);
        chatValues.put(ChatDataReaderContract.ChatDataEntry.COLUMN_NAME_GROUP_CODE, groupCode);
        chatValues.put(ChatDataReaderContract.ChatDataEntry.COLUMN_NAME_TIME, time);
        chatValues.put(ChatDataReaderContract.ChatDataEntry.COLUMN_NAME_LABEL, label);

        return chatValues;
    }
}
