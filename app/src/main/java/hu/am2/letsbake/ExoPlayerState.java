package hu.am2.letsbake;


public class ExoPlayerState {
    private final int windowIndex;
    private final long position;
    private final boolean playWhenReady;

    public ExoPlayerState(int windowIndex, long position, boolean playWhenReady) {
        this.windowIndex = windowIndex;
        this.position = position;
        this.playWhenReady = playWhenReady;
    }

    public int getWindowIndex() {
        return windowIndex;
    }

    public long getPosition() {
        return position;
    }

    public boolean isPlayWhenReady() {
        return playWhenReady;
    }

}
