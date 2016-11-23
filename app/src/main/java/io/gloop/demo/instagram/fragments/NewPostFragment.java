package io.gloop.demo.instagram.fragments;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.gloop.GloopLogger;
import io.gloop.demo.instagram.R;
import io.gloop.demo.instagram.model.Post;

import static android.app.Activity.RESULT_OK;
import static io.gloop.demo.instagram.constants.Constants.ACTIVITY_REQUEST_CODE_IMAGE;

public class NewPostFragment extends Fragment {

    private Activity activity;
    private List<File> cameraImageFiles;
    private ImageView imageView;


    public NewPostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_post, container, false);

        this.imageView = (ImageView) view.findViewById(R.id.new_post_picture);

        popImageChooser();

        final Button next = (Button) view.findViewById(R.id.new_post_bt_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // TODO implement
            }
        });

        return view;
    }


    private void popImageChooser() {
        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = activity.getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);

        cameraImageFiles = new ArrayList<File>();

        final String fileName = createCameraImageFileName();

        int i = 0;
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.MEDIA_IGNORE_FILENAME, ".nomedia");


            File cameraImageOutputFile = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    fileName);
            cameraImageFiles.add(cameraImageOutputFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraImageFiles.get(i)));
            i++;

            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
//        final Intent chooserIntent = Intent.createChooser(galleryIntent, getString(R.string.attach_images_title));
        final Intent chooserIntent = Intent.createChooser(galleryIntent, fileName);

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));
        startActivityForResult(chooserIntent, ACTIVITY_REQUEST_CODE_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case ACTIVITY_REQUEST_CODE_IMAGE:
                if (resultCode == RESULT_OK) {

                    Uri uri = null;
                    if (imageReturnedIntent == null) {   //since we used EXTRA_OUTPUT for camera, so it will be null

                        uri = Uri.fromFile(cameraImageFiles.get(0));    // TODO only one image can be saves for now
                        GloopLogger.d("attach image from camera: " + uri);
                    } else {  // from gallery
                        uri = imageReturnedIntent.getData();
                        GloopLogger.d("attach image from gallery: " + uri.toString());
                    }

                    if (uri != null) {
                        saveImage(uri);
                    }
                }
        }
    }

    private String createCameraImageFileName() {
        return UUID.randomUUID().toString();
    }

    private void saveImage(Uri uri) {

        String title = UUID.randomUUID().toString();    // TODO get real title

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
            imageView.setImageBitmap(bitmap);

            new Post(title, bitmap).save(); // TODO move to save button
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}