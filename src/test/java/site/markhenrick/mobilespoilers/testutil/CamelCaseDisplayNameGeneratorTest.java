package site.markhenrick.mobilespoilers.testutil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// I'm so meta even this acronym
class CamelCaseDisplayNameGeneratorTest {
	private CamelCaseDisplayNameGenerator generator;

	@BeforeEach
	void setUp() {
		this.generator = new CamelCaseDisplayNameGenerator();
	}

	@Test
	void shouldRemoveTestSuffixFromTestClass() {
		var expected = CamelCaseDisplayNameGenerator.class.getSimpleName();
		var actual = generator.generateDisplayNameForClass(getClass());
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void shouldRemoveCamelCaseFromNestedClass() {
		var expected = "camel case display name generator test";
		var actual = generator.generateDisplayNameForNestedClass(getClass());
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void shouldRemoveCamelCaseFromMethod() throws NoSuchMethodException {
		var method = getClass().getDeclaredMethod("shouldRemoveCamelCaseFromMethod");
		var expected = "should remove camel case from method";
		var actual = generator.generateDisplayNameForMethod(getClass(), method);
		assertThat(actual).isEqualTo(expected);
	}
}
