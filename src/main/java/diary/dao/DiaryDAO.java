package diary.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import diary.dto.DiaryDTO;

/*
  [DiaryDAO]
  DIARY_ENTRY, DIARY_TAG, TAG, MOVIE 테이블 관련 DB 처리
  모든 메서드는 Connection을 외부(Service)에서 받아 처리 (트랜잭션 관리를 Service에서)
 
  ※ DB 연결 방식: 팀 공통 DBUtil.getConnection() 사용
    → import 경로가 다르면 해당 부분만 수정
 */

public class DiaryDAO {
	
	// ════════════════════════════════════════════════════════
    // 1. 다이어리 목록 조회
    //    - member_id 기준, MOVIE 조인하여 title/poster_url 포함
    //    - watch_date 기준 연도 필터 (year 파라미터)
    //    - sort: "latest"(최신순, 기본), "oldest"(오래된순), "star"(별점높은순)
    // ════════════════════════════════════════════════════════
	public List<DiaryDTO> getDiaryList(Connection conn, int memberId, int year, String sort) throws SQLException {
        List<DiaryDTO> list = new ArrayList<>();

        // 정렬 조건 - SQL Injection 방지를 위해 화이트리스트로 처리
        String orderBy;
        if ("oldest".equals(sort)) {
            orderBy = "d.WATCH_DATE ASC";
        } else if ("star".equals(sort)) {
            orderBy = "d.STAR_RATING DESC NULLS LAST, d.WATCH_DATE DESC";
        } else {
            orderBy = "d.WATCH_DATE DESC"; // 기본: 최신순
        }

        String sql = "SELECT d.DIARY_ID, d.MEMBER_ID, d.MOVIE_ID, d.RESERVATION_ID, d.REVIEW_ID, "
                   + "       TO_CHAR(d.WATCH_DATE, 'YYYY-MM-DD') AS WATCH_DATE, "
                   + "       d.STAR_RATING, TO_CHAR(d.CREATED_AT, 'YYYY-MM-DD HH24:MI') AS CREATED_AT, "
                   + "       m.TITLE, m.POSTER_URL "
                   + "FROM DIARY_ENTRY d "
                   + "JOIN MOVIE m ON d.MOVIE_ID = m.MOVIE_ID "
                   + "WHERE d.MEMBER_ID = ? "
                   + "AND EXTRACT(YEAR FROM d.WATCH_DATE) = ? "
                   + "ORDER BY " + orderBy;

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, memberId);
        pstmt.setInt(2, year);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            DiaryDTO dto = new DiaryDTO();
            dto.setDiaryId(rs.getInt("DIARY_ID"));
            dto.setMemberId(rs.getInt("MEMBER_ID"));
            dto.setMovieId(rs.getInt("MOVIE_ID"));
            dto.setReservationId(rs.getInt("RESERVATION_ID"));
            dto.setReviewId(rs.getInt("REVIEW_ID"));
            dto.setWatchDate(rs.getString("WATCH_DATE"));
            dto.setStarRating(rs.getDouble("STAR_RATING"));
            dto.setCreatedAt(rs.getString("CREATED_AT"));
            dto.setTitle(rs.getString("TITLE"));
            dto.setPosterUrl(rs.getString("POSTER_URL"));
            list.add(dto);
        }

        rs.close();
        pstmt.close();
        return list;
    }

    // ════════════════════════════════════════════════════════
    // 2. 달력용 데이터 조회 (AJAX)
    //    - 특정 연월(year, month)에 해당하는 다이어리만 반환
    //    - watch_date와 title만 포함 (달력에 티켓 표시용)
    // ════════════════════════════════════════════════════════
    public List<DiaryDTO> getDiaryByMonth(Connection conn, int memberId, int year, int month) throws SQLException {
        List<DiaryDTO> list = new ArrayList<>();

        String sql = "SELECT d.DIARY_ID, TO_CHAR(d.WATCH_DATE, 'YYYY-MM-DD') AS WATCH_DATE, "
                   + "       m.TITLE, m.POSTER_URL "
                   + "FROM DIARY_ENTRY d "
                   + "JOIN MOVIE m ON d.MOVIE_ID = m.MOVIE_ID "
                   + "WHERE d.MEMBER_ID = ? "
                   + "AND EXTRACT(YEAR FROM d.WATCH_DATE) = ? "
                   + "AND EXTRACT(MONTH FROM d.WATCH_DATE) = ? "
                   + "ORDER BY d.WATCH_DATE ASC";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, memberId);
        pstmt.setInt(2, year);
        pstmt.setInt(3, month);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            DiaryDTO dto = new DiaryDTO();
            dto.setDiaryId(rs.getInt("DIARY_ID"));
            dto.setWatchDate(rs.getString("WATCH_DATE"));
            dto.setTitle(rs.getString("TITLE"));
            dto.setPosterUrl(rs.getString("POSTER_URL"));
            list.add(dto);
        }

        rs.close();
        pstmt.close();
        return list;
    }

    // ════════════════════════════════════════════════════════
    // 3. 다이어리 상세 조회
    //    - diary_id 기준, MOVIE 조인
    //    - 태그 목록은 별도 메서드(getTagsByDiaryId)로 조회
    // ════════════════════════════════════════════════════════
    public DiaryDTO getDiaryDetail(Connection conn, int diaryId) throws SQLException {
        DiaryDTO dto = null;

        String sql = "SELECT d.DIARY_ID, d.MEMBER_ID, d.MOVIE_ID, d.RESERVATION_ID, d.REVIEW_ID, "
                   + "       TO_CHAR(d.WATCH_DATE, 'YYYY-MM-DD') AS WATCH_DATE, "
                   + "       d.STAR_RATING, TO_CHAR(d.CREATED_AT, 'YYYY-MM-DD HH24:MI') AS CREATED_AT, "
                   + "       m.TITLE, m.POSTER_URL "
                   + "FROM DIARY_ENTRY d "
                   + "JOIN MOVIE m ON d.MOVIE_ID = m.MOVIE_ID "
                   + "WHERE d.DIARY_ID = ?";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, diaryId);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            dto = new DiaryDTO();
            dto.setDiaryId(rs.getInt("DIARY_ID"));
            dto.setMemberId(rs.getInt("MEMBER_ID"));
            dto.setMovieId(rs.getInt("MOVIE_ID"));
            dto.setReservationId(rs.getInt("RESERVATION_ID"));
            dto.setReviewId(rs.getInt("REVIEW_ID"));
            dto.setWatchDate(rs.getString("WATCH_DATE"));
            dto.setStarRating(rs.getDouble("STAR_RATING"));
            dto.setCreatedAt(rs.getString("CREATED_AT"));
            dto.setTitle(rs.getString("TITLE"));
            dto.setPosterUrl(rs.getString("POSTER_URL"));
        }

        rs.close();
        pstmt.close();
        return dto;
    }

    // ════════════════════════════════════════════════════════
    // 4. 다이어리에 붙은 태그 목록 조회
    //    - diary_id 기준으로 TAG 조인
    // ════════════════════════════════════════════════════════
    public List<String> getTagsByDiaryId(Connection conn, int diaryId) throws SQLException {
        List<String> tags = new ArrayList<>();

        String sql = "SELECT t.TAG_NAME "
                   + "FROM DIARY_TAG dt "
                   + "JOIN TAG t ON dt.TAG_ID = t.TAG_ID "
                   + "WHERE dt.DIARY_ID = ? "
                   + "ORDER BY t.TAG_ID";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, diaryId);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            tags.add(rs.getString("TAG_NAME"));
        }

        rs.close();
        pstmt.close();
        return tags;
    }

    // ════════════════════════════════════════════════════════
    // 5. 전체 태그 목록 조회 (태그 선택 화면에 뿌리기용)
    // ════════════════════════════════════════════════════════
    public List<diary.dto.DiaryDTO> getAllTags(Connection conn) throws SQLException {
        // TAG 테이블 전체 조회는 간단하므로 Map 대신 별도 TagDTO 없이 DiaryDTO List로 전달
        // 실제로는 tagId/tagName만 필요하므로 Map<Integer, String> 반환이 더 깔끔함
        List<diary.dto.DiaryDTO> dummy = new ArrayList<>(); // 사용 안 함 - 아래 메서드 사용
        return dummy;
    }

    /**
     * TAG 테이블 전체 조회 → Map<tagId, tagName>
     * filmDiary.jsp에서 태그 선택 팝업에 사용
     */
    public Map<Integer, String> getAllTagMap(Connection conn) throws SQLException {
        Map<Integer, String> map = new LinkedHashMap<>(); // 입력 순서 유지

        String sql = "SELECT TAG_ID, TAG_NAME FROM TAG ORDER BY TAG_ID";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            map.put(rs.getInt("TAG_ID"), rs.getString("TAG_NAME"));
        }

        rs.close();
        pstmt.close();
        return map;
    }

    // ════════════════════════════════════════════════════════
    // 6. 태그 수정 (기존 태그 전체 삭제 후 새 태그 INSERT)
    //    - diary_id에 해당하는 DIARY_TAG 행 삭제 후 tagIds 기준 재등록
    // ════════════════════════════════════════════════════════
    public void deleteTagsByDiaryId(Connection conn, int diaryId) throws SQLException {
        String sql = "DELETE FROM DIARY_TAG WHERE DIARY_ID = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, diaryId);
        pstmt.executeUpdate();
        pstmt.close();
    }

    public void insertDiaryTag(Connection conn, int diaryId, int tagId) throws SQLException {
        String sql = "INSERT INTO DIARY_TAG (DIARY_ID, TAG_ID) VALUES (?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, diaryId);
        pstmt.setInt(2, tagId);
        pstmt.executeUpdate();
        pstmt.close();
    }

    // ════════════════════════════════════════════════════════
    // 7. 다이어리 자동 등록 (예매 완료 시 호출)
    //    - reservation_id가 있으면 UNIQUE 제약으로 중복 방지
    // ════════════════════════════════════════════════════════
    public void insertDiary(Connection conn, DiaryDTO dto) throws SQLException {
        String sql = "INSERT INTO DIARY_ENTRY (MEMBER_ID, MOVIE_ID, RESERVATION_ID, REVIEW_ID, WATCH_DATE, STAR_RATING) "
                   + "VALUES (?, ?, ?, ?, TO_DATE(?, 'YYYY-MM-DD'), ?)";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, dto.getMemberId());
        pstmt.setInt(2, dto.getMovieId());

        // reservation_id가 0이면 NULL 처리
        if (dto.getReservationId() == 0) {
            pstmt.setNull(3, Types.INTEGER);
        } else {
            pstmt.setInt(3, dto.getReservationId());
        }

        // review_id가 0이면 NULL 처리
        if (dto.getReviewId() == 0) {
            pstmt.setNull(4, Types.INTEGER);
        } else {
            pstmt.setInt(4, dto.getReviewId());
        }

        pstmt.setString(5, dto.getWatchDate());

        // star_rating이 0.0이면 NULL 처리
        if (dto.getStarRating() == 0.0) {
            pstmt.setNull(6, Types.NUMERIC);
        } else {
            pstmt.setDouble(6, dto.getStarRating());
        }

        pstmt.executeUpdate();
        pstmt.close();
    }

    // ════════════════════════════════════════════════════════
    // 8. 별점 + 리뷰 연동 수정
    //    - 상세 페이지에서 별점/리뷰 연동 변경 시 사용
    // ════════════════════════════════════════════════════════
    public void updateStarAndReview(Connection conn, int diaryId, double starRating, int reviewId) throws SQLException {
        String sql = "UPDATE DIARY_ENTRY SET STAR_RATING = ?, REVIEW_ID = ? WHERE DIARY_ID = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);

        if (starRating == 0.0) {
            pstmt.setNull(1, Types.NUMERIC);
        } else {
            pstmt.setDouble(1, starRating);
        }

        if (reviewId == 0) {
            pstmt.setNull(2, Types.INTEGER);
        } else {
            pstmt.setInt(2, reviewId);
        }

        pstmt.setInt(3, diaryId);
        pstmt.executeUpdate();
        pstmt.close();
    }

    // ════════════════════════════════════════════════════════
    // 9. 연도 목록 조회 (사이드바 연도별 폴더용)
    //    - 해당 회원의 다이어리가 존재하는 연도만 반환
    // ════════════════════════════════════════════════════════
    public List<Integer> getYearList(Connection conn, int memberId) throws SQLException {
        List<Integer> years = new ArrayList<>();

        String sql = "SELECT DISTINCT EXTRACT(YEAR FROM WATCH_DATE) AS YR "
                   + "FROM DIARY_ENTRY "
                   + "WHERE MEMBER_ID = ? "
                   + "ORDER BY YR DESC";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, memberId);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            years.add(rs.getInt("YR"));
        }

        rs.close();
        pstmt.close();
        return years;
    }

    // ════════════════════════════════════════════════════════
    // 10. 연간 통계용 조회 (stat.do 용)
    //     - 해당 연도 전체 다이어리 (장르/별점/태그 포함)
    // ════════════════════════════════════════════════════════

    /* 총 관람 편수 */
    public int getTotalCountByYear(Connection conn, int memberId, int year) throws SQLException {
        String sql = "SELECT COUNT(*) FROM DIARY_ENTRY "
                   + "WHERE MEMBER_ID = ? AND EXTRACT(YEAR FROM WATCH_DATE) = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, memberId);
        pstmt.setInt(2, year);
        ResultSet rs = pstmt.executeQuery();
        int count = 0;
        if (rs.next()) count = rs.getInt(1);
        rs.close(); pstmt.close();
        return count;
    }

    /* 평균 별점 */
    public double getAvgStarByYear(Connection conn, int memberId, int year) throws SQLException {
        String sql = "SELECT NVL(AVG(STAR_RATING), 0) FROM DIARY_ENTRY "
                   + "WHERE MEMBER_ID = ? AND EXTRACT(YEAR FROM WATCH_DATE) = ? AND STAR_RATING IS NOT NULL";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, memberId);
        pstmt.setInt(2, year);
        ResultSet rs = pstmt.executeQuery();
        double avg = 0.0;
        if (rs.next()) avg = rs.getDouble(1);
        rs.close(); pstmt.close();
        return avg;
    }

    /* 월별 관람 편수 배열 (index 0=1월 ~ 11=12월) */
    public int[] getMonthlyCountByYear(Connection conn, int memberId, int year) throws SQLException {
        int[] monthly = new int[12];

        String sql = "SELECT EXTRACT(MONTH FROM WATCH_DATE) AS MON, COUNT(*) AS CNT "
                   + "FROM DIARY_ENTRY "
                   + "WHERE MEMBER_ID = ? AND EXTRACT(YEAR FROM WATCH_DATE) = ? "
                   + "GROUP BY EXTRACT(MONTH FROM WATCH_DATE) "
                   + "ORDER BY MON";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, memberId);
        pstmt.setInt(2, year);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            int mon = rs.getInt("MON");
            monthly[mon - 1] = rs.getInt("CNT"); // 0-index
        }

        rs.close(); pstmt.close();
        return monthly;
    }

    /* 감정 태그 빈도 조회 (해당 연도, 빈도 높은 순) */
    public List<Map.Entry<String, Integer>> getTagFrequencyByYear(Connection conn, int memberId, int year) throws SQLException {
        Map<String, Integer> freqMap = new LinkedHashMap<>();

        String sql = "SELECT t.TAG_NAME, COUNT(*) AS CNT "
                   + "FROM DIARY_TAG dt "
                   + "JOIN TAG t ON dt.TAG_ID = t.TAG_ID "
                   + "JOIN DIARY_ENTRY d ON dt.DIARY_ID = d.DIARY_ID "
                   + "WHERE d.MEMBER_ID = ? AND EXTRACT(YEAR FROM d.WATCH_DATE) = ? "
                   + "GROUP BY t.TAG_NAME "
                   + "ORDER BY CNT DESC";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, memberId);
        pstmt.setInt(2, year);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            freqMap.put(rs.getString("TAG_NAME"), rs.getInt("CNT"));
        }

        rs.close(); pstmt.close();
        return new ArrayList<>(freqMap.entrySet());
    }

    /* 뱃지 판단을 위한 전체 다이어리 수 조회 (전체 연도) */
    public int getTotalCountAll(Connection conn, int memberId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM DIARY_ENTRY WHERE MEMBER_ID = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, memberId);
        ResultSet rs = pstmt.executeQuery();
        int count = 0;
        if (rs.next()) count = rs.getInt(1);
        rs.close(); pstmt.close();
        return count;
    }
	}

