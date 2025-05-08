package com.example.medicalreminder;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditMedicineActivity extends AppCompatActivity {

    // العناصر في الواجهة
    EditText editMedicineName;
    TimePicker timePicker;
    Button btnSaveChanges, btnDeleteMedicine;

    // قاعدة البيانات
    DBHelper dbHelper;

    // بيانات الدواء
    long medicineId;
    String currentName;
    String currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine); // استخدام واجهة خاصة بالتعديل

        // 🔗 ربط العناصر من الواجهة
        editMedicineName = findViewById(R.id.editMedicineName);
        timePicker = findViewById(R.id.timePicker);
        btnSaveChanges = findViewById(R.id.btnSaveMedicine);



        dbHelper = new DBHelper(this);

        // 📥 استلام بيانات الدواء من HomeActivity
        medicineId = getIntent().getLongExtra("MEDICINE_ID", -1);
        currentName = getIntent().getStringExtra("MEDICINE_NAME");
        currentTime = getIntent().getStringExtra("MEDICINE_TIME");


        if (medicineId == -1 || currentName == null || currentTime == null) {
            Toast.makeText(this, "Error loading medicine", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        editMedicineName.setText(currentName);
        // ⏰ تقسيم الوقت إلى ساعة ودقيقة
        String[] parts = currentTime.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            timePicker.setHour(hour);
            timePicker.setMinute(minute);
        } else {
            timePicker.setCurrentHour(hour);
            timePicker.setCurrentMinute(minute);
        }


        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = editMedicineName.getText().toString();
                int newHour, newMinute;

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    newHour = timePicker.getHour();
                    newMinute = timePicker.getMinute();
                } else {
                    newHour = timePicker.getCurrentHour();
                    newMinute = timePicker.getCurrentMinute();
                }

                String newTime = newHour + ":" + newMinute;

                if (!newName.isEmpty()) {

                    dbHelper.updateMedicine(medicineId, newName, newTime);
                    Toast.makeText(EditMedicineActivity.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                    finish(); // العودة إلى الصفحة الرئيسية
                } else {
                    Toast.makeText(EditMedicineActivity.this, "Please enter a name", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnDeleteMedicine.setOnClickListener(v -> {

            new android.app.AlertDialog.Builder(EditMedicineActivity.this)
                    .setTitle("Delete Medicine")
                    .setMessage("Are you sure you want to delete this medicine?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // 🧨 حذف الدواء من قاعدة البيانات
                        dbHelper.deleteMedicine(medicineId);
                        Toast.makeText(EditMedicineActivity.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                        finish(); // العودة إلى الصفحة الرئيسية
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }
}