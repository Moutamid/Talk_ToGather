package com.moutamid.talk_togather;

import android.app.Application;

import com.moutamid.talk_togather.model.CurrentUserSettings;
import com.moutamid.talk_togather.model.WorkerThread;

public class AGApplication extends Application {

    private WorkerThread mWorkerThread;


    public synchronized void initWorkerThread() {
        if (mWorkerThread == null) {
            mWorkerThread = new WorkerThread(getApplicationContext());
            mWorkerThread.start();

            mWorkerThread.waitForReady();
        }
    }

    public synchronized WorkerThread getWorkerThread() {
        return mWorkerThread;
    }

    public synchronized void deInitWorkerThread() {
        mWorkerThread.exit();
        try {
            mWorkerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mWorkerThread = null;
    }

    public static final CurrentUserSettings mAudioSettings = new CurrentUserSettings();
}
