package com.carlosmape.eventos;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class EventoDetalles extends AppCompatActivity {
    final int SOLICITUD_SUBIR_PUTDATA = 0;
    final int SOLICITUD_SUBIR_PUTSTREAM = 1;
    final int SOLICITUD_SUBIR_PUTFILE = 2;
    final int SOLICITUD_SELECCION_STREAM = 100;
    final int SOLICITUD_SELECCION_PUTFILE = 101;

    TextView txtEvento, txtFecha, txtCiudad;
    ImageView imgImagen;
    String evento;
    CollectionReference registros;

    static UploadTask uploadTask = null;
    StorageReference imagenRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.evento_detalle);
        txtEvento = (TextView) findViewById(R.id.txtEvento);
        txtFecha = (TextView) findViewById(R.id.txtFecha);
        txtCiudad = (TextView) findViewById(R.id.txtCiudad);
        imgImagen = (ImageView) findViewById(R.id.imgImagen);
        Bundle extras = getIntent().getExtras();
        evento = extras.getString("evento");
        if (evento == null) evento = "";
        registros = FirebaseFirestore.getInstance().collection("events");
        registros.document(evento).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    txtEvento.setText(task.getResult().get("event").toString());
                    txtCiudad.setText(task.getResult().get("city").toString());
                    txtFecha.setText(task.getResult().get("date").toString());
                    new DownloadImageTask((ImageView) imgImagen).execute(task.getResult().get("image").toString());
                }
            }
        });
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mImagen = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mImagen = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mImagen;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        View vista = (View) findViewById(android.R.id.content);
        int id = item.getItemId();
        switch (id) {
            case R.id.action_putData:
                subirAFirebaseStorage(SOLICITUD_SUBIR_PUTDATA, null);
                break;
            case R.id.action_streamData:
                seleccionarFotografiaDispositivo(vista, SOLICITUD_SELECCION_STREAM);
                break;
            case R.id.action_putFile:
                seleccionarFotografiaDispositivo(vista, SOLICITUD_SELECCION_PUTFILE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void seleccionarFotografiaDispositivo(View v, Integer solicitud) {
        Intent seleccionFotografiaIntent = new Intent(Intent.ACTION_PICK);
        seleccionFotografiaIntent.setType("image/*");
        startActivityForResult(seleccionFotografiaIntent, solicitud);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri ficheroSeleccionado;
        Cursor cursor;
        String rutaImagen;
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case SOLICITUD_SELECCION_STREAM:
                    ficheroSeleccionado = data.getData();
                    String[] proyeccionStream = {MediaStore.Images.Media.DATA};
                    cursor = getContentResolver().query(ficheroSeleccionado, proyeccionStream, null, null, null);
                    cursor.moveToFirst();
                    rutaImagen = cursor.getString(cursor.getColumnIndex(proyeccionStream[0]));
                    cursor.close();
                    subirAFirebaseStorage(SOLICITUD_SUBIR_PUTSTREAM, rutaImagen);
                    break;
                case SOLICITUD_SELECCION_PUTFILE:
                    ficheroSeleccionado = data.getData();
                    String[] proyeccionFile = {MediaStore.Images.Media.DATA};
                    cursor = getContentResolver().query(ficheroSeleccionado, proyeccionFile, null, null, null);
                    cursor.moveToFirst();
                    rutaImagen = cursor.getString(cursor.getColumnIndex(proyeccionFile[0]));
                    cursor.close();
                    subirAFirebaseStorage(SOLICITUD_SUBIR_PUTFILE, rutaImagen);
                    break;
            }
        }
    }

    public void subirAFirebaseStorage(Integer opcion, String ficheroDispositivo) {
        String fichero = evento;
        imagenRef = Common.storageRef.child(fichero);
        try {
            switch (opcion) {
                case SOLICITUD_SUBIR_PUTDATA:
                    imgImagen.setDrawingCacheEnabled(true);
                    imgImagen.buildDrawingCache();
                    Bitmap bitmap = imgImagen.getDrawingCache();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();
                    uploadTask = imagenRef.putBytes(data);
                    break;
                case SOLICITUD_SUBIR_PUTSTREAM:
                    InputStream stream = new FileInputStream(new File(ficheroDispositivo));
                    uploadTask = imagenRef.putStream(stream);
                    break;
                case SOLICITUD_SUBIR_PUTFILE:
                    Uri file = Uri.fromFile(new File(ficheroDispositivo));
                    uploadTask = imagenRef.putFile(file);
                    break;
            }
        } catch (IOException e) {
            Common.showDialog(getApplicationContext(), e.toString());
        }
    }
}

