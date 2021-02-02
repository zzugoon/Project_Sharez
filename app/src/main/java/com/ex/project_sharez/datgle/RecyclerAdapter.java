package com.ex.project_sharez.datgle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ex.project_sharez.R;
import com.ex.project_sharez.ui.my.MyData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {

    private DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReferenceFromUrl("https://project-sharez-default-rtdb.firebaseio.com/");
    // adapter에 들어갈 list 입니다.
    private ArrayList<Data> listData = new ArrayList<>();
    MyData myData;
    int datcount;
    String title;
    boolean tf=false;
  
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.newdat, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return listData.size();
    }

    void addmyData(MyData myData){
        this.myData=myData;
    }

    void addCandT(int datcount,String title){
        this.datcount=datcount;
        this.title=title;
    }

    void addItem(Data data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }

    boolean gettf(){
        return tf;
    }
    void settf(boolean tf){
        this.tf=tf;
    }
  
    void clearItem(){
        listData.clear();
    }
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView textView1;
        private TextView textView2;
        private TextView textView3;
        private Button btndel;

        ItemViewHolder(View itemView) {
            super(itemView);

            textView1 = itemView.findViewById(R.id.textView1);//작성자
            textView2 = itemView.findViewById(R.id.textView2);//작성일
            textView3 = itemView.findViewById(R.id.textView3);//내용
            btndel=itemView.findViewById(R.id.datdelete);//댓글삭제
        }

        void onBind(Data data) {
            textView1.setText("작성자:"+data.getname());
            textView2.setText("작성일:"+data.getdate());
            textView3.setText(data.gettxt());          

            int num=data.getnum();
            if(data.getname().equals(myData.getUserId())){
                btndel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (int i=num;i<=datcount;i++){
                                    if (i!=datcount) {
                                        int x = i + 1;
                                        String n = snapshot.child("댓글").child(title).child(x + "번").child("아이디").getValue().toString();
                                        String d = snapshot.child("댓글").child(title).child(x + "번").child("날짜").getValue().toString();
                                        String txt = snapshot.child("댓글").child(title).child(x + "번").child("내용").getValue().toString();
                                        mDatabase.child("댓글").child(title).child(i + "번").child("아이디").setValue(n);
                                        mDatabase.child("댓글").child(title).child(i + "번").child("날짜").setValue(d);
                                        mDatabase.child("댓글").child(title).child(i + "번").child("내용").setValue(txt);
                                    }
                                    else{
                                        mDatabase.child("댓글").child(title).child(i + "번").removeValue();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        tf=true;
                        System.out.println("삭제");
                    }
                });
            }
            else {
                btndel.setEnabled(false);
                btndel.setVisibility(View.INVISIBLE);
            }
        }
    }
}
