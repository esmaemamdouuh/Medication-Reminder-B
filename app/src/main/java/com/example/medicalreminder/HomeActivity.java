package com.example.medicalreminder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    ListView listMedicines;
    Button btnAddMedicine;

    DBHelper dbHelper;
    String userEmail;
    int userId = -1;

    MedicineAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // ربط العناصر من الواجهة
        listMedicines = findViewById(R.id.listMedicines);
        btnAddMedicine = findViewById(R.id.btnAddMedicine);

        // إعداد قاعدة البيانات
        dbHelper = new DBHelper(this);

        // استلام البريد الإلكتروني من LoginActivity
        userEmail = getIntent().getStringExtra("email");

        if (userEmail != null && !userEmail.isEmpty()) {
            userId = dbHelper.getUserId(userEmail);

            if (userId == -1) {
                Log.e("HomeActivity", "User ID is -1 for email: " + userEmail);
                finish();
            }
        } else {
            finish();
        }

        // تحميل الأدوية وعرضها
        loadMedicines();

        // عند الضغط على زر إضافة دواء جديد
        btnAddMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userId != -1) {
                    Intent intent = new Intent(HomeActivity.this, AddMedicineActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // إعادة تحميل الأدوية عند العودة من تعديل أو إضافة
        loadMedicines();
    }

    private void loadMedicines() {
        if (userId == -1) return;

        List<Medicine> medicines = dbHelper.getAllMedicinesForUser(userId);
        adapter = new MedicineAdapter(this, medicines);
        listMedicines.setAdapter(adapter);
    }
}