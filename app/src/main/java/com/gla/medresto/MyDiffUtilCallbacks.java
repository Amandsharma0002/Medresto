package com.gla.medresto;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.util.ArrayList;

public class MyDiffUtilCallbacks extends DiffUtil.Callback {
    ArrayList<Model> oldMeds = new ArrayList<>();
    ArrayList<Model> newMeds = new ArrayList<>();

    public MyDiffUtilCallbacks(ArrayList<Model> oldMeds, ArrayList<Model> newMeds) {
        this.oldMeds = oldMeds;
        this.newMeds = newMeds;
    }

    @Override
    public int getOldListSize() {
        return oldMeds != null ? oldMeds.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return newMeds != null ? newMeds.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return true;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        int result = newMeds.get(newItemPosition).compareTo(oldMeds.get(oldItemPosition));
        if (result == 0) {
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        Model newMed = newMeds.get(newItemPosition);
        Model oldMed = oldMeds.get(oldItemPosition);

        Bundle bundle = new Bundle();
        if (!newMed.getTitle().equals(oldMed.getTitle())) {
            bundle.putString("title", newMed.getTitle());
        }
        if (!newMed.getDate().equals(oldMed.getDate())) {
            bundle.putString("date", newMed.getDate());
        }
        if (!newMed.getTime().equals(oldMed.getTime())) {
            bundle.putString("time", newMed.getTime());
        }

        if (bundle.size() == 0) {
            return null;
        }

        return bundle;
    }
}
