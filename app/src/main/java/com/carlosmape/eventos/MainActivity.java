package com.carlosmape.eventos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;

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
        adapter.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = recyclerView.getChildAdapterPosition(view);
                Event currentItem = (Event) adapter.getItem(position);
                String idEvento = adapter.getSnapshots().getSnapshot(position).getId();
                Context context = view.getContext();
                Intent intent = new Intent(context, EventoDetalles.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("evento", idEvento);
                context.startActivity(intent);
            }
        });

        final SharedPreferences preferencias = getApplicationContext().getSharedPreferences("Temas", Context.MODE_PRIVATE);
        if (preferencias.getBoolean("Inicializado", false) == false) {
            final SharedPreferences prefs = getApplicationContext().getSharedPreferences("Temas", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("Inicializado", true);
            editor.commit();
            FirebaseMessaging.getInstance().subscribeToTopic("Todos");
        }

        Common.storage = FirebaseStorage.getInstance();
        Common.storageRef = Common.storage.getReferenceFromUrl("gs://eventos2020-fdd8a.appspot.com");

        String[] PERMISOS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.GET_ACCOUNTS};
        ActivityCompat.requestPermissions(this, PERMISOS, 1);
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

    public static Context getAppContext() {
        return MainActivity.getCurrentContext();
    }

    public static MainActivity getCurrentContext() {
        return current;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_topics) {
            Intent intent = new Intent(getBaseContext(), Topics.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(MainActivity.this, "Has denegado algún permiso de la aplicación.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}
