package human.nature.customerorderapp.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import human.nature.customerorderapp.AutoLayout;
import human.nature.customerorderapp.ListData.StoreListData;
import human.nature.customerorderapp.R;

public class StoreListAdapter extends RecyclerView.Adapter<StoreListAdapter.Holder> {

    ArrayList<StoreListData> datas = new ArrayList<>();
    View.OnClickListener onClickListener;

    public StoreListAdapter(ArrayList<StoreListData> datas, View.OnClickListener onClickListener) {
        this.datas = datas;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_list_item, parent, false);
        AutoLayout.setView(v);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        StoreListData data = datas.get(position);
        holder.store_name.setText(data.getStore_name());
        if(TextUtils.isEmpty(data.getAcpt_yn()) || "null".equals(data.getAcpt_yn())){
            holder.blocked.setVisibility(View.VISIBLE);
            holder.request.setText("승인요청");
            holder.request.setEnabled(true);
        } else if("N".equals(data.getAcpt_yn())){
            holder.blocked.setVisibility(View.VISIBLE);
            holder.request.setText("승인요청중");
            holder.request.setEnabled(false);
        } else if("Y".equals(data.getAcpt_yn())){
            holder.blocked.setVisibility(View.GONE);
            holder.request.setText("이동");
            holder.request.setEnabled(true);
        }
        holder.request.setTag(data);
        holder.request.setOnClickListener(onClickListener);


    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView store_name;
        private Button request;
        private View blocked;

        public Holder(View itemView) {
            super(itemView);
            store_name = itemView.findViewById(R.id.store_name);
            request = itemView.findViewById(R.id.request);
            blocked = itemView.findViewById(R.id.blocked);
        }
    }
}
