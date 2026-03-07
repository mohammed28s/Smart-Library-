package com.smartlibrary.backend;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SmartLibraryBackendApplicationTests {

    @Test
    void appClassLoads() {
        assertNotNull(SmartLibraryBackendApplication.class);
        assertEquals("com.smartlibrary.backend", SmartLibraryBackendApplication.class.getPackageName());
    }
}
