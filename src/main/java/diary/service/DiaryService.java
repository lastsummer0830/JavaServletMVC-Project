package diary.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import diary.dao.DiaryDAO;
import diary.dto.DiaryDTO;

/*
  [DiaryService]
  다이어리 비즈니스 로직 처리
  - DB 연결 관리 (Connection 획득/해제, 트랜잭션 처리)
  - 뱃지 동적 집계 로직 포함 (DB 저장 없이 Java 조건 판단)
 
  ※ DBUtil.getConnection() 경로는 확인하기(팀 설정)
     예: common.DBUtil, util.DBUtil, db.DBUtil 등
 */

public class DiaryService {
	
	private DiaryDAO diaryDAO = new DiaryDAO();
	
	// ════════════════════════════════════════════════════════
    // DB 연결 헬퍼 (팀 공통 DBUtil 경로에 맞게 수정)
    // ════════════════════════════════════════════════════════
	private Connection getConnection() throws SQLException {		
		// TODO: 팀 프로젝트의 실제 DBUtil 경로로 변경
        // 예: return common.DBUtil.getConnection();
        //     return util.DBUtil.getConnection();
		
		try {
			return common.DBUtil.getConnection();
		} catch (Exception e) {
			// TODO: handle exception
			throw new SQLException("DB 연결 실패: " + e.getMessage());
		}
	
	}
	
	// ════════════════════════════════════════════════════════
    // 1. 다이어리 목록 조회 (각 항목에 태그 목록도 세팅)
    // ════════════════════════════════════════════════════════
    public List<DiaryDTO> getDiaryList(int memberId, int year, String sort) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            List<DiaryDTO> list = diaryDAO.getDiaryList(conn, memberId, year, sort);

            // 각 다이어리에 태그 목록 세팅
            for (DiaryDTO dto : list) {
                List<String> tags = diaryDAO.getTagsByDiaryId(conn, dto.getDiaryId());
                dto.setTagNames(tags);
            }

            return list;
        } finally {
            if (conn != null) conn.close();
        }
    }

}
