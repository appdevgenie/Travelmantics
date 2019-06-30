package com.appdevgenie.travelmantics;

import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DealAdapter extends RecyclerView.Adapter<DealAdapter.DealViewHolder> {

    private ArrayList<TravelDeal> travelDeals = new ArrayList<>();
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;
    private ImageView imageView;

    public DealAdapter(){
        firebaseDatabase = FirebaseUtil.firebaseDatabase;
        databaseReference = FirebaseUtil.databaseReference;
        travelDeals = FirebaseUtil.travelDeals;
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                TravelDeal travelDeal = dataSnapshot.getValue(TravelDeal.class);
                travelDeal.setId(dataSnapshot.getKey());
                travelDeals.add(travelDeal);
                notifyItemInserted(travelDeals.size() -1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseReference.addChildEventListener(childEventListener);
    }

    @NonNull
    @Override
    public DealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_row, parent, false);
        return new DealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DealViewHolder holder, int position) {

        TravelDeal travelDeal = travelDeals.get(position);
        holder.bind(travelDeal);
    }

    @Override
    public int getItemCount() {
        return travelDeals.size();
    }

    public class DealViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvTitle;
        TextView tvDescription;
        TextView tvPrice;

        public DealViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            imageView = itemView.findViewById(R.id.imageDeal);
            itemView.setOnClickListener(this);
        }

       public void bind(TravelDeal travelDeal){
            tvTitle.setText(travelDeal.getTitle());
            tvDescription.setText(travelDeal.getDescription());
            tvPrice.setText(travelDeal.getPrice());
            showImage(travelDeal.getImageUrl());
       }

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            TravelDeal travelDeal = travelDeals.get(pos);
            Intent intent = new Intent(view.getContext(), DealActivity.class);
            intent.putExtra("deal", travelDeal);
            view.getContext().startActivity(intent);
        }

        private void showImage(String url){
            if(url != null && url.isEmpty() == false){
                Picasso
                        .get()
                        .load(url)
                        .resize(160, 160)
                        .centerCrop()
                        .into(imageView);
            }
        }
    }
}
