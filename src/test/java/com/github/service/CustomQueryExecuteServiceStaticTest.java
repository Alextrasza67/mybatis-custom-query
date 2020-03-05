package com.github.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.regex.Pattern;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Slf4j
public class CustomQueryExecuteServiceStaticTest {

    @Test
    public void testCleanParams(){
        assertTrue(Pattern.matches("__.*__","__sql__"));
        assertTrue(Pattern.matches("__.*__","___sql___"));
        assertFalse(Pattern.matches("__.*__","_sql_"));
        assertFalse(Pattern.matches("__.*__","__sql"));
        assertFalse(Pattern.matches("__.*__","sql__"));
        assertFalse(Pattern.matches("__.*__","id"));
    }
}