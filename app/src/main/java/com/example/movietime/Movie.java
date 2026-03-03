package com.example.movietime;

import java.io.Serializable;

/**
 * 영화 데이터를 표현하는 모델 클래스입니다.
 * Intent를 통해 객체를 전달하기 위해 Serializable을 구현합니다.
 */
public class Movie implements Serializable {
    private int movieId;
    private String title;
    private String info; // 간단한 정보 (러닝타임, 장르 등)
    private String synopsis; // 영화 줄거리
    private int ageLimitResId; // 연령 제한 아이콘
    private int posterResId; // 포스터 이미지
    private int likeCount; // 좋아요 수

    /**
     * Movie 객체의 생성자입니다.
     */
    public Movie(int movieId, String title, String info, String synopsis, int ageLimitResId, int posterResId, int likeCount) {
        this.movieId = movieId;
        this.title = title;
        this.info = info;
        this.synopsis = synopsis;
        this.ageLimitResId = ageLimitResId;
        this.posterResId = posterResId;
        this.likeCount = likeCount;
    }

    public int getMovieId() { return movieId; }
    public String getTitle() { return title; }
    public String getInfo() { return info; }
    public String getSynopsis() { return synopsis; }
    public int getAgeLimitResId() { return ageLimitResId; }
    public int getPosterResId() { return posterResId; }
    public int getLikeCount() { return likeCount; }

    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }
    public void setAgeLimitResId(int ageLimitResId) { this.ageLimitResId = ageLimitResId; }
    public void setPosterResId(int posterResId) { this.posterResId = posterResId; }
    // ...
}