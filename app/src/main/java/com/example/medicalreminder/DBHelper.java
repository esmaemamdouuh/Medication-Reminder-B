package com.example.medicalreminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    // 🔢 إصدار قاعدة البيانات
    private static final int DB_VERSION = 1;

    // 📄 اسم قاعدة البيانات
    private static final String DB_NAME = "medical_reminder.db";

    // 🧑 جدول المستخدمين
    private static final String TABLE_USERS = "users";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_EMAIL = "email";
    private static final String COL_PASSWORD = "password";

    // 💊 جدول الأدوية
    private static final String TABLE_MEDICINES = "medicines";
    private static final String COL_MEDICINE_ID = "id";
    private static final String COL_MEDICINE_NAME = "medicine_name";
    private static final String COL_MEDICINE_TIME = "time";
    private static final String COL_MEDICINE_USER_ID = "user_id";

    // 🛠️ SQL لإنشاء جدول المستخدمين
    private static final String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COL_NAME + " TEXT,"
            + COL_EMAIL + " TEXT UNIQUE,"
            + COL_PASSWORD + " TEXT" + ")";

    // 🛠️ SQL لإنشاء جدول الأدوية
    private static final String CREATE_MEDICINES_TABLE = "CREATE TABLE " + TABLE_MEDICINES + "("
            + COL_MEDICINE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COL_MEDICINE_NAME + " TEXT,"
            + COL_MEDICINE_TIME + " TEXT,"
            + COL_MEDICINE_USER_ID + " INTEGER" + ")";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // تنفيذ أوامر SQL لإنشاء الجداول
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_MEDICINES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // في حالة تحديث الإصدار، نحذف الجداول ونعيد إنشاءها
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICINES);
        onCreate(db);
    }

    // ✅ تسجيل مستخدم جديد
    public boolean registerUser(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_EMAIL, email);
        values.put(COL_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    // 🔐 التحقق من بيانات تسجيل الدخول
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS +
                " WHERE " + COL_EMAIL + "=? AND " + COL_PASSWORD + "=?", new String[]{email, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // 🆔 الحصول على ID المستخدم من البريد الإلكتروني
    public int getUserId(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_ID + " FROM " + TABLE_USERS +
                " WHERE " + COL_EMAIL + "=?", new String[]{email});
        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(0);
        }
        cursor.close();
        return userId;
    }

    // 💊 إضافة دواء جديد
    public boolean addMedicine(String name, String time, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_MEDICINE_NAME, name);
        values.put(COL_MEDICINE_TIME, time);
        values.put(COL_MEDICINE_USER_ID, userId);

        long result = db.insert(TABLE_MEDICINES, null, values);
        return result != -1;
    }

    // 🔄 تحديث معلومات دواء موجود
    public boolean updateMedicine(long medicineId, String newName, String newTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_MEDICINE_NAME, newName);
        values.put(COL_MEDICINE_TIME, newTime);

        return db.update(TABLE_MEDICINES, values, COL_MEDICINE_ID + "=?", new String[]{String.valueOf(medicineId)}) > 0;
    }

    // ❌ حذف دواء
    public boolean deleteMedicine(long medicineId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_MEDICINES, COL_MEDICINE_ID + "=?", new String[]{String.valueOf(medicineId)}) > 0;
    }

    // 📋 جلب جميع الأدوية الخاصة بمستخدم معين
    public List<Medicine> getAllMedicinesForUser(int userId) {
        List<Medicine> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MEDICINES +
                " WHERE " + COL_MEDICINE_USER_ID + "=?", new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_MEDICINE_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_MEDICINE_NAME));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(COL_MEDICINE_TIME));
                list.add(new Medicine(id, name, time));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }
}