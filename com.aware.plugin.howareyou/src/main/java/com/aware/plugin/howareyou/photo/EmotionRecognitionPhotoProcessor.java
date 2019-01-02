package com.aware.plugin.howareyou.photo;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Environment;
import android.util.SparseArray;

import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.microsoft.projectoxford.face.contract.Emotion;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import static com.aware.plugin.howareyou.Provider.*;
import static java.lang.Double.max;

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

            if (faceDetected(bytes)) {
                emotionRecognitionService.logDebug("Face detected.");
                if (image != null) {
                    image.close();
                }

                ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
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

    private boolean faceDetected(byte[] bytes) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Face> faces = faceDetector.detect(frame);
        return faces.size() == 1;
    }

    public void onSuccessfulEmotionDetection(Emotion emotions) {
        emotionRecognitionService.logDebug("Emotion recognition succeeded.");
        emotionRecognitionService.logDebug("Detected emotions: " + getEmotionsString(emotions));
        //FIXME FB TODO broadcast the results
        insertTheAnswers(emotions);
        emotionRecognitionService.stopSelf();
    }

    public void onFailedEmotionRecognition() {
        emotionRecognitionService.logDebug("Emotion recognition failed.");
        emotionRecognitionService.stopSelf();
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
