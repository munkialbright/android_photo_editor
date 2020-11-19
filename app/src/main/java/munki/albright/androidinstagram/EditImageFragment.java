package munki.albright.androidinstagram;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import munki.albright.androidinstagram.interfaces.EditImageFragmentListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditImageFragment extends BottomSheetDialogFragment implements SeekBar.OnSeekBarChangeListener {

    private EditImageFragmentListener listener;
    SeekBar seekBar_brightness, seekBar_constraint, seekBar_saturation;

    public void setListener(EditImageFragmentListener listener) {
        this.listener = listener;
    }

    static EditImageFragment instance;

    public static EditImageFragment getInstance() {
        if (instance == null)
            instance = new EditImageFragment();

        return instance;
    }

    public EditImageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_edit_image, container, false);
        seekBar_brightness = itemView.findViewById(R.id.seekBar_brightness);
        seekBar_constraint = itemView.findViewById(R.id.seekBar_constraint);
        seekBar_saturation = itemView.findViewById(R.id.seekBar_saturation);

        seekBar_brightness.setMax(20);
        seekBar_brightness.setProgress(0);

        seekBar_constraint.setMax(20);
        seekBar_constraint.setProgress(20);

        seekBar_saturation.setMax(20);
        seekBar_saturation.setProgress(20);

        seekBar_saturation.setOnSeekBarChangeListener(this);
        seekBar_constraint.setOnSeekBarChangeListener(this);
        seekBar_brightness.setOnSeekBarChangeListener(this);

        return itemView;
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (listener != null) {
            if (seekBar.getId() == R.id.seekBar_brightness) {
                listener.onBrightnessChanged(progress - 100);
            }
            else if (seekBar.getId() == R.id.seekBar_constraint) {
                progress+=10;
                float value = .10f*progress;
                listener.onConstraintChanged((int) value);
            }
            else {
                float value = .10f*progress;
                listener.onSaturationChanged((int) value);
            }
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (listener != null)
            listener.onEditStarted();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (listener != null)
            listener.onEditComplete();
    }

    public void resetControls() {
        seekBar_brightness.setProgress(100);
        seekBar_brightness.setProgress(0);
        seekBar_brightness.setProgress(10);
    }
}
