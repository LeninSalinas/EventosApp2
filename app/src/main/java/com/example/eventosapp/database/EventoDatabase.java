package com.example.eventosapp.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.eventosapp.listEvent.entity.EventoDao;
import com.example.eventosapp.listEvent.entity.Eventos;
import com.example.eventosapp.listEvent.entity.GPSLocation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Eventos.class, GPSLocation.class}, version = 1, exportSchema = true)
public abstract class EventoDatabase extends RoomDatabase {

    public abstract EventoDao eventoDao();

    public static volatile EventoDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;

    static final ExecutorService dataWriterExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static EventoDatabase getDatabase(final Context context) {
        if (INSTANCE==null){
            synchronized (EventoDatabase.class){
                RoomDatabase.Callback miCallback=new RoomDatabase.Callback(){
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db){
                        super.onCreate(db);
                        dataWriterExecutor.execute(()->{
                            EventoDao dao = INSTANCE.eventoDao();
                            dao.deleteAll();
                            dao.deleteGPSData();
                        });
                    }
                };
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(), EventoDatabase.class,"evento_database").addCallback(miCallback).build();
            }

        }
        return INSTANCE;
    }
}
