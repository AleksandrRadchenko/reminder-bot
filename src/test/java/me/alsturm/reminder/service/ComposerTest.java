package me.alsturm.reminder.service;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ComposerTest {
    Composer sut = new Composer(null);

    @Test
    void shouldPrettyPrintUpdate() {
        var toSerialize = new SomeObjectWithOnlyPrivateFields(1, "Field value", null);
        //act
        String prettified = sut.toPrettyJson(toSerialize);
        //assert
        assertThat(prettified).isEqualToIgnoringNewLines("""
            {
              "id" : 1,
              "field1" : "Field value"
            }""");
    }

    @AllArgsConstructor
    private static class SomeObjectWithOnlyPrivateFields {
        private Integer id;
        private String field1;
        private String nullFieldShouldBeIgnored;
    }
}
