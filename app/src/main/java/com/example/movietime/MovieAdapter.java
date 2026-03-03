package com.example.movietime;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Locale;

public class MovieAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final MainActivity activity; // MainActivity 인스턴스에 접근하여 배열 데이터 사용

    // MovieAdapter 생성자는 MainActivity 인스턴스를 받아서 배열 데이터에 접근합니다.
    public MovieAdapter(Activity context, MainActivity activity) {
        // 부모 생성자에 임시 데이터(titles)를 전달합니다.
        super(context, R.layout.list_item, activity.titles);
        this.context = context;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        // 필터링된 인덱스 목록의 크기를 반환
        return activity.filteredIndexes.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_item, parent, false);

        // 뷰 초기화
        ImageView imageView = rowView.findViewById(R.id.image);
        TextView title = rowView.findViewById(R.id.title);
        TextView rating = rowView.findViewById(R.id.rating);
        ImageButton likeButton = rowView.findViewById(R.id.like);
        ImageView ageLimit = rowView.findViewById(R.id.ageLimit);
        Button reserveBtn = rowView.findViewById(R.id.reserve);

        // 현재 필터링된 위치(position)에 해당하는 원본 배열의 인덱스를 가져옵니다.
        final int realIndex = activity.filteredIndexes.get(position);

        // 데이터 바인딩
        title.setText(activity.titles[realIndex]);
        imageView.setImageResource(activity.images[realIndex]);

        // 평점은 더미 계산식 사용 (MainActivity와 동일)
        rating.setText("⭐ " + String.format(Locale.KOREA, "%.1f", 4.0 + (realIndex % 10) * 0.1));
        ageLimit.setImageResource(activity.ageLimits[realIndex]);

        // 좋아요 상태 표시
        if (activity.liked[realIndex]) {
            likeButton.setImageResource(R.drawable.ic_heart_filled);
        } else {
            likeButton.setImageResource(R.drawable.ic_heart_unfilled);
        }

        // 좋아요 버튼 클릭 리스너
        likeButton.setOnClickListener(v -> {
            activity.liked[realIndex] = !activity.liked[realIndex];
            if (activity.liked[realIndex]) {
                activity.likes[realIndex]++;
                likeButton.setImageResource(R.drawable.ic_heart_filled);
                Toast.makeText(context, activity.titles[realIndex] + " 보관됨 (" + activity.likes[realIndex] + ")", Toast.LENGTH_SHORT).show();
            } else {
                if (activity.likes[realIndex] > 0) activity.likes[realIndex]--;
                likeButton.setImageResource(R.drawable.ic_heart_unfilled);
                Toast.makeText(context, activity.titles[realIndex] + " 보관 취소 (" + activity.likes[realIndex] + ")", Toast.LENGTH_SHORT).show();
            }
        });

        // 예매 버튼 클릭 리스너
        reserveBtn.setOnClickListener(v -> {
            Intent i = new Intent(context, ReserveActivity.class);
            i.putExtra("title", activity.titles[realIndex]);
            i.putExtra("poster", activity.images[realIndex]);
            context.startActivity(i);
        });

        return rowView;
    }
}