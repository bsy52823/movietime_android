package com.example.movietime;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 영화의 상세 정보를 표시하고, 좋아요 및 예매 기능을 처리하는 액티비티.
 * MainActivity에서 영화 데이터를 Intent로 전달받습니다.
 */
public class MovieDetailActivity extends AppCompatActivity {
    // 영화 데이터 필드
    private int movieIndex;
    private String movieTitle;
    private int likeCount;
    private boolean isLiked;
    private String movieSynopsis;
    private String trailerUrl;

    // UI View 요소
    private ImageView imgLikeIcon;
    private ImageButton btnBack;
    private ImageButton btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // 1. Intent로 전달받은 영화 데이터 로드
        getMovieDataFromIntent();

        // 2. View 초기화 및 데이터 바인딩
        initViews();
        bindMovieData();

        // 3. 클릭 리스너 설정
        setupClickListeners();
    }

    /**
     * MainActivity로부터 Intent 데이터를 수신하고 멤버 변수에 저장합니다.
     */
    private void getMovieDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            // 필수 데이터
            movieIndex = intent.getIntExtra("index", -1);
            movieTitle = intent.getStringExtra("title");

            // UI 바인딩에 사용되는 데이터
            int posterResId = intent.getIntExtra("poster", 0);
            int ageLimitResId = intent.getIntExtra("ageLimit", 0);
            String detailInfo = intent.getStringExtra("detailInfo");

            // 상태 데이터
            isLiked = intent.getBooleanExtra("isLiked", false);
            likeCount = intent.getIntExtra("likes", 0);
            movieSynopsis = intent.getStringExtra("synopsis");
            trailerUrl = intent.getStringExtra("trailerUrl");

            // 인덱스가 유효하지 않으면 액티비티 종료
            if (movieIndex == -1) {
                Toast.makeText(this, "잘못된 접근입니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    /**
     * 레이아웃의 View 요소를 초기화하고 바인딩합니다.
     */
    private void initViews() {
        // 좋아요 아이콘과 카운트 (btnLikeContainer 내부)
        imgLikeIcon = findViewById(R.id.btnLikeContainer).findViewById(R.id.imgLikeIcon);

        // 상단 내비게이션 버튼
        btnBack = findViewById(R.id.btnBack);
        btnHome = findViewById(R.id.btnHome);

        // NestedScrollView 내부의 줄거리 TextView는 bindMovieData에서 직접 찾습니다.
    }

    /**
     * 수신된 영화 데이터를 View에 표시합니다.
     */
    private void bindMovieData() {
        // 포스터, 제목, 연령가, 상세 정보 바인딩
        ((ImageView) findViewById(R.id.imgPosterBackground)).setImageResource(getIntent().getIntExtra("poster", 0));
        ((TextView) findViewById(R.id.txtTitle)).setText(movieTitle);
        ((ImageView) findViewById(R.id.imgAgeLimitTitle)).setImageResource(getIntent().getIntExtra("ageLimit", 0));
        ((TextView) findViewById(R.id.txtDetailInfo)).setText(getIntent().getStringExtra("detailInfo"));

        // 줄거리 텍스트 바인딩
        TextView txtSynopsis = findViewById(R.id.detail_scroll_view).findViewById(R.id.txtSynopsis);
        if (txtSynopsis != null && movieSynopsis != null) {
            txtSynopsis.setText(movieSynopsis);
        } else {
            txtSynopsis.setText("영화에 대한 줄거리 정보가 없습니다.");
        }

        // 좋아요 상태 및 수 표시 업데이트
        updateLikeDisplay();
    }

    /**
     * 좋아요 아이콘과 좋아요 카운트 텍스트를 현재 상태(isLiked, likeCount)에 맞게 업데이트합니다.
     */
    private void updateLikeDisplay() {
        TextView txtLikeCount = findViewById(R.id.btnLikeContainer).findViewById(R.id.txtLikeCount);

        if (isLiked) {
            imgLikeIcon.setImageResource(R.drawable.ic_heart_filled);
        } else {
            imgLikeIcon.setImageResource(R.drawable.ic_heart_unfilled);
        }
        txtLikeCount.setText(String.valueOf(likeCount));
    }

    /**
     * 모든 UI 요소의 클릭 리스너를 설정합니다.
     */
    private void setupClickListeners() {
        // 뒤로가기 버튼: 변경된 좋아요 상태를 MainActivity에 반환 후 종료
        btnBack.setOnClickListener(v -> finishAndReturnResult());

        // 홈 버튼: MainActivity로 이동 (기존 스택 클리어)
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        // 좋아요 버튼 클릭: 좋아요 상태 토글 및 카운트 업데이트
        findViewById(R.id.btnLikeContainer).setOnClickListener(v -> {
            isLiked = !isLiked;
            if (isLiked) {
                likeCount++;
                Toast.makeText(this, "보관함에 저장되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                likeCount--;
                if (likeCount < 0) likeCount = 0; // 음수 방지
                Toast.makeText(this, "보관함에서 제거되었습니다.", Toast.LENGTH_SHORT).show();
            }
            updateLikeDisplay(); // UI 즉시 업데이트
        });

        // 예매하기 버튼: ReserveActivity로 이동하며 필요한 데이터 전달
        findViewById(R.id.btnReserveDetail).setOnClickListener(v -> {
            Intent reserveIntent = new Intent(this, ReserveActivity.class);

            // ReserveActivity에 영화 정보 전달
            reserveIntent.putExtra("title", movieTitle);
            reserveIntent.putExtra("poster", getIntent().getIntExtra("poster", 0));
            reserveIntent.putExtra("detailInfo", getIntent().getStringExtra("detailInfo"));
            reserveIntent.putExtra("ageLimit", getIntent().getIntExtra("ageLimit", 0));
            reserveIntent.putExtra("synopsis", movieSynopsis); // 줄거리 전달
            reserveIntent.putExtra("index", movieIndex);       // 영화 인덱스 전달

            startActivity(reserveIntent);
        });

        // 예고편 버튼 클릭 리스너
        findViewById(R.id.btnPlayTrailerText).setOnClickListener(v -> {
            playTrailer(); // 예고편 재생 메서드 호출
        });
    }

    /**
     * 예고편 URL을 사용하여 YouTube 앱 또는 웹 브라우저를 실행합니다.
     */
    private void playTrailer() {
        if (trailerUrl != null && !trailerUrl.isEmpty()) {
            try {
                // 암시적 Intent를 사용하여 URL 열기
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "예고편을 재생할 앱을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, movieTitle + " 예고편 링크가 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Activity가 종료될 때, 변경된 좋아요 상태(index, liked, likes)를 MainActivity로 반환합니다.
     */
    private void finishAndReturnResult() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("index", movieIndex);
        resultIntent.putExtra("liked", isLiked);
        resultIntent.putExtra("likes", likeCount);

        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    /**
     * 시스템 뒤로가기 버튼 오버라이드: 결과를 MainActivity로 반환하도록 처리
     */
    @Override
    public void onBackPressed() {
        finishAndReturnResult();
        super.onBackPressed();
    }
}