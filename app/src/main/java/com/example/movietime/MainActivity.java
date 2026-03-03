package com.example.movietime;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 영화 목록을 표시하고 검색, 정렬 및 필터링 기능을 제공하는 메인 액티비티.
 */
public class MainActivity extends AppCompatActivity {
    // ----------------------------------------------------
    // UI 컴포넌트
    // ----------------------------------------------------
    ListView list;
    private ActivityResultLauncher<Intent> detailActivityResultLauncher;

    // ----------------------------------------------------
    // 영화 데이터 정의
    // ----------------------------------------------------
    // 영화 제목
    static String[] titles = {
            "주토피아 2",
            "아바타: 불과 재",
            "위키드: 포 굿",
            "프레디의 피자가게 2",
            "샤이닝",
            "나우 유 씨 미 3",
            "스위트캐슬 대모험",
            "파과: 인터내셔널 컷",
    };
    // 포스터 이미지
    static Integer[] images = {
            R.drawable.movie1,
            R.drawable.movie2,
            R.drawable.movie3,
            R.drawable.movie4,
            R.drawable.movie5,
            R.drawable.movie6,
            R.drawable.movie7,
            R.drawable.movie8
    };
    // 연령 제한 아이콘
    static Integer[] ageLimits = {
            R.drawable.ic_age_all,
            R.drawable.ic_age_12,
            R.drawable.ic_age_all,
            R.drawable.ic_age_12,
            R.drawable.ic_age_19,
            R.drawable.ic_age_12,
            R.drawable.ic_age_all,
            R.drawable.ic_age_19
    };
    // 영화 상세 정보 (개봉일 / 러닝타임 / 장르)
    static String[] details = {
            "2025.11.26 개봉  |  1시간 48분  |  애니메이션",
            "2025.12.17 개봉  |  3시간 17분  |  SF, 액션, 어드벤처",
            "2025.11.19 개봉  |  2시간 17분  |  판타지, 뮤지컬, 어드벤처",
            "2025.12.03 개봉  |  1시간 44분  |  공포(호러), 스릴러",
            "2025.12.10 재개봉  |  2시간 25분  |  공포(호러), 미스터리",
            "2025.11.12 개봉  |  1시간 52분  |  범죄, 액션",
            "2025.12.11 개봉  |  1시간 6분  |  애니메이션",
            "2025.12.10 개봉  |  2시간 12분  |  액션, 드라마, 미스터리"
    };
    // 영화 줄거리 배열
    static String[] synopses = {
            "더 화려해진 세계, 더 넓어진 주토피아!\n" +
                    "디즈니의 가~~장 사랑스러운 콤비 '주디'와 '닉'이 돌아온다!\n" +
                    "\n" +
                    "미스터리한 뱀 ‘게리’가 나타난 순간,\n" +
                    "주토피아가 다시 흔들리기 시작했다!",
            "월드 와이드 흥행 불멸의 1위 <아바타> 시리즈의 세 번째 이야기!\n" +
                    "판도라를 위협하는 재의 부족, 더 이상 인간만이 적이 아니다! \n" +
                    "12월, 모두의 운명을 뒤흔들 거대한 전투가 시작된다!",
            "영화 <위키드: 포 굿>은\n" +
                    "사람들의 시선이 더는 두렵지 않은 사악한 마녀 ‘엘파바’와\n" +
                    "사람들의 사랑을 잃는 것이 두려운 착한 마녀 ‘글린다’가\n" +
                    "엇갈린 운명 속에서 진정한 우정을 찾아가는 이야기",
            "다시 밤이 되었습니다\n" +
                    "마스코트들은 이제 밖으로 나가주세요\n" +
                    "\n" +
                    "소문과 괴담에 휘말려 폐업한 ‘프레디의 피자가게’ 본점이\n" +
                    "수십년 만에 다시 열리고,\n" +
                    "그곳에 오랫동안 잠들어 있던 신형 프레디와 친구들이 눈을 뜨게 된다.",
            "겨울 동안 호텔을 관리하며 느긋하게 소설을 쓸 수 있는 기회를 잡은 ‘잭’은\n" +
                    "가족들을 데리고 눈 내리는 고요한 오버룩 호텔로 향한다.\n" +
                    "\n" +
                    "보이지 않는 영혼을 볼 수 있는 ‘샤이닝’ 능력을 가진 아들 ‘대니’는\n" +
                    "이 호텔에 드리워진 음산한 기운을 직감적으로 느낀다.",
            "나쁜 놈들 잡는 마술사기단 호스맨이\n" +
                    "더러운 돈의 출처인 하트 다이아몬드를\n" +
                    "훔치기 위해 목숨을 건 지상 최고의\n" +
                    "마술쇼를 펼치는 블록버스터.",
            "\"이번 크리스마스가 위험해!\"\n" +
                    "\n" +
                    "마녀 '버니'의 마법으로 인형이 되어버린 산타 할아버지!\n" +
                    "올해의 크리스마스 디저트를 완성할 마법 재료 ‘산타의 토핑’이\n" +
                    "디저트 왕국에 전해지지 못하면, 크리스마스는 사라지고 만다.",
            "지킬 게 생긴 킬러 VS 잃을 게 없는 킬러\n" +
                    "40여 년간 감정 없이 바퀴벌레 같은 인간들을 방역해온\n" +
                    "60대 킬러 ‘조각’(이혜영)."
    };

    private double[] ratings = {4.5, 4.1, 4.2, 4.7, 4.3, 4.0, 4.6, 4.4,};
    static int[] likes = new int[titles.length];            // 영화별 좋아요 수
    static boolean[] liked = new boolean[titles.length];    // 사용자가 좋아요를 눌렀는지 여부 (보관함)
    boolean[] reserved = new boolean[titles.length]; // 영화 예매 완료 여부

    public static String[] adapterTrailerUrls = {
            "https://www.youtube.com/watch?v=LyMbgsFGQ6I", // 주토피아 2
            "https://www.youtube.com/watch?v=11jbQ6FMI4c", // 아바타
            "https://www.youtube.com/watch?v=hjjISKnEK1s", // 위키드
            "https://www.youtube.com/watch?v=mCDF_C_VO1Q", // 프레디
            "https://www.youtube.com/watch?v=k7gAdQVTP9o", // 샤이닝
            "https://www.youtube.com/watch?v=xPAZZrURdos", // 나우 유 씨 미
            "https://www.youtube.com/watch?v=m7g42TRbP-k", // 스위트캐슬
            "https://www.youtube.com/watch?v=en72qvufSIE"  // 파과
    };

    // 예매 상세 정보 (예매 내역 화면 구성을 위해 저장)
    String[] reservedShowtime;      // 상영 시간
    String[] reservedSeats;         // 선택 좌석
    int[] reservedTotalPrice;       // 총 금액
    int[] reservedAdultCount;       // 성인 수
    int[] reservedYouthCount;       // 청소년 수
    int[] reservedPreferentialCount; // 우대 수
    int[] reservedSeniorCount;      // 경로 수

    // 현재 화면에 표시되는 영화 인덱스 목록 (정렬 및 필터링 결과)
    ArrayList<Integer> filteredIndexes;

    private static final Map<Integer, List<String>> RESERVED_SEATS_MAP = new HashMap<>();

    /**
     * 특정 영화 인덱스에 대해 새로 예매된 좌석 정보를 추가합니다.
     */
    public static void addReservedSeats(int movieIndex, List<String> newSeats) {
        if (RESERVED_SEATS_MAP.containsKey(movieIndex)) {
            RESERVED_SEATS_MAP.get(movieIndex).addAll(newSeats);
        } else {
            RESERVED_SEATS_MAP.put(movieIndex, new ArrayList<>(newSeats));
        }
    }

    /**
     * 특정 영화 인덱스에 대해 현재까지 예약된 모든 좌석 정보를 가져옵니다.
     */
    public static List<String> getReservedSeats(int movieIndex) {
        // null 방지를 위해 비어 있는 리스트를 기본값으로 반환
        return RESERVED_SEATS_MAP.getOrDefault(movieIndex, new ArrayList<>());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 좋아요 수 초기화 및 초기 좋아요 수 설정
        Arrays.fill(likes, 0);
        Arrays.fill(liked, false); // liked 배열도 false로 초기화합니다.

        // 예매 상세 정보 배열 초기화
        int numMovies = titles.length;
        reservedShowtime = new String[numMovies];
        reservedSeats = new String[numMovies];
        reservedTotalPrice = new int[numMovies];
        reservedAdultCount = new int[numMovies];
        reservedYouthCount = new int[numMovies];
        reservedPreferentialCount = new int[numMovies];
        reservedSeniorCount = new int[numMovies];

        // 좋아요 수 (likes) 설정
        likes[0] = 3874;
        likes[1] = 8321;
        likes[2] = 5631;
        likes[3] = 1234;
        likes[4] = 3421;
        likes[5] = 5034;
        likes[6] = 1830;
        likes[7] = 2436;

        // 내가 좋아요를 누른 상태 (liked) 설정 (보관함 필터링)
        liked[1] = true; // 아바타: 내가 좋아요 누름
        liked[4] = true; // 샤이닝: 내가 좋아요 누름

        Intent intent = getIntent();
        int reservedIndex = intent.getIntExtra("reserved_movie_index", -1);
        boolean justReserved = (reservedIndex != -1);

        // 예매 완료된 영화 정보가 있을 경우 처리
        if (reservedIndex != -1 && reservedIndex < titles.length) {
            // 예매 완료된 영화를 reserved 배열에 true로 표시
            reserved[reservedIndex] = true;

            // Intent에서 상세 예매 정보 추출 및 저장
            reservedShowtime[reservedIndex] = intent.getStringExtra("reserved_showtime");
            reservedSeats[reservedIndex] = intent.getStringExtra("reserved_seats");
            reservedTotalPrice[reservedIndex] = intent.getIntExtra("reserved_total_price", 0);
            reservedAdultCount[reservedIndex] = intent.getIntExtra("reserved_adult_count", 0);
            reservedYouthCount[reservedIndex] = intent.getIntExtra("reserved_youth_count", 0);
            reservedPreferentialCount[reservedIndex] = intent.getIntExtra("reserved_preferential_count", 0);
            reservedSeniorCount[reservedIndex] = intent.getIntExtra("reserved_senior_count", 0);

            Toast.makeText(this, titles[reservedIndex] + " 예매 내역에 추가됨", Toast.LENGTH_LONG).show();
        }

        list = findViewById(R.id.list);

        filteredIndexes = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) filteredIndexes.add(i);

        CustomList adapter = new CustomList(MainActivity.this);
        list.setAdapter(adapter);

        detailActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            int index = data.getIntExtra("index", -1);
                            boolean newLiked = data.getBooleanExtra("liked", false);
                            int newLikes = data.getIntExtra("likes", 0);

                            if (index != -1) {
                                liked[index] = newLiked;
                                likes[index] = newLikes;
                                ((BaseAdapter) list.getAdapter()).notifyDataSetChanged();
                            }
                        }
                    }
                }
        );

        EditText editSearch = findViewById(R.id.editSearch);
        ImageButton btnSearch = findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(v -> {
            String q = editSearch.getText().toString().trim();
            doSearch(q);
        });

        editSearch.setOnEditorActionListener((v, actionId, event) -> {
            String q = editSearch.getText().toString().trim();
            doSearch(q);
            return false;
        });

        // 정렬 버튼
        TextView btnNow = findViewById(R.id.btnNow);
        TextView btnRating = findViewById(R.id.btnRating);
        TextView btnPopular = findViewById(R.id.btnPopular);
        TextView btnSaved = findViewById(R.id.btnSaved);
        TextView btnReserveList = findViewById(R.id.btnReserveList);

        // 예매 완료 후 복귀 시 '예매내역' 탭 기본 선택
        String selectTab = getIntent().getStringExtra("select_tab");

        // 'reserved_movie_index'가 전달되었거나, 'select_tab'이 요청된 경우에만
        if (justReserved || "reserve_list".equals(selectTab)) {
            // '예매내역' 탭이 요청된 경우
            setSortSelected(btnReserveList);
            clearSortSelected(btnNow, btnRating, btnPopular, btnSaved);
            filterReserved();
            // 필터링 후 반드시 어댑터를 갱신
            ((BaseAdapter) list.getAdapter()).notifyDataSetChanged();
        } else {
            // 기본 상태: '현재상영작'
            setSortSelected(btnNow);
            clearSortSelected(btnRating, btnPopular, btnSaved, btnReserveList);
            resetFilter(); // '현재상영작 정렬' (인덱스 순서) 적용
        }

        // --- 정렬 버튼 리스너 설정 ---
        btnNow.setOnClickListener(v -> {
            // 1. 현재상영작 정렬 (기본 순서)
            setSortSelected(btnNow);
            clearSortSelected(btnRating, btnPopular, btnSaved, btnReserveList);
            resetFilter();
            ((BaseAdapter) list.getAdapter()).notifyDataSetChanged();
        });

        btnRating.setOnClickListener(v -> {
            // 2. 평점순 정렬
            setSortSelected(btnRating);
            clearSortSelected(btnNow, btnPopular, btnSaved, btnReserveList);
            sortByRating();
        });

        btnPopular.setOnClickListener(v -> {
            // 3. 인기순 정렬
            setSortSelected(btnPopular);
            clearSortSelected(btnNow, btnRating, btnSaved, btnReserveList);
            sortByLikes();
        });

        btnSaved.setOnClickListener(v -> {
            // 4. 보관함 정렬 (내가 좋아요 누른 영화 리스트)
            setSortSelected(btnSaved);
            clearSortSelected(btnNow, btnRating, btnPopular, btnReserveList);
            filterSaved();
        });

        btnReserveList.setOnClickListener(v -> {
            // 5. 예매내역 정렬 (내가 예매완료한 영화 리스트)
            setSortSelected(btnReserveList);
            clearSortSelected(btnNow, btnRating, btnPopular, btnSaved);
            filterReserved();
        });

        list.setOnItemClickListener((parent, view, position, id) -> {
            int realIdx = filteredIndexes.get(position);

            // MovieDetailActivity로 전달할 데이터 모두 Intent에 담기
            Intent i = new Intent(MainActivity.this, MovieDetailActivity.class);
            i.putExtra("index", realIdx);
            i.putExtra("title", titles[realIdx]);
            i.putExtra("poster", images[realIdx]);
            i.putExtra("ageLimit", ageLimits[realIdx]);
            i.putExtra("detailInfo", details[realIdx]);
            i.putExtra("synopsis", synopses[realIdx]);

            i.putExtra("isLiked", liked[realIdx]);
            i.putExtra("likes", likes[realIdx]);

            detailActivityResultLauncher.launch(i);
        });
    }

    // -------------------------
    // 정렬/필터링 로직 구현
    // -------------------------

    // 5. 예매내역 필터링
    private void filterReserved() {
        filteredIndexes.clear();
        for (int i = 0; i < titles.length; i++) {
            if (reserved[i]) { // 'reserved' 배열이 true인 항목만 필터링
                filteredIndexes.add(i);
            }
        }
        ((BaseAdapter) list.getAdapter()).notifyDataSetChanged();
    }

    private void doSearch(String q) {
        filteredIndexes.clear();

        if (q.isEmpty()) {
            for (int i = 0; i < titles.length; i++) {
                filteredIndexes.add(i);
            }
        } else {
            // 검색어가 있다면 전체 titles 배열을 순회하며 검색
            for (int i = 0; i < titles.length; i++) {
                if (titles[i].toLowerCase(Locale.ROOT).contains(q.toLowerCase(Locale.ROOT))) {
                    filteredIndexes.add(i);
                }
            }
        }
        ((BaseAdapter) list.getAdapter()).notifyDataSetChanged();
    }

    // 1. 현재상영작 정렬 (초기 순서로 복귀)
    private void resetFilter() {
        filteredIndexes.clear();
        for (int i = 0; i < titles.length; i++) filteredIndexes.add(i);
    }

    // 2. 평점순 정렬 (평점이 높은 순서대로)
    private void sortByRating() {
        Integer[] idxs = new Integer[titles.length];
        for (int i = 0; i < titles.length; i++) idxs[i] = i;

        // 평점(ratings[b])이 높은 순서(내림차순)로 정렬
        Arrays.sort(idxs, (a, b) -> Double.compare(ratings[b], ratings[a]));

        filteredIndexes.clear();
        for (int id : idxs) filteredIndexes.add(id);
        ((BaseAdapter) list.getAdapter()).notifyDataSetChanged();
    }

    // 3. 인기순 정렬 (좋아요 수가 많은 순서대로)
    private void sortByLikes() {
        Integer[] idxs = new Integer[titles.length];
        for (int i = 0; i < titles.length; i++) idxs[i] = i;

        // 좋아요 수(likes[b])가 많은 순서(내림차순)로 정렬
        Arrays.sort(idxs, (a, b) -> Integer.compare(likes[b], likes[a]));

        filteredIndexes.clear();
        for (int id : idxs) filteredIndexes.add(id);
        ((BaseAdapter) list.getAdapter()).notifyDataSetChanged();
    }

    // 4. 보관함 필터링
    private void filterSaved() {
        filteredIndexes.clear();
        for (int i = 0; i < titles.length; i++) if (liked[i]) filteredIndexes.add(i);
        ((BaseAdapter) list.getAdapter()).notifyDataSetChanged();
    }

    /**
     * 현재 정렬 탭이 '예매내역'인지 확인합니다.
     */
    public boolean isReserveListSelected() {
        TextView btnReserveList = findViewById(R.id.btnReserveList);
        // btnReserveList가 null이 아니면서 선택 상태(setSelected(true))인지 확인
        return btnReserveList != null && btnReserveList.isSelected();
    }

    // -------------------------
    // 정렬 버튼 스타일 selector 적용
    // -------------------------
    private void setSortSelected(TextView b) {
        b.setSelected(true);
    }

    private void clearSortSelected(TextView... bs) {
        for (TextView b : bs) {
            b.setSelected(false);
        }
    }

    // -------------------------
    // CustomList 어댑터
    // -------------------------
    public class CustomList extends ArrayAdapter<String> {
        private final Activity context;

        public CustomList(Activity context) {
            super(context, R.layout.list_item, titles);
            this.context = context;
        }

        @Override
        public int getCount() {
            return filteredIndexes.size();
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.list_item, parent, false);

            ImageView imageView = rowView.findViewById(R.id.image);
            TextView title = rowView.findViewById(R.id.title);
            TextView rating = rowView.findViewById(R.id.rating);
            ImageButton likeButton = rowView.findViewById(R.id.like);
            ImageView ageLimit = rowView.findViewById(R.id.ageLimit);
            Button reserveBtn = rowView.findViewById(R.id.reserve);

            int realIndex = filteredIndexes.get(position);

            title.setText(titles[realIndex]);
            imageView.setImageResource(images[realIndex]);
            rating.setText("⭐ " + String.format(Locale.KOREA, "%.1f", ratings[realIndex]));
            ageLimit.setImageResource(ageLimits[realIndex]);

            // 포스터 이미지 클릭 리스너
            imageView.setOnClickListener(v -> {
                Intent i = new Intent(MainActivity.this, MovieDetailActivity.class);
                i.putExtra("index", realIndex);
                i.putExtra("title", titles[realIndex]);
                i.putExtra("poster", images[realIndex]);
                i.putExtra("ageLimit", ageLimits[realIndex]);
                i.putExtra("movie_index", realIndex);
                i.putExtra("detailInfo", details[realIndex]);
                i.putExtra("isLiked", liked[realIndex]);
                i.putExtra("likes", likes[realIndex]);
                i.putExtra("synopsis", synopses[realIndex]);
                i.putExtra("trailerUrl", adapterTrailerUrls[realIndex]);

                detailActivityResultLauncher.launch(i);
            });

            // 좋아요 상태 표시 및 리스너
            if (liked[realIndex]) {
                likeButton.setImageResource(R.drawable.ic_heart_filled);
            } else {
                likeButton.setImageResource(R.drawable.ic_heart_unfilled);
            }
            likeButton.setOnClickListener(v -> {
                liked[realIndex] = !liked[realIndex];
                if (liked[realIndex]) {
                    likes[realIndex]++;
                    likeButton.setImageResource(R.drawable.ic_heart_filled);
                    Toast.makeText(context, titles[realIndex] + " 보관됨", Toast.LENGTH_SHORT).show();
                } else {
                    if (likes[realIndex] > 0) likes[realIndex]--;
                    likeButton.setImageResource(R.drawable.ic_heart_unfilled);
                    Toast.makeText(context, titles[realIndex] + " 보관 취소", Toast.LENGTH_SHORT).show();
                }
                // 보관함 필터 상태가 켜져 있으면 갱신을 위해 notifyDataSetChanged 호출
                if (findViewById(R.id.btnSaved).isSelected()) {
                    ((BaseAdapter) list.getAdapter()).notifyDataSetChanged();
                }
            });

            // 5번 정렬(예매내역)일 때만 '내역확인' 표시
            boolean isReserveTab = isReserveListSelected();

            if (isReserveTab) {
                // '예매내역' 탭이 선택된 경우: 목록의 모든 항목을 '내역확인'으로 표시
                reserveBtn.setText("내역확인");
                reserveBtn.setTextColor(Color.WHITE);

                reserveBtn.setOnClickListener(v -> {
                    Toast.makeText(context, titles[realIndex] + " 예매 내역 페이지로 이동합니다.", Toast.LENGTH_SHORT).show();

                    // ReserveCompleteActivity로 이동 (예매내역 확인)
                    Intent i = new Intent(MainActivity.this, ReserveCompleteActivity.class);

                    // ... (데이터 전달)
                    i.putExtra("movie_title", titles[realIndex]);
                    i.putExtra("movie_poster", images[realIndex]);
                    i.putExtra("age_limit", ageLimits[realIndex]);
                    i.putExtra("movie_index", realIndex);

                    // 저장된 실제 예매 상세 내역 전달
                    i.putExtra("showtime", reservedShowtime[realIndex]);
                    i.putExtra("selected_seats", reservedSeats[realIndex]);
                    i.putExtra("total_price", reservedTotalPrice[realIndex]);
                    i.putExtra("adult_count", reservedAdultCount[realIndex]);
                    i.putExtra("youth_count", reservedYouthCount[realIndex]);
                    i.putExtra("preferential_count", reservedPreferentialCount[realIndex]);
                    i.putExtra("senior_count", reservedSeniorCount[realIndex]);
                    // ----------------------------------------------------

                    context.startActivity(i);
                });

            } else {
                // 2. 다른 정렬 탭이 선택된 경우: 예매 여부 관계없이 무조건 '예매하기' 버튼만 표시
                reserveBtn.setText("예매하기");
                reserveBtn.setOnClickListener(v -> {
                    // ReserveActivity 이동 리스너 (예매하기)
                    Intent i = new Intent(MainActivity.this, ReserveActivity.class);
                    i.putExtra("title", titles[realIndex]);
                    i.putExtra("poster", images[realIndex]);
                    i.putExtra("detailInfo", details[realIndex]);
                    i.putExtra("ageLimit", ageLimits[realIndex]);
                    i.putExtra("index", realIndex);
                    context.startActivity(i);
                    Toast.makeText(context, titles[realIndex] + " 예매 페이지로 이동합니다.", Toast.LENGTH_SHORT).show();
                });
            }

            return rowView;
        }
    }
}