package site.markhenrick.mobilespoilers.testutil;

import org.junit.jupiter.api.DisplayNameGenerator;
import org.springframework.data.util.ParsingUtils;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class CamelCaseDisplayNameGenerator extends DisplayNameGenerator.Standard {
	private static final Pattern TEST_SUFFIX = Pattern.compile("Test$");

	@Override
	public String generateDisplayNameForClass(Class<?> testClass) {
		return TEST_SUFFIX.matcher(testClass.getSimpleName()).replaceFirst("");
	}

	@Override
	public String generateDisplayNameForNestedClass(Class<?> nestedClass) {
		return deCamelCase(nestedClass.getSimpleName());
	}

	@Override
	public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {
		return deCamelCase(testMethod.getName());
	}

	private static String deCamelCase(String camelCase) {
		return String.join(" ", ParsingUtils.splitCamelCaseToLower(camelCase));
	}
}
