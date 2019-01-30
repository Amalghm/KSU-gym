package com.example.app.ksugym;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class AdminNewStudentFragment  extends Fragment {

    static View view;
    GridView newstudentsGV;
    CustomAdminNewStudentsGV customGV;

    ArrayList <Students> studentsList = new ArrayList<>();

    String [] nameArray;
    String [] numArray;
    String [] emailArray;
    String [] weightArray;
    String [] heightArray;
    String [] passwordArray;
    String [] subscribedArray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_admin_newstudents, container, false);
        newstudentsGV = view.findViewById(R.id.NewStudentsGridView);

        //Get All orders from firebase then filter which one belongs to the logged in patient
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference moviesRef = rootRef.child("Students");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
               studentsList = new ArrayList<>();
                for (DataSnapshot children : dataSnapshot.getChildren())
                {
                    if(children.child("subsicibed").getValue(String.class).equals("no")) {
                        String name = children.child("name").getValue(String.class);
                        String num = children.child("number").getValue(String.class);
                        String email = children.child("email").getValue(String.class);
                        String weight = children.child("weight").getValue(String.class);
                        String height = children.child("height").getValue(String.class);
                        String password = children.child("password").getValue(String.class);
                        String subscribed = children.child("subsicibed").getValue(String.class);

                        Students A = new Students(name, num,email,weight,height,password,subscribed);
                        studentsList.add(A);
                    }//End of if
                }//End of for loop

                if(studentsList.size() != 0) {
                    nameArray = new String[studentsList.size()];
                    numArray =new String[studentsList.size()];
                    emailArray =  new String[studentsList.size()];
                    weightArray =  new String[studentsList.size()];
                    heightArray =  new String[studentsList.size()];
                    passwordArray= new String[studentsList.size()];
                    subscribedArray=  new String[studentsList.size()];

                    for (int i = 0; i < studentsList.size(); i++) {
                        Students x = studentsList.get(i);
                        nameArray[i] = x.getName();
                        numArray[i] = x.getNumber();
                        emailArray[i] = x.getEmail();
                        weightArray[i] = x.getWeight();
                        heightArray[i] = x.getHeight();
                        passwordArray[i] = x.getPassword();
                       subscribedArray[i] = x.getSubsicibed();
                    }

                    customGV = new CustomAdminNewStudentsGV(view.getContext(), nameArray,numArray);
                    newstudentsGV.setAdapter(customGV);
                }//End of second if
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        moviesRef.addListenerForSingleValueEvent(eventListener);

        //****************** ENd of firebase reading **********************

        newstudentsGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
            {
                new AlertDialog.Builder(view.getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("New Student")
                        .setMessage("Are you sure you want to accept this new subscription?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                DatabaseReference leadersRef = FirebaseDatabase.getInstance().getReference("Students");
                                Query query = leadersRef.orderByChild("subsicibed").equalTo(subscribedArray[position]);
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        for (DataSnapshot child : snapshot.getChildren())
                                        {
                                            if(child.child("number").getValue(String.class).equals(numArray[position])) {
                                                String test = child.child("subsicibed").getValue(String.class);
                                                child.getRef().child("subsicibed").setValue("yes");
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });



                            }

                        })
                        .setNegativeButton("No", null)
                        .show();

            }
        });





        return view;
    }//End of OnCreate



} //End of Class
