package review.service;

import review.dao.ReviewDAO;
import review.dto.ReviewDTO;

import java.util.List;

public class ReviewService {

    // DAO 객체 생성 (DB 통신 담당)
    private ReviewDAO dao = new ReviewDAO();
    
    
    // 영화별 리뷰 목록
    /**
     * 특정 영화의 공개 리뷰 목록 반환
     */
    public List<ReviewDTO> getReviewListByMovie(int movieId) {
        return dao.getReviewListByMovie(movieId);
    }

    

    // 리뷰 단건 조회
    /**
     * 리뷰 1개 반환
     */
    public ReviewDTO getReviewById(int reviewId) {
        return dao.getReviewById(reviewId);
    }

    
    
    // 리뷰 등록
    /**
     * 새 리뷰 등록
     */
    public int insertReview(ReviewDTO dto) {
        return dao.insertReview(dto);
    }

    
    
    // 리뷰 수정
    /**
     * 리뷰 수정 (본인만 가능)
     * @param dto 수정할 데이터
     * @param loginMemberId 현재 로그인한 회원 번호
     * @return 성공 1, 권한없음 -1, 실패 0
     */
    public int updateReview(ReviewDTO dto, int loginMemberId) {
        // 본인 리뷰가 아니면 수정 불가
        if (dto.getMemberId() != loginMemberId) {
            return -1; // 권한 없음
        }
        return dao.updateReview(dto);
    }
    
    

    // 리뷰 삭제
    /**
     * 리뷰 삭제 (본인만 가능)
     * @param reviewId 삭제할 리뷰 번호
     * @param loginMemberId 현재 로그인한 회원 번호
     * @return 성공 1, 권한없음 or 리뷰없음 -1
     */
    public int deleteReview(int reviewId, int loginMemberId) {
        // 먼저 해당 리뷰 조회
        ReviewDTO dto = dao.getReviewById(reviewId);
        // 리뷰가 없거나 본인 리뷰가 아니면 삭제 불가
        if (dto == null || dto.getMemberId() != loginMemberId) {
            return -1;
        }
        return dao.deleteReview(reviewId, loginMemberId);
    }

    
    
    // 내 리뷰 목록
    /**
     * 로그인한 회원의 내 리뷰 목록 반환
     */
    public List<ReviewDTO> getMyReviewList(int memberId) {
        return dao.getMyReviewList(memberId);
    }

    
    
    // 리뷰 통계
    /**
     * 특정 영화의 신선도 통계 반환
     */
    public ReviewDTO getReviewStat(int movieId) {
        return dao.getReviewStat(movieId);
    }
}
