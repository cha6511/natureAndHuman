package human.nature.customerorderapp.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import human.nature.customerorderapp.AutoLayout;
import human.nature.customerorderapp.ListData.CartData;
import human.nature.customerorderapp.R;

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.Holder> {
    Context context;
    ArrayList<CartData> datas = new ArrayList<>();
    View.OnClickListener onClickListener;

    public CartListAdapter(Context context, ArrayList<CartData> datas, View.OnClickListener onClickListener) {
        this.context = context;
        this.datas = datas;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_list_item, parent, false);
        AutoLayout.setView(v);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        CartData data = datas.get(position);
        Glide.with(context).load(data.getImg_url()).apply(new RequestOptions().error(R.drawable.ic_launcher_foreground)).into(holder.img);
        holder.item_desc.setText(data.getItem_name() + "\n" + data.getItem_option());
        holder.amt.setText(String.format("%,d", Integer.parseInt(data.getItem_amt())));
        holder.price.setText(String.format("%,d", Integer.parseInt(data.getItem_price()) * Integer.parseInt(data.getItem_amt())));
        holder.delete.setTag(position);
        holder.delete.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView item_desc;
        TextView amt;
        TextView price;
        Button delete;
        public Holder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            item_desc = itemView.findViewById(R.id.name);
            amt = itemView.findViewById(R.id.amt);
            price = itemView.findViewById(R.id.price);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}
