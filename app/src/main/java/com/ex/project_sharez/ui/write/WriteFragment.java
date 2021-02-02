package com.ex.project_sharez.ui.write;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ex.project_sharez.BuildConfig;
import com.ex.project_sharez.MainActivity;
import com.ex.project_sharez.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;
import com.ex.project_sharez.ui.home.HomeFragment;
import com.ex.project_sharez.ui.my.MyData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.INPUT_METHOD_SERVICE;

public class WriteFragment extends Fragment implements View.OnClickListener {

    String TAG = "WriteFragment";

    private StorageReference mStorageRef;

    TextView tvImageCount; // DB 전송, listener 설정
    EditText etTitle; // DB 전송
    TextView tvCategory; // DB 전송, listener 설정
    EditText etPrice;// DB 전송
    EditText etSubstance; // DB 전송
    TextView etLocation; // DB 전송
    ImageButton ibComment; // listener 설정
    ImageButton ibCancel; // listener 설정

    ArrayList<String> imagePath;
    ArrayList<String> abs_imgPath;
    ArrayList<Uri> uriList;

    MyData myData; // 로그인한 사용자 정보를 담은 객체

    private InputMethodManager imm;

    // TextWatcher에 사용할 변수 설정
    private DecimalFormat decimalFormat = new DecimalFormat("#,###");
    private String result="";

    // recycler 관련 변수 설정
    RecyclerView recyclerView;
    ImageRVAdapter imageAdapter;
    Fragment fragment;

    public WriteFragment(MyData myData) {
        this.myData = myData;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity)getActivity()).navView.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MainActivity)getActivity()).navView.setVisibility(View.VISIBLE);
    }


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bot_write, container, false);

        tvImageCount = view.findViewById(R.id.write_tv_ImageCount);
        etTitle = view.findViewById(R.id.write_et_Title);
        tvCategory = view.findViewById(R.id.write_tv_Category);
        etPrice = view.findViewById(R.id.write_et_Price);
        etSubstance = view.findViewById(R.id.write_et_Substance);
        etLocation = view.findViewById(R.id.textView_SearchLoca);
        etLocation.setText(myData.getUserAddress());
        ibComment = view.findViewById(R.id.write_iB_Comment);
        ibCancel = view.findViewById(R.id.write_iv_imageCancel);

        tvImageCount.setOnClickListener(this);
        tvCategory.setOnClickListener(this);
        ibComment.setOnClickListener(this);

        imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        imagePath = new ArrayList<>(); // 이미지패스를 저장할 변수
        abs_imgPath = new ArrayList<>(); // abs 이미지패스를 저장할 변수
        uriList= new ArrayList<>(); // DB에 이미지명을 저장할 변수

        setHasOptionsMenu(true); // Fragment별로 툴바 구성을 다르게 할때 설정

        // ========== etPrice의 원 단위 형식 설정 ==========
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isEmpty(charSequence.toString()) && !charSequence.toString().equals(result)){
                    result = decimalFormat.format(Double.parseDouble(charSequence.toString().replaceAll(",","")));
                    etPrice.setText(result);
                    etPrice.setSelection(result.length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        };
        etPrice.addTextChangedListener(watcher);

        // ==================== recyclerView 설정 ====================
        // RecyclerView에 LinearLayoutManager 객체 지정.
        recyclerView = view.findViewById(R.id.write_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // RecyclerView의 레이아웃 방식을 지정
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);

        // RecyclerView에 SimpleTextAdapter 객체 지정.
        ArrayList<Uri> tempList = new ArrayList<>();
        imageAdapter = new ImageRVAdapter(tempList);        
        recyclerView.setAdapter(imageAdapter);

        // imageFile 삭제 시 RecyclerView.tvImageCount text 변경
        imageAdapter.setOnItemClickListener(new ImageRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.d(TAG, "onClick : setTextPhotoUpload");
                setTextPhotoUpload();
                //abs_imgPath.remove(position);
                uriList.remove(position);
                imagePath.remove(position);
            }
        }) ;
        return view;
    } //onCreateView

    @Override
    public void onStop() {
        super.onStop();
        ((MainActivity)getActivity()).navView.setVisibility(View.VISIBLE);
        ((MainActivity)getActivity()).setBotNavHome();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        Log.d("change", "write Menu inflate");
        getActivity().getMenuInflater().inflate(R.menu.bot_menu_write, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_my:
                Log.d("Click", "Click write menu complete");
                if(etSubstance.getText().toString().length()>5) {
                    ConnectThread th = new ConnectThread(imagePath);
                    th.start();
                    setWrite();
                    for(int i=0; i<uriList.size(); i++) {
                        uploadFile(uriList.get(i), imagePath.get(i));
                    }
                } else {
                    minWrite();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // ==================== fragment 내부 View의 Listener 설정  ====================
    @Override
    public void onClick(View view) {
        Log.d("Click", "Click write onClick");
        switch (view.getId()) {
            case R.id.write_tv_ImageCount:
                Log.d("Click", "Click write onClick // photoupload");
                getAlbumImage();
                break;
            case R.id.write_iB_Comment:
                Log.d("Click", "Click write onClick // comment");
                break;
            case R.id.write_tv_Category:
                Log.d("Click", "Click write onClick // category");
                getCategoryList();
                break;
        }
    }

    // ========== 글 작성 완료 method ==========
    public void setWrite() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("작성 완료");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int pos) {
                Log.d("click", "home frament 불러오기");

                imm.hideSoftInputFromWindow(getView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                //etSubstance.getWindowToken()

                fragment = new HomeFragment(myData);
                //((MainActivity)getActivity()).replaceFragment(fragment);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

                transaction.replace(R.id.nav_host_fragment, fragment).commit();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // ========== 글 내용이 너무 짧을 시 알림 method ==========
    public void minWrite() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("글이 너무 짧아요, 조금 더 길게 작성해주세요");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int pos) {
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // ========== 카테고리 alertDialog 불러오기 ==========
    public void getCategoryList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setItems(R.array.Category, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int pos)
            {
                String[] items = getResources().getStringArray(R.array.Category);
                tvCategory.setText(items[pos]);
                Toast.makeText(getActivity(),items[pos],Toast.LENGTH_LONG).show();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // ========== gallery 에서 imageFile 불러오기 ==========
    static final int REQUEST_CODE = 0;
    static final int GET_GALLERY_IMAGE = 100;
    public void getAlbumImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        startActivityForResult(intent, GET_GALLERY_IMAGE);
    }

    // ========== 불러온 imageFile 설정 ==========
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult", "requestCode : " + requestCode + "resultCode : " + resultCode);
        if (requestCode == GET_GALLERY_IMAGE &&
                resultCode == RESULT_OK && data != null && data.getData() != null) {            

            // 리사이클러뷰에 표시할 데이터 리스트 생성.
            for (int i=0; i<data.getClipData().getItemCount(); i++) {
                /*
                갤러리앱에서 관리하는 DB정보가 나온다 [실제 파일 경로가 아님!!]
                얻어온 Uri는 Gallery앱의 DB번호임. (content://-----/2854)
                업로드를 하려면 이미지의 절대경로(실제 경로: file:// -------/aaa.png 이런식)가 필요함
                Uri -->절대경로(String)로 변환 (절대경로를 가져오는 메소드 작성)
                */
                Uri tempUri = data.getClipData().getItemAt(i).getUri();
                uriList.add(tempUri);

                // 이미지 절대경로 가져오기
                //abs_imgPath.add(getRealPathFromUri(tempUri));                

                // 이미지 파일명만 가져오기
                String[] tempString = getRealPathFromUri(tempUri).toString().split("/");
                // Unique한 파일명을 만들자.
                SimpleDateFormat formatter = new SimpleDateFormat("yyMMHHmmss");
                Date now = new Date();
                String filename = myData.getUserId() + "_" + formatter.format(now) + "_" + tempString[tempString.length-1];
                imagePath.add(filename);

                //tempUri = cropLeftRight_Rate(getContext(), tempUri, 50, 50);
                imageAdapter.addItem(tempUri);
            }
            imageAdapter.notifyDataSetChanged();

            setTextPhotoUpload();
        }
    }

    public void setTextPhotoUpload() {
        tvImageCount.setText(""+ imageAdapter.getNum() + "/5");
    }

    // ========== Uri -> 절대경로로 바꿔서 리턴시켜주는 메소드 ==========
    String getRealPathFromUri(Uri uri) {
        String[] proj= {MediaStore.Images.Media.DATA};
        CursorLoader loader= new CursorLoader(getActivity(), uri, proj, null, null, null);
        Cursor cursor= loader.loadInBackground();
        int column_index= cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result= cursor.getString(column_index);
        cursor.close();
        return  result;
    }

    //upload the file
    private void uploadFile(Uri uri, String imagePath) {
        Uri filePath = uri;
        //업로드할 파일이 있으면 수행
        if (filePath != null) {
            //업로드 진행 Dialog 보이기
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("업로드중...");
            progressDialog.show();

            //storage 참조
            FirebaseStorage storage = FirebaseStorage.getInstance();

            //storage 주소와 폴더 파일명을 지정.
            StorageReference storageRef = storage.getReferenceFromUrl("gs://project-sharez.appspot.com").child("images/" + imagePath);
            //올라가거라...
            storageRef.putFile(filePath)
                    //성공시
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss(); //업로드 진행 Dialog 상자 닫기
                            Toast.makeText(getActivity().getApplicationContext(), "업로드 완료!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //실패시
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity().getApplicationContext(), "업로드 실패!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //진행중
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests") //이걸 넣어 줘야 아랫줄에 에러가 사라진다. 넌 누구냐?
                                    double progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();
                            //dialog에 진행률을 퍼센트로 출력해 준다
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                        }
                    });
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
        }
    }

    // ==================== server 연결을 위한 thread ====================
    class ConnectThread extends Thread {
        ArrayList<String> imagePath;
        String TAG = "WriteFragment.ConnectThread";
        String ipAddress = "http://hereo.dothome.co.kr";
        private String imgPath;

        ConnectThread(ArrayList<String> imagePath) {
            this.imagePath = imagePath;
        }
        @Override
        public void run() {
            String title = etTitle.getText().toString();
            String category = tvCategory.getText().toString();
            String price = etPrice.getText().toString();
            String substance = etSubstance.getText().toString();
            /*String location = getActivity().getIntent().getSerializableExtra("userJuso").toString();
            String writeUser = getActivity().getIntent().getSerializableExtra("userID").toString();*/
            //etLocation.getText().toString();

            String location = myData.getUserAddress();
            String writeUser = myData.getUserName();

            // ========== 쿼리문 작성 ==========
            String inputStr = "INSERT INTO ITEMLIST(title, category, price, substance, writeuser, location";
            for(int i=1; i<=imagePath.size(); i++) {
                inputStr = inputStr + ", imagepath0" + i;
            }
            inputStr = inputStr + ") values('" + title + "', '" + category + "', '" + price + "', '"
                    + substance + "', '" + writeUser + "', '" + location;

            String ips = "";
            for(int i=0; i<imagePath.size(); i++) {
                ips = ips + "', '" + imagePath.get(i);
            }
            inputStr = inputStr + ips + "');";
            System.out.println(inputStr);

            // ========== connect ==========
            // data 업로드
            String query = inputStr;
            String serverURL = "" + ipAddress + "/data_insert.php";
            String postData = "Data1="+query;

            try {
                URL url = new URL(serverURL);
                HttpURLConnection connData = (HttpURLConnection)url.openConnection();
                connData.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connData.setRequestMethod("POST");
                connData.setConnectTimeout(5000);
                connData.setDoOutput(true);
                connData.setDoInput(true);
                //conn.connect();

                OutputStream outputStream = connData.getOutputStream();
                outputStream.write(postData.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                StringBuilder jsonHtml = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connData.getInputStream()));
                String line = null;

                int i = 1;
                while((line = reader.readLine()) != null) {
                    jsonHtml.append(line);
                    i++;
                }

                reader.close();
                String result = jsonHtml.toString();

                connData.disconnect();
                Log.d(TAG, "run result : " + result);
            }
            catch (Exception e) {
                Log.i("PHPRequest", "request was failed.");
            }
        } // run
    } // thread class
} // class