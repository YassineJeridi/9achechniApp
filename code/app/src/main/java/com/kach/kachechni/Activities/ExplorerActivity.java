package com.kach.kachechni.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kach.kachechni.Adapters.PopularAdapter;
import com.kach.kachechni.Model.ItemsDomain;
import com.kach.kachechni.databinding.ActivityExplorerBinding;
import com.kach.kachechni.databinding.ActivityWishBinding;

import java.util.ArrayList;


public class ExplorerActivity extends AppCompatActivity {
    ActivityExplorerBinding binding ;

    FirebaseDatabase database;
    private PopularAdapter adap;
    private ArrayList<ItemsDomain> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityExplorerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();




        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExplorerActivity.this,MainActivity.class));

            }
        });

        binding.search.clearFocus();
        binding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filerList(newText);

                return true;
            }
        });
        // lista ta3 products
        initProduct();

    }



    private void initProduct() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Items");


        itemList = new ArrayList<>();
        // listener lil data base
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Check if the data exists in the database
                if (snapshot.exists()) {
                    // Retrieve items from Firebase database
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        try {
                            // Convert dataSnapshot to an ItemsDomain object
                            ItemsDomain item = dataSnapshot.getValue(ItemsDomain.class);
                            if (item != null) {
                                itemList.add(item);
                            }
                        } catch (Exception e) {
                            Log.e("Firebase", "Error converting data", e);
                        }
                    }


                    // Set up RecyclerView with items if itemList is not empty
                    if (!itemList.isEmpty()) {
                        binding.productRecycler.setLayoutManager(new GridLayoutManager(ExplorerActivity.this, 2));
                        adap = new PopularAdapter(itemList);
                        binding.productRecycler.setAdapter(adap);
                    } else {
                        Log.e("Firebase", "No items found");
                    }

                } else {
                    Log.e("Firebase", "No data found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled event
                Log.e("Firebase", "Database error: " + error.getMessage());
            }
        });
    }


    private void filerList(String newText) {
        // item lista
        ArrayList<ItemsDomain> filteredList = new ArrayList<>();

        for(ItemsDomain item: itemList){
            // ken el el products moujoud fih elli ketbou f search
            if(item.getTitle().toLowerCase().contains(newText.toLowerCase())){
                filteredList.add(item);
            }
        }
        if(filteredList.isEmpty()){
            Toast.makeText(this,"No data found",Toast.LENGTH_SHORT).show();
        }else{
            //update
            adap.setFiltredList(filteredList);
        }
    }
}