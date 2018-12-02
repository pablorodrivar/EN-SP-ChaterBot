package org.ieszaidinvergeles.dam.chaterbot;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class Chat implements Parcelable {
    private long id;
    private String date, chatlog, fireBaseKey;

    public Chat() {
        chatlog = "";
    }

    public Chat(String date, String chatlog) {
        this.date = date;
        this.chatlog = chatlog;
        setFireBaseKey();
    }

    protected Chat(Parcel in) {
        id = in.readLong();
        date = in.readString();
        chatlog = in.readString();
    }

    public static final Creator<Chat> CREATOR = new Creator<Chat>() {
        @Override
        public Chat createFromParcel(Parcel in) {
            return new Chat(in);
        }

        @Override
        public Chat[] newArray(int size) {
            return new Chat[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getChatlog() {
        return chatlog;
    }

    public void setChatlog(String chatlog) {
        this.chatlog = chatlog;
    }

    public String getFireBaseKey() {
        return fireBaseKey;
    }

    public void setFireBaseKey() {
        this.fireBaseKey = date.toLowerCase().replace(" ", "") + id;
    }

    public void setFireBaseKey(String fireBaseKey) {
        this.fireBaseKey = fireBaseKey;
    }

    public void appendChatLog(String chatlog){
        this.chatlog += chatlog;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("date",date);
        result.put("chatlog",chatlog);
        return result;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "date='" + date + '\'' +
                ", chatlog='" + chatlog + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(date);
        dest.writeString(chatlog);
    }
}
