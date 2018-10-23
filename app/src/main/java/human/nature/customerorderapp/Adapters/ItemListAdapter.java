package human.nature.customerorderapp.Adapters;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import human.nature.customerorderapp.AutoLayout;
import human.nature.customerorderapp.ListData.ItemListData;
import human.nature.customerorderapp.R;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.Holder> {

    private Context context;
    private ArrayList<ItemListData> datas = new ArrayList<>();
    private View.OnClickListener onClickListener;
    private TextView.OnEditorActionListener onEditorActionListener;
    AdapterView.OnItemSelectedListener onItemSelectedListener;



    Handler handler;
    public ItemListAdapter(Context context, ArrayList<ItemListData> datas, View.OnClickListener onClickListener, AdapterView.OnItemSelectedListener onItemSelectedListener, TextView.OnEditorActionListener onEditorActionListener) {
        this.context = context;
        this.datas = datas;
        this.onClickListener = onClickListener;
        this.onItemSelectedListener = onItemSelectedListener;
        this.onEditorActionListener = onEditorActionListener;
        handler = new Handler();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from((Context)parent.getContext()).inflate(R.layout.item_list_item, parent, false);
        AutoLayout.setView(v);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, final int position) {
        final ItemListData data = datas.get(position);
        ArrayList<String> options = new ArrayList<>();
        for(int i = 0 ; i < data.getOptions().size() ; i++){
            options.add(data.getOptions().get(i).getOption_name() + "  " + String.format("%,d", Integer.parseInt(data.getOptions().get(i).getOption_price())) + "  재고 : " + data.getOptions().get(i).getO_cnt());
        }
        ArrayAdapter optionsAdapter = new ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, options);

        Glide.with(context).load(data.getImg_url()).apply(new RequestOptions().error(R.drawable.ic_launcher_foreground)).into(holder.item_img);
        holder.item_name.setText(data.getName());
//        data.setPrice( String.valueOf(Integer.parseInt(data.getOriginPrice()) + Integer.parseInt(data.getOptions().get(0).getOption_price())) );
        holder.item_price.setText("판매가격 : " + String.format("%,d", Integer.parseInt(data.getOriginPrice()) )  );


        if(data.isNeedRefreshAdapter()) {
            holder.item_option.setAdapter(optionsAdapter);
        }
        holder.item_option.setTag(position);
        holder.item_option.setOnItemSelectedListener(onItemSelectedListener);

        holder.increase.setOnClickListener(onClickListener);
        holder.increase.setTag(position);
        holder.decrease.setOnClickListener(onClickListener);
        holder.decrease.setTag(position);
        holder.amt.setText(data.getAmt());
        holder.amt.setTag(position);
        holder.amt.setOnEditorActionListener(onEditorActionListener);

        try {
            holder.total_price.setText(String.format("%,d", Integer.parseInt(data.getPrice()) * Integer.parseInt(data.getAmt())));
        } catch (Exception e){
            holder.total_price.setText(String.format("%,d", Integer.parseInt(data.getPrice()) * 0));
        }

        holder.order.setOnClickListener(onClickListener);
        holder.order.setTag(position);

        holder.add_cart.setOnClickListener(onClickListener);
        holder.add_cart.setTag(position);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        ImageView item_img;
        TextView item_name;
        TextView item_price;
        Spinner item_option;

        TextView increase;
        EditText amt;
        TextView decrease;

        TextView total_price;

        Button order;
        Button add_cart;

        public Holder(View itemView) {
            super(itemView);
            item_img = itemView.findViewById(R.id.item_img);
            item_name = itemView.findViewById(R.id.item_name);
            item_price = itemView.findViewById(R.id.item_price);
            item_option = itemView.findViewById(R.id.item_option);

            increase = itemView.findViewById(R.id.increase_amt);
            amt = itemView.findViewById(R.id.item_amt);
            decrease = itemView.findViewById(R.id.decrease_amt);

            total_price = itemView.findViewById(R.id.total_price);

            order = itemView.findViewById(R.id.order);
            add_cart = itemView.findViewById(R.id.add_cart);
        }
    }


}
