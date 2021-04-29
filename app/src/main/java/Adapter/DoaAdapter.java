package Adapter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.zapdevs.yukdoamobile.MainActivity;
import com.zapdevs.yukdoamobile.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;

import ModelViewDoa.ModelDoa;

public class DoaAdapter extends RecyclerView.Adapter<DoaAdapter.MyViewHolder> {
    public Activity activity;
    ArrayList<ModelDoa> data, data2;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public DoaAdapter(Activity activity, ArrayList<ModelDoa> data) {
        this.activity = activity;
        this.data = data;
        data2 = this.data;
        this.sharedPreferences = activity.getSharedPreferences("data_pulih", 0);
        this.editor = sharedPreferences.edit();

    }

    @NonNull
    @Override
    public DoaAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(activity).inflate(R.layout.card_row_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final DoaAdapter.MyViewHolder holder, int position) {
        holder.idDoa.setText(data.get(position).id);
        holder.doaTranslation.setText(data.get(position).translation);
        holder.doaTitle.setText(data.get(position).title);
        holder.doaArabic.setText(data.get(position).arab);
        holder.doaLatin.setText(data.get(position).latin);

        holder.card_body.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.card_body.getVisibility() == View.VISIBLE) {
//                    holder.card_body.setAnimation(AnimationUtils.makeOutAnimation(activity, false));
                    holder.card_body.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.fade_scale_out));
                    holder.card_body.setVisibility(View.GONE);
                } else {
                    holder.card_body.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.fade_scale_out));
//                    holder.card_body.setAnimation(AnimationUtils.makeInAnimation(activity, true));
                    holder.card_body.setVisibility(View.VISIBLE);
                }
            }
        });
        holder.main_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.card_body.getVisibility() == View.VISIBLE) {
                    holder.card_body.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.fade_scale_translation_out));
                    holder.card_body.setVisibility(View.GONE);
                } else {
                    holder.card_body.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.fade_scale_translation));
                    holder.card_body.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView idDoa, doaTitle, doaArabic, doaLatin, doaTranslation;
        CardView card_body, main_card;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            idDoa = (TextView) itemView.findViewById(R.id.idDoa);
            doaTitle = (TextView) itemView.findViewById(R.id.titleDoa);
            doaArabic = (TextView) itemView.findViewById(R.id.doaArabic);
            doaLatin = (TextView) itemView.findViewById(R.id.doaLatin);
            doaTranslation = (TextView) itemView.findViewById(R.id.doaTranslation);
            card_body = (CardView) itemView.findViewById(R.id.card_body);
            main_card = (CardView) itemView.findViewById(R.id.main_card);
        }
    }

    public void filter(@NotNull String text) throws FileNotFoundException, JSONException {
        if(text.isEmpty()){
            data.clear();
            ((MainActivity) activity).logic();

        }else{
            text = text.toLowerCase();
            ArrayList<ModelDoa> result = new ArrayList<>();
            for(ModelDoa doa : data2){
                if(doa.title.toLowerCase().contains(text)){
                    result.add(doa);
                }
            }
            data.clear();
            data.addAll(result);
        }
        notifyDataSetChanged();

    }
    public void restore(ArrayList<ModelDoa> data){
        this.data.addAll(data);
        notifyDataSetChanged();
    }
}
