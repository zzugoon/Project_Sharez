package com.ex.project_sharez.datgle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ex.project_sharez.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//댓글읽기
public class dat extends Fragment {
    private RecyclerAdapter adapter;
    private DatabaseReference mDatabase;// ...
    private int t;
    private String title;

    public dat() {

    }

    public dat(String title) {
        this.title = title;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://project-sharez-default-rtdb.firebaseio.com/");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dat, container, false);
        t = 0;
        init(view);
        ddd();
        return view;
    }

    private void init(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.datrecycler);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new RecyclerAdapter();
        recyclerView.setAdapter(adapter);
    }
//    private String n=null,d=null,txt=null;

    private void ddd() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.clearItem();
                t = (int) dataSnapshot.child("댓글").child(title).getChildrenCount();
                if (t != 0) {
                    for (int i = 0; i < t; i++) {
                        int x = i + 1;

                        if (dataSnapshot.child("댓글").child(title).child(x + "번").child("아이디").getValue() == null) {
                            System.out.println("x=" + x + ",t=" + t);
                            break;
                        }
                        String n = dataSnapshot.child("댓글").child(title).child(x + "번").child("아이디").getValue().toString();
                        String d = dataSnapshot.child("댓글").child(title).child(x + "번").child("날짜").getValue().toString();
                        String txt = dataSnapshot.child("댓글").child(title).child(x + "번").child("내용").getValue().toString();
                        System.out.println(txt);
                        // 각 List의 값들을 data 객체에 set 해줍니다.
                        Data data = new Data();
                        data.setname(n);
                        data.setdate(d);
                        data.settxt(txt);
                        // 각 값이 들어간 data를 adapter에 추가합니다.
                        adapter.addItem(data);
                    }
                    // adapter의 값이 변경되었다는 것을 알려줍니다.
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}