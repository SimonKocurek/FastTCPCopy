import javafx.scene.control.ProgressBar;

import java.util.concurrent.atomic.AtomicLong;

class ProgressWatcher {

    private AtomicLong done;
    private long end;

    private final ProgressBar progressbar;

    ProgressWatcher(ProgressBar progressbar) {
        this.done = new AtomicLong(0);
        this.progressbar = progressbar;
    }

    public void add(long added) {
        long newDone = this.done.addAndGet(added);
        double newProgress = (double) newDone / end;

        synchronized (progressbar) {
            this.progressbar.setProgress(newProgress);
        }
    }

    public void setEnd(long end) {
        this.end = end;
    }

}
