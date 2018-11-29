package org.ieszaidinvergeles.dam.chaterbot;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class DBManager {

    public static void insert(ChatManager chatManager, FireBaseConnection fbc, Chat chat){
        chatManager.insert(chat);
        chat.setFireBaseKey();
        fbc.saveChat(chat);
    }

    public static void delete(ChatManager chatManager, FireBaseConnection fbc, Chat chat){
        chatManager.delete(chat.getId());
        fbc.deleteChat(chat);
    }

    public static void update(ChatManager chatManager, FireBaseConnection fbc, Chat chat){
        chatManager.delete(chat.getId());
        fbc.deleteChat(chat);
        insert(chatManager,fbc,chat);
    }

    /*public static void syncronize(final ReadingsManager readingsManager, final AuthorsManager authorsManager, FireBaseConnection fbc, String user){
        readingsManager.deleteTodo();
        authorsManager.deleteTodo();
        fbc.getDatabaseReference("user/" + user + "/authors/").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                while(iterator.hasNext()){
                    Author author = new Author(iterator.next().getValue(Author.class).getNombre());
                    authorsManager.insert(author);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        fbc.getDatabaseReference().child("user/" + user + "/readings/").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator().next().getChildren().iterator();
                //Log.v(TAG, iterator.next().toString());
                while(iterator.hasNext()){
                    Readings readings = new Readings();
                    DataSnapshot ds = iterator.next();
                    Log.v(TAG, ds.getValue().toString());
                    if(ds.getKey().equals("id_autor")){
                        readings.setId_autor(Integer.parseInt(String.valueOf(ds.getValue(Long.class))));
                    } else if(ds.getKey().equals("valoracion")){
                        readings.setValoracion(ds.getValue(Float.class));
                    } else if(ds.getKey().equals("titulo")){
                        readings.setTitulo(ds.getValue(String.class));
                    } else if(ds.getKey().equals("fecha_comienzo")){
                        readings.setFecha_comienzo(ds.getValue(String.class));
                    } else if(ds.getKey().equals("fecha_fin")){
                        readings.setFecha_fin(ds.getValue(String.class));
                    } else if(ds.getKey().equals("resumen")){
                        readings.setResumen(ds.getValue(String.class));
                    } else if(ds.getKey().equals("drawable_portada")){
                        readings.setDrawable_portada(ds.getValue(String.class));
                    }
                    readings.setFireBaseKey();
                    readingsManager.insert(readings);
                    Log.v(TAG + "z", readings.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/
}
