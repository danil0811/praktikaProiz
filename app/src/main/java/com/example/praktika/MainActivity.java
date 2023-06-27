package com.example.praktika;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.praktika.Data;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnFailureListener;
import androidx.annotation.NonNull;


public class MainActivity extends AppCompatActivity {
    private EditText editTextMaterial;
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

        editTextMaterial = findViewById(R.id.editTextMaterial);
        editTextMaterialUsage = findViewById(R.id.editTextMaterialUsage);
        editTextEnergyUsage = findViewById(R.id.editTextEnergyUsage);
        buttonSave = findViewById(R.id.buttonSave);

        // Получаем ссылку на узел в базе данных, где будем хранить данные
        databaseReference = FirebaseDatabase.getInstance().getReference().child("data");

        buttonSave.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        Button buttonNext = findViewById(R.id.buttonNext);
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
        String material = editTextMaterial.getText().toString().trim();
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
