package site.markhenrick.mobilespoilers.services;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class StatisticsServiceTest {

	@Nested
	class PrettyPrintSizes {
		@ParameterizedTest(name = "{0}KiB = {1}")
		@CsvSource({
			"0, 0KiB",
			"1, 1KiB",
			"1023, 1023KiB",
			"1024, 1MiB",
			"1025, 1MiB",
			"2048, 2MiB",
			"2049, 2MiB",
			"1024000, 1000MiB",
			"1048576, 1GiB",
			"1073741824, 1TiB",
			"9223372036854775807, 8ZiB"
		})
		void shouldPrettyPrintSizes(String inputString, String expected) {
			var input = Long.parseLong(inputString);
			assertThat(StatisticsService.prettyPrintSize(input)).isEqualTo(expected);
		}
	}

	@Nested
	class IsSafeColumnName {
		@ParameterizedTest
		@EnumSource(StatisticsService.Column.class)
		void shouldAcceptKnownColumnName(StatisticsService.Column column) {
			assertThat(StatisticsService.Column.isSafeColumnName(column.toString())).isTrue();
		}

		@SuppressWarnings("SpellCheckingInspection")
		@ParameterizedTest
		@ValueSource(strings = {
			"with a space",
			" withpadding ",
			"TWO_COLUMNS TWO_COLUMNS",
			"XFER_KB; drop table spoilers",
			""
		})
		void shouldRejectDangerousStrings(CharSequence string) {
			assertThat(StatisticsService.Column.isSafeColumnName(string)).isFalse();
		}
	}
}
