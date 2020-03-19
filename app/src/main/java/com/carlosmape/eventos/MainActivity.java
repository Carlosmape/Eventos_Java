package com.carlosmape.eventos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import static com.carlosmape.eventos.EventsFirestore.EVENTS;

public class MainActivity extends AppCompatActivity {
    private EventsAdapter adapter;
    private static MainActivity current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        if (extras != null && extras.keySet().size() > 7) {
            String event = "";
            event = "Evento: " + extras.getString("event") + "\n";
            event = event + "Día: " + extras.getString("date") + "\n";
            event = event + "Ciudad: " + extras.getString("cicty") + "\n";
            event = event + "Comentario: " + extras.getString("comment");
            Common.showDialog(getApplicationContext(), event);
            for (String key : extras.keySet()) {
                getIntent().removeExtra(key);
            }
            extras = null;
        } else if (getIntent().hasExtra("body")) {
            Common.showDialog(this, extras.getString("title") + ": " + extras.getString("body"));
            extras.remove("body");
        }

    }


    public static MainActivity getCurrentContext() {
        return current;
    }
}
