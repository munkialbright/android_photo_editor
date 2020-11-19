package munki.albright.androidinstagram;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import munki.albright.androidinstagram.adapter.ColorAdapter;
import munki.albright.androidinstagram.adapter.FontAdapter;
import munki.albright.androidinstagram.interfaces.TextFragmentListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddTextFragment extends BottomSheetDialogFragment implements ColorAdapter.ColorAdapterListener, FontAdapter.FontAdapterClickListener {

    int colorSelected = Color.parseColor("#000000"); //Default text color is black

    TextFragmentListener listener;

    EditText edit_text;
    RecyclerView recycler_color, recycler_font;
    Button btn_done;

    Typeface typeFaceSelected = Typeface.DEFAULT;

    static AddTextFragment instance;

    public static AddTextFragment getInstance() {
        if (instance == null)
            instance = new AddTextFragment();
        return instance;
    }

    public void setListener(TextFragmentListener listener) {
        this.listener = listener;
    }

    public AddTextFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_add_text, container, false);

        edit_text = itemView.findViewById(R.id.edit_text);
        btn_done = itemView.findViewById(R.id.btn_done);
        recycler_color = itemView.findViewById(R.id.recycler_color);
        recycler_font = itemView.findViewById(R.id.recycler_font);

        recycler_color.setHasFixedSize(true);
        recycler_color.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false));

        recycler_font.setHasFixedSize(true);
        recycler_font.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false));

        ColorAdapter colorAdapter = new ColorAdapter(getContext(), this);
        recycler_color.setAdapter(colorAdapter);

        FontAdapter fontAdapter = new FontAdapter(getContext(), this);
        recycler_font.setAdapter(fontAdapter);

        //Event
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onTextButtonClick(typeFaceSelected, edit_text.getText().toString(), colorSelected);
            }
        });

        return itemView;
    }

    @Override
    public void onColorSelected(int color) {
        colorSelected = color; //Set color when user selects
    }

    @Override
    public void onFontSelected(String fontName) {
        typeFaceSelected =Typeface.createFromAsset(getContext().getAssets(), new StringBuilder("fonts/").append(fontName).toString());
    }
}
