package com.example.medicalreminder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    ListView listMedicines;
    Button btnAddMedicine;
    ArrayAdapter<String> adapter;

    DBHelper dbHelper;
    String userEmail;
    int userId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // ربط الواجهة
        listMedicines = findViewById(R.id.listMedicines);
        btnAddMedicine = findViewById(R.id.btnAddMedicine);

        // إعداد قاعدة البيانات
        dbHelper = new DBHelper(this);

        // استلام البريد الإلكتروني من LoginActivity
        userEmail = getIntent().getStringExtra("email");

        if (userEmail != null && !userEmail.isEmpty()) {
            userId = dbHelper.getUserId(userEmail);

            if (userId == -1) {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                Log.e("HomeActivity", "User ID is -1 for email: " + userEmail);
            }
        } else {
            Toast.makeText(this, "Email not received", Toast.LENGTH_SHORT).show();
            Log.e("HomeActivity", "Email is null or empty");
        }

        // تحميل الأدوية وعرضها
        loadMedicines();

        // عند الضغط على دواء
        listMedicines.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedMedicine = (String) parent.getItemAtPosition(position);
                Toast.makeText(HomeActivity.this, "Taking: " + selectedMedicine, Toast.LENGTH_SHORT).show();
            }
        });

        // عند الضغط على زر إضافة دواء جديد
        btnAddMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userId != -1) {
                    Intent intent = new Intent(HomeActivity.this, AddMedicineActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                } else {
                    Toast.makeText(HomeActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // إعادة التحقق من البريد الإلكتروني (في حال تم الدخول من مكان آخر)
        userEmail = getIntent().getStringExtra("email");
        if (userEmail != null && !userEmail.isEmpty()) {
            userId = dbHelper.getUserId(userEmail);
        }
        loadMedicines(); // إعادة تحميل الأدوية من قاعدة البيانات
    }

    private void loadMedicines() {
        if (userId == -1) {
            Toast.makeText(this, "Please log in again", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> medicines = dbHelper.getMedicinesForUser(userId);

        if (medicines == null || medicines.isEmpty()) {
            Toast.makeText(this, "No medicines added yet", Toast.LENGTH_SHORT).show();
            medicines.add("No medicines to show");
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, medicines);
        } else {
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, medicines);
        }

        listMedicines.setAdapter(adapter);
    }
}