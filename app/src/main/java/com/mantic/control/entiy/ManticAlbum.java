package com.mantic.control.entiy;

/**
 * Created by wujiangxia on 2017/5/11.
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.ximalaya.ting.android.opensdk.model.album.LastUpTrack;
import com.ximalaya.ting.android.opensdk.model.album.RecommendTrack;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;



public class ManticAlbum implements Parcelable {
    private long id;
    @SerializedName("album_title")
    private String albumTitle;
    @SerializedName("album_tags")
    private String albumTags;
    @SerializedName("album_intro")
    private String albumIntro;
    @SerializedName("cover_url_small")
    private String coverUrlSmall;
    @SerializedName("cover_url_middle")
    private String coverUrlMiddle;
    @SerializedName("cover_url_large")
    private String coverUrlLarge;
    @SerializedName("announcer")
    private Announcer announcer;
    @SerializedName("play_count")
    private long playCount;
    @SerializedName("favorite_count")
    private long favoriteCount;
    @SerializedName("include_track_count")
    private long includeTrackCount;
    @SerializedName("last_uptrack")
    private LastUpTrack lastUptrack;
    @SerializedName("recommend_track")
    private RecommendTrack recommendTrack;
    @SerializedName("updated_at")
    private long updatedAt;
    @SerializedName("created_at")
    private long createdAt;
    private long soundLastListenId;
    @SerializedName("is_finished")
    private int isFinished;
    @SerializedName("recommend_src")
    private String recommentSrc;
    @SerializedName("based_relative_album_id")
    private long basedRelativeAlbumId;
    @SerializedName("recommend_trace")
    private String recommendTrace;
    @SerializedName("share_count")
    private String shareCount;
    private List<Track> tracks;
    @SerializedName("subscribe_count")
    private long subscribeCount;
    @SerializedName("can_download")
    private boolean canDownload;
    @SerializedName("category_id")
    private int categoryId;
    public static final Creator<ManticAlbum> CREATOR = new Creator() {
        public ManticAlbum createFromParcel(Parcel source) {
            return new ManticAlbum(source);
        }

        public ManticAlbum[] newArray(int size) {
            return new ManticAlbum[size];
        }
    };

    public ManticAlbum() {
    }

    public ManticAlbum(Parcel source) {
        this.id = source.readLong();
        this.albumTitle = source.readString();
        this.albumTags = source.readString();
        this.albumIntro = source.readString();
        this.coverUrlSmall = source.readString();
        this.coverUrlMiddle = source.readString();
        this.coverUrlLarge = source.readString();
        this.announcer = (Announcer)source.readParcelable(Announcer.class.getClassLoader());
        this.playCount = source.readLong();
        this.favoriteCount = source.readLong();
        this.includeTrackCount = source.readLong();
        this.lastUptrack = (LastUpTrack)source.readParcelable(LastUpTrack.class.getClassLoader());
        this.recommendTrack = (RecommendTrack)source.readParcelable(RecommendTrack.class.getClassLoader());
        this.updatedAt = source.readLong();
        this.createdAt = source.readLong();
        this.soundLastListenId = source.readLong();
        this.isFinished = source.readInt();
        this.recommentSrc = source.readString();
        this.basedRelativeAlbumId = source.readLong();
        this.recommendTrace = source.readString();
        this.shareCount = source.readString();
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAlbumTitle() {
        return this.albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public String getAlbumTags() {
        return this.albumTags;
    }

    public void setAlbumTags(String albumTags) {
        this.albumTags = albumTags;
    }

    public String getAlbumIntro() {
        return this.albumIntro;
    }

    public void setAlbumIntro(String albumIntro) {
        this.albumIntro = albumIntro;
    }

    public String getCoverUrlSmall() {
        return this.coverUrlSmall;
    }

    public void setCoverUrlSmall(String coverUrlSmall) {
        this.coverUrlSmall = coverUrlSmall;
    }

    public String getCoverUrlMiddle() {
        return this.coverUrlMiddle;
    }

    public void setCoverUrlMiddle(String coverUrlMiddle) {
        this.coverUrlMiddle = coverUrlMiddle;
    }

    public String getCoverUrlLarge() {
        return this.coverUrlLarge;
    }

    public void setCoverUrlLarge(String coverUrlLarge) {
        this.coverUrlLarge = coverUrlLarge;
    }

    public Announcer getAnnouncer() {
        return this.announcer;
    }

    public void setAnnouncer(Announcer announcer) {
        this.announcer = announcer;
    }

    public long getPlayCount() {
        return this.playCount;
    }

    public void setPlayCount(long playCount) {
        this.playCount = playCount;
    }

    public long getFavoriteCount() {
        return this.favoriteCount;
    }

    public void setFavoriteCount(long favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public long getIncludeTrackCount() {
        return this.includeTrackCount;
    }

    public void setIncludeTrackCount(long includeTrackCount) {
        this.includeTrackCount = includeTrackCount;
    }

    public LastUpTrack getLastUptrack() {
        return this.lastUptrack;
    }

    public void setLastUptrack(LastUpTrack lastUptrack) {
        this.lastUptrack = lastUptrack;
    }

    public RecommendTrack getRecommendTrack() {
        return this.recommendTrack;
    }

    public void setRecommendTrack(RecommendTrack recommendTrack) {
        this.recommendTrack = recommendTrack;
    }

    public long getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public long getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getSoundLastListenId() {
        return this.soundLastListenId;
    }

    public void setSoundLastListenId(long soundLastListenId) {
        this.soundLastListenId = soundLastListenId;
    }

    public int getIsFinished() {
        return this.isFinished;
    }

    public void setIsFinished(int isFinished) {
        this.isFinished = isFinished;
    }

    public String getRecommentSrc() {
        return this.recommentSrc;
    }

    public void setRecommentSrc(String recommentSrc) {
        this.recommentSrc = recommentSrc;
    }

    public long getBasedRelativeAlbumId() {
        return this.basedRelativeAlbumId;
    }

    public void setBasedRelativeAlbumId(long basedRelativeAlbumId) {
        this.basedRelativeAlbumId = basedRelativeAlbumId;
    }

    public String getRecommendTrace() {
        return this.recommendTrace;
    }

    public void setRecommendTrace(String recommendTrace) {
        this.recommendTrace = recommendTrace;
    }

    public String getShareCount() {
        return this.shareCount;
    }

    public void setShareCount(String shareCount) {
        this.shareCount = shareCount;
    }

    public List<Track> getTracks() {
        return this.tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    public String toString() {
        return "ManticAlbum [id=" + this.id + ", albumTitle=" + this.albumTitle + ", albumTags=" + this.albumTags + ", albumIntro=" + this.albumIntro + ", coverUrlSmall=" + this.coverUrlSmall + ", coverUrlMiddle=" + this.coverUrlMiddle + ", coverUrlLarge=" + this.coverUrlLarge + ", announcer=" + this.announcer + ", playCount=" + this.playCount + ", favoriteCount=" + this.favoriteCount + ", includeTrackCount=" + this.includeTrackCount + ", lastUptrack=" + this.lastUptrack + ", recommendTrack=" + this.recommendTrack + ", updatedAt=" + this.updatedAt + ", createdAt=" + this.createdAt + ", soundLastListenId=" + this.soundLastListenId + ", isFinished=" + this.isFinished + ", recommentSrc=" + this.recommentSrc + ", basedRelativeAlbumId=" + this.basedRelativeAlbumId + ", recommendTrace=" + this.recommendTrace + ", shareCount=" + this.shareCount + "]";
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.albumTitle);
        dest.writeString(this.albumTags);
        dest.writeString(this.albumIntro);
        dest.writeString(this.coverUrlSmall);
        dest.writeString(this.coverUrlMiddle);
        dest.writeString(this.coverUrlLarge);
        dest.writeParcelable(this.announcer, flags);
        dest.writeLong(this.playCount);
        dest.writeLong(this.favoriteCount);
        dest.writeLong(this.includeTrackCount);
        dest.writeParcelable(this.lastUptrack, flags);
        dest.writeParcelable(this.recommendTrack, flags);
        dest.writeLong(this.updatedAt);
        dest.writeLong(this.soundLastListenId);
        dest.writeLong(this.createdAt);
        dest.writeInt(this.isFinished);
        dest.writeString(this.recommentSrc);
        dest.writeLong(this.basedRelativeAlbumId);
        dest.writeString(this.recommendTrace);
        dest.writeString(this.shareCount);
    }

    public String getValidCover() {
        return !TextUtils.isEmpty(this.coverUrlMiddle)?this.coverUrlMiddle:(!TextUtils.isEmpty(this.coverUrlSmall)?this.coverUrlSmall:(!TextUtils.isEmpty(this.coverUrlLarge)?this.coverUrlLarge:""));
    }

    public boolean isCanDownload() {
        return this.canDownload;
    }

    public void setCanDownload(boolean canDownload) {
        this.canDownload = canDownload;
    }

    public long getSubscribeCount() {
        return this.subscribeCount;
    }

    public void setSubscribeCount(long subscribeCount) {
        this.subscribeCount = subscribeCount;
    }

    public int getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}
