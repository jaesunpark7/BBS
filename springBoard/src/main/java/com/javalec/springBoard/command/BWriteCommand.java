package com.javalec.springBoard.command;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;

import com.javalec.springBoard.dao.BDao;

public class BWriteCommand implements BCommand {

	@Override
	public void execute(Model model) {
		
		// model에 담겨서 들어온 내용들을 Map기능 이용해서 request로 담기
		Map<String, Object> map = model.asMap() ;
		HttpServletRequest request = (HttpServletRequest) map.get("request") ;
		
		// write할때 들어있는 내용들 객체에 담기
		String bName = request.getParameter("bName") ;
		String bTitle = request.getParameter("bTitle") ;
		String bContent = request.getParameter("bContent") ;
		
		BDao dao = new BDao() ;
		dao.write(bName, bTitle, bContent);

	}

}
