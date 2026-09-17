package com.printqueue.suite;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * RegressionTestSuite — Comprehensive automated test execution suite.
 * Automatically discovers and executes all unit, repository, service, controller,
 * consistency, frontend, and end-to-end integration tests under the root package.
 */
@Suite
@SuiteDisplayName("Digital Printing Queue Management System - Full Regression Test Suite")
@SelectPackages("com.printqueue")
public class RegressionTestSuite {
}
