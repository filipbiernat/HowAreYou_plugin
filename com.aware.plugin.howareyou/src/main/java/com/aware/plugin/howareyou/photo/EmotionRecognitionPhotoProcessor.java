package com.aware.plugin.howareyou.photo;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import android.media.ImageReader;
import android.os.Environment;
import android.os.Handler;
import android.util.SparseArray;
import android.widget.EditText;

import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.plugin.howareyou.PluginActions;
import com.aware.plugin.howareyou.R;
import com.aware.plugin.howareyou.plugin.DebugDialog;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.microsoft.projectoxford.face.contract.Emotion;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import static com.aware.plugin.howareyou.Provider.*;

class EmotionRecognitionPhotoProcessor implements ImageReader.OnImageAvailableListener {
    private final EmotionRecognitionService emotionRecognitionService;
    private final FaceDetector faceDetector;
    private final File file;

    public EmotionRecognitionPhotoProcessor(EmotionRecognitionService emotionRecognitionService,
                                            FaceDetector faceDetector) {
        this.emotionRecognitionService = emotionRecognitionService;
        this.faceDetector = faceDetector;
        file = new File(Environment.getExternalStorageDirectory() + "/pic.jpg");
    }

    @Override
    public void onImageAvailable(ImageReader reader) {
        Image image = null;
        try {
            image = reader.acquireLatestImage();

            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.capacity()];
            buffer.get(bytes);
            saveImage(bytes);

            Bitmap bitmap = faceDetected(bytes);
            if (bitmap != null) {
                emotionRecognitionService.logDebug("Face detected.");
                if (image != null) {
                    image.close();
                }

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapData = bos.toByteArray();
                ByteArrayInputStream inputStream = new ByteArrayInputStream(bitmapData);

                new EmotionRecognitionTask(this, emotionRecognitionService).execute(inputStream);
            } else {
                emotionRecognitionService.logDebug("Face not detected. Retrying...");
                emotionRecognitionService.takePictureSeriesDelayed();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            emotionRecognitionService.stopSelf();
        } catch (IOException e) {
            e.printStackTrace();
            emotionRecognitionService.stopSelf();
        } finally {
            if (image != null) {
                image.close();
            }
        }
    }

    private void saveImage(byte[] bytes) throws IOException {
        OutputStream output = null;
        try {
            output = new FileOutputStream(file);
            output.write(bytes);
        } finally {
            if (null != output) {
                output.close();
            }
        }
    }

    private Bitmap faceDetected(byte[] bytes) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);

        for (int i = 0; i < 4; ++i) {
            bitmap = rotateImage(bitmap, 90 * i);
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();

            SparseArray<Face> faces = faceDetector.detect(frame);
            if (faces.size() == 1){
                return bitmap;
            }
        }
        return null;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public void onSuccessfulEmotionDetection(Emotion emotions) {
        String message = "Photo emotion recognition succeeded\n" +
                "Detected emotions: " + getEmotionsString(emotions);
        Intent intent = new Intent(emotionRecognitionService, DebugDialog.class);
        intent.putExtra("MESSAGE_CONTENT", message);
        emotionRecognitionService.startActivity(intent);
        emotionRecognitionService.logDebug("Emotion recognition succeeded.");
        emotionRecognitionService.logDebug("Detected emotions: " + getEmotionsString(emotions));

        Intent broadcastIntent = new Intent(PluginActions.ACTION_ON_FINISHED_PHOTO_EMOTION_RECOGNITION);
        emotionRecognitionService.sendBroadcast(broadcastIntent);

        insertTheAnswers(emotions);
        emotionRecognitionService.stopSelf();
    }

    public void onFailedEmotionRecognition(String message) {
        Intent intent = new Intent(emotionRecognitionService, DebugDialog.class);
        intent.putExtra("MESSAGE_CONTENT", message);
        emotionRecognitionService.startActivity(intent);


        //emotionRecognitionService.logDebug("Emotion recognition failed.");
        emotionRecognitionService.logDebug("Emotion recognition failed. Retrying.");
        //emotionRecognitionService.stopSelf();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                emotionRecognitionService.takePictureSeriesDelayed();
            }
        }, 10*1000);
    }

    private String getEmotionsString(Emotion emotions)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(" Anger: ").append(emotions.anger);
        sb.append(" Contempt: ").append(emotions.contempt);
        sb.append(" Disgust: ").append(emotions.disgust);
        sb.append(" Fear: ").append(emotions.fear);
        sb.append(" Happiness: ").append(emotions.happiness);
        sb.append(" Neutral: ").append(emotions.neutral);
        sb.append(" Sadness: ").append(emotions.sadness);
        sb.append(" Surprise: ").append(emotions.surprise);
        return sb.toString();
    }

    private void insertTheAnswers(Emotion emotions){
        ContentValues answer = new ContentValues();
        answer.put(Table_Photo_Data.DEVICE_ID, Aware.getSetting(emotionRecognitionService.getApplicationContext(),
                Aware_Preferences.DEVICE_ID));
        answer.put(Table_Photo_Data.TIMESTAMP, System.currentTimeMillis());

        answer.put(Table_Photo_Data.ANGER, emotions.anger);
        answer.put(Table_Photo_Data.CONTEMPT, emotions.contempt);
        answer.put(Table_Photo_Data.DISGUST, emotions.disgust);
        answer.put(Table_Photo_Data.FEAR, emotions.fear);
        answer.put(Table_Photo_Data.HAPPINESS, emotions.happiness);
        answer.put(Table_Photo_Data.NEUTRAL, emotions.neutral);
        answer.put(Table_Photo_Data.SADNESS, emotions.sadness);
        answer.put(Table_Photo_Data.SURPRISE, emotions.surprise);

        emotionRecognitionService.getContentResolver().insert(Table_Photo_Data.CONTENT_URI, answer);
    }
}
