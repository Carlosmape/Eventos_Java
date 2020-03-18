package com.carlosmape.eventos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.io.InputStream;

public class EventsAdapter extends FirestoreRecyclerAdapter<Event, EventsAdapter.ViewHolder> {
    protected View.OnClickListener onClickListener;

    public EventsAdapter(@NonNull FirestoreRecyclerOptions<Event> options) {
        super(options);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event, parent, false);
        view.setOnClickListener(onClickListener);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtEvent, txtCity, txtDate;
        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            txtEvent = (TextView) itemView.findViewById(R.id.txtEvento);
            txtCity = (TextView) itemView.findViewById(R.id.txtCiudad);
            txtDate = (TextView) itemView.findViewById(R.id.txtFecha);
            image = (ImageView) itemView.findViewById(R.id.imgImagen);
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Event item) {
        holder.txtEvent.setText(item.getEvent());
        holder.txtCity.setText(item.getCity());
        holder.txtDate.setText(item.getDate());
        new DownloadImageTask((ImageView) holder.image).execute(item.getImage());
        holder.itemView.setOnClickListener(onClickListener);
    }

    public void setOnItemClickListener(View.OnClickListener onClick) {
        onClickListener = onClick;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageTask(ImageView bmImage) {
            this.imageView = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String displayURL = urls[0];
            Bitmap mImage = null;
            try {
                InputStream in = new java.net.URL(displayURL).openStream();
                mImage = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mImage;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
}