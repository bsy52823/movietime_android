package com.example.movietime;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class ReserveCompleteActivity extends AppCompatActivity {

    private String movieTitle;
    private int moviePoster;
    private int ageLimit = 0;
    private int movieIndex = -1;
    private String showtime;
    private int adultCount;
    private int youthCount;
    private int preferentialCount;
    private int seniorCount;
    private int totalPrice;
    private String selectedSeats;

    // UI 필드
    private ImageView imgCompletePoster;
    private TextView txtCompleteMovieTitle;
    private ImageView imgCompleteAgeLimit;
    private TextView txtCompleteDateTime;
    private TextView txtCompletePeopleSeats;
    private TextView txtCompleteTotalPrice;
    private Button btnCheckReservation;
    private ImageButton btnGoHome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_complete);

        getPaymentData();
        bindViews();
        displayData();
        setupListener();
    }

    private void getPaymentData() {
        Intent intent = getIntent();
        if (intent != null) {
            // 필수 정보
            movieTitle = intent.getStringExtra("movie_title");
            moviePoster = intent.getIntExtra("movie_poster", R.drawable.movie1);
            ageLimit = intent.getIntExtra("age_limit", 0);

            movieIndex = intent.getIntExtra("movie_index", -1);
            showtime = intent.getStringExtra("showtime");
            selectedSeats = intent.getStringExtra("selected_seats");
            totalPrice = intent.getIntExtra("total_price", 0);

            adultCount = intent.getIntExtra("adult_count", 0);
            youthCount = intent.getIntExtra("youth_count", 0);
            preferentialCount = intent.getIntExtra("preferential_count", 0);
            seniorCount = intent.getIntExtra("senior_count", 0);
        }
    }

    private void bindViews() {
        imgCompletePoster = findViewById(R.id.imgCompletePoster);
        txtCompleteMovieTitle = findViewById(R.id.txtCompleteMovieTitle);
        imgCompleteAgeLimit = findViewById(R.id.imgCompleteAgeLimit);

        txtCompleteDateTime = findViewById(R.id.txtCompleteDateTime);
        txtCompletePeopleSeats = findViewById(R.id.txtCompletePeopleSeats);
        txtCompleteTotalPrice = findViewById(R.id.txtCompleteTotalPrice);
        btnCheckReservation = findViewById(R.id.btnCheckReservation);
        btnGoHome = findViewById(R.id.btnGoHome);
    }

    private void displayData() {
        // 포스터 및 제목
        imgCompletePoster.setImageResource(moviePoster);
        txtCompleteMovieTitle.setText(movieTitle);

        // 연령가 (이미지) 표시
        if (imgCompleteAgeLimit != null && ageLimit != 0) {
            imgCompleteAgeLimit.setImageResource(ageLimit);
        }

        // 날짜 시간
        txtCompleteDateTime.setText(showtime);

        // 인원 및 좌석 정보
        StringBuilder peopleSummary = new StringBuilder();

        // 1. 성인
        if (adultCount > 0) peopleSummary.append("성인 ").append(adultCount).append("명");

        // 2. 청소년
        if (youthCount > 0) {
            if (peopleSummary.length() > 0) peopleSummary.append(", ");
            peopleSummary.append("청소년 ").append(youthCount).append("명");
        }

        // 3. 경로
        if (seniorCount > 0) {
            if (peopleSummary.length() > 0) peopleSummary.append(", ");
            peopleSummary.append("경로 ").append(seniorCount).append("명");
        }

        // 4. 우대
        if (preferentialCount > 0) {
            if (peopleSummary.length() > 0) peopleSummary.append(", ");
            peopleSummary.append("우대 ").append(preferentialCount).append("명");
        }

        // 최종 텍스트 설정: 인원 요약 | 좌석 목록
        txtCompletePeopleSeats.setText(peopleSummary.toString() + " | " + selectedSeats);

        // 결제 금액
        String formattedPrice = String.format("총 %,d원 결제", totalPrice);
        txtCompleteTotalPrice.setText(formattedPrice);
    }

    private void setupListener() {

        // 1. 예매내역 확인 버튼 로직: MainActivity로 이동하며 '예매내역' 탭 선택 및 예매 정보 전달
        btnCheckReservation.setOnClickListener(v -> {
            Toast.makeText(this, "예매 내역을 확인합니다.", Toast.LENGTH_SHORT).show();
            if (movieIndex != -1 && selectedSeats != null && !selectedSeats.isEmpty()) {
                // 좌석 문자열("A1, B2, C3")을 리스트로 변환
                List<String> seatsList = Arrays.asList(selectedSeats.split(", "));

                // MainActivity의 static 메서드를 호출하여 좌석 정보 저장
                MainActivity.addReservedSeats(movieIndex, seatsList);
            }
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("select_tab", "reserve_list");
            intent.putExtra("reserved_movie_title", movieTitle);

            if (movieIndex != -1) {
                intent.putExtra("reserved_movie_index", movieIndex);

                // 모든 예매 상세 정보를 MainActivity로 전
                intent.putExtra("reserved_showtime", showtime);
                intent.putExtra("reserved_seats", selectedSeats);
                intent.putExtra("reserved_total_price", totalPrice);
                intent.putExtra("reserved_adult_count", adultCount);
                intent.putExtra("reserved_youth_count", youthCount);
                intent.putExtra("reserved_preferential_count", preferentialCount);
                intent.putExtra("reserved_senior_count", seniorCount);
                // --------------------------------------------------
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // 2. 홈 버튼 로직: MainActivity 기본 화면으로 이동
        if (btnGoHome != null) {
            btnGoHome.setOnClickListener(v -> {
                Toast.makeText(this, "홈 화면으로 이동합니다.", Toast.LENGTH_SHORT).show();

                Intent homeIntent = new Intent(this, MainActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(homeIntent);
                finish();
            });
        }
    }
}