package dido.replay;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ReplayFileHelperTest {

    @Test
    void whenDirThePathsCorrect() {

        ReplayFileHelper fileHelper = ReplayFileHelper
                .withSettings()
                .dir(Path.of("SomeDir"))
                .make();

        assertThat(fileHelper.getDataFile().toString(), is("SomeDir" + File.separator + "data.json"));
        assertThat(fileHelper.getSchemaFile().toString(), is("SomeDir" + File.separator + "schema.json"));
        assertThat(fileHelper.getTimeFile().toString(), is("SomeDir" + File.separator + "time.json"));
    }

    @Test
    void whenPrefixThePathsCorrect() {

        ReplayFileHelper fileHelper = ReplayFileHelper
                .withSettings()
                .filesPrefix("our.")
                .make();

        assertThat(fileHelper.getDataFile().toString(), is("." + File.separator + "our.data.json"));
        assertThat(fileHelper.getSchemaFile().toString(), is("." + File.separator + "our.schema.json"));
        assertThat(fileHelper.getTimeFile().toString(), is("." + File.separator + "our.time.json"));

    }
}