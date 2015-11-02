package tv.anypoint.utils;

/**
 * FileUtils 클래스에서 발생한 예외 처리를 담당.
 * 
 * @author manasobi
 * @since 1.0.0
 *
 */
public class FileUtilsException extends RuntimeException {

	private static final long serialVersionUID = -2026806190356733457L;

	public FileUtilsException(String msg) {
		super(msg);
	}

}
