/*
 * This file is generated by jOOQ.
 */
package site.markhenrick.mobilespoilers.dal.jooqgenerated;


import site.markhenrick.mobilespoilers.dal.jooqgenerated.tables.Spoilers;
import site.markhenrick.mobilespoilers.dal.jooqgenerated.tables.records.Spoiler;

import javax.annotation.processing.Generated;

import org.jooq.UniqueKey;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables of 
 * the <code></code> schema.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.3"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // IDENTITY definitions
    // -------------------------------------------------------------------------


    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<Spoiler> PK_SPOILERS = UniqueKeys0.PK_SPOILERS;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------


    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class UniqueKeys0 {
        public static final UniqueKey<Spoiler> PK_SPOILERS = Internal.createUniqueKey(Spoilers.SPOILERS, "pk_spoilers", Spoilers.SPOILERS.MESSAGE_ID);
    }
}
