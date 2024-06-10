package com.kach.kachechni.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.kach.kachechni.Adapters.SliderAdapter;
import com.kach.kachechni.Fragements.DescriptionFragment;
import com.kach.kachechni.Helper.ManagmentCart;
import com.kach.kachechni.Model.ItemsDomain;
import com.kach.kachechni.Model.SliderItems;
import com.kach.kachechni.databinding.ActivityDetailBinding;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    ActivityDetailBinding binding;
    private ItemsDomain object;
    private SharedPreferences sharedPreferences;

    private  int numberOrder=1;
    private ManagmentCart managmentCart;
    private Handler slideHandle = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflating the layout using ViewBinding
        binding = ActivityDetailBinding.inflate(getLayoutInflater());

        // Setting the content view to the root view of the inflated layout
        setContentView(binding.getRoot());

        // Getting SharedPreferences with name "whishList"
        sharedPreferences = getSharedPreferences("whishList", MODE_PRIVATE);

        // Creating a new instance of ManagmentCart passing the context
        managmentCart = new ManagmentCart(this);

        // Calling a method to retrieve data passed through Intent extras
        getBundles();

        // Initializing the banner/slider images
        initbanners();


        // Setting up ViewPager for tabbed content
        setupViewPager();
    }


    // Method to initialize the banner/slider images
    private void initbanners() {

        // Creating a list to hold SliderItems objects
        List<SliderItems> sliderItems = new ArrayList<>();

        // Looping through the URLs of the item's images
        for(int i =0;i<object.getPicUrl().size();i++)
        {
            // Adding each image URL to the list as a SliderItems object
            sliderItems.add(new SliderItems(object.getPicUrl().get(i)));

        }
        // Setting the adapter for the ViewPager with the list of SliderItems
        binding.viewpageSlider.setAdapter(new SliderAdapter((ArrayList<SliderItems>) sliderItems,binding.viewpageSlider));

        // Disabling clipping to outline for the ViewPager
        binding.viewpageSlider.setClipToOutline(false);

        // Disabling clipping children for the ViewPager
        binding.viewpageSlider.setClipChildren(false);

        // Disabling overscroll mode for the ViewPager's child at index 0
        binding.viewpageSlider.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
    }

    private void getBundles() {
        // Retrieving the serialized object passed with key "object" from Intent extras
        object =(ItemsDomain) getIntent().getSerializableExtra("object");

        // Setting the title of the item
        binding.titleTxt.setText(object.getTitle());

        // Setting the price of the item
        binding.priceTxt.setText(object.getPrice()+"dt");

        // Setting the rating of the item
        binding.ratingBar3.setRating((float) object.getRating());

        // Setting the rating text of the item
        binding.ratingTxt.setText(object.getRating()+" Rating");

        // Setting click listener for adding the item to cart ( elli bech ychoufou click walla le )
        binding.addToCartBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Setting the number of items to be added to cart
                object.setNumberinCart(numberOrder);

                // Inserting the item into the cart using ManagmentCart
                managmentCart.insertItem(object);

            }
        });
        // Setting click listener for adding the item to favorites
        binding.backBtn.setOnClickListener(v -> finish());
        //kif lo5ra
        binding.favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Adding the item to favorites using ManagmentCart
                managmentCart.addToFavorites(object);
            }
        });
        // Finish the activity when the back button is clicked
        binding.backBtn.setOnClickListener(v -> finish());
    }

    // Method to set up ViewPager for tabbed content
    private void setupViewPager(){

        // Creating a new adapter for the ViewPager
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Creating a new DescriptionFragment
        DescriptionFragment  tab= new DescriptionFragment();

        Bundle bundle1 =new Bundle(); // Creating a new bundle for tab


        bundle1.putString("description",object.getDescription()); // Putting item description in bundle for tab

        // Setting arguments for each fragment
        tab.setArguments(bundle1);


        // Adding fragments to the adapter
        adapter.addFrag(tab,"Description");


        // Setting adapter and connecting with TabLayout
        binding.viewPager.setAdapter(adapter);


    }
    // Custom FragmentPagerAdapter for ViewPager **********************************************************************
    private class ViewPagerAdapter extends FragmentPagerAdapter{

        // List to hold Fragments
        private final List<Fragment> mFragmentList = new ArrayList<>();

        // List to hold fragment titles
        private final List<String> mFragmenTitleList = new ArrayList<>();
        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {

            // Returning fragment at specified position
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            //Returning total number of fragments
            return mFragmentList.size();
        }
        public void addFrag(Fragment fragment,String title)
        {
            // Adding fragment to the list
            mFragmentList.add(fragment);
            // Adding fragment title to the list
            mFragmenTitleList.add(title);
        }
        @Override
        public  CharSequence getPageTitle(int position){

            // Returning title of the fragment at specified position
            return mFragmenTitleList.get(position);
        }
    }

} 