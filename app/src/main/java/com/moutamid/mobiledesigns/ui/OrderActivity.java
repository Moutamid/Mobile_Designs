package com.moutamid.mobiledesigns.ui;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.moutamid.mobiledesigns.Constants;
import com.moutamid.mobiledesigns.R;
import com.moutamid.mobiledesigns.databinding.ActivityOrderBinding;
import com.moutamid.mobiledesigns.model.DesignModel;
import com.moutamid.mobiledesigns.model.OrderModel;

import java.util.UUID;

public class OrderActivity extends AppCompatActivity {
    ActivityOrderBinding binding;
    int quantity = 1;
    public String SELECTED_DEVICE = "";
    public String MODEL_ID = "";
    String id = "";
    DesignModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.toolbar.back.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        binding.toolbar.title.setText("Checkout");

        id = getIntent().getStringExtra(Constants.ID);
        SELECTED_DEVICE = getIntent().getStringExtra(Constants.DEVICE);
        MODEL_ID = getIntent().getStringExtra(Constants.MODEL_ID);
        binding.quantityRow.setText("x" + String.valueOf(quantity));

        Constants.databaseReference().child(SELECTED_DEVICE).child(Constants.DESIGNS).child(MODEL_ID).child(id)
                .get().addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.exists()) {
                        model = dataSnapshot.getValue(DesignModel.class);
                        binding.name.setText(model.name);
                        binding.nameRow.setText(model.name);
                        binding.price.setText(String.format("$%.2f", model.price));
                        binding.priceRow.setText(String.format("$%.2f", model.price));
                        calculateSubtotal();
                        Glide.with(this).load(model.image).into(binding.image);
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });

        binding.negative.setOnClickListener(v -> {
            if (quantity > 1) {
                --quantity;
                binding.quantity.setText(String.valueOf(quantity));
                binding.quantityRow.setText("x" + String.valueOf(quantity));
                calculateSubtotal();
            }
        });

        binding.positive.setOnClickListener(v -> {
            ++quantity;
            binding.quantity.setText(String.valueOf(quantity));
            binding.quantityRow.setText("x" + String.valueOf(quantity));
            calculateSubtotal();
        });

        binding.confirm.setOnClickListener(v -> {
            if (valid()) {
                uploadOrder();
            }
        });
    }

    private void uploadOrder() {
        Constants.showDialog();
        OrderModel orderModel = new OrderModel();
        orderModel.UID = UUID.randomUUID().toString();
        orderModel.modelID = model.modelID;
        orderModel.productID = model.id;
        orderModel.productName = model.name;
        orderModel.device = model.device;
        orderModel.price = model.price;
        orderModel.quantity = quantity;
        orderModel.personName = binding.personName.getEditText().getText().toString().trim();
        orderModel.email = binding.email.getEditText().getText().toString().trim();
        orderModel.number = binding.phone.getEditText().getText().toString().trim();
        orderModel.address = binding.address.getEditText().getText().toString().trim();

        Constants.databaseReference().child(Constants.ORDERS).child(orderModel.UID).setValue(orderModel)
                .addOnSuccessListener(unused -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, "Order Placed", Toast.LENGTH_SHORT).show();
                    getOnBackPressedDispatcher().onBackPressed();
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private boolean valid() {
        if (binding.personName.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Person Name is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.phone.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Phone Number is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.email.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Email is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.getEditText().getText().toString()).matches()) {
            Toast.makeText(this, "Email is not valid", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.address.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Address is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.initDialog(this);
    }

    private void calculateSubtotal() {
        double subTotal = quantity * model.price;
        binding.subtotal.setText(String.format("$%.2f", subTotal));
    }
}