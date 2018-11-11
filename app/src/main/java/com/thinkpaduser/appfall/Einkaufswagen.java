package com.thinkpaduser.appfall;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.common.ChangeEventType;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.firebase.ui.database.ObservableSnapshotArray;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Einkaufswagen extends AppCompatActivity {
    private FloatingActionButton addItem;
    private ListView einkauf;
    private ListAdapter mAdapter;
    private Button einkaufen;
    private Button beendet;
    private View.OnClickListener addlistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Einkaufswagen.this);
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_view_add_item, null, false);
            final EditText etname = (EditText) view.findViewById(R.id.etproduktname);
            final EditText etmenge = (EditText) view.findViewById(R.id.etmenge);


            builder.setTitle("Hinzufügen")
                    .setView(view)
                    . setPositiveButton("Fertig", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            ref.child(etname.getText().toString()).setValue(new Eintrag(etname.getText().toString(), Long.valueOf(etmenge.getText().toString())));


                        }
                    })
                    .setCancelable(true);

            AlertDialog alert = builder.show();


        }
    };


    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("list");
    DatabaseReference ein = FirebaseDatabase.getInstance().getReference().child("einkaufen");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_einkaufswagen);


        einkaufen = (Button) findViewById(R.id.btnEinkaufen);
        addItem = (FloatingActionButton) findViewById(R.id.btnadditem);
        einkauf = (ListView) findViewById(R.id.lvEinkauf);
        beendet = (Button) findViewById(R.id.btnBeendet);
        beendet.setEnabled(false);
        ein.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (((Long) dataSnapshot.getValue()) == 1) {
                  addItem.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          Toast.makeText(Einkaufswagen.this, "Es ist gerade jemand einkaufen", Toast.LENGTH_SHORT).show();


                      }
                  });
                    einkaufen.setEnabled(false);
                    einkaufen.setText("gerade einkaufen");
                } else {
einkaufen.setEnabled(true);

                    einkaufen.setText("Einkaufen");
                    addItem.setOnClickListener(addlistener);
                }
            }




            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        einkaufen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beendet.setEnabled(true);
                einkaufen.setEnabled(false);

                ein.setValue(1);

            }
        });
        beendet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                einkaufen.setEnabled(true);
                beendet.setEnabled(false);
                einkaufen.setText("Einkaufen");
                ein.setValue(0);
                ref.removeValue();
            }
        });
        //addItem.setOnClickListener(
        listview();
    }

    public void restartApp() {
        Intent i = new Intent(getApplicationContext(), Einkaufswagen.class);
        startActivity(i);
        finish();
    }

    private void listview() {

        Log.d("list", "hallo");

        mAdapter = new ListAdapter();
        einkauf.setAdapter(mAdapter);


    }

    public class ListAdapter extends BaseAdapter {
        private static final String TAG = "FirebaseListAdapter";
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("list");


        List<String> namen = Collections.EMPTY_LIST;
        List<String> mengen = Collections.EMPTY_LIST;

        public ListAdapter() {
            ref.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    namen = new ArrayList<>();
                    mengen = new ArrayList<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {

                        namen.add(child.getKey());

                        mengen.add(
                                String.valueOf(((HashMap<String, Long>) child.getValue()).get("Menge"))
                        );
                        Log.d("line", child.getKey());
                    }
                    notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }


        @NonNull
        @Override
        public String getItem(int position) {
            return namen.get(position);
        }


        @Override
        public int getCount() {
            return namen.size();
        }

        @Override
        public long getItemId(int i) {
            // http://stackoverflow.com/questions/5100071/whats-the-purpose-of-item-ids-in-android-listview-adapter
            return namen.get(i).hashCode();
        }


        @Override
        @NonNull
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listviewitems, parent, false);
            }

            String model = getItem(position);
            ((TextView) convertView.findViewById(R.id.tvname)).setText(namen.get(position));
            ((TextView) convertView.findViewById(R.id.tvmenge)).setText(mengen.get(position));


            return convertView;
        }
    }

    public class Eintrag {
        public String Name;
        public Long Menge;

        public Eintrag(String name, Long menge) {
            this.Name = name;
            this.Menge = menge;
        }
    }

    }



