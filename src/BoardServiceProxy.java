package board.board.service;
package board.aop;
import board.aop.LoggerAspect;
import board.board.dto.BoardDto;
import board.board.dto.BoardFileDto;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.dao.DataAccessException;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.List;

public class BoardServiceProxy implements  BoardService{

    LoggerAspect loggerAspect = new LoggerAspect();
    ProceedingJoinPoint proceedingJoinPoint;

    @Override
    public List<BoardDto> selectBoardList() throws Exception {

        try {
            Object = loggerAspect.logPrint(proceedingJoinPoint);
        }catch(Exception e){
            if(e instanceof DataAccessException)
            loggerAspect.afterThrowingAdvice(proceedingJoinPoint,e);
        }
        return Obejct;
    }

    @Override
    public void insertBoard(BoardDto board, MultipartHttpServletRequest multipartHttpServletRequest) throws Exception {

    }

    @Override
    public BoardDto selectBoardDetail(int boardIdx) throws Exception {
        return null;
    }

    @Override
    public void updateBoard(BoardDto board) throws Exception {

    }

    @Override
    public void deleteBoard(int boardIdx) throws Exception {

    }

    @Override
    public BoardFileDto selectBoardFileInformation(int idx, int boardIdx) throws Exception {
        return null;
    }
}
