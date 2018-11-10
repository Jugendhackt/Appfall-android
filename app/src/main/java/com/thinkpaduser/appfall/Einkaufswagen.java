package com.thinkpaduser.appfall;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Einkaufswagen extends AppCompatActivity {
private FloatingActionButton addItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_einkaufswagen);

        addItem = (FloatingActionButton) findViewById(R.id.btnadditem);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Einkaufswagen.this);
                LayoutInflater inflater = getLayoutInflater();
View view = inflater.inflate(R.layout.dialog_view_add_item, null, false);
                final EditText etname = (EditText) view.findViewById(R.id.etproduktname);
                EditText etmenge = (EditText) view.findViewById(R.id.etmenge);
                etmenge
                builder.setTitle("Hinzufügen")
                        .setView(view)
                        .setPositiveButton("Fertig", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("list").child(etname);

                            }
                        })
                        .setCancelable(true);

               AlertDialog alert = builder.show();



        }
    });
}}
