package diary.dto;

import java.util.List;

/*
  [DiayryDTO]
  DIARY_ENTRY 테이블 + TAG 정보 + MOVIE 정보를 함께 담는 DTO
  필름 다이어리 목록/상세/달력/태그 수정에 모두 사용
   


*/
public class DiaryDTO {
	
	// ── DIARY_ENTRY 컬럼 ──────────────────────────────────────
    private int diaryId;          // diary_id (PK)
    private int memberId;         // member_id (FK → MEMBER)
    private int movieId;          // movie_id  (FK → MOVIE)
    private int reservationId;    // reservation_id (FK → RESERVATION, NULL 가능)
    private int reviewId;         // review_id (FK → REVIEW, NULL 가능 - 리뷰 연동 선택)
    private String watchDate;     // watch_date (관람일, "yyyy-MM-dd" 형태로 사용)
    private double starRating;    // star_rating (별점 1.0~5.0, 0.5단위, NULL 가능 → 0.0으로 처리)
    private String createdAt;     // created_at (등록일시)

    // ── MOVIE 조인 컬럼 (목록/달력 출력용) ─────────────────────
    private String title;         // movie.title (영화 제목)
    private String posterUrl;     // movie.poster_url (포스터 이미지 URL)
    private String genre;         // movie.genre (장르 - 통계용)

    // ── TAG 조인 컬럼 (다이어리에 붙은 감정 태그 목록) ─────────
    private List<String> tagNames; // 감정 태그명 리스트 (DIARY_TAG JOIN TAG)
    private List<Integer> tagIds;  // 태그 ID 리스트 (tagUpdate 시 사용)

    // ── 기본 생성자 ───────────────────────────────────────────
    public DiaryDTO() {
		// TODO Auto-generated constructor stub
	}
    
    // ── Getter / Setter ───────────────────────────────────────

	public int getDiaryId() {
		return diaryId;
	}

	public void setDiaryId(int diaryId) {
		this.diaryId = diaryId;
	}

	public int getMemberId() {
		return memberId;
	}

	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}

	public int getMovieId() {
		return movieId;
	}

	public void setMovieId(int movieId) {
		this.movieId = movieId;
	}

	public int getReservationId() {
		return reservationId;
	}

	public void setReservationId(int reservationId) {
		this.reservationId = reservationId;
	}

	public int getReviewId() {
		return reviewId;
	}

	public void setReviewId(int reviewId) {
		this.reviewId = reviewId;
	}

	public String getWatchDate() {
		return watchDate;
	}

	public void setWatchDate(String watchDate) {
		this.watchDate = watchDate;
	}

	public double getStarRating() {
		return starRating;
	}

	public void setStarRating(double starRating) {
		this.starRating = starRating;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPosterUrl() {
		return posterUrl;
	}

	public void setPosterUrl(String posterUrl) {
		this.posterUrl = posterUrl;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public List<String> getTagNames() {
		return tagNames;
	}

	public void setTagNames(List<String> tagNames) {
		this.tagNames = tagNames;
	}

	public List<Integer> getTagIds() {
		return tagIds;
	}

	public void setTagIds(List<Integer> tagIds) {
		this.tagIds = tagIds;
	}

	@Override
	public String toString() {
		return "DiaryDTO [diaryId=" + diaryId + ", memberId=" + memberId + ", movieId=" + movieId + ", reservationId="
				+ reservationId + ", reviewId=" + reviewId + ", watchDate=" + watchDate + ", starRating=" + starRating
				+ ", createdAt=" + createdAt + ", title=" + title + ", posterUrl=" + posterUrl + ", genre=" + genre
				+ ", tagNames=" + tagNames + ", tagIds=" + tagIds + "]";
	}
    
    

}
