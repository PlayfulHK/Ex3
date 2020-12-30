package com.example.exp3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final List<Integer> idList = new ArrayList<>();
    //private final List<Integer> pidList = new ArrayList<>();
    private final List<String> city_nameList = new ArrayList<>();
    private final List<String> city_codeList = new ArrayList<>();
    ArrayAdapter<String> simpleAdapter;
    Button OK, MyConcern;
    EditText etSearch;
    ListView ProvinceList;

    private void parseJSONWithJSONObject(String jsonData, int targetPid) {

        try {
            idList.clear();
            //pidList.clear();
            city_nameList.clear();
            city_codeList.clear();
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int id = jsonObject.getInt("id");
                int pid = jsonObject.getInt("pid");
                String city_code = jsonObject.getString("city_code");
                String city_name = jsonObject.getString("city_name");
                if (pid == targetPid) {
                    idList.add(id);
                    //pidList.add(pid);
                    city_codeList.add(city_code);
                    city_nameList.add(city_name);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getJson(Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream is = context.getResources().openRawResource(R.raw.city);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OK = findViewById(R.id.ok);
        etSearch = findViewById(R.id.search);
        ProvinceList = findViewById(R.id.provincelist);
        OK.setOnClickListener(this);
        MyConcern = findViewById(R.id.concern_list);
        MyConcern.setOnClickListener(this);

        String responseData = getJson(this);
        parseJSONWithJSONObject(responseData, 0);
        simpleAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, city_nameList);

        ProvinceList.setAdapter(simpleAdapter);
        ProvinceList = findViewById(R.id.provincelist);
        //配置ArrayList点击按钮
        ProvinceList.setOnItemClickListener((parent, view, position, id) -> {
            int tran_id = idList.get(position);
            String tran_city_code = city_codeList.get(position);
            String tran_city_name = city_nameList.get(position);
            if (tran_city_code.equals("")) {
                parseJSONWithJSONObject(responseData, tran_id/*hierarchy++*/);
                Toast.makeText(this, tran_city_name, Toast.LENGTH_SHORT).show();
                ProvinceList.setAdapter(simpleAdapter);
                Button buttonBack = findViewById(R.id.back);
                buttonBack.setVisibility(View.VISIBLE);
                buttonBack.setOnClickListener(v -> {
                    parseJSONWithJSONObject(responseData, 0/*hierarchy++*/);
                    ProvinceList.setAdapter(simpleAdapter);
                    buttonBack.setVisibility(View.INVISIBLE);
                });
            } else {
                Intent intent = new Intent(MainActivity.this, com.example.exp3.Weather.class);
                intent.putExtra("searchCityCode", tran_city_code);
                startActivity(intent);
            }
        });


    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok:
                String searchCityCode = String.valueOf(etSearch.getText());
                if (searchCityCode.length() > 9) {
                    Toast.makeText(this, "数字长度不能大于九位！", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(MainActivity.this, com.example.exp3.Weather.class);
                    intent.putExtra("searchCityCode", searchCityCode);
                    startActivity(intent);
                }
                break;
            case R.id.concern_list:
                Intent intent = new Intent(MainActivity.this, com.example.exp3.MyConcernList.class);
                startActivity(intent);
                break;
        }
    }
}