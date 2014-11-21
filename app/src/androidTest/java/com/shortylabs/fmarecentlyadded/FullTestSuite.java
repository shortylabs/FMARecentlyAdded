package com.shortylabs.fmarecentlyadded;

import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.Test;

/**
 * Created by Jeri on 11/21/14.
 */
public class FullTestSuite {
    public static Test suite() {
        return new TestSuiteBuilder(FullTestSuite.class)
                .includeAllPackagesUnderHere().build();
    }

    public FullTestSuite() {
        super();
    }
}
