package com.example.jooff.shuyi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jooff.shuyi.R;
import com.example.jooff.shuyi.ui.RecHistoryItem;

import java.util.ArrayList;

/**
 * Created by jooff on 16/7/30.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.historyViewHolder> {
    private Context context;
    private ArrayList<RecHistoryItem> lists;

    public HistoryAdapter(Context context,ArrayList<RecHistoryItem> lists){
        this.context = context;
        this.lists = lists;

    }
    @Override
    public historyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new historyViewHolder(LayoutInflater.from(context).inflate(R.layout.rec_item_history,parent,false));
    }

    @Override
    public void onBindViewHolder(historyViewHolder holder, int position) {
        RecHistoryItem item = lists.get(position);
        holder.textOriginal.setText(item.getTextOriginal());
        holder.textResult.setText(item.getTextResult());
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    class historyViewHolder extends RecyclerView.ViewHolder {
        private TextView textOriginal;
        private TextView textResult;

        public historyViewHolder(View itemView) {
            super(itemView);
            textOriginal = (TextView) itemView.findViewById(R.id.item_original);
            textResult = (TextView) itemView.findViewById(R.id.item_result);

        }
    }
}
