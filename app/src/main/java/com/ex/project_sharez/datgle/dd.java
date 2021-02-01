package com.ex.project_sharez.datgle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ex.project_sharez.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

//댓글쓰기
public class dd extends Fragment {
    private DatabaseReference mDatabase;
    private String name;
    private String id;
    private int t;
    public dd(){

    }
    public dd(String name, String id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://project-sharez-default-rtdb.firebaseio.com/");
    }
    EditText ed;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_dd, container, false);
        Button btndatdraw=view.findViewById(R.id.btndatdraw);
        t=0;
        System.out.println("name="+name+"id="+id);
        ed=view.findViewById(R.id.edatdraw);
        btndatdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                up(ed.getText().toString());
                ed.setText("");
            }
        });
        return view;
    }
    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private String getTime(){
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }
    //        String txt="첫댓글";//댓글내용
    public void up(String txt){
        String dat="댓글";//댓글을 저장하는 테이블
        String glename=name;//댓글이달린 게시물이름
//        String glename="귤1KG";//댓글이달린 게시물이름
        String daytime=getTime();//댓글이 달린시간
        String gleid=id;//댓글작성자 아이디
//        String gleid="ggg";//댓글작성자 아이디

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                t= (int) dataSnapshot.child("댓글").child(glename).getChildrenCount();
                int num=t+1;
                System.out.println("번호="+num);
                mDatabase.child(dat).child(glename).child(num+"번").child("날짜").setValue(daytime);
                mDatabase.child(dat).child(glename).child(num+"번").child("내용").setValue(txt);
                mDatabase.child(dat).child(glename).child(num+"번").child("아이디").setValue(gleid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}