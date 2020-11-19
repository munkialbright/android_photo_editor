package munki.albright.androidinstagram;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.yalantis.ucrop.UCrop;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import ja.burhanrashid52.photoeditor.OnSaveBitmap;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import munki.albright.androidinstagram.interfaces.BrushFragmentListener;
import munki.albright.androidinstagram.interfaces.EditImageFragmentListener;
import munki.albright.androidinstagram.interfaces.EmojiFragmentListener;
import munki.albright.androidinstagram.interfaces.FiltersListFragmentListener;
import munki.albright.androidinstagram.interfaces.FrameFragmentListener;
import munki.albright.androidinstagram.interfaces.TextFragmentListener;
import munki.albright.androidinstagram.utils.BitmapUtils;

public class MainActivity extends AppCompatActivity implements FiltersListFragmentListener, EditImageFragmentListener, BrushFragmentListener, EmojiFragmentListener, TextFragmentListener, FrameFragmentListener {

    public static final String pictureName = "dog.jpg";
    public static final int PERMISSION_PICK_IMAGE = 1000;
    public static final int PERMISSION_INSERT_IMAGE = 1001;
    private static final int CAMERA_REQUEST = 1002;

    PhotoEditorView photoEditorView;
    PhotoEditor photoEditor;
    CoordinatorLayout coordinatorLayout;

    Bitmap originalBitmap, filterdBitmap, finalBitmap;

    FilterListFragment filterListFragment;
    EditImageFragment editImageFragment;

    CardView btn_filters_list, btn_edit, btn_brush, btn_emoji, btn_text, btn_image, btn_frame, btn_crop;

    Uri img_selected_uri;
    String path;

    int brightnessFinal = 0;
    float saturationFinal = 1.0f;
    float constraintFinal = 1.0f;

    ScaleGestureDetector scaleGestureDetector;
    float scaleFactor = 1.0f;
    float xDown = 0, yDown = 0;

    //Load native image filters lib
    static {
        System.loadLibrary("NativeImageProcessor");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("Photo Editor");

        //View
        photoEditorView = findViewById(R.id.image_preview);
        photoEditor = new PhotoEditor.Builder(this, photoEditorView).setPinchTextScalable(true).setDefaultEmojiTypeface(Typeface.createFromAsset(getAssets(), "emojione-android.ttf")).build();
        coordinatorLayout = findViewById(R.id.coordinator);

        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        btn_edit = findViewById(R.id.btn_edit);
        btn_filters_list = findViewById(R.id.btn_filters_list);
        btn_brush = findViewById(R.id.btn_brush);
        btn_emoji = findViewById(R.id.btn_emoji);
        btn_text = findViewById(R.id.btn_text);
        btn_image = findViewById(R.id.btn_image);
        btn_frame = findViewById(R.id.btn_frame);
        btn_crop = findViewById(R.id.btn_crop);

        btn_filters_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filterListFragment != null) {
                    filterListFragment.show(getSupportFragmentManager(), filterListFragment.getTag());
                }
                else {
                    FilterListFragment filterListFragment = FilterListFragment.getInstance(null);
                    filterListFragment.setListener(MainActivity.this);
                    filterListFragment.show(getSupportFragmentManager(), filterListFragment.getTag());
                }
            }
        });

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditImageFragment editImageFragment = EditImageFragment.getInstance();
                editImageFragment.setListener(MainActivity.this);
                editImageFragment.show(getSupportFragmentManager(), editImageFragment.getTag());
            }
        });

        btn_brush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Enable brush mode
                photoEditor.setBrushDrawingMode(true);

                BrushFragment brushFragment = BrushFragment.getInstance();
                brushFragment.setListener(MainActivity.this);
                brushFragment.show(getSupportFragmentManager(), brushFragment.getTag());
            }
        });

        btn_emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmojiFragment emojiFragment = EmojiFragment.getInstance();
                emojiFragment.setListener(MainActivity.this);
                emojiFragment.show(getSupportFragmentManager(), emojiFragment.getTag());
            }
        });

        btn_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddTextFragment addTextFragment = AddTextFragment.getInstance();
                addTextFragment.setListener(MainActivity.this);
                addTextFragment.show(getSupportFragmentManager(), addTextFragment.getTag());
            }
        });

        btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImageToPicture();
            }
        });

        btn_frame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FrameFragment frameFragment = FrameFragment.getInstance();
                frameFragment.setListener(MainActivity.this);
                frameFragment.show(getSupportFragmentManager(), frameFragment.getTag());
            }
        });

        btn_crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCrop(img_selected_uri);
            }
        });

        loadImage();
    }

    private void startCrop(Uri uri) {
        String destinationFileName = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();

        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));
        uCrop.start(MainActivity.this);
    }

    private void addImageToPicture() {
        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, PERMISSION_INSERT_IMAGE);
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }).check();
    }

    private void loadImage() {
        originalBitmap = BitmapUtils.getBitmapFromAssets(this, pictureName, 300, 300);
        filterdBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        photoEditorView.getSource().setImageBitmap(originalBitmap);
    }

    @Override
    public void onFilterSelected(com.zomato.photofilters.imageprocessors.Filter filter) {
        //resetControls();
        filterdBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        photoEditorView.getSource().setImageBitmap(filter.processFilter(filterdBitmap));
        finalBitmap = filterdBitmap.copy(Bitmap.Config.ARGB_8888, true);
    }

    private void resetControls() {
        if (editImageFragment != null) {
            editImageFragment.resetControls();
        }
        brightnessFinal = 0;
        saturationFinal = 1.0f;
        constraintFinal = 1.0f;
    }

    @Override
    public void onBrightnessChanged(int brightness) {
        brightnessFinal = brightness;
        com.zomato.photofilters.imageprocessors.Filter myFilter = new com.zomato.photofilters.imageprocessors.Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightness));
        photoEditorView.getSource().setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onSaturationChanged(float saturation) {
        saturationFinal = saturation;
        com.zomato.photofilters.imageprocessors.Filter myFilter = new com.zomato.photofilters.imageprocessors.Filter();
        myFilter.addSubFilter(new SaturationSubfilter(saturation));
        photoEditorView.getSource().setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onConstraintChanged(int constraint) {
        constraintFinal = constraint;
        com.zomato.photofilters.imageprocessors.Filter myFilter = new com.zomato.photofilters.imageprocessors.Filter();
        myFilter.addSubFilter(new ContrastSubFilter(constraint));
        photoEditorView.getSource().setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onEditStarted() {

    }

    @Override
    public void onEditComplete() {
        Bitmap bitmap = filterdBitmap.copy(Bitmap.Config.ARGB_8888, true);
        com.zomato.photofilters.imageprocessors.Filter myFilter = new com.zomato.photofilters.imageprocessors.Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
        myFilter.addSubFilter(new SaturationSubfilter(saturationFinal));
        myFilter.addSubFilter(new ContrastSubFilter(constraintFinal));

        finalBitmap = myFilter.processFilter(bitmap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_open) {
            openImageFromGallery();
            return true;
        }

        else if (id == R.id.action_save) {
            saveImageToGallery();
            return true;
        }

        else if (id == R.id.action_camera) {
            openCamera();
            return true;
        }

        else if (id == R.id.action_edit) {
            editPhoto();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void editPhoto() {
        HorizontalScrollView filtersView = findViewById(R.id.filtersView);
        if (filtersView.getVisibility() == View.INVISIBLE) {
            filtersView.setVisibility(View.VISIBLE);
        }
        else {
            filtersView.setVisibility(View.INVISIBLE);
        }
    }

    private void openCamera() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            ContentValues values = new ContentValues();
                            values.put(MediaStore.Images.Media.TITLE, "New Picture");
                            values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");

                            img_selected_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, img_selected_uri);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void saveImageToGallery() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            photoEditor.saveAsBitmap(new OnSaveBitmap() {
                                @Override
                                public void onBitmapReady(Bitmap saveBitmap) {
                                    photoEditorView.getSource().setImageBitmap(saveBitmap);
                                    path = BitmapUtils.insertImage(getContentResolver(), saveBitmap, System.currentTimeMillis() + "_profile.jpg", null);
                                    if (!TextUtils.isEmpty(path)) {
                                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Image saved to gallery!", Snackbar.LENGTH_LONG).setAction("OPEN", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                openImage(path);
                                            }
                                        });
                                        snackbar.show();
                                    }
                                    else  {
                                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Unable to save image!", Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                    }
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Toast.makeText(MainActivity.this, "Unable to save image as bitmap!", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void openImage(String path) {
        Intent intent = new Intent();
        intent.setAction(intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(path), "image/*");
        startActivity(intent);
    }

    private void openImageFromGallery() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(intent, PERMISSION_PICK_IMAGE);
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Permission denied!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PERMISSION_PICK_IMAGE) {
                Bitmap bitmap = BitmapUtils.getBitmapFromGallery(this, data.getData(), 1500, 1500);

                img_selected_uri = data.getData();

                //clear bitmap memory
                originalBitmap.recycle();
                finalBitmap.recycle();
                filterdBitmap.recycle();

                originalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                filterdBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                photoEditorView.getSource().setImageBitmap(originalBitmap);
                bitmap.recycle();

                filterListFragment = FilterListFragment.getInstance(originalBitmap);
                filterListFragment.setListener(this);

            }
            else if (requestCode == CAMERA_REQUEST) {
                Bitmap bitmap = BitmapUtils.getBitmapFromGallery(this, img_selected_uri, 1500, 1500);

                //clear bitmap memory
                originalBitmap.recycle();
                finalBitmap.recycle();
                filterdBitmap.recycle();

                originalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                filterdBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                photoEditorView.getSource().setImageBitmap(originalBitmap);
                bitmap.recycle();

                filterListFragment = FilterListFragment.getInstance(originalBitmap);
                filterListFragment.setListener(this);

            }
            else if (requestCode == PERMISSION_INSERT_IMAGE) {
                Bitmap bitmap = BitmapUtils.getBitmapFromGallery(this, data.getData(), 250, 250);
                photoEditor.addImage(bitmap);
            }
            else if (requestCode == UCrop.REQUEST_CROP)
                handleCropResult(data);

        }
        else if (resultCode == UCrop.RESULT_ERROR)
            handleCropError(data);
    }

    private void handleCropError(Intent data) {
        final Throwable cropError = UCrop.getError(data);
        if (cropError != null)
            Toast.makeText(this, ""+cropError.getMessage(), Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Unexpected Error", Toast.LENGTH_SHORT).show();
    }

    private void handleCropResult(Intent data) {
        final Uri resultUri = UCrop.getOutput(data);
        if (resultUri != null) {
            photoEditorView.getSource().setImageURI(resultUri);

            Bitmap bitmap = ((BitmapDrawable)photoEditorView.getSource().getDrawable()).getBitmap();

            originalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            filterdBitmap = originalBitmap;
            finalBitmap = originalBitmap;
        }
        else
            Toast.makeText(this, "Cannot retrieve cropped image", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBrushSizeChangedListener(float size) {
        photoEditor.setBrushSize(size);
    }

    @Override
    public void onBrushOpacityChangedListener(int opacity) {
        photoEditor.setOpacity(opacity);
    }

    @Override
    public void onBrushColorChangedListener(int color) {
        photoEditor.setBrushColor(color);
    }

    @Override
    public void onBrushStateChangedListener(boolean isEraser) {
        if (isEraser)
            photoEditor.brushEraser();
        else
            photoEditor.setBrushDrawingMode(true);
    }

    @Override
    public void onEmojiSelected(String emoji) {
        photoEditor.addEmoji(emoji);
    }


    @Override
    public void onTextButtonClick(Typeface typeface, String text, int color) {
        photoEditor.addText(typeface, text, color);
    }

    @Override
    public void onAddFrame(int frame) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), frame);
        photoEditor.addImage(bitmap);
    }

    //Detect touch on the entire view
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return scaleGestureDetector.onTouchEvent(event);
    }

    //Setup the scale factor and apply to the image
    public class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            //Handles the scaling feature of photoEditorView
            scaleFactor *= scaleGestureDetector.getScaleFactor();
            photoEditorView.setScaleX(scaleFactor);
            photoEditorView.setScaleY(scaleFactor);

            //Implementing drag/move feature on photoEditorView
            photoEditorView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getActionMasked()) {
                        //The user puts finger down on photoEditorView
                        case MotionEvent.ACTION_DOWN:
                            xDown = event.getX();
                            yDown = event.getY();
                            break;

                        //The user moves his finger on photoEditorView
                        case MotionEvent.ACTION_MOVE:
                            float movedX, movedY;
                            movedX = event.getX();
                            movedY = event.getY();

                            //Calculate how much the user moved finger
                            float distanceX = movedX-xDown;
                            float distanceY = movedY-yDown;

                            //Now move view to that position
                            photoEditorView.setX(photoEditorView.getX()+distanceX);
                            photoEditorView.setY(photoEditorView.getY()+distanceY);
                            break;

                        case MotionEvent.ACTION_OUTSIDE:
                            //Handles the scaling feature of photoEditorView
                            scaleFactor *= scaleGestureDetector.getScaleFactor();
                            photoEditorView.setScaleX(scaleFactor);
                            photoEditorView.setScaleY(scaleFactor);
                            break;

                    }

                    return true;
                }
            });

            return true;
        }

    }

}
