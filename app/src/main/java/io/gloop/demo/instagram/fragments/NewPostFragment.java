package io.gloop.demo.instagram.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.gloop.GloopLogger;
import io.gloop.demo.instagram.CameraView;
import io.gloop.demo.instagram.R;
import io.gloop.demo.instagram.model.Post;

import static android.app.Activity.RESULT_OK;
import static io.gloop.demo.instagram.constants.Constants.ACTIVITY_REQUEST_CODE_IMAGE;

public class NewPostFragment extends Fragment {

    private Activity activity;
    private List<File> cameraImageFiles;
    private ImageView imageView;
    private EditText messageEditText;
    private View formView;
    private View progressView;

    private Camera mCamera = null;
    private CameraView mCameraView = null;

    private Post post;

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
        this.messageEditText = (EditText) view.findViewById(R.id.new_post_message);

        this.formView = view.findViewById(R.id.new_post_form);
        this.progressView = view.findViewById(R.id.new_post_progress);

        popImageChooser();

        final Button next = (Button) view.findViewById(R.id.new_post_bt_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                showProgress(true);
                post.setMessage(messageEditText.getText().toString());
                post.save();
//                showProgress(false);

                showPostsView();
            }
        });

//        setupCamera(view);

        return view;
    }

    public void showPostsView() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, new PostsFragment()).commit();
    }

//    private void setupCamera(View view) {
//        try {
//            mCamera = Camera.open();//you can use open(int) to use different cameras
//            if (mCamera != null) {
//                mCameraView = new CameraView(getContext(), mCamera);//create a SurfaceView to show camera data
//                FrameLayout camera_view = (FrameLayout) view.findViewById(R.id.camera_view);
//                camera_view.addView(mCameraView);//add the SurfaceView to the layout
//            }
//        } catch (Exception e) {
//            GloopLogger.e("Failed to get camera: " + e.getMessage());
//        }
//    }


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
                        createPost(uri);
                    }
                }
        }
    }

    private String createCameraImageFileName() {
        return UUID.randomUUID().toString();
    }

    private void createPost(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
            imageView.setImageBitmap(bitmap);

            post = new Post();
            post.setPicture(bitmap);
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        formView.setVisibility(show ? View.GONE : View.VISIBLE);
        formView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                formView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }
}