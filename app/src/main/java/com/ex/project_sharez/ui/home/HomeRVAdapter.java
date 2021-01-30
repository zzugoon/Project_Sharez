package com.ex.project_sharez.ui.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.misc.AsyncTask;
import com.bumptech.glide.Glide;
import com.ex.project_sharez.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class HomeRVAdapter extends RecyclerView.Adapter<HomeRVAdapter.ViewHolder> {

    private Context context;
    private ArrayList<HomeItemData> list = null ;
    private OnItemClickListener mListener = null; // 리스너 객체 참조를 저장하는 변수
    String TAG = "HomeRVAdapter";

    // ========== Constructure(데이터 리스트 객체를 전달받음) ==========
    HomeRVAdapter(Context context, ArrayList<HomeItemData> list) {
        this.context = context;
        this.list = list ;
    }

    // ========== 리스너 인터페이스 정의 ==========
    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    // ========== OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드 ==========
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }

    // ========== onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴. ==========
    @Override
    public HomeRVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.home_recycler_item, parent, false) ;
        HomeRVAdapter.ViewHolder vh = new HomeRVAdapter.ViewHolder(view) ;

        return vh;
    }

    // ========== onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시. ==========
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HomeItemData itemData = list.get(position);
        holder.tvTitle.setText(itemData.getTitle());
        holder.tvLocation.setText(itemData.getLocation());
        holder.tvPrice.setText(itemData.getPrice());
        holder.tvSubmit.setText("0");

        //Storage에서 이미지파일을 가져와서 imageView에 올린다.
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://project-sharez.appspot.com");

        StorageReference storageRef = storage.getReference();
        storageRef.child("images/"+itemData.getImagesPath()[0]).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    //이미지 로드 성공시
                    Glide.with(context.getApplicationContext()).load(uri).into(holder.imageView);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    //이미지 로드 실패시
                    Log.d(TAG, "onFailure: 이미지로드 실패");
                }
            });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.imageView.setClipToOutline(true);
        }
    }

    // ========== getItemCount() - 전체 데이터 갯수 리턴. ==========
    @Override
    public int getItemCount() {
        return list.size() ;
    }

    // ========== 아이템 뷰를 저장하는 뷰홀더 클래스 ==========
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvLocation;
        TextView tvPrice;
        TextView tvSubmit;
        ImageView imageView;

        String TAG = "ViewHolder";

        ViewHolder(View itemView) {
            super(itemView) ;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onItemClick(v, pos) ;
                        }
                    }
                }
            });

            // 뷰 객체에 대한 참조. (hold strong reference)
            tvTitle = itemView.findViewById(R.id.textviewTitle) ;
            tvPrice = itemView.findViewById(R.id.textviewPrice) ;
            tvLocation = itemView.findViewById(R.id.textviewLocation) ;
            tvSubmit = itemView.findViewById(R.id.textviewSubmit) ;
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}