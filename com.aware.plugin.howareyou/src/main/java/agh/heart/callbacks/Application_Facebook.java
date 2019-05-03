package agh.heart.callbacks;

import heart.Callback;

public class Application_Facebook extends Application_Generic implements Callback {
    private static final String APPLICATION_NAME = "facebook";
    private static final String APPLICATION_PACKAGE = "com.facebook.katana";

    public Application_Facebook(){
        super(APPLICATION_NAME, APPLICATION_PACKAGE);
    }
}