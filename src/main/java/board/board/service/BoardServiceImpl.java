package board.board.service;


import java.util.List;

import board.board.dto.BoardFileDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import board.board.dto.BoardDto;
import board.board.dto.BoardFileReqDto;
import board.board.mapper.BoardMapper;
import board.common.FileUtils;



public class BoardServiceImpl implements BoardService{

	@Autowired
	private BoardMapper boardMapper;

	@Autowired
	private FileUtils fileUtils;

	//@Autowired
	//private PlatformTransactionManager platformTransactionManager;


	@Override
	public List<BoardDto> selectBoardList() throws Exception {
		return boardMapper.selectBoardList();
	}

	@Override
	public void insertBoard(BoardDto board, MultipartHttpServletRequest multipartHttpServletRequest) throws Exception {
		//TransactionStatus transactionStatus = platformTransactionManager.getTransaction(new DefaultTransactionDefinition());
//		Connection con = dataSource.getConnection();
//		con.setAutoCommit(false);
		try {
			boardMapper.insertBoard(board);
			List<BoardFileReqDto> list = fileUtils.parseFileInfo(board.getBoardIdx(), multipartHttpServletRequest);
			if(CollectionUtils.isEmpty(list) == false){
				boardMapper.insertBoardFileList(list);
			}
			//con.commit();
			//platformTransactionManager.commit(transactionStatus);
		}catch (Exception e){
			//con.rollback();
		//	platformTransactionManager.rollback(transactionStatus);
		}
		}

		@Override
		public BoardDto selectBoardDetail(int boardIdx) throws Exception{
			BoardDto board = boardMapper.selectBoardDetail(boardIdx);
			List<BoardFileDto> fileList = boardMapper.selectBoardFileList(boardIdx);
			board.setFileList(fileList);

			boardMapper.updateHitCount(boardIdx);

			return board;
		}

		@Override
		public void updateBoard(BoardDto board) throws Exception {
			boardMapper.updateBoard(board);
		}

		@Override
		public void deleteBoard(int boardIdx) throws Exception {
			boardMapper.deleteBoard(boardIdx);
		}

		@Override
		public BoardFileDto selectBoardFileInformation(int idx, int boardIdx) throws Exception {
			return boardMapper.selectBoardFileInformation(idx, boardIdx);
		}
}

