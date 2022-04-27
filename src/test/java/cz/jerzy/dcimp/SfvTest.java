package cz.jerzy.dcimp;


import com.drew.lang.annotations.NotNull;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class SfvTest {

    @Test
    void load() {
        Path path = getResourcePath("/samples/DSC00053.sfv");

        Sfv sfv = Sfv.load(path);

        assertThat(sfv).isNotNull();
    }

    @Test
    void calculate() {
        Path path = getResourcePath("/samples/DSC00053.ARW");

        Sfv sfv = Sfv.calculate(path);

        assertThat(sfv).isNotNull();
    }

    @SneakyThrows
    private Path getResourcePath(@NotNull String resource) {
        return Path.of(Objects.requireNonNull(getClass().getResource(resource)).toURI());
    }

}