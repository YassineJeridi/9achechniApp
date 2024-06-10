package com.kach.kachechni.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.FirebaseDatabase;
import com.kach.kachechni.Adapters.PopularAdapter;
import com.kach.kachechni.Helper.ManagmentCart;
import com.kach.kachechni.Model.ItemsDomain;
import com.kach.kachechni.databinding.ActivityWishBinding;

import java.util.ArrayList;


public class WhishList extends AppCompatActivity {
    ActivityWishBinding binding ;

    FirebaseDatabase database;
    private PopularAdapter adap;
    private ArrayList<ItemsDomain> itemList;
    private ManagmentCart managmentCart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityWishBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();

        managmentCart = new ManagmentCart(this);



        binding.backBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        startActivity(new Intent(WhishList.this,MainActivity.class));

    }
});
        initFavList();

    }



    private void initFavList(){
        // Get the list of favorite items from the cart
        itemList = managmentCart.getFavoriteList();
        //fammech fav
        if(itemList.isEmpty()){
            binding.textView6.setVisibility(View.VISIBLE);
        }
        binding.whichRecycler.setLayoutManager(new GridLayoutManager(WhishList.this, 2));
        adap = new PopularAdapter(itemList,true,false); // Pass itemList to the adapter
        binding.whichRecycler.setAdapter(adap);

    };
}



