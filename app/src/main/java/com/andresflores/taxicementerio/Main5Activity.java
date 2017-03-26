package com.andresflores.taxicementerio;

import android.app.Activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class Main5Activity extends Activity {

    ListView listView;
    ArrayList<String> listViewContent;
    ArrayAdapter arrayAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);

        listView = (ListView) findViewById(R.id.listView);
        listViewContent = new ArrayList<String>();
        listViewContent.add("Prueba");
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listViewContent);
        listView.setAdapter(arrayAdapter);




    }
}
