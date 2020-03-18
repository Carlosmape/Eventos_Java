package com.carlosmape.eventos;

import android.app.Service;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static com.carlosmape.eventos.Common.mostrarDialogo;

public class EventsFCMService extends FirebaseMessagingService {
    @Override public
    void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            mostrarDialogo(getApplicationContext(), remoteMessage.getNotification().getTitle() + ": " + remoteMessage.getNotification().getBody());
        }
    }
}
