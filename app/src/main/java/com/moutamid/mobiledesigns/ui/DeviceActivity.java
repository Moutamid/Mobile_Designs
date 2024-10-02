package com.moutamid.mobiledesigns.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.mobiledesigns.Constants;
import com.moutamid.mobiledesigns.R;
import com.moutamid.mobiledesigns.adapter.DesignAdapter;
import com.moutamid.mobiledesigns.databinding.ActivityDeviceBinding;
import com.moutamid.mobiledesigns.model.DesignModel;

import java.util.ArrayList;
import java.util.Collections;

public class DeviceActivity extends AppCompatActivity {
    ActivityDeviceBinding binding;
    public String SELECTED_DEVICE = "";
    public String MODEL_ID = "";
    ArrayList<DesignModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityDeviceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SELECTED_DEVICE = getIntent().getStringExtra(Constants.DEVICE);
        MODEL_ID = getIntent().getStringExtra(Constants.MODEL_ID);

        binding.toolbar.back.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        binding.toolbar.title.setText("Designs");

        list = new ArrayList<>();

        binding.designRC.setLayoutManager(new LinearLayoutManager(this));
        binding.designRC.setHasFixedSize(false);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.initDialog(this);
        getData();
    }

    private void getData() {
        Constants.showDialog();
        Constants.databaseReference().child(SELECTED_DEVICE).child(Constants.DESIGNS).child(MODEL_ID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Constants.dismissDialog();
                        if (snapshot.exists()) {
                            list.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                DesignModel model = dataSnapshot.getValue(DesignModel.class);
                                list.add(model);
                            }
                            if (!list.isEmpty()) {
                                Collections.reverse(list);
                                binding.dataLayout.setVisibility(View.VISIBLE);
                                binding.noLayout.setVisibility(View.GONE);
                            } else {
                                binding.dataLayout.setVisibility(View.GONE);
                                binding.noLayout.setVisibility(View.VISIBLE);
                            }
                            DesignAdapter adapter = new DesignAdapter(DeviceActivity.this, list);
                            binding.designRC.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Constants.dismissDialog();
                        Toast.makeText(DeviceActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}