package review.dao;

import review.dto.ReviewDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {

    private Connection getConnection() throws Exception {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        return DriverManager.getConnection(
            "jdbc:oracle:thin:@localhost:1521:xe", "아이디", "비밀번호"
        );
    }

    // 리뷰 목록 조회 (영화별)

    /**
     * 특정 영화의 공개 리뷰 목록 가져오기
     * @param movieId 조회할 영화 번호
     * @return 리뷰 목록 (최신순 정렬)
     */
    public List<ReviewDTO> getReviewListByMovie(int movieId) {

        List<ReviewDTO> list = new ArrayList<>();

        // REVIEW + MEMBER 테이블 JOIN → 작성자 이름도 함께 가져옴
        // public_yn = 'Y' 인 것만 (공개 리뷰만 조회)
        String sql = "SELECT r.review_id, r.movie_id, r.member_id, r.fresh_yn, " +
                     "r.public_yn, r.content, r.created_at, r.updated_at, m.member_name " +
                     "FROM review r JOIN member m ON r.member_id = m.member_id " +
                     "WHERE r.movie_id = ? AND r.public_yn = 'Y' " +
                     "ORDER BY r.created_at DESC"; // 최신순 정렬

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, movieId); // ?에 movieId 값 넣기
            ResultSet rs = ps.executeQuery();

           
            while (rs.next()) {
                ReviewDTO dto = new ReviewDTO();
            
                dto.setReviewId(rs.getInt("review_id"));
                dto.setMovieId(rs.getInt("movie_id"));
                dto.setMemberId(rs.getInt("member_id"));
                dto.setFreshYn(rs.getString("fresh_yn"));
                dto.setPublicYn(rs.getString("public_yn"));
                dto.setContent(rs.getString("content"));
                dto.setCreatedAt(rs.getString("created_at"));
                dto.setUpdatedAt(rs.getString("updated_at"));
                dto.setMemberName(rs.getString("member_name")); // JOIN 결과
                list.add(dto); // 리스트에 추가
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

   
    // 리뷰 단건 조회
   
    /**
     * 특정 리뷰 1개 가져오기
     * @param reviewId 조회할 리뷰 번호
     * @return ReviewDTO (없으면 null)
     */
    public ReviewDTO getReviewById(int reviewId) {

        ReviewDTO dto = null;

        String sql = "SELECT r.*, m.member_name " +
                     "FROM review r JOIN member m ON r.member_id = m.member_id " +
                     "WHERE r.review_id = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, reviewId);
            ResultSet rs = ps.executeQuery();

            // 결과가 있으면 DTO에 담기
            if (rs.next()) {
                dto = new ReviewDTO();
                dto.setReviewId(rs.getInt("review_id"));
                dto.setMovieId(rs.getInt("movie_id"));
                dto.setMemberId(rs.getInt("member_id"));
                dto.setFreshYn(rs.getString("fresh_yn"));
                dto.setPublicYn(rs.getString("public_yn"));
                dto.setContent(rs.getString("content"));
                dto.setCreatedAt(rs.getString("created_at"));
                dto.setUpdatedAt(rs.getString("updated_at"));
                dto.setMemberName(rs.getString("member_name"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dto;
    }

    
    // 리뷰 등록
    

    /**
     * 새 리뷰 DB에 저장
     * @param dto 저장할 리뷰 데이터
     * @return 성공 1, 실패 0
     */
    public int insertReview(ReviewDTO dto) {

        int result = 0;

        // review_id는 시퀀스 자동증가이므로 INSERT에서 제외
        // created_at, updated_at은 DEFAULT SYSTIMESTAMP 사용
        String sql = "INSERT INTO review (movie_id, member_id, fresh_yn, public_yn, content) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, dto.getMovieId());
            ps.setInt(2, dto.getMemberId());
            ps.setString(3, dto.getFreshYn());
            ps.setString(4, dto.getPublicYn());
            ps.setString(5, dto.getContent());
            result = ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    
    // 리뷰 수정
    

    /**
     * 리뷰 내용 수정
     * @param dto 수정할 데이터 (reviewId, memberId 필수!)
     * @return 성공 1, 실패 0
     */
    public int updateReview(ReviewDTO dto) {

        int result = 0;

        // AND member_id=? → 본인 리뷰만 수정 가능하도록 보안 처리
        // updated_at은 수정 시 현재 시간으로 자동 갱신
        String sql = "UPDATE review " +
                     "SET fresh_yn=?, public_yn=?, content=?, updated_at=SYSTIMESTAMP " +
                     "WHERE review_id=? AND member_id=?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dto.getFreshYn());
            ps.setString(2, dto.getPublicYn());
            ps.setString(3, dto.getContent());
            ps.setInt(4, dto.getReviewId());
            ps.setInt(5, dto.getMemberId()); // 본인 확인
            result = ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    
    // 리뷰 삭제
    

    /**
     * 리뷰 삭제
     * @param reviewId 삭제할 리뷰 번호
     * @param memberId 삭제 요청한 회원 번호 (본인 확인용)
     * @return 성공 1, 실패 0
     */
    public int deleteReview(int reviewId, int memberId) {

        int result = 0;

        // AND member_id=? → 본인 리뷰만 삭제 가능
        String sql = "DELETE FROM review WHERE review_id=? AND member_id=?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, reviewId);
            ps.setInt(2, memberId); // 본인 확인
            result = ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    
    // 내 리뷰 목록 조회
    

    /**
     * 로그인한 회원의 내 리뷰 목록 가져오기
     * @param memberId 로그인한 회원 번호
     * @return 내 리뷰 목록 (최신순)
     */
    public List<ReviewDTO> getMyReviewList(int memberId) {

        List<ReviewDTO> list = new ArrayList<>();

        // 내 리뷰는 비공개 포함 전부 보임 (public_yn 조건 없음)
        String sql = "SELECT r.*, m.member_name " +
                     "FROM review r JOIN member m ON r.member_id = m.member_id " +
                     "WHERE r.member_id = ? ORDER BY r.created_at DESC";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, memberId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ReviewDTO dto = new ReviewDTO();
                dto.setReviewId(rs.getInt("review_id"));
                dto.setMovieId(rs.getInt("movie_id"));
                dto.setMemberId(rs.getInt("member_id"));
                dto.setFreshYn(rs.getString("fresh_yn"));
                dto.setPublicYn(rs.getString("public_yn"));
                dto.setContent(rs.getString("content"));
                dto.setCreatedAt(rs.getString("created_at"));
                dto.setMemberName(rs.getString("member_name"));
                list.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    
    // 리뷰 통계 조회
    

    /**
     * 특정 영화의 신선도 통계 가져오기
     * @param movieId 통계 낼 영화 번호
     * @return ReviewDTO에 통계 필드 채워서 반환
     */
    public ReviewDTO getReviewStat(int movieId) {
        ReviewDTO stat = new ReviewDTO();

        String sql = "SELECT COUNT(*) AS total_count, " +
                     "SUM(CASE WHEN fresh_yn='Y' THEN 1 ELSE 0 END) AS fresh_count " +
                     "FROM review WHERE movie_id=? AND public_yn='Y'";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, movieId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int total = rs.getInt("total_count");
                int fresh = rs.getInt("fresh_count");

                stat.setMovieId(movieId);
                stat.setTotalCount(total);
                stat.setFreshCount(fresh);
                stat.setNotFreshCount(total - fresh); // 비신선 = 전체 - 신선
                // 리뷰가 0개면 0.0으로 처리 (0으로 나누기 방지)
                stat.setFreshRate(total > 0 ? (fresh * 100.0 / total) : 0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return stat;
    }
}
