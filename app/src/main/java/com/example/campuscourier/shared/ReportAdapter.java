package com.example.campuscourier.shared;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campuscourier.R;
import com.example.campuscourier.shared.Requests_2;

import java.util.ArrayList;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {
    private ArrayList<Requests_2> reportItems;
    private Context context;

    public ReportAdapter(ArrayList<Requests_2> reportItems, Context context) {
        this.reportItems = reportItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_card_report, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReportAdapter.ViewHolder holder, int position) {
        Requests_2 reportItem = reportItems.get(position);
        holder.ReportId.setText(reportItem.getReportId());
        holder.Description.setText(reportItem.getDescription());
        holder.ReasonDetail.setText(reportItem.getSelectedDescription().toString());
        holder.OtherReason.setText(reportItem.getOtherReason());
        holder.Status.setText(reportItem.getStatus());

        if (reportItem.getSelectedDescription().contains("Others")) {
            holder.OtherReason.setVisibility(View.VISIBLE);
            holder.OtherReason.setText(reportItem.getOtherReason());
            holder.Other_Description.setVisibility(View.VISIBLE);
        } else {
            holder.OtherReason.setVisibility(View.GONE);
            holder.Other_Description.setVisibility(View.GONE);
        }
    }





    @Override
    public int getItemCount() {
        return reportItems.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView ReportId, Description,ReasonDetail,OtherReason,Status,Other_Description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ReportId = itemView.findViewById(R.id.telegram_handle);
            Description = itemView.findViewById(R.id.Reason_detail);
            ReasonDetail = itemView.findViewById(R.id.Reason_report);
            OtherReason = itemView.findViewById(R.id.Other_detail);
            Status = itemView.findViewById(R.id.status_detail);
            Other_Description = itemView.findViewById(R.id.Other_Description);
        }
    }
}
