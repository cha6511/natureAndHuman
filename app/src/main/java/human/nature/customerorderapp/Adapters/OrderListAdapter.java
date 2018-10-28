package human.nature.customerorderapp.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;

import human.nature.customerorderapp.AutoLayout;
import human.nature.customerorderapp.ListData.OrderData;
import human.nature.customerorderapp.R;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.Holder> {
    LinkedList<OrderData> datas = new LinkedList<>();
    View.OnClickListener onClickListener;
    Context context;

    public OrderListAdapter(Context context, LinkedList<OrderData> datas, View.OnClickListener onClickListener) {
        this.context = context;
        this.datas = datas;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_list_body, parent, false);
        AutoLayout.setView(v);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        OrderData data = datas.get(position);

        holder.order_number.setText("주문번호 : " + data.getNo());

        holder.total_price.setText(String.format("%,d", Integer.parseInt(data.getTotal_price())) + " 원");
        if("0".equals(data.getStatus())){
            holder.status.setText("주문완료");
            holder.status.setEnabled(false);
        } else if("1".equals(data.getStatus())){
            holder.status.setText("입금확인");
            holder.status.setEnabled(false);
        } else if("2".equals(data.getStatus())){
            holder.status.setText("수취확인");
            holder.status.setEnabled(true);
        } else if("3".equals(data.getStatus())){
            holder.status.setText("거래완료");
            holder.status.setEnabled(false);
        }
        holder.status.setOnClickListener(onClickListener);
        holder.status.setTag(data.getNo());

        if("Y".equals(data.getCancel_yn())){
            holder.cancel.setText("취소완료");
            holder.cancel.setEnabled(false);
        } else if("N".equals(data.getCancel_yn())){
            holder.cancel.setText("취소");
            holder.cancel.setEnabled(true);
        } else if("REQ".equals(data.getCancel_yn())){
            holder.cancel.setText("취소 요청중");
            holder.cancel.setEnabled(true);
        }
        holder.cancel.setOnClickListener(onClickListener);
        holder.cancel.setTag(position);
        holder.addView(position);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView order_number;
        TextView total_price;
        TextView status;
        TextView cancel;
        LinearLayout add_view;
        public Holder(View itemView) {
            super(itemView);
            order_number = itemView.findViewById(R.id.order_number);
            total_price = itemView.findViewById(R.id.total_price);
            status = itemView.findViewById(R.id.status);
            cancel = itemView.findViewById(R.id.cancel);
            add_view = itemView.findViewById(R.id.add_view);
        }

        public void addView(int pos){
            add_view.removeAllViews();
            String[] names = datas.get(pos).getProduct_names().split("/");
            String[] prices = datas.get(pos).getProduct_prices().split("/");
            String[] cnts = datas.get(pos).getCount().split("/");
            String[] options = datas.get(pos).getOptions().split("/");
            for(int i = 0 ; i < names.length ; i++){
                View v = LayoutInflater.from(context).inflate(R.layout.order_list_item, null);
                TextView product_name = v.findViewById(R.id.product_name);
                TextView product_amt = v.findViewById(R.id.product_amt);
                TextView product_price = v.findViewById(R.id.product_price);

                product_name.setText(names[i] + "\n(" + options[i] + ")");
                product_amt.setText(cnts[i] + " EA");
                product_price.setText(String.format("%,d", Integer.parseInt(prices[i])) + " 원");
                AutoLayout.setView(v);
                add_view.addView(v);
            }
        }
    }


}
