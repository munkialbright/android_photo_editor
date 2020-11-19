package munki.albright.androidinstagram.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import munki.albright.androidinstagram.R;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorViewHolder> {

    Context context;
    List<Integer> colorList;
    ColorAdapterListener listener;

    public ColorAdapter(Context context, ColorAdapterListener listener) {
        this.context = context;
        this.colorList = getColorList();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.color_item, viewGroup, false);
        return new ColorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder colorViewHolder, int i) {
        colorViewHolder.color_section.setCardBackgroundColor(colorList.get(i));
    }

    @Override
    public int getItemCount() {
        return colorList.size();
    }

    public class ColorViewHolder extends RecyclerView.ViewHolder {

        public CardView color_section;

        public ColorViewHolder(@NonNull View itemView) {
            super(itemView);

            color_section = itemView.findViewById(R.id.color_section);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onColorSelected(colorList.get(getAdapterPosition()));
                }
            });
        }
    }

    private List<Integer> getColorList() {
        List<Integer> colorList = new ArrayList<>();

        colorList.add(Color.parseColor("#ffffff"));
        colorList.add(Color.parseColor("#131722"));
        colorList.add(Color.parseColor("#663000"));
        colorList.add(Color.parseColor("#ff545e"));
        colorList.add(Color.parseColor("#57bb82"));
        colorList.add(Color.parseColor("#aaaaaa"));
        colorList.add(Color.parseColor("#dbeeff"));
        colorList.add(Color.parseColor("#fffd37"));
        colorList.add(Color.parseColor("#be5796"));

        colorList.add(Color.parseColor("#bb349b"));
        colorList.add(Color.parseColor("#6e557e"));
        colorList.add(Color.parseColor("#5e40b2"));
        colorList.add(Color.parseColor("#8051ef"));
        colorList.add(Color.parseColor("#895adc"));
        colorList.add(Color.parseColor("#935da0"));
        colorList.add(Color.parseColor("#7a5e93"));
        colorList.add(Color.parseColor("#6c4475"));

        colorList.add(Color.parseColor("#403890"));
        colorList.add(Color.parseColor("#1b36eb"));
        colorList.add(Color.parseColor("#10d6a2"));
        colorList.add(Color.parseColor("#f8ff85"));
        colorList.add(Color.parseColor("#664e3b"));


        return colorList;
    }

    public interface ColorAdapterListener {
        void onColorSelected (int color);
    }
}
