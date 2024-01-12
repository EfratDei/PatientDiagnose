package com.example.project;
import android.app.Activity;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.project.R;
import com.example.project.item;

import java.util.ArrayList;

public class listAdapter extends ArrayAdapter<item> {
    private ArrayList<item> list;
    private Activity context;
    public listAdapter(Activity context, ArrayList<item> tours) {
        super(context, R.layout.item, tours);
        this.context=context;
        this.list = tours;
    }


    @Override
    public View getView(int position, View rowView, ViewGroup parent) {
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.item, parent, false);
        }

        TextView criteria = rowView.findViewById(R.id.critirea);
        TextView treatment = rowView.findViewById(R.id.treatment);
        TextView diagnosis = rowView.findViewById(R.id.diagnosis);

        criteria.setText(list.get(position).getCriterea());
        treatment.setText(list.get(position).getTreatment());
        diagnosis.setText(list.get(position).getDiagnosis());

        treatment.setMovementMethod(new ScrollingMovementMethod());
        diagnosis.setMovementMethod(new ScrollingMovementMethod());

        return rowView;
    }

    @Override
    public item getItem(int position) {
        return list.get(position);
    }
}