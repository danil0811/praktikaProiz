package com.example.praktika;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
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
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
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
                if (dataPoints != null) {
                    sortData(position);
                }
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

                // Прочитайте данные и добавьте их в список
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DataPoint dataPoint = snapshot.getValue(DataPoint.class);
                    dataPoints.add(dataPoint);
                }

                // Обновление графика после чтения данных
                updateChart();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Обработка ошибок чтения данных
            }
        });
    }

    private void updateChart() {
        List<Entry> entries = new ArrayList<>();

        // Добавление точек на график
        for (int i = 0; i < dataPoints.size(); i++) {
            DataPoint dataPoint = dataPoints.get(i);
            float x = i;
            float y = Float.parseFloat(dataPoint.getEnergyUsage());
            entries.add(new Entry(x, y));
        }

        dataSet = new LineDataSet(entries, "Energy Usage");

        // Настройка стиля линии
        dataSet.setLineWidth(2f);
        dataSet.setColor(Color.BLUE);
        dataSet.setCircleColor(Color.RED);
        dataSet.setCircleRadius(5f);
        dataSet.setDrawCircleHole(false);

        lineData = new LineData(dataSet);

        // Настройка форматтера для оси X
        List<String> xAxisValues = new ArrayList<>();
        for (DataPoint dataPoint : dataPoints) {
            xAxisValues.add(dataPoint.getMaterial());
        }
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisValues));

        lineChart.setData(lineData);
        lineChart.invalidate(); // Обновление графика
    }

    private void sortData(int position) {
        switch (position) {
            case 0: // Сортировка по материалу
                Collections.sort(dataPoints, new Comparator<DataPoint>() {
                    @Override
                    public int compare(DataPoint o1, DataPoint o2) {
                        return o1.getMaterial().compareTo(o2.getMaterial());
                    }
                });
                break;
            case 1: // Сортировка по использованию материала
                Collections.sort(dataPoints, new Comparator<DataPoint>() {
                    @Override
                    public int compare(DataPoint o1, DataPoint o2) {
                        return o1.getMaterialUsage().compareTo(o2.getMaterialUsage());
                    }
                });
                break;
            case 2: // Сортировка по использованию энергии
                Collections.sort(dataPoints, new Comparator<DataPoint>() {
                    @Override
                    public int compare(DataPoint o1, DataPoint o2) {
                        float energyUsage1 = Float.parseFloat(o1.getEnergyUsage());
                        float energyUsage2 = Float.parseFloat(o2.getEnergyUsage());
                        return Float.compare(energyUsage1, energyUsage2);
                    }
                });
                break;
        }

        updateChart();
    }
}
