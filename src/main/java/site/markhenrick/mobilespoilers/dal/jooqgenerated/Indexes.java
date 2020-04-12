/*
 * This file is generated by jOOQ.
 */
package site.markhenrick.mobilespoilers.dal.jooqgenerated;


import site.markhenrick.mobilespoilers.dal.jooqgenerated.tables.Spoilers;

import javax.annotation.processing.Generated;

import org.jooq.Index;
import org.jooq.OrderField;
import org.jooq.impl.Internal;


/**
 * A class modelling indexes of tables of the <code></code> schema.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.3"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Indexes {

    // -------------------------------------------------------------------------
    // INDEX definitions
    // -------------------------------------------------------------------------

    public static final Index SQLITE_AUTOINDEX_SPOILERS_1 = Indexes0.SQLITE_AUTOINDEX_SPOILERS_1;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Indexes0 {
        public static Index SQLITE_AUTOINDEX_SPOILERS_1 = Internal.createIndex("sqlite_autoindex_spoilers_1", Spoilers.SPOILERS, new OrderField[] { Spoilers.SPOILERS.MESSAGE_ID }, true);
    }
}
