package com.carlosmape.eventos;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static com.carlosmape.eventos.Common.saveRegisterID;
import static com.carlosmape.eventos.Common.showDialog;

public class EventsFCMService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            if (remoteMessage.getData().size() > 0) {
                String evento = "";
                evento = "Evento: " + remoteMessage.getData().get("evento") + "\n";
                evento = evento + "Día: " + remoteMessage.getData().get("dia") + "\n";
                evento = evento + "Ciudad: " + remoteMessage.getData().get("ciudad") + "\n";
                evento = evento + "Comentario: " + remoteMessage.getData().get("comentario");
                showDialog(getApplicationContext(), evento);
            } else {
                if (remoteMessage.getNotification() != null) {
                    showDialog(getApplicationContext(), remoteMessage.getNotification().getTitle() + ": " + remoteMessage.getNotification().getBody());
                }
            }
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        String idPush = s;
        saveRegisterID(getApplicationContext(), idPush);
    }
}
