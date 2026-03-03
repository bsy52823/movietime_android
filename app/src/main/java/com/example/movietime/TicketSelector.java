package com.example.movietime;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 인원/매수 선택 화면(ReserveTicketActivity)에서 사용되는 티켓 타입별 선택 UI의 로직을 관리하는 헬퍼 클래스입니다.
 */
public class TicketSelector {

    // ----------------------------------------------------
    // UI 필드
    // ----------------------------------------------------
    private final TextView txtTicketType;
    private final TextView txtCount;
    private final TextView btnDecrease;
    private final TextView btnIncrease;

    // ----------------------------------------------------
    // 상태 필드
    // ----------------------------------------------------
    private int count = 0;
    public final int maxCount; // 개별 티켓 타입의 최대 인원 제한 (TotalCount와 무관한 개별 제한)
    private final String ticketName;

    // 총 인원수 변경을 ReserveTicketActivity에 알리는 리스너
    private final TotalCountListener totalCountListener; // 변수명 통일: listener -> totalCountListener

    // ----------------------------------------------------
    // 액티비티에 총 인원수 변경을 알리는 인터페이스
    // ----------------------------------------------------
    public interface TotalCountListener {
        /**
         * 이 선택자의 카운트가 변경될 때 호출됩니다.
         */
        void onCountChanged(int selectorCount);
    }

    /**
     * TicketSelector의 생성자입니다.
     */
    public TicketSelector(View itemView, String name, TotalCountListener listener, int maxCount) {
        this.ticketName = name;
        this.totalCountListener = listener;
        this.maxCount = maxCount;

        txtTicketType = itemView.findViewById(R.id.txtTicketType);
        txtCount = itemView.findViewById(R.id.txtCount);
        btnDecrease = itemView.findViewById(R.id.btnDecrease);
        btnIncrease = itemView.findViewById(R.id.btnIncrease);

        // 초기 텍스트 설정
        txtTicketType.setText(ticketName);
        txtCount.setText(String.valueOf(count));
        updateButtonState(); // 초기 버튼 상태 설정

        // 클릭 리스너 설정
        Context context = itemView.getContext();
        btnDecrease.setOnClickListener(v -> decreaseCount(context));
        btnIncrease.setOnClickListener(v -> increaseCount(context));
    }

    /**
     * 현재 카운트를 반환합니다.
     */
    public int getCount() {
        return count;
    }

    /**
     * 카운트 값을 업데이트하고 UI 및 리스너를 호출합니다.
     */
    private void updateCount(int newCount) {
        if (newCount != count) {
            count = newCount;
            txtCount.setText(String.valueOf(count));
            updateButtonState();
            totalCountListener.onCountChanged(count); // 총 인원수 변경을 액티비티에 알림
        }
    }

    /**
     * 카운트를 1 증가시킵니다.
     */
    private void increaseCount(Context context) {
        // 개별 최대 인원 체크
        if (count < maxCount) {
            updateCount(count + 1);
        } else {
            Toast.makeText(context, ticketName + "은(는) 최대 " + maxCount + "명까지 선택 가능합니다.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 카운트를 1 감소시킵니다.
     * @param context Toast 메시지를 위한 Context
     */
    private void decreaseCount(Context context) {
        if (count > 0) {
            updateCount(count - 1);
        }
    }

    /**
     * 감소 버튼 및 개별 최대 인원 제한에 따른 증가 버튼의 활성화/비활성화 상태를 업데이트합니다.
     */
    private void updateButtonState() {
        // 감소 버튼: 카운트가 0보다 클 때만 활성화
        if (btnDecrease != null) {
            btnDecrease.setEnabled(count > 0);
            btnDecrease.setAlpha(count > 0 ? 1.0f : 0.3f);
        }

        // 증가 버튼: 카운트가 개별 최대치보다 작을 때만 활성화
        if (btnIncrease != null) {
            btnIncrease.setEnabled(count < maxCount);
            btnIncrease.setAlpha(count < maxCount ? 1.0f : 0.3f);
        }
    }

    /**
     * 전체 인원수 제약에 따라 증가 버튼의 활성화 상태를 최종적으로 업데이트합니다.
     */
    public void updateIncreaseButtonStateByTotalCount(int remainingSlots) {
        if (btnIncrease != null) {
            // 이 선택자에서 개별적으로 더 증가할 수 있는 카운트 (maxCount 제한)
            boolean canIncreaseByIndividualMax = (count < maxCount);

            // 이 선택자에서 전체적으로 더 증가할 수 있는 카운트 (remainingSlots 제한)
            boolean canIncreaseByTotalMax = (remainingSlots > 0);

            // 두 조건이 모두 참이고, 개별 최대치까지 남은 슬롯이 최소 1개일 때만 활성화
            boolean finalEnabled = canIncreaseByIndividualMax && canIncreaseByTotalMax;

            btnIncrease.setEnabled(finalEnabled);
            btnIncrease.setAlpha(finalEnabled ? 1.0f : 0.3f);
        }
    }
}