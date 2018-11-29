package org.ieszaidinvergeles.dam.chaterbot;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class FireBaseConnection {
    public static final String TAG = "MITAG";
    public static FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public FireBaseConnection(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }
    public DatabaseReference getDatabaseReference(String path) {
        return firebaseDatabase.getReference(path);
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }


    public String getEmail(){
        return firebaseUser.getEmail();
    }

    public void saveChat(Chat chat) {
        Map<String, Object> saveUser = new HashMap<>(); // SIEMPRE ES NULL LA FIREBASE KEY, HAY Q VER POR QUE
        String fireBaseKey = chat.getFireBaseKey();
        saveUser.put("/chatlogs/" + fireBaseKey + "/", chat.toMap());
        databaseReference.updateChildren(saveUser).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.v(TAG, "SI INSERTO READING");
                        } else {
                            Log.v(TAG, "NO INSERTO READING");
                        }
                    }
                });
    }

    public void deleteChat(Chat chat){
        databaseReference.child("/chatlogs/" + chat.getFireBaseKey()).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                //Log.v(TAG, databaseError.toString());
            }
        });
    }

    public void signOut(){
        firebaseAuth.signOut();
    }
}
