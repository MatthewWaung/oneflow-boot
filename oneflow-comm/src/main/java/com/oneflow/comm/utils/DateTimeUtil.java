/**
 * Copyright (c) 2018-2099, DreamLu 卢春梦 (qq596392912@gmail.com).
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE 3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.oneflow.comm.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;

/**
 * DateTime 工具类
 *
 * @author L.cm
 */
public class DateTimeUtil {
	public static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern(DateUtil.PATTERN_DATETIME);
	public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(DateUtil.PATTERN_DATE);
	public static final DateTimeFormatter TIME_FORMAT =  DateTimeFormatter.ofPattern(DateUtil.PATTERN_TIME);

	/**
	 * 日期时间格式化
	 *
	 * @param temporal 时间
	 * @return 格式化后的时间
	 */
	public static String formatDateTime(TemporalAccessor temporal) {
		return DATETIME_FORMAT.format(temporal);
	}

	/**
	 * 日期时间格式化
	 *
	 * @param temporal 时间
	 * @return 格式化后的时间
	 */
	public static String formatDate(TemporalAccessor temporal) {
		return DATE_FORMAT.format(temporal);
	}

	/**
	 * 时间格式化
	 *
	 * @param temporal 时间
	 * @return 格式化后的时间
	 */
	public static String formatTime(TemporalAccessor temporal) {
		return TIME_FORMAT.format(temporal);
	}

	/**
	 * 日期格式化
	 *
	 * @param temporal 时间
	 * @param pattern  表达式
	 * @return 格式化后的时间
	 */
	public static String format(TemporalAccessor temporal, String pattern) {
		return DateTimeFormatter.ofPattern(pattern).format(temporal);
	}

	/**
	 * 将字符串转换为时间
	 *
	 * @param dateStr 时间字符串
	 * @param pattern 表达式
	 * @return 时间
	 */
	public static TemporalAccessor parse(String dateStr, String pattern) {
		DateTimeFormatter format = DateTimeFormatter.ofPattern(pattern);
		return format.parse(dateStr);
	}

	/**
	 * 将字符串转换为时间
	 *
	 * @param dateStr   时间字符串
	 * @param formatter DateTimeFormatter
	 * @return 时间
	 */
	public static TemporalAccessor parse(String dateStr, DateTimeFormatter formatter) {
		return formatter.parse(dateStr);
	}

	/**
	 * 时间转 Instant
	 *
	 * @param dateTime 时间
	 * @return Instant
	 */
	public static Instant toInstant(LocalDateTime dateTime) {
		return dateTime.atZone(ZoneId.systemDefault()).toInstant();
	}

	/**
	 * Instant 转 时间
	 *
	 * @param instant Instant
	 * @return Instant
	 */
	public static LocalDateTime toDateTime(Instant instant) {
		return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
	}

	/**
	 * 本地时间转ISO标准时间
	 *
	 * @param dateTime
	 * @return
	 */
	public static String localTimeToISO8601(LocalDateTime dateTime) {
		ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
		return zonedDateTime.format(DateTimeFormatter.ISO_INSTANT);
	}

	/**
	 * ISO标准时间转本地时间
	 *
	 * @param str
	 * @return
	 */
	public static String ISO8601ToLocalTime(String str) {
		if (str == null || str.trim().isEmpty()) {
			return null;
		}

		DateTimeFormatter[] formatters = {
				DateTimeFormatter.ISO_ZONED_DATE_TIME, // Supports time with timezone offset like +09:00
				DateTimeFormatter.ISO_INSTANT,         // Supports time with 'Z' (UTC)
				DateTimeFormatter.ISO_LOCAL_DATE_TIME  // Supports time without timezone info
		};

		for (DateTimeFormatter formatter : formatters) {
			try {
				ZonedDateTime zonedDateTime = ZonedDateTime.parse(str, formatter);
				LocalDateTime localDateTime = zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
				return localDateTime.toString();
			} catch (DateTimeParseException e) {
				// Try the next formatter
			}
		}

		throw new IllegalArgumentException("Invalid ISO 8601 date-time format: " + str);
	}

}
