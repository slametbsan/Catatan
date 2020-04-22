package id.ac.aknganjuk.catatan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class CatatanAdapter extends RecyclerView.Adapter<CatatanAdapter.CatatanViewHolder> {
    private List<Catatan> catatanList;

    static class CatatanViewHolder extends RecyclerView.ViewHolder {
        TextView tvCatatan;
        TextView tvTimestamp;

        CatatanViewHolder(@NonNull View view) {
            super(view);
            tvCatatan = view.findViewById(R.id.tvCatatan);
            tvTimestamp = view.findViewById(R.id.tvTimestamp);
        }
    }

    CatatanAdapter(Context context, List<Catatan> catatanList){
        this.catatanList = catatanList;
    }

    @NonNull
    @Override
    public CatatanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.catatan_baris, parent, false);
        return new CatatanViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CatatanViewHolder holder, int position) {
        Catatan catatan = catatanList.get(position);

        //tampilkan catatan
        holder.tvCatatan.setText(catatan.getCatatan());
        //tampilkan timestamp
        holder.tvTimestamp.setText(formatDate(catatan.getTimestamp()));
    }

    private String formatDate(String timestamp) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat fmtTanggal = new SimpleDateFormat("yyyy-MM-dd");
        String today = fmtTanggal.format(calendar.getTime());   //tanggal hari ini

        try {
            SimpleDateFormat fmtTanggalDb = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            fmtTanggalDb.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = fmtTanggalDb.parse(timestamp);
            TimeZone timeZone = TimeZone.getDefault();
            String tanggal = fmtTanggal.format(date);
            SimpleDateFormat fmtOut = null;

            if(tanggal.compareTo(today) == 0){
                fmtOut = new SimpleDateFormat("HH:mm");
            } else {
                fmtOut = new SimpleDateFormat("dd MMMM yyyy");
            }
            fmtOut.setTimeZone(timeZone);
            return fmtOut.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    @Override
    public int getItemCount() {
        return catatanList.size();
    }
}
