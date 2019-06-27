package com.appdevgenie.travelmantics;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FirebaseUtil {

    public static FirebaseDatabase firebaseDatabase;
    public static DatabaseReference databaseReference;

    private static FirebaseUtil firebaseUtil;

    public static ArrayList<TravelDeal> travelDeals;

    private FirebaseUtil(){}

    public static void openFBReference(String ref){
        if(firebaseUtil == null){
            firebaseUtil = new FirebaseUtil();
            firebaseDatabase = FirebaseDatabase.getInstance();
            travelDeals = new ArrayList<>();
        }

        databaseReference = firebaseDatabase.getReference().child(ref);
    }
}