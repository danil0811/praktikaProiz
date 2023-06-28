package com.example.praktika;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.praktika.Data;
import com.example.praktika.R;
import com.example.praktika.activity_chart;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import androidx.annotation.NonNull;

public class MainActivity extends AppCompatActivity{

    private Spinner spinnerMaterial;
    private EditText editTextMaterialUsage;
    private EditText editTextEnergyUsage;
    private Button buttonSave;
    private Button buttonNext;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main); // Обновленное имя файла разметки

        spinnerMaterial = findViewById(R.id.spinnerMaterial);
        editTextMaterialUsage = findViewById(R.id.editTextMaterialUsage);
        editTextEnergyUsage = findViewById(R.id.editTextEnergyUsage);
        buttonSave = findViewById(R.id.buttonSave);

        // Настройка адаптера для спиннера
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.materials, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMaterial.setAdapter(adapter);

        // Получаем ссылку на узел в базе данных, где будем хранить данные
        databaseReference = FirebaseDatabase.getInstance().getReference().child("data");

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        buttonNext = findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Обработка нажатия на кнопку
                Intent intent = new Intent(MainActivity.this, activity_chart.class);
                startActivity(intent);
            }
        });
    }

    private void saveData() {
        String material = spinnerMaterial.getSelectedItem().toString();
        String materialUsage = editTextMaterialUsage.getText().toString().trim();
        String energyUsage = editTextEnergyUsage.getText().toString().trim();

        // Создаем уникальный идентификатор для записи данных в базу данных
        String dataId = databaseReference.push().getKey();

        // Создаем объект данных
        Data data = new Data(material, materialUsage, energyUsage);

        // Записываем данные в базу данных по уникальному идентификатору
        databaseReference.child(dataId).setValue(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MainActivity.this, "Данные сохранены", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Ошибка при сохранении данных", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
