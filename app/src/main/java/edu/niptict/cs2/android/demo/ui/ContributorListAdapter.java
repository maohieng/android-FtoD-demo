package edu.niptict.cs2.android.demo.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.niptict.cs2.android.demo.R;
import edu.niptict.cs2.android.demo.model.Contributor;

public class ContributorListAdapter extends RecyclerView.Adapter<ContributorListAdapter.ContributorViewHolder> {

    private final LayoutInflater mInflator;

    @Nullable
    private List<? extends Contributor> data;

    public ContributorListAdapter(Context context) {
        this.mInflator = LayoutInflater.from(context);
    }

    public final void notifyDataSetChanged(List<? extends Contributor> newData) {
        if (this.data != null) {
            this.data.clear(); // for garbage collector
        }

        this.data = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContributorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflator.inflate(R.layout.list_item_contributor, parent, false);
        return new ContributorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContributorViewHolder holder, int position) {
        Contributor item = data.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Internal classes
    ///////////////////////////////////////////////////////////////////////////

    class ContributorViewHolder extends RecyclerView.ViewHolder {

        TextView textContributor;

        public ContributorViewHolder(@NonNull View itemView) {
            super(itemView);
            textContributor = itemView.findViewById(R.id.textContributor);
        }

        public void bind(Contributor item) {
            textContributor.setText(item.getLogin());
        }
    }
}