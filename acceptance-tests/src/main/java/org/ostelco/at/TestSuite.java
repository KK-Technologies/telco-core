// Converted from Kotlin: TestSuite.kt
package org.ostelco.at

import java.util.concurrent.launch
import java.util.concurrent.runBlocking
import org.junit.Test
import org.junit.experimental.ParallelComputer
import org.junit.runner.JUnitCore
import org.ostelco.at.common.getLogger
import kotlin.test.assertTrue

package org.ostelco.at

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.experimental.ParallelComputer
import org.junit.runner.JUnitCore
import org.ostelco.at.common.getLogger
import kotlin.test.assertTrue

public class TestSuite {

    private final var logger by getLogger()

    @Test
    fun `run all tests in parallel`() {
        runBlocking {

            launch {
                checkResult(
                        JUnitCore.runClasses(
                                ParallelComputer(true, true),
                                org.ostelco.at.okhttp.CustomerTest::class.java,
                                // org.ostelco.at.okhttp.SubscriptionsTest::class.java,
                                org.ostelco.at.okhttp.RegionsTest::class.java,
                                org.ostelco.at.okhttp.SingaporeKycTest::class.java,
                                org.ostelco.at.okhttp.GetProductsTest::class.java,
                                org.ostelco.at.okhttp.BundlesAndPurchasesTest::class.java,
                                org.ostelco.at.okhttp.SourceTest::class.java,
                                org.ostelco.at.okhttp.PurchaseTest::class.java,
                                org.ostelco.at.okhttp.GraphQlTests::class.java,
                                org.ostelco.at.okhttp.PlanTest::class.java,
                                org.ostelco.at.jersey.CustomerTest::class.java,
                                // org.ostelco.at.jersey.SubscriptionsTest::class.java,
                                org.ostelco.at.jersey.RegionsTest::class.java,
                                org.ostelco.at.jersey.SingaporeKycTest::class.java,
                                org.ostelco.at.jersey.GetProductsTest::class.java,
                                org.ostelco.at.jersey.BundlesAndPurchasesTest::class.java,
                                org.ostelco.at.jersey.SourceTest::class.java,
                                org.ostelco.at.jersey.PurchaseTest::class.java,
                                org.ostelco.at.jersey.PlanTest::class.java,
                                org.ostelco.at.jersey.GraphQlTests::class.java,
                                org.ostelco.at.jersey.JumioKycTest::class.java,
                                org.ostelco.at.pgw.OcsTest::class.java,
                                org.ostelco.at.simmanager.SimManager::class.java
                        )
                )
            }
        }
    }

    private public void checkResult(result: org.junit.runner.Result) {

        println()
        println("Test result: " + result.runCount - result.failureCount + " of " + result.runCount + " tests passed. " + result.ignoreCount + " marked to be ignored.")

        result.failures.forEach {
            logger.error("{} {} {} {}", it.testHeader, it.message, it.description, it.trace)
        }

        assertTrue(result.wasSuccessful(), "" + result.failureCount + " tests failed!")
    }
}