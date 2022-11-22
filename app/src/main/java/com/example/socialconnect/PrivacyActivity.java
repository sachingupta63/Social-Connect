package com.example.socialconnect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

public class PrivacyActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String[] status={"Choose any one","Public","Private"};

    TextView status_tv;
    Spinner spinner;
    Button btn_Save;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    DocumentReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        status_tv=findViewById(R.id.tv_status_pa);
        btn_Save=findViewById(R.id.btn_save_status_pa);
        spinner=findViewById(R.id.spinner_status_pa);

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();

        String curId =user.getUid();
        reference=db.collection("user").document(curId);

        ArrayAdapter arrayAdapter=new ArrayAdapter(this, android.R.layout.simple_spinner_item,status);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);

        btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePrivacy();
            }
        });

    }



    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Toast.makeText(this, "Please Select a value", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onStart() {
        super.onStart();
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    String privacy_result=task.getResult().getString("privacy");
                    status_tv.setText(privacy_result);
                }else{
                    Toast.makeText(PrivacyActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void savePrivacy() {

        final String value=spinner.getSelectedItem().toString();

        if(value.equals("Choose any one")){
            Toast.makeText(this, "Please Select a Value", Toast.LENGTH_SHORT).show();
        }else{
            FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
            String curUid=user.getUid();

            final DocumentReference dtoUdate=db.collection("user").document(curUid);

            db.runTransaction(new Transaction.Function<Void>() {
                @Nullable
                @Override
                public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {


                    transaction.update(dtoUdate,"privacy",value);

                    return null;
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(PrivacyActivity.this, "Status Update", Toast.LENGTH_SHORT).show();


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PrivacyActivity.this, "There is an error", Toast.LENGTH_SHORT).show();
                }
            });



        }

    }
}