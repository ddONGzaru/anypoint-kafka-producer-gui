/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tv.anypoint.utils;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.*;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 파일 및 디렉토리 복사, 삭제, 초기화 등에 관련한 편의 기능들을 제공한다.<br>
 * 
 * @author manasobi
 * @since 1.0.0
 */
public final class FileUtils {

	private FileUtils() { }
	
	private static Result buildFailResult(Result result, String errMsg) {
		
		result = Result.FAIL;
		result.setMessage(errMsg);
		
		return result;
	}
	
	/**
     * byte단위의 파일 사이즈를 읽기 쉽도록 EB, PB, TB, GB, MB, KB, bytes 단위로 파싱하여 반환한다.
     * 
     * @param file 사이즈 측정에 사용될 파일
     * @return EB, PB, TB, GB, MB, KB, bytes 단위의 파일 사이즈
     */
    public static String byteCountToDisplaySize(File file) {
    	return org.apache.commons.io.FileUtils.byteCountToDisplaySize(file.length());    	
    }
	
	/**
     * byte단위의 파일 사이즈를 읽기 쉽도록 EB, PB, TB, GB, MB, KB, bytes 단위로 파싱하여 반환한다.
     * 
     * @param size bytes 단위의 파일 크기
     * @return EB, PB, TB, GB, MB, KB, bytes 단위의 파일 사이즈
     */
    public static String byteCountToDisplaySize(long size) {
    	return org.apache.commons.io.FileUtils.byteCountToDisplaySize(size);    	
    }

	/**
	 * 루트 디렉토리 내의 파일 및 하위 디렉토리를 삭제한다. 루트 디렉토리는 삭제하지 않는다.
	 * 
	 * @param dir 루트 디렉토리 File
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result cleanDir(File dir) {

		Result result = Result.EMPTY;
		
		if (notExistsDir(dir)) {
			return buildFailResult(result, dir + "가 존재하지 않습니다.");
		}

		if (isNotDir(dir)) {
			return buildFailResult(result, dir + "는 디렉토리가 아닙니다.");
		}

		try {
			org.apache.commons.io.FileUtils.cleanDirectory(dir);
			return Result.SUCCESS;
		} catch (Exception e) {
			return buildFailResult(result, e.getMessage());
		}
	}

	/**
	 * 루트 디렉토리 내의 파일 및 하위 디렉토리를 삭제한다. 루트 디렉토리는 삭제하지 않는다.
	 * 
	 * @param dir 루트 디렉토리 명
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result cleanDir(String dir) {
		return cleanDir(new File(dir));
	}

	/**
	 * 원본디렉토리를 대상 디렉토리명으로 복사한다. 입력된 파일 확장자랑 일치하는 파일들만 복사한다.
	 * 
	 * @param srcDir  원본 디렉토리
	 * @param destDir 대상 디렉토리
	 * @param extList 파일 확장자들
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result copyDirAfterCheckFileExt(File srcDir, File destDir, String... extList) {
		return copyDirAfterCheckFileExt(srcDir, destDir, true, extList);
	}

	/**
	 * 원본디렉토리를 대상 디렉토리명으로 복사한다. 입력된 파일 확장자랑 일치하는 파일들만 복사한다.<br>
	 * preserveFileDate가 true면 원본디렉토리의 파일 최종 수정일을 그대로 사용하고 false면 현재 일자를 최종 수정일로
	 * 설정한다.
	 * 
	 * @param srcDir  원본 디렉토리
	 * @param destDir 대상 디렉토리
	 * @param extList 파일 확장자들
	 * @param preserveFileDate 수정일 설정 플래그
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result copyDirAfterCheckFileExt(File srcDir, File destDir, boolean preserveFileDate, String... extList) {

		Result result = Result.EMPTY;
		
		if (notExistsDir(srcDir)) {
			return buildFailResult(result, srcDir + "가 존재하지 않습니다.");			
		}

		if (isNotDir(srcDir)) {
			return buildFailResult(result, srcDir + "는 디렉토리가 아닙니다.");
		}
		
		if (notExistsDir(destDir)) {
			
			result = createDir(destDir.getAbsolutePath());
			
			if (result == Result.FAIL) {
				return buildFailResult(result, destDir + " 디렉토리 생성 중에 에러가 발생하였습니다.");
			}
		}
		
		if (isNotDir(destDir)) {
			return buildFailResult(result, destDir + "는 디렉토리가 아닙니다.");
		}
		
		IOFileFilter suffixFilters = new SuffixFileFilter(extList, IOCase.INSENSITIVE);
		FileFilter filter = FileFilterUtils.or(DirectoryFileFilter.DIRECTORY, suffixFilters);
		
		try {
			org.apache.commons.io.FileUtils.copyDirectory(srcDir, destDir, filter, preserveFileDate);
		} catch (Exception e) {
			return buildFailResult(result, e.getMessage());
		}

		return Result.SUCCESS;
	}

	/**
	 * 원본디렉토리를 대상 디렉토리명으로 복사한다. 입력된 파일 확장자랑 일치하는 파일들만 복사한다.
	 * 
	 * @param srcDir  원본 디렉토리
	 * @param destDir 대상 디렉토리
	 * @param extList 파일 확장자들
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result copyDirAfterCheckFileExt(String srcDir, String destDir, String... extList) {
		return copyDirAfterCheckFileExt(srcDir, destDir, true, extList);
	}

	/**
	 * 원본디렉토리를 대상 디렉토리명으로 복사한다. 입력된 파일 확장자랑 일치하는 파일들만 복사한다.<br>
	 * preserveFileDate가 true면 원본디렉토리의 파일 최종 수정일을 그대로 사용하고 false면 현재 일자를 최종 수정일로
	 * 설정한다.
	 * 
	 * @param srcDir  원본 디렉토리
	 * @param destDir 대상 디렉토리
	 * @param extList 파일 확장자들
	 * @param preserveFileDate 수정일 설정 플래그
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result copyDirAfterCheckFileExt(String srcDir, String destDir, boolean preserveFileDate, String... extList) {
		return copyDirAfterCheckFileExt(new File(srcDir), new File(destDir), preserveFileDate, extList);
	}

	/**
	 * 원본 파일을 대상 파일명으로 복사한다
	 * 
	 * @param srcFile  원본 파일
	 * @param destFile 대상 파일
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result copyFile(File srcFile, File destFile) {
		return copyFile(srcFile, destFile, true);
	}

	/**
	 * 원본 파일을 대상 파일명으로 복사한다.<br>
	 * preserveFileDate가 true면 원본디렉토리의 파일 최종 수정일을 그대로 사용하고 false면 현재 일자를 최종 수정일로
	 * 설정한다.
	 * 
	 * @param srcFile  원본 파일
	 * @param destFile 대상 파일
	 * @param preserveFileDate 수정일 설정 플래그
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result copyFile(File srcFile, File destFile, boolean preserveFileDate) {

		Result result = Result.EMPTY;
		
		if (notExistsFile(srcFile)) {
			return buildFailResult(result, srcFile.getName() + "가 존재하지 않습니다.");
		}

		if (isNotFile(srcFile)) {
			return buildFailResult(result, srcFile.getName() + "는 디렉토리가 아닙니다.");			
		}
		
		if (existsFile(destFile)) {
			
			result = deleteFile(destFile);
			
			if (result == Result.FAIL) {
				return buildFailResult(result, destFile.getName() + "을 삭제하는 도중에 에러가 발생하였습니다.");
			}
		}

		try {
			org.apache.commons.io.FileUtils.copyFile(srcFile, destFile,	preserveFileDate);
		} catch (IOException e) {
			
			String errorMsg = e.getMessage();
			
			if (errorMsg.contains("same")) {
				return Result.SUCCESS;
			} else {
				return buildFailResult(result, e.getMessage());
			}
		}

		return Result.SUCCESS;
	}

	/**
	 * 원본 파일을 대상 파일명으로 복사한다
	 * 
	 * @param srcFile  원본 파일
	 * @param destFile 대상 파일
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result copyFile(String srcFile, String destFile) {
		return copyFile(new File(srcFile), new File(destFile), true);
	}

	/**
	 * 원본 파일을 대상 파일명으로 복사한다.<br>
	 * preserveFileDate가 true면 원본디렉토리의 파일 최종 수정일을 그대로 사용하고 false면 현재 일자를 최종 수정일로
	 * 설정한다.
	 * 
	 * @param srcFile  원본 파일
	 * @param destFile 대상 파일
	 * @param preserveFileDate 수정일 설정 플래그
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result copyFile(String srcFile, String destFile, boolean preserveFileDate) {
		return copyFile(new File(srcFile), new File(destFile), preserveFileDate);
	}

	/**
	 * 파일을 대상 디렉토리에 복사한다.
	 * 
	 * @param srcFile 원본 파일
	 * @param destDir 대상 디렉토리
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result copyFileToDir(File srcFile, File destDir) {
		return copyFileToDir(srcFile, destDir, true);
	}

	/**
	 * 파일을 대상 디렉토리에 복사한다.<br>
	 * preserveFileDate가 true면 원본디렉토리의 파일 최종 수정일을 그대로 사용하고 false면 현재 일자를 최종 수정일로
	 * 설정한다.
	 * 
	 * @param srcFile 원본 파일
	 * @param destDir 대상 디렉토리
	 * @param preserveFileDate 수정일 설정 플래그
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result copyFileToDir(File srcFile, File destDir, boolean preserveFileDate) {

		Result result = Result.EMPTY;
		
		if (!srcFile.exists()) {
			return buildFailResult(result, srcFile + "가 존재하지 않습니다.");
		}

		if (notExistsDir(destDir)) {
			
			result = createDir(destDir.getAbsolutePath());
			
			if (result == Result.FAIL) {
				return buildFailResult(result, destDir + " 디렉토리 생성 중에 에러가 발생하였습니다.");
			}
		}
		
		if (isNotDir(destDir)) {
			return buildFailResult(result, destDir + "는 디렉토리가 아닙니다.");
		}
		
		try {
			org.apache.commons.io.FileUtils.copyFileToDirectory(srcFile, destDir, preserveFileDate);
		} catch (Exception e) {
			return buildFailResult(result, e.getMessage());
		}

		return Result.SUCCESS;
	}

	/**
	 * 파일을 대상 디렉토리에 복사한다.
	 * 
	 * @param srcFile 원본 파일
	 * @param destDir 대상 디렉토리
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result copyFileToDir(String srcFile, String destDir) {
		return copyFileToDir(new File(srcFile), new File(destDir), true);
	}

	/**
	 * 파일을 대상 디렉토리에 복사한다.<br>
	 * preserveFileDate가 true면 원본디렉토리의 파일 최종 수정일을 그대로 사용하고 false면 현재 일자를 최종 수정일로
	 * 설정한다.
	 * 
	 * @param srcFile 원본 파일
	 * @param destDir 대상 디렉토리
	 * @param preserveFileDate 수정일 설정 플래그
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result copyFileToDir(String srcFile, String destDir, boolean preserveFileDate) {
		return copyFileToDir(new File(srcFile), new File(destDir), preserveFileDate);
	}

	/**
	 * 주어진 디렉토리명으로 디렉토리를 생성한다.
	 * 
	 * @param dirPath 생성할 디렉토리 명
	 * @return enum 타입의 Result로 작업 결과를 반환
	 */
	public static Result createDir(String dirPath) {
		
		try {
			org.apache.commons.io.FileUtils.forceMkdir(new File(dirPath));
		} catch (IOException e) {
			return buildFailResult(Result.FAIL, "directory 생성에 실패하였습니다.");
		}
		
		return Result.SUCCESS;
	}

	/**
	 * 해당 디렉토리 및 하위 파일을 삭제한다.
	 * 
	 * @param targetDir 삭제할 디렉토리
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result deleteDir(File targetDir) {

		Result result = Result.EMPTY;
		
		if (!targetDir.exists()) {
			return buildFailResult(result, targetDir	+ "가 존재하지 않습니다.");
		}

		if (isNotDir(targetDir)) {
			return buildFailResult(result, targetDir + "는(은) 디렉토리가 아닙니다.");
		}

		try {
			org.apache.commons.io.FileUtils.deleteDirectory(targetDir);
		} catch (Exception e) {
			return buildFailResult(result, e.getMessage());
		}

		return Result.SUCCESS;
	}

	/**
	 * 해당 디렉토리 및 하위 파일을 삭제한다.
	 * 
	 * @param targetDir 삭제할 디렉토리
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result deleteDir(String targetDir) {
		return deleteDir(new File(targetDir));
	}

	/**
	 * 해당파일을 삭제한다.
	 * 
	 * @param targetFile 삭제할 File
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result deleteFile(File targetFile) {

		Result result = Result.EMPTY;
		
		if (!targetFile.exists()) {
			return buildFailResult(result, targetFile	+ "가 존재하지 않습니다.");
		}

		if (isNotFile(targetFile)) {
			return buildFailResult(result, targetFile	+ "은 파일이 아닙니다.");
		}

		try {
			org.apache.commons.io.FileUtils.forceDelete(targetFile);
		} catch (Exception e) {
			return buildFailResult(result, e.getMessage());
		}

		return Result.SUCCESS;
	}

	/**
	 * 해당파일을 삭제한다.
	 * 
	 * @param targetFile 삭제할 File
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result deleteFile(String targetFile) {
		return deleteFile(new File(targetFile));
	}

	/**
	 * 해당 디렉토리의 존재유무를 확인한다.
	 * 
	 * @param dir 존재유무를 확인할 디렉토리
	 * @return 존재유무에 대한 결과
	 */
	public static boolean existsDir(File dir) {
		return (dir.exists() && isDir(dir)) ? true : false;
	}

	/**
	 * 해당 디렉토리의 존재유무를 확인한다.
	 * 
	 * @param dir 존재유무를 확인할 디렉토리
	 * @return 존재유무에 대한 결과
	 */
	public static boolean existsDir(String dir) {
		return existsDir(new File(dir));
	}

	/**
	 * 해당 파일의 존재유무를 확인한다.
	 * 
	 * @param file 존재유무를 확인할 파일
	 * @return 존재유무에 대한 결과
	 */
	public static boolean existsFile(File file) {
		return (file.exists() && isFile(file)) ? true : false; 
	}

	/**
	 * 해당 파일의 존재유무를 확인한다.
	 * 
	 * @param file 존재유무를 확인할 파일
	 * @return 존재유무에 대한 결과
	 */
	public static boolean existsFile(String file) {
		return existsFile(new File(file));
	}
	
	/**
	 * 해당 디렉토리의 존재유무를 확인한다.
	 * 
	 * @param dir 존재유무를 확인할 디렉토리
	 * @return 존재유무에 대한 결과
	 */
	public static boolean notExistsDir(File dir) {
		return !existsDir(dir);
	}
	
	/**
	 * 해당 디렉토리의 존재유무를 확인한다.
	 * 
	 * @param dir 존재유무를 확인할 디렉토리
	 * @return 존재유무에 대한 결과
	 */
	public static boolean notExistsDir(String dir) {
		return !existsDir(new File(dir));
	}

	/**
	 * 해당 파일의 존재유무를 확인한다.
	 * 
	 * @param file 존재유무를 확인할 파일
	 * @return 존재유무에 대한 결과
	 */
	public static boolean notExistsFile(File file) {
		return !existsFile(file);
	}

	/**
	 * 해당 파일의 존재유무를 확인한다.
	 * 
	 * @param file 존재유무를 확인할 파일
	 * @return 존재유무에 대한 결과
	 */
	public static boolean notExistsFile(String file) {
		return !existsFile(new File(file));
	}

	/**
     * Java가 실행되는 현재 폴더 경로를 반환한다.
     * 
     * @return 현재 폴더 경로
     */
    public static String getCurrentDir() {
        return System.getProperty("user.dir");
    }

	/**
     * Java가 실행되는 현재 폴더 경로를 File 객체로 반환한다.
     * 
     * @return 현재 위치 경로를 가진 File 객체.
     */
    public static File getCurrentDirAsFile() {
        return new File(getUserHomeDir());
    }

	/**
	 * 해당 디렉토리의 부모 디렉토리명을 반환한다.
	 * 
	 * @param file 디렉토리명
	 * @return 현재 디렉토리에 대한 부모 디렉토리 명
	 */
	public static String getParenet(String file) {
		return new File(file).getParent();
	}

	/**
     * system temporary directory 경로를 반환한다.
     * 
     * @return system temporary directory 경로
     */
    public static String getTempDir() {
        return System.getProperty("java.io.tmpdir");
    }

	/**
     * system temporary directory 경로를 File 객체로 반환한다.
     * 
     * @return system temporary directory 경로를 가진 File 객체. 
     */
    public static File getTempDirAsFile() {
        return new File(getTempDir());
    }
    
	/**
     * user's home directory 경로를 반환한다.
     * 
     * @return user's home directory 경로
     */
    public static String getUserHomeDir() {
        return System.getProperty("user.home");
    }
	
	/**
     * user's home directory 경로를 File 객체로 반환한다.
     * 
     * @return user's home directory 경로를 가진 File 객체.
     */
    public static File getUserHomeDirAsFile() {
        return new File(getUserHomeDir());
    }
	
	/**
	 * 해당 target이 디렉토리인지를 체크한다.
	 * 
	 * @param dir 디렉토리
	 * @return 해당 파일이 디렉토리이면 true, 아니면 false
	 */
	public static boolean isDir(File dir) {
		return (!dir.isDirectory()) ? false : true;
	}
	
	/**
	 * 해당 target이 디렉토리인지를 체크한다.
	 * 
	 * @param dir 디렉토리
	 * @return 해당 파일이 디렉토리이면 true, 아니면 false
	 */
	public static boolean isDir(String dir) {
		return isDir(new File(dir));
	}

	/**
	 * 해당 target이 파일인지를 체크한다.
	 * 
	 * @param file 파일
	 * @return 해당 파일이 파일이면 true, 아니면 false
	 */
	public static boolean isFile(File file) {
		return (!file.isFile()) ? false : true;
	}
	
	/**
	 * 해당 target이 파일인지를 체크한다.
	 * 
	 * @param file 파일
	 * @return 해당 파일이 파일이면 true, 아니면 false
	 */
	public static boolean isFile(String file) {
		return isFile(new File(file));
	}

	/**
	 * 해당 target이 디렉토리가 아닌지를 체크한다.
	 * 
	 * @param dir 디렉토리
	 * @return 해당 파일이 디렉토리가 아니면 true, 아니면 false
	 */
	public static boolean isNotDir(File dir) {
		return (!dir.isDirectory()) ? true : false;
	}
	
	/**
	 * 해당 target이 디렉토리가 아닌지를 체크한다.
	 * 
	 * @param dir 디렉토리
	 * @return 해당 파일이 디렉토리가 아니면 true, 아니면 false
	 */
	public static boolean isNotDir(String dir) {
		return isNotDir(new File(dir));
	}

	/**
	 * 해당 target이 파일이 아닌지를 체크한다.
	 * 
	 * @param file 파일
	 * @return 해당 파일이 파일이 아니면 true, 아니면 false
	 */
	public static boolean isNotFile(File file) {
		return (!file.isFile()) ? true : false;
	}
	
	/**
	 * 해당 target이 파일이 아닌지를 체크한다.
	 * 
	 * @param file 파일
	 * @return 해당 파일이 파일이 아니면 true, 아니면 false
	 */
	public static boolean isNotFile(String file) {
		return isNotFile(new File(file));
	}
	
	/**
	 * 지정한 디렉토리 하위의 디렉토리명과 파일명을 가져온다. 
	 * 
	 * @param dir 디렉토리
	 * @param includeRootDir 가장 상위의 디렉토리명을 포함할지를 결정. (true면 최상위 폴더 포함)
	 * @return 디렉토리 하위의 디렉토리명과 파일명에 대한 리스트
	 */
	public static List<String> listFileAndDirNames(String dir, boolean includeRootDir) {
		
		List<File> dirs = (List<File>) org.apache.commons.io.FileUtils.listFilesAndDirs(new File(dir), 
				FileFileFilter.FILE, DirectoryFileFilter.DIRECTORY);
		
		List<String> dirNameList = new ArrayList<String>();
		
		int index = 0;
		
		for (File dirUnit : dirs) {
			
			if (index++ == 0 && !includeRootDir) {
				continue;
			}
			
			dirNameList.add(dirUnit.getAbsolutePath());			
			index++;
		}
		
		return dirNameList;
	}
	
	/**
	 * 지정한 디렉토리 하위의 디렉토리명과 파일명을 File 타입의 배열로 가져온다. 
	 * 
	 * @param dir 디렉토리
	 * @param includeRootDir 가장 상위의 디렉토리명을 포함할지를 결정. (true면 최상위 폴더 포함)
	 * @return 디렉토리 하위의 디렉토리명과 파일명에 대한 File 타입의 배열
	 */
	public static File[] listFilesAndDirs(String dir, boolean includeRootDir) {
		
		List<File> filesAndDirs = (List<File>) org.apache.commons.io.FileUtils.listFilesAndDirs(new File(dir), 
				FileFileFilter.FILE, DirectoryFileFilter.DIRECTORY);
		
		List<File> filesAndDirsList = new ArrayList<File>();
		
		int index = 0;
		
		for (File fileOrDir : filesAndDirs) {
			
			if (index++ == 0 && !includeRootDir) {
				continue;
			}
			
			filesAndDirsList.add(fileOrDir);			
			index++;
		}
		
		return filesAndDirsList.toArray(new File[filesAndDirsList.size()]);
		
	}
	
	/**
	 * 지정한 디렉토리 하위의 디렉토리명을 가져온다. 
	 * 
	 * @param dir 디렉토리
	 * @param includeRootDir 가장 상위의 디렉토리명을 포함할지를 결정. (true면 최상위 폴더 포함)
	 * @return 디렉토리 하위의 디렉토리명에 대한 리스트
	 */
	public static List<String> listDirNames(String dir, boolean includeRootDir) {
		
		List<File> dirs = (List<File>) org.apache.commons.io.FileUtils.listFilesAndDirs(new File(dir), 
				new NotFileFilter(TrueFileFilter.INSTANCE), DirectoryFileFilter.DIRECTORY);
		
		List<String> dirNameList = new ArrayList<String>();
		
		int index = 0;
		
		for (File dirUnit : dirs) {
			
			if (index++ == 0 && !includeRootDir) {
				continue;
			}
			
			dirNameList.add(dirUnit.getAbsolutePath());			
			index++;
		}
		
		return dirNameList;		
	}
	
	/**
	 * 지정한 디렉토리 하위의 디렉토리명을 File 타입의 배열로 가져온다. 
	 * 
	 * @param dir 디렉토리
	 * @param includeRootDir 가장 상위의 디렉토리명을 포함할지를 결정. (true면 최상위 폴더 포함)
	 * @return 디렉토리 하위의 디렉토리명에 대한 File 타입의 배열
	 */
	public static File[] listDirs(String dir, boolean includeRootDir) {
		
		List<File> dirs = (List<File>) org.apache.commons.io.FileUtils.listFilesAndDirs(new File(dir), 
				new NotFileFilter(TrueFileFilter.INSTANCE), DirectoryFileFilter.DIRECTORY);
		
		List<File> dirList = new ArrayList<File>();
		
		int index = 0;
		
		for (File dirUnit : dirs) {
			
			if (index++ == 0 && !includeRootDir) {
				continue;
			}
			
			dirList.add(dirUnit);			
			index++;
		}
		
		return dirList.toArray(new File[dirList.size()]);
	}
	
	/**
	 * 지정한 디렉토리 하위의 파일들을 File 타입의 배열로 가져온다. 
	 * 
	 * @param dir 디렉토리
	 * @param recursive 하위 폴더 내부의 파일들에 대한 탐색 유무
	 * @return 디렉토리 하위의 파일에 대한 File 타입의 배열
	 */
	public static File[] listFiles(String dir, boolean recursive) {
		
		return org.apache.commons.io.FileUtils.convertFileCollectionToFileArray(
				org.apache.commons.io.FileUtils.listFiles(new File(dir), null, recursive));	
	}
	
	/**
	 * 지정한 디렉토리 하위의 파일명들에 대한 리스트를 가져온다.
	 * 
	 * @param dir 디렉토리
	 * @param recursive 하위 폴더 내부의 파일들에 대한 탐색 유무
	 * @return 디렉토리 하위의 파일들에 대한 파일명 리스트
	 */
	public static List<String> listFileNames(String dir, boolean recursive) throws FileUtilsException {
		
		Collection<File> files = null;
		
		try {
			files = org.apache.commons.io.FileUtils.listFiles(new File(dir), null, recursive);
		} catch (Exception e) {
			throw new FileUtilsException(e.getMessage());
		}
		
		List<String> fileNamesList = new ArrayList<String>();
		
		for (File file : files) {
			fileNamesList.add(file.getAbsolutePath());
		}
		
		return fileNamesList;
	}
	
	/**
	 * 지정한 파일 확장자로 구성된 파일들에 대한 리스트를 가져온다.
	 * 
	 * @param dir 디렉토리 
	 * @param recursive 하위 폴더 내부의 파일들에 대한 작업 유무
	 * @param extList 포함시킬 확장자 리스트
	 * @return 지정한 파일 확장자로 구성된 파일들에 대한 리스트
	 */
	public static List<String> listFilenamesIncludeExt(String dir, boolean recursive, String... extList) {
		
		List<File> extFilterList = (List<File>) org.apache.commons.io.FileUtils.listFiles(new File(dir), extList,  recursive);
		
		List<String> resultList = new ArrayList<String>();
		
		for (File file : extFilterList) {
			resultList.add(file.getAbsolutePath());
		}
		
		return resultList;		
	}

	/**
	 * 지정한 파일 확장자로 구성된 파일들을 File 타입의 배열로 가져온다.
	 * 
	 * @param dir 디렉토리 
	 * @param recursive 하위 폴더 내부의 파일들에 대한 작업 유무
	 * @param extList 포함시킬 확장자 리스트
	 * @return 지정한 파일 확장자로 구성된 파일들에 대한 File 타입의 배열
	 */
	public static File[] listFilesIncludeExt(String dir, boolean recursive, String... extList) {
		
		Collection<File> resultFiles = org.apache.commons.io.FileUtils.listFiles(new File(dir), extList,  recursive);
		
		return org.apache.commons.io.FileUtils.convertFileCollectionToFileArray(resultFiles);		
	}
	
	/**
	 * 지정한 파일 확장자를 제외한 파일들의 파일명 리스트를 가져온다.
	 * 
	 * @param dir 디렉토리 
	 * @param recursive 하위 폴더 내부의 파일들에 대한 작업 유무
	 * @param extList 포함시킬 확장자 리스트
	 * @return 지정한 파일 확장자를 제외한 파일들의 파일명 리스트
	 */
	public static List<String> listFilenamesExcludeExt(String dir, boolean recursive, String... extList) {
		
		IOFileFilter suffixFileFilters = new SuffixFileFilter(extList, IOCase.INSENSITIVE);
		IOFileFilter excludeExtFilter = FileFilterUtils.notFileFilter(FileFilterUtils.or(suffixFileFilters));
    	
    	List<File> resultFiles = (List<File>) org.apache.commons.io.FileUtils.listFiles(new File(dir), excludeExtFilter, TrueFileFilter.INSTANCE);
		
		List<String> resultList = new ArrayList<String>();
		
		for (File file : resultFiles) {
			resultList.add(file.getAbsolutePath());
		}
		
		return resultList;		
	}

	/**
	 * 지정한 파일 확장자를 제외한 파일들을 File 타입의 배열로 가져온다.
	 * 
	 * @param dir 디렉토리 
	 * @param recursive 하위 폴더 내부의 파일들에 대한 작업 유무
	 * @param extList 포함시킬 확장자 리스트
	 * @return 지정한 파일 확장자를 제외한 파일들에 대한 File 타입의 배열
	 */
	public static File[] listFilesExcludeExt(String dir, boolean recursive, String... extList) {
		
		IOFileFilter suffixFileFilters = new SuffixFileFilter(extList, IOCase.INSENSITIVE);
		IOFileFilter excludeExtFilter = FileFilterUtils.notFileFilter(FileFilterUtils.or(suffixFileFilters));
    	
		Collection<File> resultFiles = org.apache.commons.io.FileUtils.listFiles(new File(dir), excludeExtFilter, TrueFileFilter.INSTANCE);

		return org.apache.commons.io.FileUtils.convertFileCollectionToFileArray(resultFiles);		
	}

	
	/**
	 * 원본 파일을 대상 파일 명으로 이동한다.
	 * 
	 * @param srcFile  원본 파일
	 * @param destFile 대상 파일
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result moveFile(File srcFile, File destFile) {
		return moveFile(srcFile, destFile, true);
	}
	
	/**
	 * 원본 파일을 대상 파일 명으로 이동한다.
	 * 
	 * @param srcFile  원본 파일
	 * @param destFile 대상 파일
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result moveFile(String srcFile, String destFile) {
		return moveFile(new File(srcFile), new File(destFile), true);
	}
	
	/**
	 * 원본 파일을 대상 파일 명으로 이동한다.<br>
	 * preserveFileDate가 true면 원본디렉토리의 파일 최종 수정일을 그대로 사용하고 false면 현재 일자를 최종 수정일로
	 * 설정한다.
	 * 
	 * @param srcFile  원본 파일
	 * @param destFile 대상 파일
	 * @param preserveFileDate 수정일 설정 플래그
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result moveFile(File srcFile, File destFile, boolean preserveFileDate) {

		Result result = Result.EMPTY;
		
		if (!srcFile.exists()) {
			return buildFailResult(result, srcFile + "가 존재하지 않습니다.");
		}

		if (isNotFile(srcFile)) {
			return buildFailResult(result, srcFile + "는 파일이 아닙니다.");
		}

		if (destFile.exists()) {
			
			result = deleteFile(destFile);
			
			if (result == Result.FAIL) {
				return buildFailResult(result, destFile + " 파일 삭제중에 에러가 발생하였습니다.");
			}
		}
		
		result = copyFile(srcFile, destFile, preserveFileDate);
		
		if (result == Result.FAIL) {
			return buildFailResult(result, srcFile + " 파일 복사중에 에러가 발생하였습니다.");
		}
            
		result = deleteFile(srcFile);
		
		if (result == Result.FAIL) {
			return buildFailResult(result, srcFile + " 파일 삭제중에 에러가 발생하였습니다.");
		}
		
		return Result.SUCCESS;
	}
	
	/**
	 * 원본 파일을 대상 파일 명으로 이동한다.<br>
	 * preserveFileDate가 true면 원본디렉토리의 파일 최종 수정일을 그대로 사용하고 false면 현재 일자를 최종 수정일로
	 * 설정한다.
	 * 
	 * @param srcFile  원본 파일
	 * @param destFile 대상 파일
	 * @param preserveFileDate 수정일 설정 플래그
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result moveFile(String srcFile, String destFile, boolean preserveFileDate) {
		return moveFile(new File(srcFile), new File(destFile), preserveFileDate);
	}
	
	/**
	 * 원본 파일을 대상 디렉토리 하위로 이동한다.
	 * 
	 * @param srcFile 원본 파일
	 * @param destDir 대상 디렉토리
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result moveFileToDir(File srcFile, File destDir) {
		return moveFileToDir(srcFile, destDir, true);
	}
	
	/**
	 * 원본 파일을 대상 디렉토리 하위로 이동한다.
	 * 
	 * @param srcFile 원본 파일
	 * @param destDir 대상 디렉토리
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result moveFileToDir(String srcFile, String destDir) {
		return moveFileToDir(new File(srcFile), new File(destDir), true);
	}
	
	/**
	 * 원본 파일을 대상 디렉토리 하위로 이동한다.<br>
	 * preserveFileDate가 true면 원본디렉토리의 파일 최종 수정일을 그대로 사용하고 false면 현재 일자를 최종 수정일로
	 * 설정한다.
	 * 
	 * @param srcFile 원본 파일
	 * @param destDir 대상 디렉토리
	 * @param preserveFileDate 수정일 설정 플래그
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result moveFileToDir(File srcFile, File destDir, boolean preserveFileDate) {

		Result result = Result.EMPTY;
		
		if (isNotFile(srcFile)) {
			return buildFailResult(result, srcFile + "은 파일이 아닙니다.");
		}
		
		if (FileUtils.notExistsDir(destDir)) {
			
			result = createDir(destDir.getAbsolutePath());
			
			if (result == Result.FAIL) {
				return buildFailResult(result, destDir + " 디렉토리 생성 중에 에러가 발생하였습니다.");
			}
		}

		if (isNotDir(destDir)) {
			return buildFailResult(result, srcFile + "는 디렉토리가 아닙니다.");
		}
		
		String destFile = destDir + File.separator + srcFile.getName();
		
		result = moveFile(srcFile, new File(destFile), preserveFileDate);

		if (result == Result.FAIL) {
			return buildFailResult(result, srcFile + " 이동 중에 에러가 발생하였습니다.");
		}
		
		return Result.SUCCESS;
	}
	
	/**
	 * 원본 파일을 대상 디렉토리 하위로 이동한다.<br>
	 * preserveFileDate가 true면 원본디렉토리의 파일 최종 수정일을 그대로 사용하고 false면 현재 일자를 최종 수정일로
	 * 설정한다.
	 * 
	 * @param srcFile 원본 파일
	 * @param destDir 대상 디렉토리
	 * @param preserveFileDate 수정일 설정 플래그            
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result moveFileToDir(String srcFile, String destDir, boolean preserveFileDate) {
		return moveFileToDir(new File(srcFile), new File(destDir), preserveFileDate);
	}
	
	

	/**
	 * 대상 파일을 FileInputStream으로 반환한다.<br>
	 * 
	 * @param file 대상 파일
	 * @return 대상 파일에 대한 FileInputStream
	 */
	public static FileInputStream openInputStream(File file) {

		if (file.exists()) {

			if (file.isDirectory()) {
				return null;
			}

			if (!file.canRead()) {
				return null;
			}

		} else {
			return null;
		}

		FileInputStream fis = null;

		try {
			fis = new FileInputStream(file);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return fis;
	}
	
	/**
	 * 대상 파일을 FileInputStream으로 반환한다.<br>
	 * 
	 * @param filePath 대상 파일 경로
	 * @return 대상 파일에 대한 FileInputStream
	 */
	public static FileInputStream openInputStream(String filePath) {
		return openInputStream(new File(filePath));
	}

	/**
	 * 대상 파일을 outputStream으로 반환한다.
	 * 
	 * @param file 대상 파일
	 * @return 대상 파일에 대한 FileOutputStream
	 */
	public static FileOutputStream openOutputStream(File file) {
		return openOutputStream(file, false);
	}
	
	/**
	 * 대상 파일을 outputStream으로 반환한다. append가 true이면 출력시 기존 파일의 내용 마지막에 덧붙인다.
	 * 
	 * @param file   대상 파일
	 * @param append 기존 파일에 내용을 덧붙일지에 대한 플래그
	 * @return 대상 파일에 대한 FileOutputStream
	 */
	public static FileOutputStream openOutputStream(File file, boolean append) {

		if (file.exists()) {

			if (file.isDirectory()) {
				return null;
			}
			if (!file.canWrite()) {
				return null;
			}
		} else {
			File parent = file.getParentFile();
			if (parent != null) {
				if (!parent.mkdirs() && !parent.isDirectory()) {
					return null;
				}
			}
		}

		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(file, append);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fos;
	}
	
	/**
	 * 대상 파일을 outputStream으로 반환한다.
	 * 
	 * @param filePath 대상 파일 경로
	 * @return 대상 파일에 대한 FileOutputStream
	 */
	public static FileOutputStream openOutputStream(String filePath) {
		return openOutputStream(new File(filePath), false);
	}
	
	/**
	 * 대상 파일을 outputStream으로 반환한다. append가 true이면 출력시 기존 파일의 내용 마지막에 덧붙인다.
	 * 
	 * @param filePath 대상 파일 경로
	 * @param append   기존 파일에 내용을 덧붙일지에 대한 플래그
	 * @return 대상 파일에 대한 FileOutputStream
	 */
	public static FileOutputStream openOutputStream(String filePath, boolean append) {
		return openOutputStream(new File(filePath), append);		
	}
	
	/**
	 * 대상 파일을 FileReader로 반환한다.
	 * 
	 * @param file 대상 파일
	 * @return 대상 파일에 대한 FileReader
	 */
	public static FileReader openReader(File file) {
		
		if (file.exists()) {

			if (file.isDirectory()) {
				return null;
			}

			if (!file.canRead()) {
				return null;
			}

		} else {
			return null;
		}

		FileReader fileReader = null;

		try {
			fileReader = new FileReader(file);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return fileReader;
	}
	
	/**
	 * 대상 파일을 FileReader로 반환한다.
	 * 
	 * @param filePath 대상 파일 경로
	 * @return 대상 파일에 대한 FieReader
	 */
	public static FileReader openReader(String filePath) {
		return openReader(new File(filePath));
	}
    
    /**
	 * 대상 파일을 FileWriter로 반환한다.
	 * 
	 * @param file 대상 파일
	 * @return 대상 파일에 대한 FileWriter
	 */
	public static FileWriter openWriter(File file) { 
		return openWriter(file, false);
	}
    
    /**
	 * 대상 파일을 FileWriter로 반환한다. append가 true이면 출력시 기존 파일의 내용 마지막에 덧붙인다.
	 * 
	 * @param file 대상 파일
	 * @param append 기존 파일에 내용을 덧붙일지에 대한 플래그
	 * @return 대상 파일에 대한 FileWriter
	 */
	public static FileWriter openWriter(File file, boolean append) {

		if (file.exists()) {

			if (file.isDirectory()) {
				return null;
			}
			if (!file.canWrite()) {
				return null;
			}
		} else {
			
			File parent = file.getParentFile();
			
			if (parent != null) {
				
				if (!parent.mkdirs() && !parent.isDirectory()) {
					return null;
				}
			}
		}

		FileWriter fileWriter = null;

		try {
			fileWriter = new FileWriter(file, append);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileWriter;
	}
    
    /**
	 * 대상 파일을 FileWriter로 반환한다.
	 * 
	 * @param filePath 대상 파일 경로
	 * @return 대상 파일에 대한 FileWriter
	 */
	public static FileWriter openWriter(String filePath) { 
		return openWriter(new File(filePath));
	}
    
    /**
	 * 대상 파일을 FileWriter로 반환한다. append가 true이면 출력시 기존 파일의 내용 마지막에 덧붙인다.
	 * 
	 * @param filePath 대상 파일 경로
	 * @param append 기존 파일에 내용을 덧붙일지에 대한 플래그
	 * @return 대상 파일에 대한 FileWriter
	 */
	public static FileWriter openWriter(String filePath, boolean append) { 
		return openWriter(new File(filePath), append);
	}
	
	/**
	 * 대상 파일을 BufferedWriter로 반환한다.
	 * 
	 * @param file 대상 파일
	 * @return 대상 파일에 대한 FileWriter
	 */
	public static BufferedWriter openBufferWriter(File file) { 
		return openBufferWriter(file, "UTF-8", false);
	}
	
	/**
	 * 대상 파일을 BufferedWriter로 반환한다.
	 * 
	 * @param file 대상 파일
	 * @param charSet 인코딩에 사용할 캐릭터셋
	 * @return 대상 파일에 대한 FileWriter
	 */
	public static BufferedWriter openBufferWriter(File file, String charSet) { 
		return openBufferWriter(file, charSet, false);
	}
    
    /**
	 * 대상 파일을 BufferedWriter로 반환한다. append가 true이면 출력시 기존 파일의 내용 마지막에 덧붙인다.
	 * 
	 * @param file 대상 파일
	 * @param charSet 인코딩에 사용할 캐릭터셋
	 * @param append 기존 파일에 내용을 덧붙일지에 대한 플래그
	 * @return 대상 파일에 대한 FileWriter
	 */
	public static BufferedWriter openBufferWriter(File file, String charSet, boolean append) {

		if (file.exists()) {

			if (file.isDirectory()) {
				return null;
			}
			
			if (!file.canWrite()) {
				return null;
			}
			
		} else {
			
			File parent = file.getParentFile();
			
			if (parent != null) {
				
				if (!parent.mkdirs() && !parent.isDirectory()) {
					return null;
				}
			}
		}
		
		BufferedWriter bufferedWriter = null; 
		
		try {
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charSet));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return bufferedWriter;
	}
    
    /**
	 * 대상 파일을 BufferedWriter로 반환한다.
	 * 
	 * @param filePath 대상 파일 경로
	 * @return 대상 파일에 대한 FileWriter
	 */
	public static BufferedWriter openBufferWriter(String filePath) { 
		return openBufferWriter(new File(filePath), "UTF-8", false);
	}
	
	/**
	 * 대상 파일을 BufferedWriter로 반환한다.
	 * 
	 * @param filePath 대상 파일 경로
	 * @param charSet 인코딩에 사용할 캐릭터셋
	 * @return 대상 파일에 대한 FileWriter
	 */
	public static BufferedWriter openBufferWriter(String filePath, String charSet) { 
		return openBufferWriter(new File(filePath), charSet, false);
	}
    
    /**
	 * 대상 파일을 BufferedWriter로 반환한다. append가 true이면 출력시 기존 파일의 내용 마지막에 덧붙인다.
	 * 
	 * @param filePath 대상 파일 경로
	 * @param append 기존 파일에 내용을 덧붙일지에 대한 플래그
	 * @return 대상 파일에 대한 FileWriter
	 */
	public static BufferedWriter openBufferWriter(String filePath, boolean append) { 
		return openBufferWriter(new File(filePath), "UTF-8", append);
	}
	
	/**
	 * 대상 파일을 BufferedWriter로 반환한다. append가 true이면 출력시 기존 파일의 내용 마지막에 덧붙인다.
	 * 
	 * @param filePath 대상 파일 경로
	 * @param charSet 인코딩에 사용할 캐릭터셋
	 * @param append 기존 파일에 내용을 덧붙일지에 대한 플래그
	 * @return 대상 파일에 대한 FileWriter
	 */
	public static BufferedWriter openBufferWriter(String filePath, String charSet, boolean append) { 
		return openBufferWriter(new File(filePath), charSet, append);
	}
    
    /**
	 * 파일을 읽어 들인 후 바이트 배열로 반환한다.<br>
	 * 
	 * @param file 대상 파일
	 * @return 원본 파일의 byte[]
	 */
	public static byte[] readFileToByteArray(File file) {

		InputStream in = null;

		try {
			in = openInputStream(file);
			return IOUtils.toByteArray(in, file.length());
		} catch (Exception e) {
			return null;
		} finally {
			IOUtils.closeQuietly(in);
		}
	}
    
    /**
	 * 파일을 읽어 들인 후 바이트 배열로 반환한다.<br>
	 * 
	 * @param file 대상 파일 경로
	 * @return 원본 파일의 byte[]
	 */
	public static byte[] readFileToByteArray(String file) {
		return readFileToByteArray(new File(file));
	}
	
    /**
     * 파일을 읽어 들인 후 지정한 charset으로 인토딩한 문자열을 반환한다.
     * 
     * @param file 대상 파일 경로
     * @param encoding 인코딩할 charset
     * @return 파일 내용을 지정한 charset으로 인코딩한 문자열
     */
    public static String readFileToString(File file, Charset encoding) {
        
    	InputStream in = null;
        in = openInputStream(file);
        
        try {
			return IOUtils.toString(in, Charsets.toCharset(encoding));
		} catch (Exception e) {
			return null;
		} finally {
			IOUtils.closeQuietly(in);
		}
    }
    
    /**
     * 파일을 읽어 들인 후 지정한 charset으로 인토딩한 문자열을 반환한다.
     * 
     * @param file 대상 파일 경로
     * @param encoding 인코딩할 charset 문자열
     * @return 파일 내용을 지정한 charset으로 인코딩한 문자열
     */
    public static String readFileToString(File file, String encoding) {
    	return readFileToString(file, Charsets.toCharset(encoding));
    }

    /**
     * 파일을 읽어 들인 후 문자열을 반환한다.
     * 
     * @param file 대상 파일 경로
     * @return 파일 내용을 지정한 charset으로 인코딩한 문자열
     */
    public static String readFileToString(File file) {
        return readFileToString(file, Charset.defaultCharset());
    }
    
    /**
	 * 원본 파일명을 대상 파일 명으로 변경한다.<br>
	 * 파일변경후 원본 파일 삭제
	 * 
	 * @param srcFile  원본 파일
	 * @param destFile 대상 파일
	 * @return 성공하면 enum 타입의 Result.SUCCESS를 그렇지 않으면 Result.FAIL을 반환
	 */
	public static Result rename(String srcFile, String destFile) {
		
		Result result = Result.EMPTY;
		
		if (!existsFile(srcFile)) {
			return buildFailResult(result, srcFile + "가 존재하지 않습니다.");
		}
		
		if (existsFile(destFile)) {
			
			result = deleteFile(destFile);

			if (result == Result.FAIL) {
				return buildFailResult(result, destFile + " 삭제 중에 에러가 발생하였습니다");
			}
		}
		
		File file = new File(srcFile);
		
		if (!file.renameTo(new File(destFile))) {
			
			result = copyFile(srcFile, destFile, false);

			if (result == Result.SUCCESS) {
				
				result = deleteFile(srcFile);
				
				if (result == Result.FAIL) {
					deleteFile(destFile);
				}
			}

			return result;
			
		} else {
			
			return Result.SUCCESS;
		}
	}
	
	/*public static void main(String[] args) {
		
		*//*List<File> resultFiles = (List<File>) org.apache.commons.io.FileUtils.listFiles(new File("c:/ePapyrus"), new String[] {"end", "done"},  true);
		
		for (File file : resultFiles) {
			System.out.println(file.getAbsolutePath());
		}*//*
		
		*//*File[] files = listFilesByExt("c:/epapyrus", true, "end", "done");
		
		for (File file : files) {
			System.out.println(file.getAbsolutePath());
		}*//*
		
		*//*List<String> resultFiles = listFilenamesByWildcard("c:/temp/Scratch", "@MG@*.*", false);
		
		for (String file : resultFiles) {
			System.out.println(file);
		}
		
		File[] files = listFilesByWildcard("c:/temp/Scratch", "@MG@*.*", false);
		
		for (File file : files) {
			System.out.println(file.getAbsolutePath());
		}
		
		List<String> resultFiles2 = listFilenamesByWildcard("c:/temp", "@MG@*.*", true);
		
		for (String file : resultFiles2) {
			System.out.println(file);
		}
		
		File[] files2 = listFilesByWildcard("c:/temp", "@MG@*.*", true);
		
		for (File file : files2) {
			System.out.println(file.getAbsolutePath());
		}*//*
    	
    	*//*System.out.println(getTempDir());
    	System.out.println(getUserHomeDir());
    	System.out.println(getCurrentDir());*//*
		
    	*//*IOFileFilter excludeExtFilter = FileFilterUtils.notFileFilter(FileFilterUtils.or(
    			FileFilterUtils.suffixFileFilter(".html"), 
    			FileFilterUtils.suffixFileFilter(".pdf"), 
    			FileFilterUtils.suffixFileFilter(".mht")));
    	
    	IOFileFilter fileFilter = FileFilterUtils.makeFileOnly(excludeExtFilter);*//*
    	
		*//*IOFileFilter suffixFileFilters = new SuffixFileFilter(new String[] {"html", "pdf", "mht"}, IOCase.SENSITIVE);
		IOFileFilter excludeExtFilter = FileFilterUtils.notFileFilter(FileFilterUtils.or(suffixFileFilters));
    	
    	List<File> resultFiles = (List<File>) org.apache.commons.io.FileUtils.listFiles(new File("C:/ePapyrus/Hotfolder/03.InProc/512ee3cc-428e-4341-a446-62e755dc0efe"), excludeExtFilter, TrueFileFilter.INSTANCE);
		
		for (File file : resultFiles) {
			System.out.println(file.getAbsolutePath());
		}*//*
		
		
		*//*List<String> list = FileUtils.listFileAndDirNames("C:/ePapyrus/log", false);
		
		for (String file : list) {
			System.out.println(file);
		}*//*
		
		*//*File[] files = FileUtils.listFilesAndDirs("C:/ePapyrus/log", false);
		
		for (File file : files) {
			System.out.println(file.getAbsolutePath());
		}*//*
		
		//System.out.println(getNumPagesOfMultiTif("c:/ePapyrus/HotFolder/3.tif"));
		
		List<String> list = listExcludeFilenamesByWildcard("c:/test", new String[] {"seq-*.*"}, false); 
		
		for (String str : list) {
			System.out.println(str);			
		}
		
		File[] files = listExcludeFilesByWildcard("c:/test", new String[] {"seq-*.*"}, false); 
		
		for (File file : files) {
			System.out.println(file.getAbsolutePath());			
			System.out.println(file.getName());			
		}
		
	}*/
	
}
