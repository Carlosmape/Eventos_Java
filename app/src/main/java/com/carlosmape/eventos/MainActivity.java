package com.carlosmape.eventos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import static com.carlosmape.eventos.Common.mostrarDialogo;
import static com.carlosmape.eventos.EventsFirestore.EVENTS;
import static com.carlosmape.eventos.EventsFirestore.createEvents;

public class MainActivity extends AppCompatActivity {
    private EventsAdapter adapter;
    private static MainActivity current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        //createEvents();
        Query query = FirebaseFirestore.getInstance().collection(EVENTS).limit(50);
        FirestoreRecyclerOptions<Event> options = new FirestoreRecyclerOptions.Builder<Event>().setQuery(query, Event.class).build();
        adapter = new EventsAdapter(options);
        final RecyclerView recyclerView = findViewById(R.id.recyclerViewEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle extras = getIntent().getExtras();
        if (getIntent().hasExtra("body")) {
            mostrarDialogo(this, extras.getString("title") + ": " + extras.getString("body"));
            extras.remove("body");
        }
    }

    public static MainActivity getCurrentContext() {
        return current;
    }
}
