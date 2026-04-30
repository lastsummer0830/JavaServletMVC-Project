package review.dto;

//DB에서 꺼낸 데이터를 이 클래스에 담아서 Service → Controller → JSP로 전달
public class ReviewDTO {

    private int reviewId;
    // 리뷰 고유 번호 (PK) - DB: REVIEW_ID

    private int movieId;
    // 어떤 영화에 대한 리뷰인지 (FK) - DB: MOVIE_ID

    private int memberId;
    // 누가 쓴 리뷰인지 작성자 번호 (FK) - DB: MEMBER_ID

    private String freshYn;
    // 신선한 리뷰 여부 - DB: FRESH_YN
    // 'Y' = 신선(좋아요), 'N' = 비신선(별로)

    private String publicYn;
    // 공개/비공개 여부 - DB: PUBLIC_YN
    // 'Y' = 전체 공개, 'N' = 친구만 공개

    private String content;
    // 리뷰 본문 내용 - DB: CONTENT

    private String createdAt;
    // 리뷰 작성일시 - DB: CREATED_AT

    private String updatedAt;
    // 리뷰 수정일시 - DB: UPDATED_AT
    
    

    
    // JOIN용 추가 필드 (DB 컬럼 아님!)
    // SQL에서 JOIN해서 가져온 데이터를 담는 용도

    private String memberName;
    // 작성자 이름 - MEMBER 테이블을 JOIN해서 가져옴
    // JSP에서 "홍길동이 작성" 처럼 표시할 때 사용

    private String movieTitle;
    // 영화 제목 - MOVIE 테이블을 JOIN해서 가져옴
    // JSP에서 영화 제목 보여줄 때 사용

    
    

    // 통계용 필드
    // COUNT, 계산된 값을 담는 용도

    private int totalCount;
    // 해당 영화의 전체 리뷰 개수
    // SQL의 COUNT(*) 결과를 담음

    private int freshCount;
    // 신선 리뷰만 센 개수
    // SQL의 SUM(CASE WHEN fresh_yn='Y' THEN 1 ELSE 0 END) 결과

    private int notFreshCount;
    // 비신선 리뷰만 센 개수
    // Java에서 (totalCount - freshCount)로 계산

    private double freshRate;
    // 신선도 비율 (%)
    // Java에서 (freshCount * 100.0 / totalCount)로 계산
    // 예: 리뷰 10개 중 신선 7개 → 70.0

    
    
    
    // Getter/Setter
    public int getReviewId() { return reviewId; }
    public void setReviewId(int reviewId) { this.reviewId = reviewId; }

    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }

    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }

    public String getFreshYn() { return freshYn; }
    public void setFreshYn(String freshYn) { this.freshYn = freshYn; }

    public String getPublicYn() { return publicYn; }
    public void setPublicYn(String publicYn) { this.publicYn = publicYn; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }

    public int getFreshCount() { return freshCount; }
    public void setFreshCount(int freshCount) { this.freshCount = freshCount; }

    public int getNotFreshCount() { return notFreshCount; }
    public void setNotFreshCount(int notFreshCount) { this.notFreshCount = notFreshCount; }

    public double getFreshRate() { return freshRate; }
    public void setFreshRate(double freshRate) { this.freshRate = freshRate; }
}
