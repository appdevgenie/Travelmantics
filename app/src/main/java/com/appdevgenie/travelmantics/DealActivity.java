package com.appdevgenie.travelmantics;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.data.model.Resource;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class DealActivity extends AppCompatActivity {

    public static final int PICTURE_RESULT = 42;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private EditText txtTitle;
    private EditText txtDescription;
    private EditText txtPrice;
    private TravelDeal travelDeal;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);

        //FirebaseUtil.openFBReference("traveldeals", this);
        firebaseDatabase = FirebaseUtil.firebaseDatabase;
        databaseReference = FirebaseUtil.databaseReference;

        txtTitle = findViewById(R.id.txtTitle);
        txtDescription = findViewById(R.id.txtDescription);
        txtPrice = findViewById(R.id.txtPrice);
        imageView = findViewById(R.id.image);

        Intent intent = getIntent();
        TravelDeal travelDeal = (TravelDeal) intent.getSerializableExtra("deal");
        if(travelDeal == null){
            travelDeal = new TravelDeal();
        }
        this.travelDeal = travelDeal;
        txtTitle.setText(travelDeal.getTitle());
        txtDescription.setText(travelDeal.getDescription());
        txtPrice.setText(travelDeal.getPrice());
        showImage(travelDeal.getImageUrl());

        Button button = findViewById(R.id.btnImage);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
                intent1.setType("image/jpeg");
                intent1.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent1.createChooser(intent1, "Insert picture"), PICTURE_RESULT);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.save_menu, menu);
        if (FirebaseUtil.isAdmin) {
            menu.findItem(R.id.delete_menu).setVisible(true);
            menu.findItem(R.id.save_menu).setVisible(true);
            enableEditTexts(true);
            findViewById(R.id.btnImage).setEnabled(true);
        }
        else {
            menu.findItem(R.id.delete_menu).setVisible(false);
            menu.findItem(R.id.save_menu).setVisible(false);
            enableEditTexts(false);
            findViewById(R.id.btnImage).setEnabled(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.save_menu:
                saveDeal();
                Toast.makeText(this, "Deal saved", Toast.LENGTH_LONG).show();
                clean();
                backToList();
                return true;

            case R.id.delete_menu:
                delete();
                Toast.makeText(this, "Deal deleted", Toast.LENGTH_LONG).show();
                backToList();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICTURE_RESULT && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            StorageReference storageReference = FirebaseUtil.storageReference.child(imageUri.getLastPathSegment());
            storageReference.putFile(imageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    String url = taskSnapshot.getStorage().toString();
                    String pictureName = taskSnapshot.getMetadata().getPath();
                    travelDeal.setImageUrl(url);
                    travelDeal.setImageName(pictureName);
                    showImage(url);
                }
            });
        }
    }

    private void saveDeal() {

        travelDeal.setTitle(txtTitle.getText().toString());
        travelDeal.setDescription(txtDescription.getText().toString());
        travelDeal.setPrice(txtPrice.getText().toString());

        if(travelDeal.getId() == null){
            databaseReference.push().setValue(travelDeal);
        }else{
            databaseReference.child(travelDeal.getId()).setValue(travelDeal);
        }
    }

    private void delete(){
        
        if(travelDeal == null){
            Toast.makeText(this, "Save deal before deleting", Toast.LENGTH_SHORT).show();
            return;
        }
        databaseReference.child(travelDeal.getId()).removeValue();
        if(travelDeal.getImageName() != null && travelDeal.getImageName().isEmpty() == false) {
            StorageReference picRef = FirebaseUtil.firebaseStorage.getReference().child(travelDeal.getImageName());
            picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }
    }

    private void backToList(){

        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }

    private void clean() {

        txtTitle.setText("");
        txtDescription.setText("");
        txtPrice.setText("");
        txtTitle.requestFocus();
    }

    private void enableEditTexts(boolean isEnabled) {
        txtTitle.setEnabled(isEnabled);
        txtDescription.setEnabled(isEnabled);
        txtPrice.setEnabled(isEnabled);
    }

    private void showImage(String url){
        if(url != null && url.isEmpty() == false){
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso
                    .get()
                    .load(url)
                    .resize(width, width * 2/3)
                    .centerCrop()
                    .into(imageView);
        }
    }

}
