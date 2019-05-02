package agh.heart.actions;
import android.util.Log;

import heart.Action;
import heart.State;

public class HowAreYou_NoAction implements Action {

    private static final String TAG = "AWARE::HowAreYou_Color::HeaRT";

    @Override
    public void execute(State state) {
        Log.d(TAG, "Execute action: NoAction");
    }
}
