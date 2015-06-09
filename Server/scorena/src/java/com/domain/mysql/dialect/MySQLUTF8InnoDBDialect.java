package com.domain.mysql.dialect;

import org.hibernate.dialect.MySQLInnoDBDialect;

/**
 * Sets the default charset to UTF-8.
 */
public class MySQLUTF8InnoDBDialect extends MySQLInnoDBDialect {

    @Override
    public String getTableTypeString() {
        return " ENGINE=InnoDB DEFAULT CHARSET=utf8";
    }
}
