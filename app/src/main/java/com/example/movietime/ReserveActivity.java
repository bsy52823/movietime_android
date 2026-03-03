package com.example.movietime;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;
import java.util.Map;

public class ReserveActivity extends AppCompatActivity {
    private static final String TAG = "ReserveActivity";

    // 인텐트 데이터
    private String movieTitle;
    private int moviePoster;
    private String movieDetailInfo;
    private int ageLimit;
    private int movieIndex;

    // UI 상태 관리
    private View currentlySelectedDateView = null;

    // 날짜 정보를 저장할 맵: View ID -> "YYYY.MM.DD (요일)" 형식의 문자열
    private final Map<Integer, String> dateInfoMap = new HashMap<>();

    // 현재 상영 연도 및 월
    private final String CURRENT_YEAR = "2025";
    private final String CURRENT_MONTH = "12";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve);

        getMovieDataFromIntent();
        bindMovieData();
        setupClickListeners();
        setupDateSelectionListeners();
        setupTimeSelectionListeners();
    }

    private void getMovieDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            movieTitle = intent.getStringExtra("title");
            moviePoster = intent.getIntExtra("poster", 0);
            movieDetailInfo = intent.getStringExtra("detailInfo");
            ageLimit = intent.getIntExtra("ageLimit", 0);

            // MainActivity에서 보낸 영화 인덱스를 수신
            movieIndex = intent.getIntExtra("index", -1);

            Log.d(TAG, "Received Movie Index: " + movieIndex);
        }
    }

    private void bindMovieData() {
        TextView txtScreenTitle = findViewById(R.id.txtScreenTitle);
        ImageView imgPoster = findViewById(R.id.imgReservedMoviePoster);
        TextView txtTitle = findViewById(R.id.txtReservedMovieTitle);
        ImageView imgAgeLimit = findViewById(R.id.imgReservedAgeLimit);
        TextView txtRuntime = findViewById(R.id.txtRuntime);

        if (txtScreenTitle != null) {
            txtScreenTitle.setText("영화별 예매");
        }

        if (txtTitle != null && movieTitle != null) {
            txtTitle.setText(movieTitle);
        }

        if (txtRuntime != null && movieDetailInfo != null && movieDetailInfo.contains("|")) {
            try {
                String[] parts = movieDetailInfo.split("\\|");
                if (parts.length >= 2) {
                    txtRuntime.setText(parts[1].trim());
                }
            } catch (Exception e) {
                Log.e(TAG, "Runtime parsing failed: " + e.getMessage());
            }
        }

        if (imgPoster != null) {
            if (moviePoster != 0) {
                imgPoster.setImageResource(moviePoster);
            } else {
                imgPoster.setVisibility(View.GONE);
            }
        }

        if (imgAgeLimit != null && ageLimit != 0) {
            imgAgeLimit.setImageResource(ageLimit);
        }
    }

    private void setupClickListeners() {
        ImageButton btnClose = findViewById(R.id.btnClose);
        if (btnClose != null) {
            btnClose.setOnClickListener(v -> finish());
        }
    }

    // --- 날짜 선택 로직 ---

    private void setupDateSelectionListeners() {
        int[] dateIds = {
                R.id.date12, R.id.date13, R.id.date14, R.id.date15,
                R.id.date16, R.id.date17, R.id.date18, R.id.date19
        };

        String[] dayTexts = {"금", "토", "일", "월", "화", "수", "목", "금"};
        String[] dateNumTexts = {"12", "13", "14", "15", "16", "17", "18", "19"};

        View.OnClickListener dateClickListener = v -> {
            if (currentlySelectedDateView != null) {
                currentlySelectedDateView.setSelected(false);
            }
            v.setSelected(true);
            currentlySelectedDateView = v;

            String selectedDate = dateInfoMap.get(v.getId());
            Toast.makeText(this, "날짜: " + selectedDate + " 선택됨", Toast.LENGTH_SHORT).show();
        };

        for (int i = 0; i < dateIds.length; i++) {
            View dateItemView = findViewById(dateIds[i]);
            if (dateItemView != null) {
                String dayDisplay = (i == 0) ? "오늘" : dayTexts[i];
                String dateNum = dateNumTexts[i];

                TextView txtDayOfWeek = dateItemView.findViewById(R.id.txtDayOfWeek);
                if (txtDayOfWeek != null) {
                    txtDayOfWeek.setText(dayDisplay);
                }

                TextView txtDate = dateItemView.findViewById(R.id.txtDate);
                if (txtDate != null) {
                    txtDate.setText(dateNum);
                }

                String fullDateInfo = CURRENT_YEAR + "." + CURRENT_MONTH + "." + dateNum + " (" + dayTexts[i] + ")";
                dateInfoMap.put(dateIds[i], fullDateInfo);

                dateItemView.setOnClickListener(dateClickListener);
            }
        }

        View date12 = findViewById(R.id.date12);
        if (date12 != null) {
            date12.setSelected(true);
            currentlySelectedDateView = date12;
        }
    }

    // --- 시간 선택 로직 수정 ---

    private void setupTimeSelectionListeners() {
        int[] timeIds = {
                R.id.timeBox1, R.id.timeBox2, R.id.timeBox3, R.id.timeBox4, R.id.timeBox5, R.id.timeBox6, R.id.timeBox7
        };

        String[] startTimes = {"10:00", "11:00", "13:40", "14:40", "16:00", "17:00", "17:40"};
        String[] availableSeats = {"182", "137", "121", "150", "117", "146", "139"};
        String totalSeats = "183석";

        int runtimeMinutes = parseRuntime(movieDetailInfo);

        for (int i = 0; i < timeIds.length; i++) {
            View timeBoxView = findViewById(timeIds[i]);
            if (timeBoxView != null) {

                String currentStartTime = startTimes[i];
                int currentAvailableSeats = Integer.parseInt(availableSeats[i]);

                String endTime = (runtimeMinutes > 0)
                        ? calculateEndTime(currentStartTime, runtimeMinutes)
                        : "XX:XX";
                String screenName = "1관";


                // 시작 시간, 종료 시간, 좌석 정보 설정
                TextView txtStartTime = timeBoxView.findViewById(R.id.txtStartTime);
                if (txtStartTime != null) {
                    txtStartTime.setText(currentStartTime);
                }
                TextView txtEndTime = timeBoxView.findViewById(R.id.txtEndTime);
                if (txtEndTime != null) {
                    txtEndTime.setText("-" + endTime);
                }
                TextView txtAvailableSeats = timeBoxView.findViewById(R.id.txtAvailableSeats);
                if (txtAvailableSeats != null) {
                    txtAvailableSeats.setText(availableSeats[i]);
                }
                TextView txtTotalSeats = timeBoxView.findViewById(R.id.txtTotalSeats);
                if (txtTotalSeats != null) {
                    txtTotalSeats.setText("/" + totalSeats);
                }

                // 매진 처리
                if (currentAvailableSeats <= 0) {
                    timeBoxView.setEnabled(false);
                    timeBoxView.setAlpha(0.5f);
                    if (txtAvailableSeats != null) {
                        txtAvailableSeats.setText("매진");
                    }
                    if (txtTotalSeats != null) {
                        txtTotalSeats.setText("");
                    }
                }

                // 인원 선택 화면(ReserveTicketActivity)으로 연결
                timeBoxView.setOnClickListener(v -> {
                    String selectedDate = getSelectedDateInfo();
                    String showtime = selectedDate + " " + currentStartTime + " ~ " + endTime;

                    Intent intent = new Intent(ReserveActivity.this, ReserveTicketActivity.class);

                    // 다음 화면에 필요한 정보 전달
                    intent.putExtra("movie_title", movieTitle);
                    intent.putExtra("movie_poster", moviePoster);
                    intent.putExtra("age_limit", ageLimit);
                    intent.putExtra("showtime", showtime);
                    intent.putExtra("start_time", currentStartTime);
                    intent.putExtra("end_time", endTime);
                    intent.putExtra("screen_name", screenName);

                    // 영화의 원본 인덱스를 다음 액티비티로 전달
                    if (movieIndex != -1) {
                        intent.putExtra("movie_index", movieIndex);
                    }

                    startActivity(intent);
                });
            }
        }
    }

    /**
     * 현재 선택된 날짜 정보를 "YYYY.MM.DD (요일)" 형식으로 반환합니다.
     */
    private String getSelectedDateInfo() {
        if (currentlySelectedDateView == null) {
            return dateInfoMap.getOrDefault(R.id.date12, CURRENT_YEAR + "." + CURRENT_MONTH + ".12 (월)");
        }
        return dateInfoMap.getOrDefault(currentlySelectedDateView.getId(), "날짜 미선택");
    }

    // ... (parseRuntime, calculateEndTime 함수는 동일)

    private int parseRuntime(String detailInfo) {
        if (detailInfo == null || !detailInfo.contains("|")) return 0;
        try {
            String[] parts = detailInfo.split("\\|");
            if (parts.length >= 2) {
                String runtimeStr = parts[1].trim();
                int totalMinutes = 0;

                if (runtimeStr.contains("시간")) {
                    String hourPart = runtimeStr.substring(0, runtimeStr.indexOf("시간")).trim();
                    totalMinutes += Integer.parseInt(hourPart) * 60;
                }
                if (runtimeStr.contains("분")) {
                    int minIndex = runtimeStr.indexOf("분");
                    int hourIndex = runtimeStr.lastIndexOf("시간");
                    String minPart = runtimeStr.substring(hourIndex > -1 ? hourIndex + 2 : 0, minIndex).trim();
                    totalMinutes += Integer.parseInt(minPart);
                }
                return totalMinutes;
            }
        } catch (Exception e) {
            Log.e(TAG, "Runtime parsing failed: " + e.getMessage());
        }
        return 0;
    }

    private String calculateEndTime(String startTime, int runtimeMinutes) {
        try {
            String[] parts = startTime.split(":");
            int startHour = Integer.parseInt(parts[0]);
            int startMinute = Integer.parseInt(parts[1]);

            int totalStartMinutes = startHour * 60 + startMinute;
            int totalEndMinutes = totalStartMinutes + runtimeMinutes;

            int endHour = (totalEndMinutes / 60) % 24;
            int endMinute = totalEndMinutes % 60;

            return String.format("%02d:%02d", endHour, endMinute);
        } catch (Exception e) {
            Log.e(TAG, "EndTime calculation failed: " + e.getMessage());
            return "XX:XX";
        }
    }
}