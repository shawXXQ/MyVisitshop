package com.example.shaw.myvisitshop.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shaw.myvisitshop.R;
import com.example.shaw.myvisitshop.bean.TaskBody;

import java.util.List;

/**
 * Created by Shaw on 2017/7/24.
 */

public class TaskListBaseAdapter extends RecyclerView.Adapter
        <TaskListBaseAdapter.TaskViewHolder> {
    private Context mContext;
    private List<TaskBody> list;
    private int count;


    public TaskListBaseAdapter(Context mContext, List<TaskBody> list, int count) {
        this.mContext = mContext;
        this.list = list;
        this.count = count;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.fragment_home_task_item, null);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskListBaseAdapter.TaskViewHolder holder, int position) {
        TaskBody tb = list.get(position);
        holder.title.setText(tb.getTitle());
        if (position == count - 1) {
            holder.view.setVisibility(View.GONE);
        }
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "任务详情界面敬请期待", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list.size() > 0) {
            return count;
        } else {
            return 0;
        }
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {

        ImageView arrow;
        TextView title;
        RelativeLayout root;
        View view;

        public TaskViewHolder(View itemView) {
            super(itemView);
            arrow = (ImageView) itemView.findViewById(R.id.fragment_home_task_item_arrow);
            title = (TextView) itemView.findViewById(R.id.fragment_home_task_item_title);
            root = (RelativeLayout) itemView.findViewById(R.id.item_root_home);
            view = itemView.findViewById(R.id.item_home_task);
        }
    }
}
