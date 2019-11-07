package com.example.doodling.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.doodling.R;

import java.util.List;

public class MyAdapter extends BaseAdapter {
    private Context context;
    private List<Drawing> drawings;
    private int defItem = -1;//声明默认选中的项

    public MyAdapter(Context context, List<Drawing> drawing){
        this.context=context;
        this.drawings=drawing;
    }
    @Override
    public int getCount() {
        return drawings.size();
    }

    @Override
    public Object getItem(int position) {
        return drawings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        Drawing drawing=drawings.get(i);
        if(convertView==null){
            viewHolder=new ViewHolder();
            convertView= LayoutInflater.from(context).inflate(R.layout.left_item,null);
            viewHolder.name=(TextView)convertView.findViewById(R.id.tv_name);
            viewHolder.time=(TextView)convertView.findViewById(R.id.tv_time);
            //viewHolder.delect=(ImageButton)convertView.findViewById(R.id.delect);
            convertView.setTag(viewHolder);
        }else {
            viewHolder=(ViewHolder)convertView.getTag();
        }
        if (defItem == i) {
            convertView.setBackgroundResource(R.color.item_select);
        } else {
            convertView.setBackgroundResource(android.R.color.transparent);
        }
        viewHolder.name.setText(drawing.getName());
        viewHolder.time.setText(drawing.getDate());
//        viewHolder.delect.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mOnItemDeleteListener.onDeleteClick(i);
//            }
//        });
//        viewHolder.name.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onItemNameListener.onNameClick(i);
//            }
//        });
        return convertView;
    }

//    public interface onItemDeleteListener {
//        void onDeleteClick(int i);
//    }
//
//    public interface onItemClickListener{
//        void onNameClick(int i);
//    }
//    private onItemDeleteListener mOnItemDeleteListener;
//    private onItemClickListener onItemNameListener;
//
//    public void setOnItemDeleteClickListener(onItemDeleteListener mOnItemDeleteListener) {
//        this.mOnItemDeleteListener = mOnItemDeleteListener;
//    }
//
//    public void setOnItemNameClickListener(onItemClickListener mOnItemNameListener){
//        this.onItemNameListener=mOnItemNameListener;
//    }
    class ViewHolder{
        TextView name;
        TextView time;
    }

    public void setDefSelect(int position) {
        this.defItem = position;
        notifyDataSetChanged();
    }
}
