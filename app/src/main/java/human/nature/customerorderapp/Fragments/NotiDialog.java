package human.nature.customerorderapp.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import human.nature.customerorderapp.R;

public class NotiDialog extends Dialog {
    String title;
    String add_time;
    String content;

    TextView tv_title;
    TextView tv_add_time;
    TextView tv_content;
    Button confirm;

    public NotiDialog(@NonNull Context context) {
        super(context);
    }

    public NotiDialog(@NonNull Context context, String title, String add_time, String content){
        super(context);
        this.title = title;
        this.add_time = add_time;
        this.content = content;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.noti_layout);
        tv_title = findViewById(R.id.title);
        tv_add_time = findViewById(R.id.add_time);
        tv_content = findViewById(R.id.content);
        confirm = findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotiDialog.this.dismiss();
            }
        });

        tv_title.setText(title);
        tv_add_time.setText(add_time);
        tv_content.setText(content);
    }
}
