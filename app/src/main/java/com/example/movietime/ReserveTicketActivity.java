package com.example.movietime;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ReserveTicketActivity extends AppCompatActivity {
    private static final String TAG = "ReserveTicketActivity";

    // 인텐트에서 받은 상영 정보
    private String movieTitle;
    private int moviePoster;
    private int ageLimit;
    private String showtime;
    private String screenName;
    private int movieIndex;

    // 인원수 상태 필드
    private int adultCount = 0;
    private int youthCount = 0;
    private int preferentialCount = 0;
    private int seniorCount = 0;
    private int totalPeople = 0;
    private final int MAX_PEOPLE = 8; // 최대 인원수 제한

    // UI 필드
    private ImageButton btnBack, btnClose;
    private TextView txtMovieTitle, txtShowtime;
    private ImageView imgAgeLimit;
    private ImageView imgMoviePoster;
    private ImageView btnDetail;
    private Button btnSeatSelection;

    // 인원 선택자 뷰 (ticket_type_selector.xml 레이아웃 포함 뷰)
    private View selectorAdult, selectorYouth, selectorPreferential, selectorSenior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_ticket);

        getReservationDataFromIntent();
        bindViews();
        displayInitialData();
        setupDetailButton();
        setupSelectorData();
        setupCounterListeners();
        setupSeatSelectionButton();
    }

    // --- 초기화 및 데이터 바인딩 ---

    private void getReservationDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            movieTitle = intent.getStringExtra("movie_title");
            moviePoster = intent.getIntExtra("movie_poster", 0);
            ageLimit = intent.getIntExtra("age_limit", 0);
            showtime = intent.getStringExtra("showtime");
            screenName = intent.getStringExtra("screen_name");

            movieIndex = intent.getIntExtra("movie_index", -1);
            Log.d(TAG, "Received Movie Index: " + movieIndex);
        }
    }

    private void bindViews() {
        btnBack = findViewById(R.id.btnBack);
        btnClose = findViewById(R.id.btnClose);
        txtMovieTitle = findViewById(R.id.txtMovieTitle);
        txtShowtime = findViewById(R.id.txtShowtime);
        imgAgeLimit = findViewById(R.id.imgAgeLimit);
        imgMoviePoster = findViewById(R.id.imgBackgroundPoster);
        btnDetail = findViewById(R.id.btnDetail);

        selectorAdult = findViewById(R.id.selectorAdult);
        selectorYouth = findViewById(R.id.selectorYouth);
        selectorPreferential = findViewById(R.id.selectorPreferential);
        selectorSenior = findViewById(R.id.selectorSenior);

        btnSeatSelection = findViewById(R.id.btnSeatSelection);
    }

    private void displayInitialData() {
        if (txtMovieTitle != null) txtMovieTitle.setText(movieTitle);
        if (txtShowtime != null) {
            txtShowtime.setText(showtime);
        }
        if (imgAgeLimit != null && ageLimit != 0) imgAgeLimit.setImageResource(ageLimit);

        if (imgMoviePoster != null && moviePoster != 0) {
            imgMoviePoster.setImageResource(moviePoster);
        }

        if (btnBack != null) btnBack.setOnClickListener(v -> finish());
        if (btnClose != null) btnClose.setOnClickListener(v -> finish());

        updateTotalCount();
        updateButtonStates();
    }
    private void setupDetailButton() {
        if (btnDetail != null) {
            btnDetail.setOnClickListener(v -> navigateToMovieDetail()); // <--- [추가] 리스너 설정
        }
    }

    private void navigateToMovieDetail() {
        if (movieIndex == -1) {
            Toast.makeText(this, "영화 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(ReserveTicketActivity.this, MovieDetailActivity.class);

        // MainActivity의 정적(static) 필드에서 상세 정보를 가져옵니다.
        // MainActivity.java를 먼저 수정해야 이 코드가 작동합니다.
        try {
            intent.putExtra("index", movieIndex);
            intent.putExtra("title", movieTitle);
            intent.putExtra("poster", moviePoster);
            intent.putExtra("ageLimit", ageLimit);

            // MainActivity에서 가져올 데이터
            intent.putExtra("detailInfo", MainActivity.details[movieIndex]);
            intent.putExtra("synopsis", MainActivity.synopses[movieIndex]);
            intent.putExtra("isLiked", MainActivity.liked[movieIndex]);
            intent.putExtra("likes", MainActivity.likes[movieIndex]);
            intent.putExtra("trailerUrl", MainActivity.adapterTrailerUrls[movieIndex]);

            startActivity(intent);
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.e(TAG, "Movie index error or MainActivity data missing: " + e.getMessage());
            Toast.makeText(this, "상세 정보를 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupSelectorData() {
        setupSelector(selectorAdult, "성인", adultCount);
        setupSelector(selectorYouth, "청소년", youthCount);
        setupSelector(selectorPreferential, "우대", preferentialCount);
        setupSelector(selectorSenior, "경로", seniorCount);
    }

    private void setupSelector(View selectorView, String typeName, int initialCount) {
        if (selectorView != null) {
            TextView txtType = selectorView.findViewById(R.id.txtTicketType);
            TextView txtCount = selectorView.findViewById(R.id.txtCount);

            if (txtType != null) txtType.setText(typeName);
            if (txtCount != null) txtCount.setText(String.valueOf(initialCount));
        }
    }


    // --- 인원수 관리 및 리스너 설정 (변경 없음) ---

    private void setupCounterListeners() {
        // Adult
        View btnIncAdult = selectorAdult.findViewById(R.id.btnIncrease);
        View btnDecAdult = selectorAdult.findViewById(R.id.btnDecrease);
        if (btnIncAdult != null) {
            btnIncAdult.setOnClickListener(v -> changeCount(0, true));
            btnIncAdult.setClickable(true);
        }
        if (btnDecAdult != null) {
            btnDecAdult.setOnClickListener(v -> changeCount(0, false));
            btnDecAdult.setClickable(true);
        }

        // Youth
        View btnIncYouth = selectorYouth.findViewById(R.id.btnIncrease);
        View btnDecYouth = selectorYouth.findViewById(R.id.btnDecrease);
        if (btnIncYouth != null) {
            btnIncYouth.setOnClickListener(v -> changeCount(1, true));
            btnIncYouth.setClickable(true);
        }
        if (btnDecYouth != null) {
            btnDecYouth.setOnClickListener(v -> changeCount(1, false));
            btnDecYouth.setClickable(true);
        }

        // Preferential
        View btnIncPref = selectorPreferential.findViewById(R.id.btnIncrease);
        View btnDecPref = selectorPreferential.findViewById(R.id.btnDecrease);
        if (btnIncPref != null) {
            btnIncPref.setOnClickListener(v -> changeCount(2, true));
            btnIncPref.setClickable(true);
        }
        if (btnDecPref != null) {
            btnDecPref.setOnClickListener(v -> changeCount(2, false));
            btnDecPref.setClickable(true);
        }

        // Senior
        View btnIncSenior = selectorSenior.findViewById(R.id.btnIncrease);
        View btnDecSenior = selectorSenior.findViewById(R.id.btnDecrease);
        if (btnIncSenior != null) {
            btnIncSenior.setOnClickListener(v -> changeCount(3, true));
            btnIncSenior.setClickable(true);
        }
        if (btnDecSenior != null) {
            btnDecSenior.setOnClickListener(v -> changeCount(3, false));
            btnDecSenior.setClickable(true);
        }
    }

    private void changeCount(int typeIndex, boolean isIncrease) {
        int currentCount = getCountByType(typeIndex);

        if (isIncrease) {
            if (totalPeople <= MAX_PEOPLE) {
                currentCount++;
            } else {
                Toast.makeText(this, "최대 " + MAX_PEOPLE + "명까지 선택 가능합니다.", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            if (currentCount > 0) {
                currentCount--;
            } else {
                return;
            }
        }

        setCountByType(typeIndex, currentCount);
        updateTotalCount();
        updateButtonStates();

        View selectorView = getSelectorViewByType(typeIndex);
        if (selectorView != null) {
            TextView txtCount = selectorView.findViewById(R.id.txtCount);
            if (txtCount != null) {
                txtCount.setText(String.valueOf(currentCount));
            }
        }
    }

    private int getCountByType(int index) {
        switch (index) {
            case 0: return adultCount;
            case 1: return youthCount;
            case 2: return preferentialCount;
            case 3: return seniorCount;
            default: return 0;
        }
    }

    private void setCountByType(int index, int count) {
        switch (index) {
            case 0: adultCount = count; break;
            case 1: youthCount = count; break;
            case 2: preferentialCount = count; break;
            case 3: seniorCount = count; break;
        }
    }

    private View getSelectorViewByType(int index) {
        switch (index) {
            case 0: return selectorAdult;
            case 1: return selectorYouth;
            case 2: return selectorPreferential;
            case 3: return selectorSenior;
            default: return null;
        }
    }

    private void updateTotalCount() {
        totalPeople = adultCount + youthCount + preferentialCount + seniorCount;
    }

    /**
     * 버튼의 활성화 상태와 투명도를 설정합니다.
     */
    private void setButtonState(View buttonView, boolean enabled) {
        buttonView.setEnabled(enabled);
        // 비활성화되면 40% 투명하게 설정하여 연하게 보이도록 함
        buttonView.setAlpha(enabled ? 1.0f : 0.4f);
    }

    /**
     * +/- 버튼 상태와 예매 버튼 상태를 업데이트합니다.
     */
    private void updateButtonStates() {
        // 총 인원 제한 (Max 8)
        boolean canIncrease = totalPeople < MAX_PEOPLE;

        // --- (+) 버튼 상태 업데이트 (총 인원 8명 미만일 때만 활성화) ---
        setButtonState(selectorAdult.findViewById(R.id.btnIncrease), canIncrease);
        setButtonState(selectorYouth.findViewById(R.id.btnIncrease), canIncrease);
        setButtonState(selectorPreferential.findViewById(R.id.btnIncrease), canIncrease);
        setButtonState(selectorSenior.findViewById(R.id.btnIncrease), canIncrease);

        // --- (-) 버튼 상태 업데이트 (해당 인원수가 0명을 초과할 때만 활성화) ---
        setButtonState(selectorAdult.findViewById(R.id.btnDecrease), adultCount > 0);
        setButtonState(selectorYouth.findViewById(R.id.btnDecrease), youthCount > 0);
        setButtonState(selectorPreferential.findViewById(R.id.btnDecrease), preferentialCount > 0);
        setButtonState(selectorSenior.findViewById(R.id.btnDecrease), seniorCount > 0);

        // 예매 버튼 활성화 (총 인원 0명 초과 시 활성화)
        boolean isPeopleSelected = totalPeople > 0;
        btnSeatSelection.setEnabled(isPeopleSelected);
        btnSeatSelection.setAlpha(isPeopleSelected ? 1.0f : 0.5f);
    }

    private void setupSeatSelectionButton() {
        if (btnSeatSelection != null) {
            btnSeatSelection.setOnClickListener(v -> {
                if (totalPeople > 0) {
                    navigateToReserveSeat();
                } else {
                    Toast.makeText(this, "인원수를 1명 이상 선택해 주세요.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void navigateToReserveSeat() {
        Intent intent = new Intent(ReserveTicketActivity.this, ReserveSeatActivity.class);

        intent.putExtra("movie_title", movieTitle);
        intent.putExtra("movie_poster", moviePoster);
        intent.putExtra("age_limit", ageLimit);
        intent.putExtra("showtime", showtime);
        intent.putExtra("screen_name", screenName);

        intent.putExtra("total_people", totalPeople);
        intent.putExtra("adult_count", adultCount);
        intent.putExtra("youth_count", youthCount);
        intent.putExtra("preferential_count", preferentialCount);
        intent.putExtra("senior_count", seniorCount);

        // 화의 원본 인덱스를 다음 액티비티로 전달
        if (movieIndex != -1) {
            intent.putExtra("movie_index", movieIndex);
        }

        Log.d(TAG, "Moving to ReserveSeatActivity. Total: " + totalPeople +
                ", Adult: " + adultCount + ", Youth: " + youthCount);

        startActivity(intent);
    }
}