package com.kach.kachechni.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kach.kachechni.Adapters.CartAdapter;
import com.kach.kachechni.Helper.ManagmentCart;
import com.kach.kachechni.Model.ItemsDomain;
import com.kach.kachechni.Model.Modif;
import com.kach.kachechni.Model.Order;
import com.kach.kachechni.R;
import com.kach.kachechni.databinding.ActivityCartBinding;
import com.kach.kachechni.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {
    // View binding for ActivityCartBinding class
    ActivityCartBinding binding;
    // Firebase Database reference
    DatabaseReference databaseReference;
    // List to store cart items
    ArrayList <ItemsDomain> listCart2;
    // Variable to store tax amount
    private double tax;
    // Instance of ManagmentCart class for managing cart operations
    private ManagmentCart   managmentCart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize view binding
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get reference to the "orders" node in Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference().child("orders");

        // Create an instance of ManagmentCart class
        managmentCart = new ManagmentCart(this);
        // Get the list of cart items from ManagmentCart
        listCart2 = managmentCart.getListCart();

        // apply Apply Coupon
        binding.button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyCoupon();
            }
        });



        // Calculate cart total initially without any discount
        calculatorCart(0.0);
        // Set necessary variables for the cart UI
        setVariable();
        // Initialize the cart item list
        initCartList();


    }
    private void initCartList(){
        // Check if the cart is empty
        if(managmentCart.getListCart().isEmpty()){
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.scrollViewCart.setVisibility(View.GONE);
        }
        else {
            binding.emptyTxt.setVisibility(View.GONE);
            binding.scrollViewCart.setVisibility(View.VISIBLE);
        }
        // Set layout manager and adapter for the cart recycler view
        binding.cartView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        binding.cartView.setAdapter(new CartAdapter(managmentCart.getListCart(), this, () -> calculatorCart(0.0)));
    }

    private void setVariable() {
        // Set click listener for back button to finish the activity
        binding.backBtn.setOnClickListener(v -> finish());
        // Set click listener for "Checkout" button
        binding.checkOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                // Check if there are items in the cart
                if (listCart2 != null && !listCart2.isEmpty()) {

                    // Generate a unique order ID
                    String orderId = databaseReference.push().getKey();


                    // Get current timestamp
                    long timestamp = System.currentTimeMillis();

                    // Create a list to store order modifications (items and quantities)
                    ArrayList<Modif> modifList = new ArrayList<>();

                    // Loop through each item in the cart and add it to the modifList
                    for (ItemsDomain item : listCart2) {
                        String title = item.getTitle();
                        String count = String.valueOf(item.getNumberinCart());
                        Modif modif = new Modif(title, count);
                        modifList.add(modif);
                    }

                    // Create an Order object with order details
                    Order order = new Order(orderId, timestamp, modifList);

                    // Save the order to Firebase Database under the current user's ID
                    databaseReference.child(uid).child(orderId).setValue(order);
                    Toast.makeText(CartActivity.this, "Order placed successfully", Toast.LENGTH_SHORT).show();

                    // Go back to MainActivity and clear the cart
                    Intent intent = new Intent(CartActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    managmentCart.clearCart();
                }
                else {
                    Toast.makeText(CartActivity.this, "Your cart is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void calculatorCart(double discount) {
        // Set tax percentage
        double percentTax = 0.02;

        // Set delivery fee
        double delivery = 0.02;

        // Calculate tax amount
        tax = Math.round((managmentCart.getTotalFee() * percentTax * 100.0)) / 100.0;

        // Calculate total amount considering discount
        double total = Math.round((managmentCart.getTotalFee() + tax + delivery) * (1 - discount) * 100.0) / 100.0;

        // Calculate total item price before discount
        double itemTotal = Math.round(managmentCart.getTotalFee() * 100) / 100;

        // Update UI elements with calculated values
        binding.totalFeeTxt.setText(itemTotal + "DT");
        binding.taxTxt.setText(tax + "DT");
        binding.deliverytTxt.setText(delivery + "DT");
        binding.totalTxt.setText(total + "DT");
    }

    private boolean couponApplied = false;

    private void applyCoupon() {
        // Get the coupon code entered by the user
        String couponCode = binding.textView18.getText().toString().trim();

        // Check if coupon has already been applied
        if (couponApplied) {
            Toast.makeText(CartActivity.this, "Coupon already applied", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get a reference to the specific coupon node in Firebase Database
        DatabaseReference couponRef = FirebaseDatabase.getInstance().getReference().child("coupons").child(couponCode);

        // Add a listener to handle the response from Firebase
        couponRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {

                    Toast.makeText(CartActivity.this, "Coupon applied successfully", Toast.LENGTH_SHORT).show();

                    couponApplied = true;

                    calculatorCart(0.2); // Assuming the coupon offers a 20% discount (adjust as needed)
                } else {

                    Toast.makeText(CartActivity.this, "Invalid coupon code", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(CartActivity.this, "Error applying coupon", Toast.LENGTH_SHORT).show();

            }
        });
    }

}