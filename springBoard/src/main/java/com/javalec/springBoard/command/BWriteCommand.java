package com.javalec.springBoard.command;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;

import com.javalec.springBoard.dao.BDao;

public class BWriteCommand implements BCommand {

	@Override
	public void execute(Model model) {
		
		// model�� ��ܼ� ���� ������� Map��� �̿��ؼ� request�� ���
		Map<String, Object> map = model.asMap() ;
		HttpServletRequest request = (HttpServletRequest) map.get("request") ;
		
		// write�Ҷ� ����ִ� ����� ��ü�� ���
		String bName = request.getParameter("bName") ;
		String bTitle = request.getParameter("bTitle") ;
		String bContent = request.getParameter("bContent") ;
		
		BDao dao = new BDao() ;
		dao.write(bName, bTitle, bContent);

	}

}