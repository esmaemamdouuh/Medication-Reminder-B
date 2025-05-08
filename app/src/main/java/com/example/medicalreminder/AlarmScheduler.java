package com.example.medicalreminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class AlarmScheduler {

    private static final String TAG = "AlarmScheduler";

    /**
     * ت_SCHEDULE إشعار يومي لتذكير المستخدم بتناول الدواء
     *
     * @param context       سياق التطبيق (مثل Activity)
     * @param time          الوقت بصيغة "ساعة:دقيقة" مثل "14:30"
     * @param medicineName  اسم الدواء لإظهاره في الإشعار
     */
    public static void scheduleMedicineReminder(Context context, String time, String medicineName) {
        try {
            // تقسيم الوقت إلى ساعة ودقيقة
            String[] parts = time.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);

            // إعداد التاريخ والوقت للتنبيه
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            // إذا كان الوقت قد مر اليوم، نضيف يومًا
            if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            // إنشاء Intent يُرسل إلى Broadcast Receiver عند وقت التنبيه
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra("medicine_name", medicineName);

            // إنشاء PendingIntent فريد لكل دواء بناءً على الاسم + الوقت
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    medicineName.hashCode(), // ID فريد لكل دواء
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            // الحصول على AlarmManager
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            if (alarmManager != null) {
                // جدولة تنبيه متكرر يوميًا
                alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,             // نوع التنبيه (وقت حقيقي)
                        calendar.getTimeInMillis(),         // أول مرة سيُرسل فيها التنبيه
                        AlarmManager.INTERVAL_DAY,          // تكرار كل يوم
                        pendingIntent                       // ما يجب فعله عند الوقت المحدد
                );

                Log.d(TAG, "Scheduled reminder for: " + medicineName + " at " + time);
            } else {
                Log.e(TAG, "AlarmManager is null. Could not schedule reminder.");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error scheduling alarm: " + e.getMessage());
            e.printStackTrace();
        }
    }
}