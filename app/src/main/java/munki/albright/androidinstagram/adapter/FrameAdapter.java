package munki.albright.androidinstagram.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import munki.albright.androidinstagram.R;

public class FrameAdapter extends RecyclerView.Adapter<FrameAdapter.FrameViewHolder> {

    Context context;
    List<Integer> frameList;
    FrameAdapterListener listener;

    int row_selected = -1;

    public FrameAdapter(Context context, FrameAdapterListener listener) {
        this.context = context;
        this.frameList = getFrameList();
        this.listener = listener;
    }

    private List<Integer> getFrameList() {
        List<Integer> result = new ArrayList<>();

        result.add(R.drawable.frame_1);
        result.add(R.drawable.frame_2);
        result.add(R.drawable.frame_3);
        result.add(R.drawable.frame_4);
        result.add(R.drawable.frame_5);
        result.add(R.drawable.frame_6);
        result.add(R.drawable.frame_7);
        result.add(R.drawable.frame_8);
        result.add(R.drawable.frame_9);
        result.add(R.drawable.frame_10);
        result.add(R.drawable.frame_11);



        return result;
    }

    @NonNull
    @Override
    public FrameViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.frame_item, viewGroup, false);
        return new FrameViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FrameViewHolder frameViewHolder, int i) {
        if (row_selected == i)
            frameViewHolder.img_check.setVisibility(View.VISIBLE);
        else
            frameViewHolder.img_check.setVisibility(View.INVISIBLE);

        frameViewHolder.img_frame.setImageResource(frameList.get(i));
    }

    @Override
    public int getItemCount() {
        return frameList.size();
    }

    public class FrameViewHolder extends RecyclerView.ViewHolder {

        ImageView img_check, img_frame;

        public FrameViewHolder(@NonNull View itemView) {
            super(itemView);

            img_check = itemView.findViewById(R.id.img_check);
            img_frame = itemView.findViewById(R.id.img_frame);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onFrameSelected(frameList.get(getAdapterPosition()));

                    row_selected = getAdapterPosition();
                    notifyDataSetChanged();
                }
            });
        }
    }

    public interface FrameAdapterListener {
        void onFrameSelected(int frame);
    }
}
