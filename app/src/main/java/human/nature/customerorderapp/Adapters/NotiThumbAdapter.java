package human.nature.customerorderapp.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import human.nature.customerorderapp.AutoLayout;
import human.nature.customerorderapp.ListData.NotiThumbData;
import human.nature.customerorderapp.R;

public class NotiThumbAdapter extends RecyclerView.Adapter<NotiThumbAdapter.Holder> {
    ArrayList<NotiThumbData> datas = new ArrayList<>();
    View.OnClickListener onClickListener;

    public NotiThumbAdapter(ArrayList<NotiThumbData> datas, View.OnClickListener onClickListener) {
        this.datas = datas;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.noti_thumb_list_item, parent, false);
        AutoLayout.setView(v);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        NotiThumbData data = datas.get(position);
        holder.title.setText(data.getTitle());
        holder.add_time.setText(data.getAdd_time());
        holder.body.setOnClickListener(onClickListener);
        holder.body.setTag(data.getNo());
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView title;
        TextView add_time;
        LinearLayout body;
        public Holder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            add_time = itemView.findViewById(R.id.add_time);
            body = itemView.findViewById(R.id.body);
        }
    }
}
