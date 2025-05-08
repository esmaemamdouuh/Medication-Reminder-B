package com.example.medicalreminder;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class MedicineAdapter extends BaseAdapter {

    private Context context;
    private List<Medicine> medicinesList;

    // 🔁 المُنشئ: نمرّر له قائمة الأدوية ومعلومات التطبيق (Context)
    public MedicineAdapter(Context context, List<Medicine> medicinesList) {
        this.context = context;
        this.medicinesList = medicinesList;
    }

    // ✅ عدد الأدوية في القائمة
    @Override
    public int getCount() {
        return medicinesList.size();
    }

    // ✅ الحصول على دواء محدد حسب الموقع (position)
    @Override
    public Object getItem(int position) {
        return medicinesList.get(position);
    }

    // ✅ الحصول على ID الخاص بالدواء (من قاعدة البيانات)
    @Override
    public long getItemId(int position) {
        return medicinesList.get(position).getId(); // يجب أن يكون هناك getId()
    }

    // 🧱 تحويل تصميم item_medicine.xml إلى عنصر في القائمة
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // إذا لم يكن هناك عرض جاهز، نقوم بإنشائه من ملف XML
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_medicine, parent, false);
        }

        // 🔍 ربط العناصر من الواجهة
        TextView textMedicineName = convertView.findViewById(R.id.textMedicineName);
        Button btnEdit = convertView.findViewById(R.id.btnEdit);
        Button btnDelete = convertView.findViewById(R.id.btnDelete);

        // 📦 أخذ الدواء الحالي من القائمة
        Medicine medicine = medicinesList.get(position);

        // 💬 عرض اسم الدواء والوقت
        textMedicineName.setText(medicine.getName() + " - " + medicine.getTime());

        // ✏️ عند النقر على زر Edit
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditMedicineActivity.class);
            intent.putExtra("MEDICINE_ID", medicine.getId());
            intent.putExtra("MEDICINE_NAME", medicine.getName());
            intent.putExtra("MEDICINE_TIME", medicine.getTime());
            context.startActivity(intent);
        });

        // ❌ عند النقر على زر Delete
        btnDelete.setOnClickListener(v -> {
            DBHelper dbHelper = new DBHelper(context);
            dbHelper.deleteMedicine(medicine.getId()); // حذف من قاعدة البيانات
            medicinesList.remove(position); // إزالة من القائمة المعروضة
            notifyDataSetChanged(); // تحديث الشاشة
        });

        return convertView;
    }
}