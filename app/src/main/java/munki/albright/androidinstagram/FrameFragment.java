package munki.albright.androidinstagram;


import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import munki.albright.androidinstagram.adapter.FrameAdapter;
import munki.albright.androidinstagram.interfaces.FrameFragmentListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class FrameFragment extends BottomSheetDialogFragment implements FrameAdapter.FrameAdapterListener {

    RecyclerView recycler_frame;
    Button btn_add_frame;

    FrameFragmentListener listener;

    int frame_selected = -1;

    static FrameFragment instance;

    public void setListener(FrameFragmentListener listener) {
        this.listener = listener;
    }

    public static FrameFragment getInstance() {
        if (instance == null)
            instance = new FrameFragment();
        return instance;
    }

    public FrameFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_frame, container, false);

        recycler_frame = itemView.findViewById(R.id.recycler_frame);
        btn_add_frame = itemView.findViewById(R.id.btn_add_frame);

        recycler_frame.setHasFixedSize(true);
        recycler_frame.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recycler_frame.setAdapter(new FrameAdapter(getContext(), this));

        btn_add_frame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAddFrame(frame_selected);
            }
        });

        return itemView;
    }

    @Override
    public void onFrameSelected(int frame) {
        frame_selected = frame;
    }
}
