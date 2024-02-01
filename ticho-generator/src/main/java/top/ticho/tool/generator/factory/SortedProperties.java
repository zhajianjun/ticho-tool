package top.ticho.tool.generator.factory;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;


/**
 * Specialization of {@link Properties} that sorts properties alphanumerically
 * based on their keys.
 *
 * <p>This can be useful when storing the {@link Properties} instance in a
 * properties file, since it allows such files to be generated in a repeatable
 * manner with consistent ordering of properties.
 *
 * <p>Comments in generated properties files can also be optionally omitted.
 *
 * @author Sam Brannen
 * @since 5.2
 * @see java.util.Properties
 */
@SuppressWarnings("serial")
public class SortedProperties extends Properties {

    static final String EOL = System.lineSeparator();

    private static final Comparator<Object> KEY_COMPARATOR = Comparator.comparing(String::valueOf);

    private static final Comparator<Entry<Object, Object>> ENTRY_COMPARATOR = Entry.comparingByKey(KEY_COMPARATOR);


    private final boolean omitComments;


    /**
     * Construct a new {@code SortedProperties} instance that honors the supplied
     * {@code omitComments} flag.
     * @param omitComments {@code true} if comments should be omitted when
     * storing properties in a file
     */
    public SortedProperties(boolean omitComments) {
        this.omitComments = omitComments;
    }

    /**
     * Construct a new {@code SortedProperties} instance with properties populated
     * from the supplied {@link Properties} object and honoring the supplied
     * {@code omitComments} flag.
     * <p>Default properties from the supplied {@code Properties} object will
     * not be copied.
     * @param properties the {@code Properties} object from which to copy the
     * initial properties
     * @param omitComments {@code true} if comments should be omitted when
     * storing properties in a file
     */
    public SortedProperties(Properties properties, boolean omitComments) {
        this(omitComments);
        putAll(properties);
    }


    @Override
    public void store(OutputStream out, String comments) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        super.store(baos, (this.omitComments ? null : comments));
        String contents = baos.toString(StandardCharsets.ISO_8859_1.name());
        for (String line : contents.split(EOL)) {
            if (!(this.omitComments && line.startsWith("#"))) {
                out.write((line + EOL).getBytes(StandardCharsets.ISO_8859_1));
            }
        }
    }

    @Override
    public void store(Writer writer, String comments) throws IOException {
        StringWriter stringWriter = new StringWriter();
        super.store(stringWriter, (this.omitComments ? null : comments));
        String contents = stringWriter.toString();
        for (String line : contents.split(EOL)) {
            if (!(this.omitComments && line.startsWith("#"))) {
                writer.write(line + EOL);
            }
        }
    }

    @Override
    public void storeToXML(OutputStream out, String comments) throws IOException {
        super.storeToXML(out, (this.omitComments ? null : comments));
    }

    @Override
    public void storeToXML(OutputStream out, String comments, String encoding) throws IOException {
        super.storeToXML(out, (this.omitComments ? null : comments), encoding);
    }

    /**
     * Return a sorted enumeration of the keys in this {@link Properties} object.
     * @see #keySet()
     */
    @Override
    public synchronized Enumeration<Object> keys() {
        return Collections.enumeration(keySet());
    }

    /**
     * Return a sorted set of the keys in this {@link Properties} object.
     * <p>The keys will be converted to strings if necessary using
     * {@link String#valueOf(Object)} and sorted alphanumerically according to
     * the natural order of strings.
     */
    @Override
    public Set<Object> keySet() {
        Set<Object> sortedKeys = new TreeSet<>(KEY_COMPARATOR);
        sortedKeys.addAll(super.keySet());
        return Collections.synchronizedSet(sortedKeys);
    }

    /**
     * Return a sorted set of the entries in this {@link Properties} object.
     * <p>The entries will be sorted based on their keys, and the keys will be
     * converted to strings if necessary using {@link String#valueOf(Object)}
     * and compared alphanumerically according to the natural order of strings.
     */
    @Override
    public Set<Entry<Object, Object>> entrySet() {
        Set<Entry<Object, Object>> sortedEntries = new TreeSet<>(ENTRY_COMPARATOR);
        sortedEntries.addAll(super.entrySet());
        return Collections.synchronizedSet(sortedEntries);
    }

}

