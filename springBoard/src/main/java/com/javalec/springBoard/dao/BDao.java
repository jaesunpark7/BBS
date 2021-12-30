package com.javalec.springBoard.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.javalec.springBoard.dto.BDto;
import com.javalec.springBoard.util.Constant;

public class BDao {
	
	DataSource dataSource;
	
	// Jdbc 사용하기위해서 Template 만들어 둔 거 가져오기
	JdbcTemplate template = null;
	
	public BDao() {
		
		// DB와 연결할 수 있는 Constant template 할당하는 것으로 위의 소스코드 단순화
		template = Constant.template;
	}
	
	public ArrayList<BDto> list() {
		
		String query = "SELECT bId, bName, bTitle, bContent, bDate, bHit, bGroup, bStep, bIndent FROM MVC_BOARD ORDER BY bGroup DESC, bStep ASC";
		return (ArrayList<BDto>) template.query(query, new BeanPropertyRowMapper<BDto>(BDto.class));
		
		// 이렇게 길었던 내용을 위의 2줄로 변경한 것 (Jdbc이용)
//		ArrayList<BDto> dtos = new ArrayList<BDto>();
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//		
//		try {
//			connection = dataSource.getConnection();
//			
//			String query = "select bId, bName, bTitle, bContent, bDate, bHit, bGroup, bStep, bIndent from mvc_board order by bGroup desc, bStep asc";
//			preparedStatement = connection.prepareStatement(query);
//			resultSet = preparedStatement.executeQuery();
//			
//			while (resultSet.next()) {
//				int bId = resultSet.getInt("bId");
//				String bName = resultSet.getString("bName");
//				String bTitle = resultSet.getString("bTitle");
//				String bContent = resultSet.getString("bContent");
//				Timestamp bDate = resultSet.getTimestamp("bDate");
//				int bHit = resultSet.getInt("bHit");
//				int bGroup = resultSet.getInt("bGroup");
//				int bStep = resultSet.getInt("bStep");
//				int bIndent = resultSet.getInt("bIndent");
//				
//				BDto dto = new BDto(bId, bName, bTitle, bContent, bDate, bHit, bGroup, bStep, bIndent);
//				dtos.add(dto);
//			}
//			
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		} finally {
//			try {
//				if(resultSet != null) resultSet.close();
//				if(preparedStatement != null) preparedStatement.close();
//				if(connection != null) connection.close();
//			} catch (Exception e2) {
//				// TODO: handle exception
//				e2.printStackTrace();
//			}
//		}
//		return dtos;
	}
	
	public void write(final String bName, final String bTitle, final String bContent) {
			
		template.update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				
				String query = "INSERT INTO MVC_BOARD (bId, bName, bTitle, bContent, bHit, bGroup, bStep, bIndent) "
						+ "VALUES (MVC_BOARD_SEQ.NEXTVAL, ?, ?, ?, 0, MVC_BOARD_SEQ.CURRVAL, 0, 0 )";
				PreparedStatement pstmt = con.prepareStatement(query);
				pstmt.setString(1, bName);
				pstmt.setString(2, bTitle);
				pstmt.setString(3, bContent);
				return pstmt;
			}			
		});
	}
	
	// Content 내용 호출용
	public BDto contentView(String strID) {
		// TODO Auto-generated method stub
		
		upHit(strID);
		
		String query = "SELECT * FROM MVC_BOARD WHERE bId = " + strID;
		// 화면에 보여줄 결과를 받아와야 하기 때문에 QueryForObject 사용
		return template.queryForObject(query, new BeanPropertyRowMapper<BDto>(BDto.class));
		
	}
	
	public void modify(final String bId, final String bName, final String bTitle, final String bContent) {
		
		String query = "UPDATE MVC_BOARD SET bName = ?, bTitle = ?, bContent =? WHERE bId = ?";
		// 화면에 반환되는 값이 있는게 아니라 query만 실행하면 되는거니까 update 사용
		template.update(query, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, bName);
				ps.setString(2, bTitle);
				ps.setString(3, bContent);
				ps.setInt(4, Integer.parseInt(bId));
			}
			
		});
	}
	
	public void delete(final String bId) {
		
		String query = "DELETE FROM MVC_BOARD WHERE bId = ?";
		// 화면에 반환되는 값이 있는게 아니라 query만 실행하면 되는거니까 update 사용
		template.update(query, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, bId);
			}
			
		});
		
	}
	
	public BDto reply_view(String strID) {
		
		String query = "SELECT * FROM MVC_BOARD WHERE bId = ";
		// 화면에 보여줄 결과를 받아와야 하기 때문에 QueryForObject 사용
		return template.queryForObject(query, new BeanPropertyRowMapper<BDto>(BDto.class));
	}
	
	public void reply(final String bId, final String bName, final String bTitle, final String bContent, final String bGroup, final String bStep, final String bIndent) {

		replyShape(bGroup, bStep);
		
		String query = "INSERT INTO MVC_BOARD (bId, bName, bTitle, bContent, bGroup, bStep, bIndent) VALUES (MVC_BOARD_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?)";
		
		template.update(query, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, bName);
				ps.setString(2, bTitle);
				ps.setString(3, bContent);
				ps.setInt(4, Integer.parseInt(bGroup));
				ps.setInt(5, Integer.parseInt(bStep) + 1);
				ps.setInt(6, Integer.parseInt(bIndent) + 1);
			}
		});
		
	}
	
	// Hit수 증가 로직 정의
	public void upHit(final String bId) {
		
		String query = "UPDATE MVC_BOARD SET bHit = bHit + 1 WHERE bId = ?" ;
		template.update(query, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, Integer.parseInt(bId));
			}
			
		});
	}
	
	// 답변에서 bGroup, bStep +1씩 증가시켜주는 로직 정의
	public void replyShape(final String strGroup, final String strStep) {
		
		String query = "UPDATE MVC_BOARD SET bStep = bStep + 1 WHERE bGroup = ? and bStep > ?";
		
		template.update(query, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, Integer.parseInt(strGroup));
				ps.setInt(2, Integer.parseInt(strStep));
			}
			
		});
	}

}
