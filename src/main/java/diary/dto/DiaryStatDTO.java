package diary.dto;

import java.util.List;
import java.util.Map;

/*
  [DiaryStatDTO]
  /diary/stat.do 연간 통계 화면에서 사용하는 DTO
  DiaryDAO에서 집계한 결과를 담아서 DiaryStatServlet → diaryStat.jsp로 전달
 */


public class DiaryStatDTO {
	
	 // ── 연간 요약 ──────────────────────────────────────────────
	private int year; // 조회 연도 (예: 2025)
	private int totalCount; // 총 관람 편수
	private double avgStarRating; // 평균 별점(소수점 한 자리)
	
	
	
	// ── 장르 TOP3 ──────────────────────────────────────────────
    // Key: 장르명, Value: 편수 (예: {"액션": 5, "로맨스": 3, "공포": 2})
	private List<Map.Entry<String, Integer>> genreTop3;
	
	
	
	// ── 감정 태그 빈도 ─────────────────────────────────────────
    // Key: 태그명, Value: 사용 횟수 (빈도 높은 순)
	private List<Map.Entry<String, Integer>> tagFrequency;
	
	
	
	// ── 뱃지 목록 (동적 집계 방식, DB 저장 없음) ───────────────
    // Java에서 조건 판단 후 획득한 뱃지명만 담음
    // 예: ["첫 기록", "10편 달성", "별점왕"]
	private List<String> earnedBadges;
	
	
	
	// ── 월별 관람 편수 (달력/차트용) ──────────────────────────
    // index 0 = 1월, index 11 = 12월
	private int[] monthlyCount; // 크기 12
	
	
	// ── 기본 생성자 ───────────────────────────────────────────
	public DiaryStatDTO() {
		// TODO Auto-generated constructor stub
		this.monthlyCount = new int[12]; // 초기화
	}
	
	
	// ── Getter / Setter ───────────────────────────────────────
	public int getYear() {
		return year;
	}


	public void setYear(int year) {
		this.year = year;
	}


	public int getTotalCount() {
		return totalCount;
	}


	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}


	public double getAvgStarRating() {
		return avgStarRating;
	}


	public void setAvgStarRating(double avgStarRating) {
		this.avgStarRating = avgStarRating;
	}


	public List<Map.Entry<String, Integer>> getGenreTop3() {
		return genreTop3;
	}


	public void setGenreTop3(List<Map.Entry<String, Integer>> genreTop3) {
		this.genreTop3 = genreTop3;
	}


	public List<Map.Entry<String, Integer>> getTagFrequency() {
		return tagFrequency;
	}


	public void setTagFrequency(List<Map.Entry<String, Integer>> tagFrequency) {
		this.tagFrequency = tagFrequency;
	}


	public List<String> getEarnedBadges() {
		return earnedBadges;
	}


	public void setEarnedBadges(List<String> earnedBadges) {
		this.earnedBadges = earnedBadges;
	}


	public int[] getMonthlyCount() {
		return monthlyCount;
	}


	public void setMonthlyCount(int[] monthlyCount) {
		this.monthlyCount = monthlyCount;
	}
	
	
	
	
	
	


}
