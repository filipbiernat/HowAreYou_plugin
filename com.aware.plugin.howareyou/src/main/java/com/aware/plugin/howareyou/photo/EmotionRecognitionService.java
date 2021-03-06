package com.aware.plugin.howareyou.photo;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.widget.Toast;

import com.aware.Aware;
import com.aware.plugin.howareyou.PluginActions;
import com.aware.plugin.howareyou.Settings;
import com.aware.plugin.howareyou.plugin.DebugDialog;
import com.google.android.gms.vision.face.FaceDetector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EmotionRecognitionService extends Service {

    protected static final String TAG = "AWARE::HowAreYou::Photo";
    protected static final Boolean DEBUG = true;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private static final int PHOTO_ITERATIONS = 5;
    private static final int TAKE_PICTURE_DELTA_MS = 5000;

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private String cameraId;
    protected CameraDevice cameraDevice;
    protected CameraCaptureSession cameraCaptureSessions;
    protected CaptureRequest.Builder captureRequestBuilder;
    private ImageReader imageReader;
    private Handler mBackgroundHandler;
    private Handler mToastHandler;
    private HandlerThread mBackgroundThread;
    private SurfaceTexture surfaceTexture;
    private Surface surface;
    private FaceDetector faceDetector;
    private int rotationSetting;
    private int rearCameraId;
    private int iterationsLeft;

    public EmotionRecognitionService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initializeService();
        startBackgroundThread();
        openCamera();
        mToastHandler = new Handler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        takePictureSeriesDelayed();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        faceDetector.release();
        if (imageReader != null) {
            imageReader.close();
        }
        stopBackgroundThread();
        closeCamera();
    }

    private void initializeService()
    {
        surfaceTexture = new SurfaceTexture(1);
        surface = new Surface(surfaceTexture);
        iterationsLeft = PHOTO_ITERATIONS;

        faceDetector = new FaceDetector.Builder(this)
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();

        rotationSetting = 0;
        rearCameraId = 1;
    }

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) { //Called when camera is opening
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            String message = "Photo emotion recognition failed\n" + "CameraDevice.StateCallback: Service disconnected";
            stopPhotoEmotionRecognition(message);
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            String message = "Photo emotion recognition failed\n" + "CameraDevice.StateCallback: onError";
            stopPhotoEmotionRecognition(message);
        }
    };

    protected void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    protected void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            String message = "Photo emotion recognition failed\n" +
                    "InterruptedException in stopBackgroundThread\n" + e.getMessage();
            stopPhotoEmotionRecognition(message);
        }
    }

    protected void takePictureSeriesDelayed() {
        toastDebug("HowAreYou: Taking photo in 5 seconds");
        if (Looper.myLooper() == null){
            Looper.prepare();
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (null == cameraDevice) {
                    logDebug("Error when takePictureSeriesDelayed: cameraDevice is null.");
                    String message = "Photo emotion recognition failed\n" +
                            "Error when takePictureSeriesDelayed: cameraDevice is null.";
                    stopPhotoEmotionRecognition(message);
                    return;
                }

                --iterationsLeft;
                if (0 == iterationsLeft) {
                    logDebug("TakePictureSeries. No more retries left.");
                    String message = "Photo emotion recognition failed\n" +
                            "TakePictureSeries. No more retries left.";
                    stopPhotoEmotionRecognition(message);
                    return;
                }

                logDebug("TakePictureSeries. RotationSetting: " + rotationSetting + ", IterationsLeft: " + iterationsLeft);
                takePictureSeries();
            }
        }, TAKE_PICTURE_DELTA_MS);
    }

    protected void takePictureSeries() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpegSizes = null;
            if (characteristics != null) {
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            }
            int width = 640;
            int height = 480;
            if (jpegSizes != null && 0 < jpegSizes.length) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            final int MAX_IMG_DIMENSION = 1200;
            if (width>MAX_IMG_DIMENSION)  width =  MAX_IMG_DIMENSION;
            if (height>MAX_IMG_DIMENSION) height = MAX_IMG_DIMENSION;

            if (imageReader != null) {
                imageReader.close();
            }
            imageReader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 2);

            List<Surface> outputSurfaces = new ArrayList<>(2);
            outputSurfaces.add(imageReader.getSurface());
            outputSurfaces.add(surface);

            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(imageReader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            // Orientation
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotationSetting));
            ImageReader.OnImageAvailableListener readerListener =
                    new EmotionRecognitionPhotoProcessor(this, faceDetector);
            imageReader.setOnImageAvailableListener(readerListener, mBackgroundHandler);
            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    logDebug("Capture Completed");
                    createCameraPreview();
                }
            };
            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                        String message = "Photo emotion recognition failed\n" +
                                "CameraAccessException in onConfigured\n" + e.getMessage();
                        stopPhotoEmotionRecognition(message);
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            String message = "Photo emotion recognition failed\n" +
                    "CameraAccessException in takePictureSeries\n" + e.getMessage();
            stopPhotoEmotionRecognition(message);
        }
    }

    protected void createCameraPreview() {
        try {
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    if (null == cameraDevice) {
                        logDebug("Error when createCameraPreview: cameraDevice is null.");
                        String message = "Photo emotion recognition failed\n" + "cameraDevice is null";
                        stopPhotoEmotionRecognition(message);
                        return;
                    }
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            String message = "Photo emotion recognition failed\n" +
                    "CameraAccessException in createCameraPreview\n" + e.getMessage();
            stopPhotoEmotionRecognition(message);
        }
    }

    private void openCamera() {
        logDebug("Open Camera. RearCameraId: " + rearCameraId);
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = manager.getCameraIdList()[rearCameraId];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                logDebug("Error: permissions not granted.");
                String message = "Photo emotion recognition failed\n" + "Error: permissions not granted.";
                stopPhotoEmotionRecognition(message);
                return;
            }

            manager.openCamera(cameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            String message = "Photo emotion recognition failed\n" +
                    "CameraAccessException in openCamera\n" + e.getMessage();
            stopPhotoEmotionRecognition(message);
        }
    }

    protected void updatePreview() {
        if (null == cameraDevice) {
            logDebug("Error when updatePreview: cameraDevice is null.");
            String message = "Photo emotion recognition failed\n" +
                    "Error when updatePreview: cameraDevice is null.";
            stopPhotoEmotionRecognition(message);
            return;
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            String message = "Photo emotion recognition failed\n" +
                    "CameraAccessException in updatePreview\n" + e.getMessage();
            stopPhotoEmotionRecognition(message);
        }
    }

    private void closeCamera() {
        logDebug("Close Camera");

        if (null != cameraDevice) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (null != imageReader) {
            imageReader.close();
            imageReader = null;
        }
    }

    public void stopPhotoEmotionRecognition(String message) {
        Intent intent = new Intent(this, DebugDialog.class);
        intent.putExtra("MESSAGE_CONTENT", message);
        startActivity(intent);

        logDebug("Stopping emotion recognition service.");
        Intent broadcastIntent = new Intent(PluginActions.ACTION_ON_FINISHED_PHOTO_EMOTION_RECOGNITION);
        sendBroadcast(broadcastIntent);
        stopSelf();
    }

    protected void logDebug(String debugString) {
        if (DEBUG) {
            Log.d(TAG, debugString);
        }
        if(Aware.getSetting(this, Settings.SETTINGS_DEBUG_MODE).equals("true")) {
            toastDebug("Photo: " + debugString);
        }
    }

    private void toastDebug(final String debugString) {
        if (mToastHandler != null) {
            mToastHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(EmotionRecognitionService.this, debugString, Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
