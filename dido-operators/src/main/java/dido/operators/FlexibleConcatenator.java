package dido.operators;

import dido.data.DidoData;
import dido.data.ReadSchema;

/**
 * Compares previous schemas so we can maybe shortcut.
 */
public class FlexibleConcatenator {

    private final Concatenator.Settings settings;

    private Concatenator last;

    private ReadSchema[] previous;

    private FlexibleConcatenator(Concatenator.Settings settings) {
        this.settings = settings;
    }

    public static class Settings {

        Concatenator.Settings settings = Concatenator.with();

        public Settings excludeFields(String... exclusions) {
            settings.excludeFields(exclusions);
            return this;
        }

        public Settings skipDuplicates(boolean skipDuplicates) {
            settings.skipDuplicates(skipDuplicates);
            return this;
        }

        public FlexibleConcatenator create() {
            return new FlexibleConcatenator(settings);
        }
    }

    public static Settings with() {
        return new Settings();
    }

    public DidoData concat(DidoData... data) {

        boolean recreate = false;
        if (last == null) {
            recreate = true;
            previous = new ReadSchema[data.length];
            for (int i = 0; i < data.length; ++i) {
                previous[i] = ReadSchema.from(data[i].getSchema());
            }
        } else {
            for (int i = 0; i < data.length; ++i) {
                if (previous[i] != data[i].getSchema()) {
                    recreate = true;
                    previous[i] = ReadSchema.from(data[i].getSchema());
                }
            }
        }

        if (recreate) {
            last = settings.fromSchemas(previous);
        }

        return last.concat(data);
    }

}
