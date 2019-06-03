package com.example.myapplication;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class TestRecyclerAdapter extends RecyclerView.Adapter<TestRecyclerAdapter.TestHolder> {

    private List<String> testList;

    public TestRecyclerAdapter() {

    }

    public void setTestList(List<String> testList) {
        this.testList = testList;
    }

    @NonNull
    @Override
    public TestHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new TestHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_test_recycle, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TestHolder testHolder, int i) {
        String str = i + testList.get(i) + getItemCount();
        Log.e("dongxl", "onBindViewHolder==" + str);
        testHolder.textView.setText(str);
    }

    @Override
    public int getItemCount() {
        return null == testList ? 0 : testList.size();
    }

    class TestHolder extends RecyclerView.ViewHolder {
        View itemView;
        TextView textView;

        public TestHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            textView = itemView.findViewById(R.id.item_text);
        }
    }

}
