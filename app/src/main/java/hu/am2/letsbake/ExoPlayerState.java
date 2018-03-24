package hu.am2.letsbake;


import android.os.Parcel;
import android.os.Parcelable;

public class ExoPlayerState implements Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.windowIndex);
        dest.writeLong(this.position);
        dest.writeByte(this.playWhenReady ? (byte) 1 : (byte) 0);
    }

    protected ExoPlayerState(Parcel in) {
        this.windowIndex = in.readInt();
        this.position = in.readLong();
        this.playWhenReady = in.readByte() != 0;
    }

    public static final Parcelable.Creator<ExoPlayerState> CREATOR = new Parcelable.Creator<ExoPlayerState>() {
        @Override
        public ExoPlayerState createFromParcel(Parcel source) {
            return new ExoPlayerState(source);
        }

        @Override
        public ExoPlayerState[] newArray(int size) {
            return new ExoPlayerState[size];
        }
    };
}
