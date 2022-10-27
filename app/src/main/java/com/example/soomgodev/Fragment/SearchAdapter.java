package com.example.soomgodev.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;

import com.example.soomgodev.R;

import java.util.List;

public class SearchAdapter extends BaseAdapter {

    private Context context;
    private List<String> list;
    private LayoutInflater inflate;
    private ViewHolder viewHolder;

    public SearchAdapter(List<String> list, Context context){
        this.list = list;
        this.context = context;
        this.inflate = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if(convertView == null){
            convertView = inflate.inflate(R.layout.row_listview,null);

            viewHolder = new ViewHolder();
            viewHolder.label = (TextView) convertView.findViewById(R.id.label);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        // 리스트에 있는 데이터를 리스트뷰 셀에 뿌린다.
        viewHolder.label.setText(list.get(position));
        viewHolder.label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("SearchAdapter", "SearchAdapter에서 아이템을 클릭했습니다");
                Intent intent = new Intent(context, UserMainActivity.class);
                //어디서 왔다는 걸 보내고,
                //무슨 서비스를 선택했는지 보내자.
                // 어디로? userMainActivity로 가서, Fragment 2로 연결
                Log.i("SearchAdapter", "클릭한 건? = " + list.get(position));
                intent.putExtra("service", list.get(position));
                intent.putExtra("moveToFragment2", true);
                context.startActivity(intent);

            }
        });

        return convertView;
    }

    class ViewHolder{
        public TextView label;
    }

}
