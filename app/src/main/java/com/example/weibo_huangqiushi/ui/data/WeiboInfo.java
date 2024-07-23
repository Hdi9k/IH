package com.example.weibo_huangqiushi.ui.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class WeiboInfo implements Parcelable {
    private Long weiboId;
    private Long userId;
    private String username;
    private String avatar;
    private String title;
    private String videoUrl;
    private String poster;
    private List<String> images;
    private String imagesJson;
    private Integer likeCount;
    private Boolean likeFlag;
    private Integer commentCount;
    private Integer category;
    private String createTime;

    public WeiboInfo(Long id, Long userId, String username, String avatar, String title, String videoUrl, String poster, List<String> images, String imagesJson, int likeCount, Boolean likeFlag, Integer commentCount, Integer category, String createTime) {
        this.weiboId = id;
        this.userId = userId;
        this.username = username;
        this.avatar = avatar;
        this.title = title;
        this.videoUrl = videoUrl;
        this.poster = poster;
        this.images = images;
        this.imagesJson = imagesJson;
        this.likeCount = likeCount;
        this.likeFlag = likeFlag;
        this.commentCount = commentCount;
        this.category = category;
        this.createTime = createTime;
    }
    public WeiboInfo(String title, List<String> images, Integer category){
        this.title = title;
        this.images = images;
        this.category=category;
    }
    public WeiboInfo(String title, Integer category){
        this.title = title;
        this.images = images;
        this.category=category;
    }

    public Long getWeiboId() {
        return weiboId;
    }
    public void setWeiboId(Long id) {
        this.weiboId = id;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getVideoUrl() {
        return videoUrl;
    }
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
    public String getPoster() {
        return poster;
    }
    public void setPoster(String poster) {
        this.poster = poster;
    }
    public List<String> getImages() {
        return images;
    }
    public String getImagesJson() {
        return imagesJson;
    }
    public void setImagesJson(String imagesJson) {
        this.imagesJson = imagesJson;
    }
    public int getImageCount(){
        if(images!=null){
            return images.size();
        }else return 0;
    }
    public void setImages(List<String> images) {
        this.images = images;
    }
    public int getLikeCount() {
        return likeCount;
    }
    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
    public void addLikeCount(){
        this.likeCount++;
    }
    public void decLikeCount(){
        if (this.likeCount!=0)
            this.likeCount--;
    }
    public Boolean getLikeFlag() {
        return likeFlag;
    }
    public void setLikeFlag(Boolean likeFlag) {
        this.likeFlag = likeFlag;
    }
    public Integer getCommentCount() {
        return commentCount;
    }
    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }
    public Integer getCategory() {
        return category;
    }
    public void setCategory(Integer category) {
        this.category = category;
    }
    public String getCreateTime() {
        return createTime;
    }
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    // Parcelable implementation
    protected WeiboInfo(Parcel in) {
        if (in.readByte() == 0) {
            weiboId = null;
        } else {
            weiboId = in.readLong();
        }
        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readLong();
        }
        username = in.readString();
        avatar = in.readString();
        title = in.readString();
        videoUrl = in.readString();
        poster = in.readString();
        images = in.createStringArrayList();
        imagesJson = in.readString();
        if (in.readByte() == 0) {
            likeCount = null;
        } else {
            likeCount = in.readInt();
        }
        byte tmpLikeFlag = in.readByte();
        likeFlag = tmpLikeFlag == 0 ? null : tmpLikeFlag == 1;
        if (in.readByte() == 0) {
            commentCount = null;
        } else {
            commentCount = in.readInt();
        }
        if (in.readByte() == 0) {
            category = null;
        } else {
            category = in.readInt();
        }
        createTime = in.readString();
    }

    public static final Creator<WeiboInfo> CREATOR = new Creator<WeiboInfo>() {
        @Override
        public WeiboInfo createFromParcel(Parcel in) {
            return new WeiboInfo(in);
        }

        @Override
        public WeiboInfo[] newArray(int size) {
            return new WeiboInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        if (weiboId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(weiboId);
        }
        if (userId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(userId);
        }
        dest.writeString(username);
        dest.writeString(avatar);
        dest.writeString(title);
        dest.writeString(videoUrl);
        dest.writeString(poster);
        dest.writeStringList(images);
        dest.writeString(imagesJson);
        if (likeCount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(likeCount);
        }
        dest.writeByte((byte) (likeFlag == null ? 0 : likeFlag ? 1 : 2));
        if (commentCount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(commentCount);
        }
        if (category == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(category);
        }
        dest.writeString(createTime);
    }
}
