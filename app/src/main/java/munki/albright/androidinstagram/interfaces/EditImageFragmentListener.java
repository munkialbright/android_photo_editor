package munki.albright.androidinstagram.interfaces;

public interface EditImageFragmentListener {
    void onBrightnessChanged(int brightness);
    void onSaturationChanged(float saturation);
    void onConstraintChanged(int constraint);
    void onEditStarted();
    void onEditComplete();
}
