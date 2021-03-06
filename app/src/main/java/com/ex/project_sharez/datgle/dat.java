package com.ex.project_sharez.datgle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ex.project_sharez.R;
import com.ex.project_sharez.ui.my.MyData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

//댓글읽기
public class dat extends Fragment implements OnItemClick{
    private RecyclerAdapter adapter;
    private DatabaseReference mDatabase;// ...
    private int t, t1;
    private String title;

    MyData myData;

    public dat() {

    }

    public dat(String title, MyData myData) {
        this.title = title;
        this.myData = myData;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://project-sharez-default-rtdb.firebaseio.com/");
    }


    @Override
    public void onClick() {
        System.out.println("onClick");
        ddd();
    }
    EditText ed;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dat, container, false);
        t = 0;
        t1 = 0;
        init(view);
        ddd();

        Button btndatdraw = view.findViewById(R.id.btndatdraw);
        ed = view.findViewById(R.id.edatdraw);
        btndatdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                up(ed.getText().toString());
                ed.setText("");
            }
        });
        return view;
    }

    private void init(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.datrecycler);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new RecyclerAdapter(this);
        recyclerView.setAdapter(adapter);


    }

    boolean tf = true;

    public void settf(boolean tf) {
        this.tf = tf;
    }

    //댓글을 FB에서 읽어와서 리사이클뷰에 올리는 함수
    public void ddd() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adapter.clearItem();
//                for (int a = 0; a < 2; a++) {
//                    if (tf) {
                System.out.println("시작");
                t = (int) snapshot.child("댓글").child(title).getChildrenCount();
                System.out.println("댓글출력 t=" + t);
                if (t != 0) {
                    for (int i = 0; i < t; i++) {
                        int x = i + 1;
                        if (snapshot.child("댓글").child(title).child(x + "번").child("아이디").getValue() == null) {
                            System.out.println("x=" + x + ",t=" + t);
                        } else {
                            System.out.println("반복문");
                            String n = snapshot.child("댓글").child(title).child(x + "번").child("아이디").getValue().toString();
                            String d = snapshot.child("댓글").child(title).child(x + "번").child("날짜").getValue().toString();
                            String txt = snapshot.child("댓글").child(title).child(x + "번").child("내용").getValue().toString();
                            // 각 List의 값들을 data 객체에 set 해줍니다.
                            Data data = new Data();
                            data.setname(n);
                            data.setdate(d);
                            data.settxt(txt);
                            data.setnum(x);
                            System.out.println("x=" + x + "t=" + t);
                            // 각 값이 들어간 data를 adapter에 추가합니다.
                            adapter.addItem(data);
                        }
                    }

                    adapter.addmyData(myData);//로그인데이터 전송
                    adapter.addCandT(t, title);//댓글 총갯수,해당글이름전송;
                    tf = false;
                    adapter.settf(tf);
                    // adapter의 값이 변경되었다는 것을 알려줍니다.
                    adapter.notifyDataSetChanged();
//                            break;
//                        } else {
//                            tf = adapter.gettf();
//                        }
//                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //시간 획득 함수
    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    private String getTime() {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }

    // String txt="첫댓글";//댓글내용
    public void up(String txt) {
        System.out.println("댓글저장");
        String dat = "댓글";//댓글을 저장하는 테이블
        String glename = title;//댓글이달린 게시물이름
        String daytime = getTime();//댓글이 달린시간
        String gleid = myData.getUserId();//댓글작성자 아이디
        tf = true;
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                t1 = (int) dataSnapshot.child("댓글").child(glename).getChildrenCount();
                int num = t1 + 1;
                System.out.println("번호=" + num);
                mDatabase.child(dat).child(glename).child(num + "번").child("날짜").setValue(daytime);
                mDatabase.child(dat).child(glename).child(num + "번").child("내용").setValue(txt);
                mDatabase.child(dat).child(glename).child(num + "번").child("아이디").setValue(gleid);
                ddd();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}