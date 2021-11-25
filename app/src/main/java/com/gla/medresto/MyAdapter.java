package com.gla.medresto;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.myviewholder> {
    ArrayList<Model> dataHolder = new ArrayList<Model>();                                               //array list to hold the reminders
    Context context;
    RecyclerView mRecyclerView;

    public MyAdapter(Context context, ArrayList<Model> dataHolder, RecyclerView mRecyclerView) {
        this.dataHolder = dataHolder;
        this.context = context;
        this.mRecyclerView = mRecyclerView;
    }


    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_reminder_file, parent, false);  //inflates the xml file in recyclerview
        return new myviewholder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        holder.mTitle.setText(dataHolder.get(position).getTitle());                                 //Binds the single reminder objects to recycler view
        holder.mDate.setText(dataHolder.get(position).getDate());
        holder.mTime.setText(dataHolder.get(position).getTime());
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            Bundle bundle = (Bundle) payloads.get(0);
            for (String key : bundle.keySet()) {
                if (key.equals("title")) {
                    holder.mTitle.setText(bundle.getString("title"));
                }
                if (key.equals("date")) {
                    holder.mDate.setText(bundle.getString("date"));
                }
                if (key.equals("time")) {
                    holder.mTime.setText(bundle.getString("time"));
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataHolder.size();
    }

    public void updateModels(ArrayList<Model> newModels) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MyDiffUtilCallbacks(dataHolder, newModels));
        diffResult.dispatchUpdatesTo(this);
        dataHolder.clear();
        dataHolder.addAll(newModels);
    }

    public class myviewholder extends RecyclerView.ViewHolder {

        TextView mTitle, mDate, mTime;
        ImageButton delTask;

        public myviewholder(@NonNull View itemView) {
            super(itemView);

            mTitle = (TextView) itemView.findViewById(R.id.txtTitle);                               //holds the reference of the materials to show data in recyclerview
            mDate = (TextView) itemView.findViewById(R.id.txtDate);
            mTime = (TextView) itemView.findViewById(R.id.txtTime);
            delTask = (ImageButton) itemView.findViewById(R.id.deleteTask);

            delTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new DatabaseManager(context).deleteTask(mTitle.getText().toString(), mDate.getText().toString(), mTime.getText().toString());
                    Cursor cursor = new DatabaseManager(context).readAllReminders();                  //Cursor To Load data From the database
                    ArrayList<Model> newModel = new ArrayList<>();
                    if (cursor != null) {
                        while (cursor.moveToNext()) {
                            Model model = new Model(cursor.getString(1), cursor.getString(2), cursor.getString(3));
                            newModel.add(model);
                        }
                    }
                    updateModels(newModel);
                }
            });
        }
    }
}
