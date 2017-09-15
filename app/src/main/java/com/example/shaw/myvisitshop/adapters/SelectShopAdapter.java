package com.example.shaw.myvisitshop.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.shaw.myvisitshop.Activity.CreateVisitShopActivity;
import com.example.shaw.myvisitshop.R;
import com.example.shaw.myvisitshop.bean.SelectShop;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shaw on 2017/7/29.
 */

public class SelectShopAdapter extends
        RecyclerView.Adapter<SelectShopAdapter.SelectViewHolder> {
    private Context mContext;
    private List<SelectShop.ShopLists> list;
    private String mSearchText;

    public SelectShopAdapter(Context mContext, List<SelectShop.ShopLists> list, String mSearchText) {
        this.mContext = mContext;
        this.list = list;
        this.mSearchText = mSearchText;
        if (list == null) {
            list = new ArrayList<>();
        }
    }

    @Override
    public SelectShopAdapter.SelectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=View.inflate(mContext,R.layout.select_shop_item,null);
        return new SelectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SelectShopAdapter.SelectViewHolder holder, final int position) {
        SelectShop.ShopLists s1=list.get(position);
        //设置关键搜索字为红色
        int changeTextColor;
        ForegroundColorSpan redSpan=new ForegroundColorSpan(mContext.getResources().getColor(R.color.red));
        SpannableStringBuilder builder=new SpannableStringBuilder(s1.getName());
        changeTextColor=s1.getName().indexOf(mSearchText);
        if (changeTextColor==-1){
            builder.setSpan(redSpan,changeTextColor,changeTextColor+mSearchText.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.name.setText(builder);
        }else {
            holder.name.setText(builder);
        }
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectShop.ShopLists s1=list.get(position);
                ((CreateVisitShopActivity) mContext).selectShop(s1);
            }
        });
        holder.id.setText("店面编号："+s1.getId());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    class SelectViewHolder extends RecyclerView.ViewHolder{
        TextView name,id;
        RelativeLayout root;

        public SelectViewHolder(View itemView) {
            super(itemView);
            name= (TextView) itemView.findViewById(R.id.select_shop_item_name);
            id= (TextView) itemView.findViewById(R.id.select_shop_item_id);
            root= (RelativeLayout) itemView.findViewById(R.id.item_root_select);
        }
    }
}
