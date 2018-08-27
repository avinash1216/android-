package nnk.com.firebase1;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    ListView lv;
    ArrayList al;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = findViewById(R.id.lv);
        pd = new ProgressDialog(this);
        pd.setMessage("Please Wait");
    }

    public void saveDetails(View view) {
        EditText et1 = findViewById(R.id.et1);
        EditText et2 = findViewById(R.id.et2);
        EditText et3 = findViewById(R.id.et3);

        String name = et1.getText().toString().trim();
        String age = et2.getText().toString().trim();
        String cno = et3.getText().toString().trim();

        FirebaseDatabase fd = FirebaseDatabase.getInstance();
        DatabaseReference dr = fd.getReference("user").child(cno);

        MyFBHandler myfb = new MyFBHandler(name, age);
        dr.setValue(myfb);

        Toast.makeText(this, "User Created", Toast.LENGTH_SHORT).show();
    }

    public void viewDetails(View view)
    {
        al = new ArrayList();
        pd.show();
       FirebaseDatabase fd = FirebaseDatabase.getInstance();
       DatabaseReference dr = fd.getReference("user");
       dr.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot)
           {
                for (DataSnapshot ds:dataSnapshot.getChildren())
                {
                    String key = ds.getKey();
                    al.add(key);
                }
                ArrayAdapter aa = new ArrayAdapter(MainActivity.this,android.R.layout.simple_dropdown_item_1line,al);
                lv.setAdapter(aa);
                lv.setVisibility(View.VISIBLE);
                pd.dismiss();
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });

       lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id)
           {
               String key = (String)al.get(position);

                FirebaseDatabase fd = FirebaseDatabase.getInstance();
                DatabaseReference dr = fd.getReference("user").child(key);

                dr.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        MyFBHandler my = dataSnapshot.getValue(MyFBHandler.class);
                        String name = my.getName();
                        String age = my.getAge();

                        AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                        adb.setMessage("Name : "+name+"\nAge : "+age);
                        adb.show();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

           }
       });
    }
}
