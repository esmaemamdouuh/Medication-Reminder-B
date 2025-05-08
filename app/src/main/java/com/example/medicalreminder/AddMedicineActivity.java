package com.example.medicalreminder;
import android.content.pm.PackageManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.medicalreminder.AlarmScheduler;
import com.example.medicalreminder.NotificationHelper;
import com.example.medicalreminder.R;

public class AddMedicineActivity extends AppCompatActivity {

    EditText editMedicineName;
    TimePicker timePicker;
    Button btnSaveMedicine;

    DBHelper dbHelper;
    int userId;

    private static final String TAG = "AddMedicineActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        // ربط العناصر من الواجهة
        editMedicineName = findViewById(R.id.editMedicineName);
        timePicker = findViewById(R.id.timePicker);
        btnSaveMedicine = findViewById(R.id.btnSaveMedicine);

        dbHelper = new DBHelper(this);

        // استلام userId من HomeActivity
        userId = getIntent().getIntExtra("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "Error: User not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 🔔 طلب إذن الإشعارات إذا كان النظام Android 13 أو أعلى
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            String permission = "android.permission.POST_NOTIFICATIONS";
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{permission}, 1);
            }
        }

        btnSaveMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editMedicineName.getText().toString();
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();

                String time = hour + ":" + minute;

                if (!name.isEmpty()) {
                    boolean isAdded = dbHelper.addMedicine(name, time, userId);
                    if (isAdded) {

                        // ✅ إظهار إشعار فوري للمستخدم
                        NotificationHelper.showMedicineAddedNotification(
                                AddMedicineActivity.this,
                                name,
                                time
                        );

                        Toast.makeText(AddMedicineActivity.this, "Medicine Added", Toast.LENGTH_SHORT).show();

                        // ✅ جدولة تنبيه عند وقت تناول الدواء
                        Log.d(TAG, "Scheduling reminder for: " + name + " at " + time);
                        AlarmScheduler.scheduleMedicineReminder(AddMedicineActivity.this, time, name);

                        // العودة إلى الصفحة الرئيسية
                        finish();
                    } else {
                        Toast.makeText(AddMedicineActivity.this, "Failed to add medicine", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddMedicineActivity.this, "Please enter medicine name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}