package com.example.movietime;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReserveSeatActivity extends AppCompatActivity {
    private final String TAG = "ReserveSeatActivity";

    // 전달받은 데이터 필드
    private int totalPeople;
    private int adultCount;
    private int youthCount;
    private int preferentialCount = 0;
    private int seniorCount = 0;
    private String movieTitle;
    private int moviePoster;
    private int ageLimit;
    private int movieIndex;

    private String showtime;
    private String screenName;

    // 가격 설정
    private final int ADULT_PRICE = 14000;
    private final int YOUTH_PRICE = 11000;
    private final int PREFERENTIAL_PRICE = 5000;
    private final int SENIOR_PRICE = 7000;

    // UI 필드
    private GridLayout seatMapLayout;
    private Button btnNextStep;
    private Button btnChangePeople;
    private ImageButton btnBack;
    private ImageButton btnHome;
    private LinearLayout layoutPaymentContainer;
    private TextView txtSelectedSeatsList;
    private TextView txtPeopleSummary;
    private TextView txtTotalPrice;
    private TextView txtMovieTitle;

    // 좌석 관리 필드
    private List<String> selectedSeats = new ArrayList<>();
    private final int NUM_ROWS = 7;
    private final int NUM_COLS = 9;

    private List<String> currentMovieReservedSeats = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_seat);

        getReservationData();

        currentMovieReservedSeats = MainActivity.getReservedSeats(movieIndex);

        bindViews();
        displayData();
        setupButtonListeners();
        initializeSeatMap();
        updateNextButtonState();
    }

    private void getReservationData() {
        Intent intent = getIntent();
        if (intent != null) {
            totalPeople = intent.getIntExtra("total_people", 0);
            movieTitle = intent.getStringExtra("movie_title");
            moviePoster = intent.getIntExtra("movie_poster", 0);
            ageLimit = intent.getIntExtra("age_limit", 0);
            adultCount = intent.getIntExtra("adult_count", 0);
            youthCount = intent.getIntExtra("youth_count", 0);
            preferentialCount = intent.getIntExtra("preferential_count", 0);
            seniorCount = intent.getIntExtra("senior_count", 0);

            showtime = intent.getStringExtra("showtime");
            screenName = intent.getStringExtra("screen_name");

            movieIndex = intent.getIntExtra("movie_index", -1);


            if (totalPeople == 0) totalPeople = 1;
        }
    }

    private void bindViews() {
        btnBack = findViewById(R.id.btnBack);
        btnHome = findViewById(R.id.btnHome);

        seatMapLayout = findViewById(R.id.seatMapLayout);
        btnChangePeople = findViewById(R.id.btnChangePeople);
        txtMovieTitle = findViewById(R.id.txtMovieTitle);

        txtSelectedSeatsList = findViewById(R.id.txtSelectedSeatsList);
        txtPeopleSummary = findViewById(R.id.txtPeopleSummary);

        layoutPaymentContainer = findViewById(R.id.layoutPaymentContainer);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        btnNextStep = findViewById(R.id.btnNextStep);
    }

    private void displayData() {
        if (movieTitle != null) {
            txtMovieTitle.setText(movieTitle);
        }

        StringBuilder summary = new StringBuilder();
        if (adultCount > 0) summary.append("성인 ").append(adultCount).append("명 ");
        if (youthCount > 0) summary.append("청소년 ").append(youthCount).append("명 ");

        if (summary.length() == 0) {
            summary.append("인원 정보 없음");
        }
        txtPeopleSummary.setText(summary.toString().trim());

        updateSelectedSeatsDisplay();
        layoutPaymentContainer.setVisibility(View.GONE);
    }

    // --- 좌석 맵 로직 ---
    private String getSeatId(int row, int col) {
        char rowChar = (char) ('A' + row);
        return rowChar + String.valueOf(col + 1);
    }

    private boolean isSeatReserved(String seatId) {
        // 임시 목록 대신 실제 예약된 좌석 목록을 참조하도록 변경
        return currentMovieReservedSeats.contains(seatId);
    }

    private void initializeSeatMap() {
        seatMapLayout.setRowCount(NUM_ROWS);
        seatMapLayout.setColumnCount(NUM_COLS);

        int buttonSizeDp = 35;
        float density = getResources().getDisplayMetrics().density;
        int buttonSizePx = (int) (buttonSizeDp * density);

        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {
                String seatId = getSeatId(row, col);

                Button seatButton = createSeatButton(seatId);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = buttonSizePx;
                params.height = buttonSizePx;
                params.setMargins( (int)(3 * density), (int)(3 * density), (int)(3 * density), (int)(3 * density));
                seatButton.setLayoutParams(params);

                if (isSeatReserved(seatId)) {
                    seatButton.setBackgroundResource(R.drawable.rounded_seat_reserved);
                    seatButton.setEnabled(false);
                    seatButton.setTextColor(Color.WHITE);
                } else {
                    seatButton.setBackgroundResource(R.drawable.rounded_seat_available);
                    seatButton.setEnabled(true);
                    seatButton.setTextColor(Color.BLACK);
                }

                seatMapLayout.addView(seatButton);
            }
        }
    }

    private Button createSeatButton(String seatId) {
        Button button = new Button(this);
        button.setText(seatId);
        button.setTextSize(10);
        button.setPadding(0, 0, 0, 0);

        button.setBackgroundResource(R.drawable.rounded_seat_available);

        button.setOnClickListener(v -> toggleSeatSelection(seatId, (Button) v));
        return button;
    }


    private void toggleSeatSelection(String seatId, Button seatButton) {
        if (seatButton.isEnabled()) {
            if (selectedSeats.contains(seatId)) {
                selectedSeats.remove(seatId);
                seatButton.setBackgroundResource(R.drawable.rounded_seat_available);
                seatButton.setTextColor(Color.BLACK);
            } else {
                if (selectedSeats.size() < totalPeople) {
                    selectedSeats.add(seatId);
                    seatButton.setBackgroundResource(R.drawable.rounded_seat_selected);
                    seatButton.setTextColor(Color.WHITE);
                } else {
                    Toast.makeText(this, "총 인원 " + totalPeople + "석만 선택할 수 있습니다.", Toast.LENGTH_SHORT).show();
                }
            }
            updateSelectedSeatsDisplay();
            updateNextButtonState();
            updatePaymentInfo();
        }
    }
    // --- UI 업데이트 및 결제 (기존 유지) ---

    private void updateSelectedSeatsDisplay() {
        if (selectedSeats.isEmpty()) {
            txtSelectedSeatsList.setText("좌석을 선택해주세요.");
            txtSelectedSeatsList.setTextColor(Color.GRAY);
        } else {
            String seatsList = String.join(", ", selectedSeats);
            txtSelectedSeatsList.setText(seatsList);
            txtSelectedSeatsList.setTextColor(Color.BLACK);
        }
    }

    private void updatePaymentInfo() {
        int currentCount = selectedSeats.size();

        if (currentCount > 0) {
            layoutPaymentContainer.setVisibility(View.VISIBLE);

            int totalPrice = calculateTotalPrice(currentCount);
            txtTotalPrice.setText(String.format("총 %,d원", totalPrice));

        } else {
            layoutPaymentContainer.setVisibility(View.GONE);
        }
    }

    private int calculateTotalPrice(int selectedCount) {
        int price = 0;
        int remainingSeats = selectedCount;

        // 성인 가격 우선 적용
        int adults = Math.min(adultCount, remainingSeats);
        price += adults * ADULT_PRICE;
        remainingSeats -= adults;

        // 청소년 가격 적용
        int youth = Math.min(youthCount, remainingSeats);
        price += youth * YOUTH_PRICE;
        remainingSeats -= youth;

        // 경로 가격 적용
        int senior = Math.min(seniorCount, remainingSeats);
        price += senior * SENIOR_PRICE;
        remainingSeats -= senior;

        // 우대 가격 적용
        int preferential = Math.min(preferentialCount, remainingSeats);
        price += preferential * PREFERENTIAL_PRICE;
        remainingSeats -= preferential;

        if (remainingSeats > 0) {
            price += remainingSeats * ADULT_PRICE;
        }

        return price;
    }

    private void updateNextButtonState() {
        boolean isComplete = selectedSeats.size() == totalPeople;
        if (btnNextStep != null) {
            btnNextStep.setEnabled(isComplete);
            btnNextStep.setAlpha(isComplete ? 1.0f : 0.5f);
        }
    }

    private void setupButtonListeners() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
        if (btnHome != null) {
            btnHome.setOnClickListener(v -> navigateToHome());
        }

        if (btnChangePeople != null) {
            btnChangePeople.setOnClickListener(v -> navigateToTicketCount());
        }

        if (btnNextStep != null) {
            btnNextStep.setOnClickListener(v -> {
                if (selectedSeats.size() == totalPeople) {
                    // 예매 완료 페이지로 이동하는 메서드 호출
                    navigateToReserveComplete();
                }
            });
        }
    }

    private void navigateToHome() {
        Toast.makeText(this, "홈 화면으로 이동합니다.", Toast.LENGTH_SHORT).show();

        Intent homeIntent = new Intent(ReserveSeatActivity.this, MainActivity.class);
        // 이전 Activity 스택을 모두 제거하고 새 작업을 시작합니다.
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
        finish();
    }

    private void navigateToTicketCount() {
        Toast.makeText(this, "인원 변경을 위해 이전 화면으로 돌아갑니다.", Toast.LENGTH_SHORT).show();
        finish();
    }

    // 예매 완료 페이지로 이동하는 메서드
    private void navigateToReserveComplete() {
        Intent intent = new Intent(ReserveSeatActivity.this, ReserveCompleteActivity.class);

        // 1. 최종 데이터 준비
        String seatsList = String.join(", ", selectedSeats);
        int totalPrice = calculateTotalPrice(selectedSeats.size());

        // 2. 인텐트에 데이터 추가 (영화/상영 정보)
        intent.putExtra("movie_title", movieTitle);
        intent.putExtra("movie_poster", moviePoster);
        intent.putExtra("age_limit", ageLimit);
        intent.putExtra("showtime", showtime);
        intent.putExtra("screen_name", screenName);

        // 인원수 및 좌석/가격 정보
        intent.putExtra("selected_seats", seatsList);
        intent.putExtra("total_price", totalPrice);
        intent.putExtra("adult_count", adultCount);
        intent.putExtra("youth_count", youthCount);
        intent.putExtra("preferential_count", preferentialCount);
        intent.putExtra("senior_count", seniorCount);

        // 영화의 원본 인덱스를 ReserveCompleteActivity로 전달
        if (movieIndex != -1) {
            intent.putExtra("movie_index", movieIndex);
        }

        // 3. Activity 시작 및 현재 Activity 종료
        startActivity(intent);
        finish();
    }
}