package net.lguplus.subwaywifi.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.opencsv.CSVWriter;

/**
 *
 * @author MJYoun
 * @since 2017. 03. 06.
 *
 */
public class CsvUtil {

	/**
	 * 목록을 받아 CSV 데이터 형식으로 바꿔주는 함수
	 *
	 * @param list
	 * 			데이터 목록
	 * @param clazz
	 * 			데이터 타입
	 * @return CSV 데이터
	 */
	public static byte [] createCsv(List<?> list, Class<?> clazz) {
		String csvString = "";
		try {
			Field [] fieldList = clazz.getDeclaredFields();
			List<String> fieldName = Arrays.asList(fieldList).stream()
																.map(field -> (field.getName()))
																.collect(Collectors.toList());

			csvString = createHeader(fieldName);

			for (Object item : list) {
				csvString = csvString + CSVWriter.RFC4180_LINE_END + createCsvLine(item, fieldList, clazz);
			}
		} catch (IllegalArgumentException | IllegalAccessException | InstantiationException iae) {
			iae.printStackTrace();
		}

		return ArrayUtils.addAll(encodeUTF8BOM(), csvString.getBytes());
	}

	/**
	 * CSV 헤더를 만들어주는 함수
	 *
	 * @param fileNameList
	 * 			필드 이름 목록
	 * @return CSV 헤더
	 */
	private static String createHeader(List<String> fileNameList) {
		String header = "";

		for (String fileName : fileNameList) {
			if (StringUtils.equals(header, "") == true) {
				header = fileName;
			} else {
				header = header + CSVWriter.DEFAULT_SEPARATOR + fileName;
			}
		}

		return header;
	}

	/**
	 * 한 줄의 CSV 데이터를 만들어 주기 위한 함수
	 *
	 * @param object
	 * 			데이터
	 * @param fieldList
	 * 			데이터가 갖고 있는 필드 목록
	 * @param clazz
	 * 			데이터 타입
	 * @return CSV 데이터
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private static String createCsvLine(Object object, Field [] fieldList, Class<?> clazz) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		String line = "";

		if (StringUtils.equals(object.getClass().getName(), clazz.getName()) == true) {
			for (int i = 0 ; i < fieldList.length ; i++) {
				boolean accessible = fieldList[i].isAccessible();
				fieldList[i].setAccessible(true);

				if (StringUtils.equals(line, "") == true) {
					line = convertObjectToString(fieldList[i].get(object));
				} else {
					line = line + CSVWriter.DEFAULT_SEPARATOR + convertObjectToString(fieldList[i].get(object));
				}

				fieldList[i].setAccessible(accessible);
			}
		}

		return line;
	}

	/**
	 * obejct형식의 데이터를 String으로 바꿔주는 함수. null일 경우 "-"을 출력한다.
	 *
	 * @param object
	 * 			변환할 데이터
	 * @return
	 */
	private static String convertObjectToString(Object object) {
		return object == null ? "-" : object.toString();
	}

	/**
	 * csv가 excel에서도 정상적으로 열리게 하기 위한 인코딩 추가 함수
	 *
	 * @return
	 */
	private static byte [] encodeUTF8BOM() {
		byte [] encoding = new byte[6];

		encoding[0] = (byte) 239;
		encoding[1] = (byte) 187;
		encoding[2] = (byte) 191;
		encoding[3] = (byte) 239;
		encoding[4] = (byte) 187;
		encoding[5] = (byte) 191;

		return encoding;
	}

}
