package com.moutamid.mobiledesigns.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.mobiledesigns.Constants;
import com.moutamid.mobiledesigns.R;
import com.moutamid.mobiledesigns.databinding.ActivityMainBinding;
import com.moutamid.mobiledesigns.model.DeviceModels;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    public String SELECTED_DEVICE = Constants.iPhone;
    ArrayList<DeviceModels> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Constants.checkApp(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.devices.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.iphone) {
                SELECTED_DEVICE = Constants.iPhone;
            } else if (checkedId == R.id.samsung) {
                SELECTED_DEVICE = Constants.Samsung;
            } else if (checkedId == R.id.watch) {
                SELECTED_DEVICE = Constants.Apple_Watch;
            }
            binding.models.getEditText().setText("");
            getData();
        });

        binding.next.setOnClickListener(v -> {
            if (!binding.models.getEditText().getText().toString().isEmpty()) {
                Optional<DeviceModels> optionalDevice = list.stream()
                        .filter(deviceModels -> deviceModels.name.equalsIgnoreCase(binding.models.getEditText().getText().toString().trim()))
                        .findFirst();
                if (optionalDevice.isPresent()) {
                    DeviceModels device = optionalDevice.get();
                    startActivity(new Intent(this, DeviceActivity.class)
                            .putExtra(Constants.DEVICE, SELECTED_DEVICE)
                            .putExtra(Constants.MODEL_ID, device.id));
                } else {
                    Toast.makeText(this, "Device not found! Make Sure name is correct", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Device Model is empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.initDialog(this);
        list = new ArrayList<>();
        getData();
    }

    private void getData() {
        Constants.showDialog();
        Constants.databaseReference().child(SELECTED_DEVICE).child(Constants.MODELS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Constants.dismissDialog();
                if (snapshot.exists()) {
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        DeviceModels topicsModel = dataSnapshot.getValue(DeviceModels.class);
                        list.add(topicsModel);
                    }
                    List<String> names = new ArrayList<>();
                    for (DeviceModels model : list) {
                        names.add(model.name);
                    }
                    ArrayAdapter<String> models = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, names);
                    binding.modelsList.setAdapter(models);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError e) {
                Constants.dismissDialog();
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}