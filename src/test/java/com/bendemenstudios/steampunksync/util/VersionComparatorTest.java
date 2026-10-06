package com.bendemenstudios.steampunksync.util;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class VersionComparatorTest {
 @Test void comparesVersions(){assertTrue(VersionComparator.compare("v1.2","v1.1")>0);assertEquals(0,VersionComparator.compare("1.0","v1.0.0"));assertTrue(VersionComparator.compare("v2","v10")<0);}
}
