package com.example.praktika;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class activity_chart extends AppCompatActivity {

    private LineChart lineChart;
    private LineDataSet dataSet;
    private LineData lineData;

    private List<DataPoint> dataPoints; // Список данных для графика

    private Spinner spinnerSort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        lineChart = findViewById(R.id.chart);
        spinnerSort = findViewById(R.id.spinnerSort);

        // Если есть сохраненные данные, используйте их, иначе создайте новые
        if (savedInstanceState != null && savedInstanceState.containsKey("dataPoints")) {
            dataPoints = savedInstanceState.getParcelableArrayList("dataPoints");
            updateChart();
        } else {
            // Здесь происходит чтение данных из Firebase и получение списка DataPoint
            readData();
        }

        // Настройка осей графика
        XAxis xAxis = lineChart.getXAxis();
        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);

        // Настройка адаптера для спиннера
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sort_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(adapter);

        // Установка слушателя для сортировки при выборе элемента из спиннера
        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortData(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Ничего не делать
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Сохранение данных при повороте экрана или изменении состояния активности
        outState.putParcelableArrayList("dataPoints", new ArrayList<>(dataPoints));
    }

    private void readData() {
        // Получите ссылку на базу данных Firebase
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        // Выполните операцию чтения данных из нужного узла в базе данных
        databaseRef.child("data").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataPoints = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Прочитайте значения из снимка данных и создайте объект DataPoint
                    String material = snapshot.child("material").getValue(String.class);
                    String materialUsage = snapshot.child("materialUsage").getValue(String.class);
                    String energyUsage = snapshot.child("energyUsage").getValue(String.class);
                    DataPoint dataPoint = new DataPoint(material, materialUsage, energyUsage);

                    // Добавьте DataPoint в список dataPoints
                    dataPoints.add(dataPoint);
                }

                // После чтения данных, обновите график
                updateChart();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Обработка ошибок чтения данных из Firebase
            }
        });
    }

    private void updateChart() {
        // Преобразование данных в формат Entry
        List<Entry> entries = convertDataToEntries(dataPoints);

        // Создание набора данных для графика
        dataSet = new LineDataSet(entries, "Data");
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setColor(getResources().getColor(R.color.lineColor));
        dataSet.setCircleColor(getResources().getColor(R.color.circleColor));

        // Создание объекта LineData
        lineData = new LineData(dataSet);

        // Установка данных на график
        lineChart.setData(lineData);
        lineChart.invalidate(); // Обновление графика
    }

    private List<Entry> convertDataToEntries(List<DataPoint> dataPoints) {
        List<Entry> entries = new ArrayList<>();
        for (DataPoint dataPoint : dataPoints) {
            Entry entry = new Entry(Float.parseFloat(dataPoint.getMaterialUsage()), Float.parseFloat(dataPoint.getEnergyUsage()));
            entries.add(entry);
        }
        return entries;
    }

    private void sortData(int position) {
        if (dataPoints == null) {
            return; // Проверка на null для избежания ошибки NullPointerException
        }

        switch (position) {
            case 0: // Сортировка по материалу
                Collections.sort(dataPoints, new Comparator<DataPoint>() {
                    @Override
                    public int compare(DataPoint dp1, DataPoint dp2) {
                        return dp1.getMaterial().compareTo(dp2.getMaterial());
                    }
                });
                break;
            case 1: // Сортировка по количеству израсходованного материала
                Collections.sort(dataPoints, new Comparator<DataPoint>() {
                    @Override
                    public int compare(DataPoint dp1, DataPoint dp2) {
                        float usage1 = Float.parseFloat(dp1.getMaterialUsage());
                        float usage2 = Float.parseFloat(dp2.getMaterialUsage());
                        return Float.compare(usage1, usage2);
                    }
                });
                break;
            case 2: // Сортировка по количеству израсходованной энергии
                Collections.sort(dataPoints, new Comparator<DataPoint>() {
                    @Override
                    public int compare(DataPoint dp1, DataPoint dp2) {
                        float energy1 = Float.parseFloat(dp1.getEnergyUsage());
                        float energy2 = Float.parseFloat(dp2.getEnergyUsage());
                        return Float.compare(energy1, energy2);
                    }
                });
                break;
        }

        updateChart();
    }
}
