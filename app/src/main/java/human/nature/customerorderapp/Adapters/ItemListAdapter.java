package human.nature.customerorderapp.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
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
import human.nature.customerorderapp.Fragments.CustomSpinner;
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

    @SuppressLint("DefaultLocale")
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


//        if(data.isNeedRefreshAdapter()) {
            holder.item_option.setAdapter(optionsAdapter);
//        }
        holder.item_option.setTag(position);
        holder.item_option.setOnItemSelectedListener(onItemSelectedListener);
//        holder.item_option.setOnClickListener(onClickListener);

        holder.increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.setAmt(String.valueOf(Integer.parseInt(data.getAmt()) +1));
                holder.amt.setText(data.getAmt());
            }
        });
        holder.increase.setTag(position);
        holder.decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(! holder.amt.getText().toString().equals("0") || !TextUtils.isEmpty(holder.amt.getText().toString())){
                    data.setAmt(String.valueOf(Integer.parseInt(data.getAmt()) -1));
                    holder.amt.setText(data.getAmt());
                } else{
                    holder.amt.setText(data.getAmt());
                }
            }
        });
        holder.decrease.setTag(position);


        holder.amt.setText(data.getAmt());
        holder.amt.setTag(position);
//        holder.amt.setOnEditorActionListener(onEditorActionListener);
        holder.amt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((EditText)view).getText().clear();
            }
        });
        holder.amt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                holder.amt.setText("");
            }

            @SuppressLint("DefaultLocale")
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    data.setAmt(charSequence.toString());
//                    data.setPrice(String.valueOf(Integer.parseInt(charSequence.toString()) * Integer.parseInt(data.getPrice())));
                    holder.total_price.setText(String.format("%,d", Integer.parseInt(data.getAmt()) * Integer.parseInt(data.getPrice())));
                } catch (Exception e){
                    Log.d("amt, price zero", data.getAmt() + " / " + data.getPrice());
                    holder.total_price.setText("0");
                    data.setAmt("0");
//                    data.setPrice("0");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
//                data.setPrice(holder.total_price.getText().toString());
            }
        });

        try {
            holder.total_price.setText(String.format("%,d", Integer.parseInt(data.getPrice()) * Integer.parseInt(data.getAmt())));
        } catch (Exception e){
            holder.total_price.setHint("0");
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
        CustomSpinner item_option;

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
