package com.mahasbr.util;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Standard, cache-safe wrapper for cursor-based pagination.
 *
 * <p>Why this exists:
 * <ul>
 *   <li>Redis cannot deserialize Spring Data {@code Slice}</li>
 *   <li>Cursor pagination must not expose total counts</li>
 *   <li>Safe for JSON & Redis serialization</li>
 * </ul>
 *
 * @param content the current page data
 * @param hasNext whether more data is available
 */
public record SliceResponse<T>(
        List<T> content,
        boolean hasNext
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}