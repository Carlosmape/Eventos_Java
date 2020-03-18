package com.carlosmape.eventos;

import com.google.firebase.firestore.FirebaseFirestore;

public class EventsFirestore {
    public static String EVENTS = "events";
    static String SERVER ="http://curso-firebase.000webhostapp.com/";

    public static void createEvents() {
        Event event;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        event = new Event("Carnaval", "Rio de Janeiro", "21/02/2017",
                SERVER +"imagenes/carnaval.jpg");
        db.collection(EVENTS).document("carnaval").set(event);
        event = new Event("Fallas", "Valencia", "19/03/2017",
                SERVER +"imagenes/fallas.jpg");
        db.collection(EVENTS).document("fallas").set(event);
        event = new Event("Nochevieja", "Nueva York", "31/12/2016",
                SERVER +"imagenes/nochevieja.jpg");
        db.collection(EVENTS).document("nochevieja").set(event);
        event = new Event("Noche de San Juan", "Alicante", "23/06/2017",
                SERVER +"imagenes/sanjuan.jpg");
        db.collection(EVENTS).document("sanjuan").set(event);
        event = new Event("Semana Santa", "Sevilla", "14/04/2017",
                SERVER +"imagenes/semanasanta.jpg");
        db.collection(EVENTS).document("semanasanta").set(event);
    }
}