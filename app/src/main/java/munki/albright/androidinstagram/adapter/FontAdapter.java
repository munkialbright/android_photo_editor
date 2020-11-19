package munki.albright.androidinstagram.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import munki.albright.androidinstagram.R;

public class FontAdapter extends RecyclerView.Adapter<FontAdapter.FontViewHolder> {

    Context context;
    FontAdapterClickListener listener;
    List<String> fontList;

    int row_selected = -1;

    public FontAdapter(Context context, FontAdapterClickListener listener) {
        this.context = context;
        this.listener = listener;
        fontList = loadFontList();
    }

    private List<String> loadFontList() {
        List<String> result = new ArrayList<>();

        //More fonts can be added here
        result.add("Cheque-Regular.otf");
        result.add("Cheque-Black.otf");
        result.add("dinoffcpro-cond.ttf");
        result.add("dinoffcpro-condbold.ttf");
        result.add("dinoffcpro-condmedium.ttf");
        result.add("BebasNeue.ttf");
        result.add("Champions.ttf");
        result.add("FontAwesome.otf");
        result.add("MR ROBOT.ttf");
        result.add("AndroidClock.ttf");
        result.add("CarroisGothicSC-Regular.ttf");
        result.add("ComingSoon.ttf");
        result.add("CutiveMono.ttf");
        result.add("DancingScript-Regular.ttf");
        result.add("DancingScript-Bold.ttf");

        return result;
    }

    @NonNull
    @Override
    public FontViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.font_item, viewGroup, false);
        return new FontViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FontViewHolder fontViewHolder, int i) {
        if (row_selected == i)
            fontViewHolder.img_check.setVisibility(View.VISIBLE);
        else
            fontViewHolder.img_check.setVisibility(View.INVISIBLE);

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), new StringBuilder("fonts/").append(fontList.get(i)).toString());

        fontViewHolder.text_font_name.setText(fontList.get(i));
        fontViewHolder.text_font.setTypeface(typeface);
    }

    @Override
    public int getItemCount() {
        return fontList.size();
    }

    public class FontViewHolder extends RecyclerView.ViewHolder {

        TextView text_font_name, text_font;
        ImageView img_check;

        public FontViewHolder(@NonNull View itemView) {
            super(itemView);

            text_font_name = itemView.findViewById(R.id.text_font_name);
            text_font = itemView.findViewById(R.id.text_font);
            img_check = itemView.findViewById(R.id.img_check);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onFontSelected(fontList.get(getAdapterPosition()));
                    row_selected = getAdapterPosition();
                    notifyDataSetChanged();
                }
            });
        }
    }

    public interface FontAdapterClickListener {
        void onFontSelected(String fontName);
    }
}
