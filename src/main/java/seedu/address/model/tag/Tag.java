package seedu.address.model.tag;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Tag in the address book.
 * Guarantees: immutable; name is valid as declared in {@link #isValidTagName(String)}
 */
public class Tag {
    enum Type {
        GENERAL,
        EVENT,
        MODULE
    }

    public static final String MESSAGE_CONSTRAINTS = "Tags names should be alphanumeric";
    public static final String VALIDATION_REGEX =
            "\\p{Alnum}+|event-\\s*\\p{Alnum}+|mod-\\s*\\p{Alnum}+";

    public final String tagName;
    public final Type type;

    /**
     * Constructs a {@code Tag}.
     *
     * @param tagDescription A valid tag description.
     */
    public Tag(String tagDescription) {
        requireNonNull(tagDescription);
        checkArgument(isValidTagName(tagDescription));
        this.type = parseTagType(tagDescription);
        this.tagName = parseTagName(type, tagDescription);
    }

    /**
     * Returns true if a given string is a valid tag name.
     */
    public static boolean isValidTagName(String test) {
        boolean isValidTagName = test.matches(VALIDATION_REGEX);
        return isValidTagName;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Tag // instanceof handles nulls
                && tagName.equals(((Tag) other).tagName)); // state check
    }

    @Override
    public int hashCode() {
        return tagName.hashCode();
    }

    /**
     * Format state as text for viewing.
     */
    public String toString() {
        return '[' + tagName + ']';
    }

    /**
     * Parses the tag type based on the given tag description.
     *
     * @param tagDescription A valid tag description.
     * @return Type of tag represented by tag description.
     */
    public Type parseTagType(String tagDescription) {
        assert(!tagName.isEmpty());
        if (tagName.indexOf("event-") == 0) {
            return Type.EVENT;
        } else if (tagName.indexOf("mod-") == 0) {
            return Type.MODULE;
        } else {
            return Type.GENERAL;
        }
    }

    /**
     * Parses the tag name based on the given tag type and description.
     *
     * @param tagDescription A valid tag description.
     * @param tagType Type of tag.
     * @return Name of tag represented by tag description.
     */
    public String parseTagName(Type tagType, String tagDescription) {
        if (tagType == Type.GENERAL) {
            return tagName;
        } else if (tagType == Type.EVENT) {
            return tagName.substring(6).trim();
        } else {
            return tagName.substring(4).trim();
        }
    }
}
