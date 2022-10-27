package ExpertUpdate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.soomgodev.R;

import java.util.ArrayList;

import ConnectToServer.DataClass;
import ConnectToServer.NetworkClient;
import ConnectToServer.UploadApis;
import retrofit2.Call;
import retrofit2.Callback;

public class MultiImageAdapter extends RecyclerView.Adapter<MultiImageAdapter.ViewHolder>{

    // 레트로핏 통신 공통
    NetworkClient networkClient;
    UploadApis uploadApis;

    private ArrayList<Uri> mData = null ;
    private Context mContext = null ;

    public Uri getItemUri(int position) {
        return mData.get(position);
    }

    public void setItem(int position, Uri uri){
        mData.set(position,uri);
    }

    public void removeItem(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public void addItem(Uri uri){
        mData.add(uri);
    }

    // 생성자에서 데이터 리스트 객체, Context를 전달받음.
    public MultiImageAdapter(ArrayList<Uri> list, Context context) {
        mData = list ;
        mContext = context;
    }


    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_photo;
//        ImageView iv_remove;

        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조.
            iv_photo = itemView.findViewById(R.id.iv_photo);
//            iv_remove = itemView.findViewById(R.id.iv_remove);
        }
    }


                // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
                // LayoutInflater - XML에 정의된 Resource(자원) 들을 View의 형태로 반환.
        @Override
        public MultiImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            Context context = parent.getContext() ;
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;    // context에서 LayoutInflater 객체를 얻는다.
            view = inflater.inflate(R.layout.photo_item, parent, false) ;	// 리사이클러뷰에 들어갈 아이템뷰의 레이아웃을 inflate.
            MultiImageAdapter.ViewHolder vh = new MultiImageAdapter.ViewHolder(view) ;
            return vh ;
        }

        // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
        @Override
        public void onBindViewHolder(MultiImageAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

            Uri image_uri = mData.get(position) ;
            Glide.with(mContext)
                    .load(image_uri)
                    .into(holder.iv_photo);

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mData.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mData.size());
                return false;
            }
        });
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size() ;
    }

}