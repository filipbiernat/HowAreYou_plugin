package com.aware.plugin.howareyou.photo;

import java.io.InputStream;

import android.content.Context;
import android.os.AsyncTask;

import com.aware.plugin.howareyou.R;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;

// Background task of face detection.
class EmotionRecognitionTask extends AsyncTask<InputStream, String, Face[]> {
    EmotionRecognitionPhotoProcessor emotionRecognitionPhotoProcessor;
    private Context context;

    public EmotionRecognitionTask(EmotionRecognitionPhotoProcessor emotionRecognitionPhotoProcessor, Context context) {
        this.emotionRecognitionPhotoProcessor = emotionRecognitionPhotoProcessor;
        this.context = context;
    }

    @Override
    protected Face[] doInBackground(InputStream... params) {
        // Get an instance of face service client to detect faces in image.
        FaceServiceClient faceServiceClient = new FaceServiceRestClient(context.getResources().getString(R.string.endpoint),
                context.getResources().getString(R.string.subscription_key));

        try {
            // Start detection.
            return faceServiceClient.detect(
                    params[0],  /* Input stream of image to detect */
                    true,       /* Whether to return face ID */
                    true,       /* Whether to return face landmarks */
                /* Which face attributes to analyze, currently we support:
                   age,gender,headPose,smile,facialHair */
                    new FaceServiceClient.FaceAttributeType[]{
                            FaceServiceClient.FaceAttributeType.Emotion,
                    });
        } catch (Exception e) {
            publishProgress(e.getMessage());
            emotionRecognitionPhotoProcessor.onFailedEmotionRecognition();
            return null;
        }
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(String... progress) {
    }

    @Override
    protected void onPostExecute(Face[] result) {
        if (result == null || result.length != 1){
            emotionRecognitionPhotoProcessor.onFailedEmotionRecognition();
        } else {
            emotionRecognitionPhotoProcessor.onSuccessfulEmotionDetection(result[0].faceAttributes.emotion);
        }
    }
}

