package agh.heart;

import android.os.AsyncTask;
import android.util.Log;

import com.aware.Aware;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import agh.heart.model.ModelWrapper;
import agh.heart.observers.Observer;


public class HeaRTService extends JobService {
    private AsyncTask<JobParameters, Void, JobParameters> modelExecutor = new ModelExecutor();
    private boolean isJobRunning = false;

    @Override
    public boolean onStartJob(JobParameters job) {
        if (!isJobRunning){
            isJobRunning = true;
            Log.d(Observer.TAG, "Job executed.");
            modelExecutor.execute(job);
        } else {
            Log.d(Observer.TAG, "Job dropped as another is already job running.");
        }

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        isJobRunning = false;
        return false;
    }

    private class ModelExecutor extends AsyncTask<JobParameters, Void, JobParameters> {

        @Override
        protected JobParameters doInBackground(JobParameters... params) {
            String modelSrc = readModel();
            ModelWrapper model = new ModelWrapper(modelSrc);
            model.runInference();
            return params[0];
        }

        @Override
        protected void onPostExecute(JobParameters jobParameters) {
            jobFinished(jobParameters, false);
        }
    }

    private String readModel()
    {
        try {
            StringBuilder sb = new StringBuilder();
            InputStream is = getApplicationContext().getAssets().open("model.hmr");
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str+"\n");
            }
            br.close();
            return sb.toString();
        }
        catch (Exception e){
            if (Aware.DEBUG)
                Log.e(Aware.TAG, e.getMessage());
            return null;
        }
    }
}
