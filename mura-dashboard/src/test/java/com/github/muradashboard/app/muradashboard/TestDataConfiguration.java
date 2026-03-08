package com.github.muradashboard.app.muradashboard;

import com.github.muradashboard.app.testreport.TestReportRepository;
import com.github.muradashboard.app.testreport.entity.TestCaseEntity;
import com.github.muradashboard.app.testreport.entity.TestReportEntity;
import com.github.muradashboard.app.testreport.entity.TestSuiteEntity;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@TestConfiguration
public class TestDataConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(TestDataConfiguration.class);

    @Bean
    ApplicationRunner testDataInitializer(TestReportRepository reportRepository) {
        return _ -> {
            StopWatch stopWatch = StopWatch.createStarted();
            logger.info("Initializing test data ...");
            var now = Instant.now();
            var reports = new ArrayList<TestReportEntity>();

            // ─── Project: 1% flaky, Module: :app ────────────────────────────
            for (int i = 0; i < 99; i++) {
                reports.add(report("1% flaky", ":app", "test", now.minus(i, ChronoUnit.HOURS), List.of(
                        suite("com.example.app.UserServiceTest", now.minus(i, ChronoUnit.HOURS),
                                5, 0, 0, 0, 1.2, List.of(
                                        passed("shouldCreateUser", "com.example.app.UserServiceTest", 0.15),
                                        passed("shouldFindUserById", "com.example.app.UserServiceTest", 0.08),
                                        passed("shouldUpdateUser", "com.example.app.UserServiceTest", 0.12),
                                        passed("shouldDeleteUser", "com.example.app.UserServiceTest", 0.05),
                                        passed("shouldListAllUsers", "com.example.app.UserServiceTest", 0.80))),
                        suite("com.example.app.OrderServiceTest", now.minus(i, ChronoUnit.HOURS),
                                6, 0, 0, 0, 0.5, List.of(
                                        passed("shouldCreateOrder", "com.example.app.OrderServiceTest", 0.20),
                                        passed("shouldCancelOrder", "com.example.app.OrderServiceTest", 0.15),
                                        passed("shouldCalculateAvg", "com.example.app.OrderServiceTest", 0.20),
                                        passed("shouldCalculateMax", "com.example.app.OrderServiceTest", 0.20),
                                        passed("shouldCalculateMin", "com.example.app.OrderServiceTest", 0.20),
                                        passed("shouldCalculateTotal", "com.example.app.OrderServiceTest", 0.15)))
                )));
                if (i % 10 == 0) {
                    reports.add(report("10% flaky", ":app", "test", now.minus(100, ChronoUnit.HOURS), List.of(
                            suite("com.example.app.OrderServiceTest", now.minus(100, ChronoUnit.HOURS),
                                    3, 0, 1, 0, 0.5, List.of(
                                            flakyPassed("shouldCreateOrder", "com.example.app.OrderServiceTest", 0.20),
                                            flakyFailed("shouldCreateOrder", "com.example.app.OrderServiceTest", 0.20, "fail1", "type", "details"),
                                            passed("shouldCalculateTotal", "com.example.app.OrderServiceTest", 0.15))))));
                }
                if (i % 5 == 0) {
                    reports.add(report("15% flaky", ":app", "test", now.minus(100, ChronoUnit.HOURS), List.of(
                            suite("com.example.app.OrderServiceTest", now.minus(100, ChronoUnit.HOURS),
                                    2, 0, 2, 0, 0.5, List.of(
                                            flakyFailed("shouldCalculateTotal", "com.example.app.OrderServiceTest", 0.20, "fail1", "type", "details"),
                                            flakyPassed("shouldCalculateTotal", "com.example.app.OrderServiceTest", 0.15))))));
                }
                if (i % 4 == 0) {
                    reports.add(report("20% flaky", ":app", "test", now.minus(100, ChronoUnit.HOURS), List.of(
                            suite("com.example.app.OrderServiceTest", now.minus(100, ChronoUnit.HOURS),
                                    2, 0, 2, 0, 0.5, List.of(
                                            flakyFailed("shouldCalculateAvg", "com.example.app.OrderServiceTest", 0.20, "fail1", "type", "details"),
                                            flakyPassed("shouldCalculateAvg", "com.example.app.OrderServiceTest", 0.15))))));
                }
                if (i % 3 == 0) {
                    reports.add(report("25% flaky", ":app", "test", now.minus(100, ChronoUnit.HOURS), List.of(
                            suite("com.example.app.OrderServiceTest", now.minus(100, ChronoUnit.HOURS),
                                    2, 0, 2, 0, 0.5, List.of(
                                            flakyFailed("shouldCalculateMin", "com.example.app.OrderServiceTest", 0.20, "fail1", "type", "details"),
                                            flakyPassed("shouldCalculateMin", "com.example.app.OrderServiceTest", 0.15))))));
                }
                if (i % 2 == 0) {
                    reports.add(report("33% flaky", ":app", "test", now.minus(100, ChronoUnit.HOURS), List.of(
                            suite("com.example.app.OrderServiceTest", now.minus(100, ChronoUnit.HOURS),
                                    2, 0, 2, 0, 0.5, List.of(
                                            flakyFailed("shouldCalculateMax", "com.example.app.OrderServiceTest", 0.20, "fail1", "type", "details"),
                                            flakyPassed("shouldCalculateMax", "com.example.app.OrderServiceTest", 0.15))))));
                }
            }
            reports.add(report("1% flaky", ":app", "test", now.minus(100, ChronoUnit.HOURS), List.of(
                    suite("com.example.app.UserServiceTest", now.minus(100, ChronoUnit.HOURS),
                            5, 0, 0, 0, 1.2, List.of(
                                    passed("shouldCreateUser", "com.example.app.UserServiceTest", 0.15),
                                    passed("shouldFindUserById", "com.example.app.UserServiceTest", 0.08),
                                    passed("shouldUpdateUser", "com.example.app.UserServiceTest", 0.12),
                                    passed("shouldDeleteUser", "com.example.app.UserServiceTest", 0.05),
                                    passed("shouldListAllUsers", "com.example.app.UserServiceTest", 0.80))),
                    suite("com.example.app.OrderServiceTest", now.minus(100, ChronoUnit.HOURS),
                            13, 0, 10, 0, 0.5, List.of(
                                    flakyPassed("shouldCreateOrder", "com.example.app.OrderServiceTest", 0.20),
                                    flakyFailed("shouldCreateOrder", "com.example.app.OrderServiceTest", 0.20, "fail1", "type", "details"),
                                    flakyPassed("shouldCancelOrder", "com.example.app.OrderServiceTest", 0.15),
                                    flakyFailed("shouldCancelOrder", "com.example.app.OrderServiceTest", 0.15, "fail1", "type", "details"),
                                    flakyFailed("shouldCancelOrder", "com.example.app.OrderServiceTest", 0.15, "fail2", "type", "details"),
                                    flakyFailed("shouldCancelOrder", "com.example.app.OrderServiceTest", 0.15, "fail3", "type", "details"),
                                    flakyFailed("shouldCancelOrder", "com.example.app.OrderServiceTest", 0.15, "fail4", "type", "details"),
                                    flakyFailed("shouldCancelOrder", "com.example.app.OrderServiceTest", 0.15, "fail5", "type", "details"),
                                    flakyFailed("shouldCancelOrder", "com.example.app.OrderServiceTest", 0.15, "fail6", "type", "details"),
                                    flakyFailed("shouldCancelOrder", "com.example.app.OrderServiceTest", 0.15, "fail7", "type", "details"),
                                    flakyFailed("shouldCancelOrder", "com.example.app.OrderServiceTest", 0.15, "fail8", "type", "details"),
                                    flakyFailed("shouldCancelOrder", "com.example.app.OrderServiceTest", 0.15, "fail9", "type", "details"),
                                    passed("shouldCalculateTotal", "com.example.app.OrderServiceTest", 0.15))))));

            // ─── Project: mass-flaky-data flaky, Module: :app ────────────────────────────
            for (int i = 0; i < 1_000; i++) {
                final Instant createdAt = now.minus(8, ChronoUnit.DAYS).minusSeconds(i);
                reports.add(report("mass-flaky-data", ":app", "test", createdAt, List.of(
                        suite("com.foo.app.OrderServiceTest" + i, createdAt,
                                3, 0, 1, 0, 0.5, List.of(
                                        flakyPassed("shouldCreateOrder" + i , "com.foo.app.OrderServiceTest" + i, 0.20),
                                        flakyFailed("shouldCreateOrder" + i, "com.foo.app.OrderServiceTest" + i, 0.20, "fail1", "type", "details"),
                                        passed("shouldCalculateTotal" + i, "com.foo.app.OrderServiceTest" + i, 0.15)))
                )));
            }

            // ─── Project: my-app, Module: :app ────────────────────────────

            // 1) Recent unit tests — all passing
            reports.add(report("my-app", ":app", "test", now.minus(1, ChronoUnit.HOURS), List.of(
                    suite("com.example.app.UserServiceTest", now.minus(1, ChronoUnit.HOURS),
                            5, 0, 0, 0, 1.2, List.of(
                                    passed("shouldCreateUser", "com.example.app.UserServiceTest", 0.15),
                                    passed("shouldFindUserById", "com.example.app.UserServiceTest", 0.08),
                                    passed("shouldUpdateUser", "com.example.app.UserServiceTest", 0.12),
                                    passed("shouldDeleteUser", "com.example.app.UserServiceTest", 0.05),
                                    passed("shouldListAllUsers", "com.example.app.UserServiceTest", 0.80))),
                    suite("com.example.app.OrderServiceTest", now.minus(1, ChronoUnit.HOURS),
                            3, 0, 0, 0, 0.5, List.of(
                                    passed("shouldCreateOrder", "com.example.app.OrderServiceTest", 0.20),
                                    passed("shouldCancelOrder", "com.example.app.OrderServiceTest", 0.15),
                                    passed("shouldCalculateTotal", "com.example.app.OrderServiceTest", 0.15))))));

            // 2) Recent unit tests — two assertion failures
            reports.add(report("my-app", ":app", "test", now.minus(30, ChronoUnit.MINUTES), List.of(
                    suite("com.example.app.PaymentServiceTest", now.minus(30, ChronoUnit.MINUTES),
                            4, 0, 2, 0, 3.5, List.of(
                                    passed("shouldProcessPayment", "com.example.app.PaymentServiceTest", 0.50),
                                    failed("shouldHandleTimeout", "com.example.app.PaymentServiceTest", 1.20,
                                            "Expected timeout exception",
                                            "java.lang.AssertionError",
                                            "at PaymentServiceTest.shouldHandleTimeout(PaymentServiceTest.java:45)"),
                                    failed("shouldRetryOnFailure", "com.example.app.PaymentServiceTest", 1.50,
                                            "Expected 3 retries but got 0",
                                            "org.opentest4j.AssertionFailedError",
                                            "at PaymentServiceTest.shouldRetryOnFailure(PaymentServiceTest.java:78)"),
                                    passed("shouldRefund", "com.example.app.PaymentServiceTest", 0.30))))));

            // 3) Integration tests — one flaky test (initial failure + passing retry)
            reports.add(report("my-app", ":app", "integrationTest", now.minus(2, ChronoUnit.HOURS), List.of(
                    suite("com.example.app.UserApiIntegrationTest", now.minus(2, ChronoUnit.HOURS),
                            6, 0, 1, 0, 12.5, List.of(
                                    passed("shouldRegisterUser", "com.example.app.UserApiIntegrationTest", 2.10),
                                    passed("shouldLoginUser", "com.example.app.UserApiIntegrationTest", 1.80),
                                    flakyFailed("shouldHandleConcurrentRequests", "com.example.app.UserApiIntegrationTest", 3.50,
                                            "Race condition: expected 1 but got 2",
                                            "java.lang.AssertionError",
                                            "at UserApiIntegrationTest.shouldHandleConcurrentRequests(UserApiIntegrationTest.java:112)"),
                                    passed("shouldGetUserProfile", "com.example.app.UserApiIntegrationTest", 1.50),
                                    passed("shouldUpdateProfile", "com.example.app.UserApiIntegrationTest", 1.30),
                                    passed("shouldDeleteAccount", "com.example.app.UserApiIntegrationTest", 2.30))),
                    // retry suite — the flaky test passes this time
                    suite("com.example.app.UserApiIntegrationTest", now.minus(2, ChronoUnit.HOURS),
                            1, 0, 0, 0, 3.0, List.of(
                                    flakyPassed("shouldHandleConcurrentRequests", "com.example.app.UserApiIntegrationTest", 3.00))))));

            // 4) Unit tests — three skipped tests
            reports.add(report("my-app", ":app", "test", now.minus(1, ChronoUnit.DAYS), List.of(
                    suite("com.example.app.LegacyImporterTest", now.minus(1, ChronoUnit.DAYS),
                            5, 3, 0, 0, 0.8, List.of(
                                    passed("shouldImportCsv", "com.example.app.LegacyImporterTest", 0.40),
                                    passed("shouldValidateHeaders", "com.example.app.LegacyImporterTest", 0.40))))));

            // 5) Integration tests — infrastructure errors (not assertion failures)
            reports.add(report("my-app", ":app", "integrationTest", now.minus(3, ChronoUnit.HOURS), List.of(
                    suite("com.example.app.DatabaseMigrationTest", now.minus(3, ChronoUnit.HOURS),
                            3, 0, 0, 2, 15.0, List.of(
                                    passed("shouldMigrateV1", "com.example.app.DatabaseMigrationTest", 2.00),
                                    error("shouldMigrateV2", "com.example.app.DatabaseMigrationTest", 5.00,
                                            "Connection refused",
                                            "org.postgresql.util.PSQLException",
                                            "org.postgresql.util.PSQLException: Connection refused\n\tat ConnectionFactoryImpl.openConnectionImpl(ConnectionFactoryImpl.java:315)"),
                                    error("shouldRollbackOnError", "com.example.app.DatabaseMigrationTest", 8.00,
                                            "Lock wait timeout exceeded",
                                            "org.hibernate.exception.LockTimeoutException",
                                            "org.hibernate.exception.LockTimeoutException: Lock wait timeout exceeded\n\tat SQLExceptionTypeDelegate.convert(SQLExceptionTypeDelegate.java:73)"))))));

            // 6) Same flaky test appearing again 5 h ago (builds flakiness history)
            reports.add(report("my-app", ":app", "integrationTest", now.minus(5, ChronoUnit.HOURS), List.of(
                    suite("com.example.app.UserApiIntegrationTest", now.minus(5, ChronoUnit.HOURS),
                            6, 0, 1, 0, 14.0, List.of(
                                    passed("shouldRegisterUser", "com.example.app.UserApiIntegrationTest", 2.30),
                                    passed("shouldLoginUser", "com.example.app.UserApiIntegrationTest", 1.90),
                                    flakyFailed("shouldHandleConcurrentRequests", "com.example.app.UserApiIntegrationTest", 4.00,
                                            "Race condition: expected 1 but got 3",
                                            "java.lang.AssertionError",
                                            "at UserApiIntegrationTest.shouldHandleConcurrentRequests(UserApiIntegrationTest.java:112)"),
                                    passed("shouldGetUserProfile", "com.example.app.UserApiIntegrationTest", 1.60),
                                    passed("shouldUpdateProfile", "com.example.app.UserApiIntegrationTest", 1.40),
                                    passed("shouldDeleteAccount", "com.example.app.UserApiIntegrationTest", 2.80))),
                    suite("com.example.app.UserApiIntegrationTest", now.minus(5, ChronoUnit.HOURS),
                            1, 0, 0, 0, 3.2, List.of(
                                    flakyPassed("shouldHandleConcurrentRequests", "com.example.app.UserApiIntegrationTest", 3.20))))));

            // 7) Functional tests — mixed: passing, failing, error, skipped in one suite
            reports.add(report("my-app", ":app", "functionalTest", now.minus(15, ChronoUnit.MINUTES), List.of(
                    suite("com.example.app.CheckoutFlowTest", now.minus(15, ChronoUnit.MINUTES),
                            6, 1, 1, 1, 25.0, List.of(
                                    passed("shouldAddToCart", "com.example.app.CheckoutFlowTest", 3.00),
                                    passed("shouldApplyCoupon", "com.example.app.CheckoutFlowTest", 2.50),
                                    failed("shouldCalculateShipping", "com.example.app.CheckoutFlowTest", 4.00,
                                            "Shipping cost mismatch: expected $5.99 but was $0.00",
                                            "org.opentest4j.AssertionFailedError",
                                            "at CheckoutFlowTest.shouldCalculateShipping(CheckoutFlowTest.java:134)"),
                                    error("shouldProcessCheckout", "com.example.app.CheckoutFlowTest", 10.00,
                                            "Payment gateway unavailable",
                                            "java.io.IOException",
                                            "java.io.IOException: Payment gateway unavailable\n\tat PaymentClient.charge(PaymentClient.java:42)"),
                                    passed("shouldSendConfirmationEmail", "com.example.app.CheckoutFlowTest", 3.00),
                                    passed("shouldUpdateInventory", "com.example.app.CheckoutFlowTest", 2.50))),
                    suite("com.example.app.SearchFeatureTest", now.minus(15, ChronoUnit.MINUTES),
                            3, 0, 0, 0, 6.0, List.of(
                                    passed("shouldSearchByKeyword", "com.example.app.SearchFeatureTest", 2.00),
                                    passed("shouldFilterResults", "com.example.app.SearchFeatureTest", 2.00),
                                    passed("shouldPaginateResults", "com.example.app.SearchFeatureTest", 2.00))))));

            // 8) Two-week-old run — multiple flaky tests in the same suite
            reports.add(report("my-app", ":app", "test", now.minus(14, ChronoUnit.DAYS), List.of(
                    suite("com.example.app.CacheServiceTest", now.minus(14, ChronoUnit.DAYS),
                            4, 0, 2, 0, 5.0, List.of(
                                    flakyFailed("shouldEvictExpiredEntries", "com.example.app.CacheServiceTest", 2.00,
                                            "Expected cache size 0 but was 1",
                                            "java.lang.AssertionError",
                                            "at CacheServiceTest.shouldEvictExpiredEntries(CacheServiceTest.java:56)"),
                                    passed("shouldCacheResult", "com.example.app.CacheServiceTest", 0.50),
                                    flakyFailed("shouldHandleConcurrentAccess", "com.example.app.CacheServiceTest", 2.00,
                                            "ConcurrentModificationException",
                                            "java.util.ConcurrentModificationException",
                                            "java.util.ConcurrentModificationException\n\tat HashMap$HashIterator.nextNode(HashMap.java:1597)"),
                                    passed("shouldInvalidateOnUpdate", "com.example.app.CacheServiceTest", 0.50))),
                    suite("com.example.app.CacheServiceTest", now.minus(14, ChronoUnit.DAYS),
                            2, 0, 0, 0, 3.0, List.of(
                                    flakyPassed("shouldEvictExpiredEntries", "com.example.app.CacheServiceTest", 1.50),
                                    flakyPassed("shouldHandleConcurrentAccess", "com.example.app.CacheServiceTest", 1.50))))));

            // ─── Project: my-app, Module: :libs:common ─────────────────────

            // 9) Library module — all green
            reports.add(report("my-app", ":libs:common", "test", now.minus(4, ChronoUnit.HOURS), List.of(
                    suite("com.example.common.StringUtilsTest", now.minus(4, ChronoUnit.HOURS),
                            4, 0, 0, 0, 0.3, List.of(
                                    passed("shouldTrimWhitespace", "com.example.common.StringUtilsTest", 0.05),
                                    passed("shouldCapitalize", "com.example.common.StringUtilsTest", 0.03),
                                    passed("shouldJoinStrings", "com.example.common.StringUtilsTest", 0.02),
                                    passed("shouldHandleNull", "com.example.common.StringUtilsTest", 0.01))),
                    suite("com.example.common.DateUtilsTest", now.minus(4, ChronoUnit.HOURS),
                            3, 0, 0, 0, 0.2, List.of(
                                    passed("shouldFormatDate", "com.example.common.DateUtilsTest", 0.08),
                                    passed("shouldParseDate", "com.example.common.DateUtilsTest", 0.06),
                                    passed("shouldCalculateDiff", "com.example.common.DateUtilsTest", 0.06))))));

            // ─── Project: payment-service, Module: : ───────────────────────

            // 10) Mixed: failures, skipped, and a flaky webhook test
            reports.add(report("payment-service", ":", "test", now.minus(6, ChronoUnit.HOURS), List.of(
                    suite("com.example.payment.TransactionServiceTest", now.minus(6, ChronoUnit.HOURS),
                            5, 1, 1, 0, 4.5, List.of(
                                    passed("shouldProcessTransaction", "com.example.payment.TransactionServiceTest", 0.80),
                                    passed("shouldValidateAmount", "com.example.payment.TransactionServiceTest", 0.20),
                                    failed("shouldHandleCurrencyConversion", "com.example.payment.TransactionServiceTest", 1.50,
                                            "Expected USD but got EUR",
                                            "org.opentest4j.AssertionFailedError",
                                            "at TransactionServiceTest.shouldHandleCurrencyConversion(TransactionServiceTest.java:89)"),
                                    passed("shouldApplyDiscount", "com.example.payment.TransactionServiceTest", 0.30))),
                    suite("com.example.payment.WebhookHandlerTest", now.minus(6, ChronoUnit.HOURS),
                            3, 0, 1, 0, 8.2, List.of(
                                    passed("shouldReceiveWebhook", "com.example.payment.WebhookHandlerTest", 2.50),
                                    flakyFailed("shouldRetryFailedWebhook", "com.example.payment.WebhookHandlerTest", 3.20,
                                            "Timeout waiting for retry",
                                            "java.util.concurrent.TimeoutException",
                                            "java.util.concurrent.TimeoutException: Timeout waiting for retry after 5000ms"),
                                    passed("shouldVerifySignature", "com.example.payment.WebhookHandlerTest", 2.50))),
                    // retry suite
                    suite("com.example.payment.WebhookHandlerTest", now.minus(6, ChronoUnit.HOURS),
                            1, 0, 0, 0, 2.8, List.of(
                                    flakyPassed("shouldRetryFailedWebhook", "com.example.payment.WebhookHandlerTest", 2.80))))));

            // 11) Integration tests — total infrastructure outage (all errors), 3 days ago
            reports.add(report("payment-service", ":", "integrationTest", now.minus(3, ChronoUnit.DAYS), List.of(
                    suite("com.example.payment.PaymentGatewayIntegrationTest", now.minus(3, ChronoUnit.DAYS),
                            4, 0, 0, 4, 60.0, List.of(
                                    error("shouldChargeCard", "com.example.payment.PaymentGatewayIntegrationTest", 15.00,
                                            "Connection timed out",
                                            "java.net.SocketTimeoutException",
                                            "java.net.SocketTimeoutException: Connection timed out\n\tat java.net.Socket.connect(Socket.java:647)"),
                                    error("shouldRefundCharge", "com.example.payment.PaymentGatewayIntegrationTest", 15.00,
                                            "Connection timed out",
                                            "java.net.SocketTimeoutException",
                                            "java.net.SocketTimeoutException: Connection timed out"),
                                    error("shouldCreateSubscription", "com.example.payment.PaymentGatewayIntegrationTest", 15.00,
                                            "Connection timed out",
                                            "java.net.SocketTimeoutException",
                                            "java.net.SocketTimeoutException: Connection timed out"),
                                    error("shouldCancelSubscription", "com.example.payment.PaymentGatewayIntegrationTest", 15.00,
                                            "Connection timed out",
                                            "java.net.SocketTimeoutException",
                                            "java.net.SocketTimeoutException: Connection timed out"))))));

            // 12) Clean unit test run — one week ago
            reports.add(report("payment-service", ":", "test", now.minus(7, ChronoUnit.DAYS), List.of(
                    suite("com.example.payment.InvoiceServiceTest", now.minus(7, ChronoUnit.DAYS),
                            4, 0, 0, 0, 1.0, List.of(
                                    passed("shouldGenerateInvoice", "com.example.payment.InvoiceServiceTest", 0.30),
                                    passed("shouldApplyTax", "com.example.payment.InvoiceServiceTest", 0.20),
                                    passed("shouldSendInvoice", "com.example.payment.InvoiceServiceTest", 0.30),
                                    passed("shouldVoidInvoice", "com.example.payment.InvoiceServiceTest", 0.20))))));

            // ─── Project: notification-service, Module: : ──────────────────

            // 13) Heavily-skipped suite plus some failures
            reports.add(report("notification-service", ":", "test", now.minus(12, ChronoUnit.HOURS), List.of(
                    suite("com.example.notify.EmailSenderTest", now.minus(12, ChronoUnit.HOURS),
                            8, 5, 1, 0, 2.0, List.of(
                                    passed("shouldSendPlainText", "com.example.notify.EmailSenderTest", 0.40),
                                    passed("shouldSendHtml", "com.example.notify.EmailSenderTest", 0.35),
                                    failed("shouldHandleBounce", "com.example.notify.EmailSenderTest", 0.80,
                                            "Bounce handler returned null",
                                            "java.lang.NullPointerException",
                                            "java.lang.NullPointerException\n\tat EmailSenderTest.shouldHandleBounce(EmailSenderTest.java:67)"))),
                    suite("com.example.notify.SmsServiceTest", now.minus(12, ChronoUnit.HOURS),
                            4, 0, 0, 0, 1.5, List.of(
                                    passed("shouldSendSms", "com.example.notify.SmsServiceTest", 0.50),
                                    passed("shouldValidatePhoneNumber", "com.example.notify.SmsServiceTest", 0.30),
                                    passed("shouldHandleOptOut", "com.example.notify.SmsServiceTest", 0.40),
                                    passed("shouldTrackDelivery", "com.example.notify.SmsServiceTest", 0.30))))));

            // 14) Flaky notification test — appeared twice
            reports.add(report("notification-service", ":", "integrationTest", now.minus(10, ChronoUnit.HOURS), List.of(
                    suite("com.example.notify.PushNotificationTest", now.minus(10, ChronoUnit.HOURS),
                            3, 0, 1, 0, 9.0, List.of(
                                    passed("shouldSendPushToAndroid", "com.example.notify.PushNotificationTest", 2.00),
                                    flakyFailed("shouldSendPushToIos", "com.example.notify.PushNotificationTest", 5.00,
                                            "APNS connection reset",
                                            "java.net.SocketException",
                                            "java.net.SocketException: Connection reset\n\tat ApnsClient.send(ApnsClient.java:88)"),
                                    passed("shouldHandleInvalidToken", "com.example.notify.PushNotificationTest", 2.00))),
                    suite("com.example.notify.PushNotificationTest", now.minus(10, ChronoUnit.HOURS),
                            1, 0, 0, 0, 3.5, List.of(
                                    flakyPassed("shouldSendPushToIos", "com.example.notify.PushNotificationTest", 3.50))))));

            // 15) Same flaky test again, more recent
            reports.add(report("notification-service", ":", "integrationTest", now.minus(2, ChronoUnit.HOURS), List.of(
                    suite("com.example.notify.PushNotificationTest", now.minus(2, ChronoUnit.HOURS),
                            3, 0, 1, 0, 8.5, List.of(
                                    passed("shouldSendPushToAndroid", "com.example.notify.PushNotificationTest", 1.80),
                                    flakyFailed("shouldSendPushToIos", "com.example.notify.PushNotificationTest", 4.70,
                                            "APNS connection reset",
                                            "java.net.SocketException",
                                            "java.net.SocketException: Connection reset\n\tat ApnsClient.send(ApnsClient.java:88)"),
                                    passed("shouldHandleInvalidToken", "com.example.notify.PushNotificationTest", 2.00))),
                    suite("com.example.notify.PushNotificationTest", now.minus(2, ChronoUnit.HOURS),
                            1, 0, 0, 0, 3.0, List.of(
                                    flakyPassed("shouldSendPushToIos", "com.example.notify.PushNotificationTest", 3.00))))));

            reportRepository.saveAllAndFlush(reports);

            stopWatch.stop();
            logger.info("Test data initialization complete: {} reports created in {}", reports.size(), stopWatch.getDuration());
        };
    }

    // ── Factory helpers ────────────────────────────────────────────────

    private static TestReportEntity report(String name, String modulePath, String testTaskName,
                                           Instant createdAt, List<TestSuiteEntity> suites) {
        var report = TestReportEntity.builder()
                .name(name)
                .modulePath(modulePath)
                .testTaskName(testTaskName)
                .createdAt(createdAt)
                .build();
        report.setTestSuites(new ArrayList<>(suites));
        report.getTestSuites().forEach(s -> s.setTestReport(report));
        return report;
    }

    private static TestSuiteEntity suite(String name, Instant timestamp,
                                         int tests, int skipped, int failures, int errors,
                                         double time, List<TestCaseEntity> cases) {
        var s = TestSuiteEntity.builder()
                .name(name)
                .timestamp(timestamp)
                .tests(tests)
                .skipped(skipped)
                .failures(failures)
                .errors(errors)
                .time(time)
                .build();
        s.setTestCases(new ArrayList<>(cases));
        s.getTestCases().forEach(tc -> tc.setTestSuite(s));
        return s;
    }

    /**
     * A test that passed cleanly.
     */
    private static TestCaseEntity passed(String name, String classname, double time) {
        return TestCaseEntity.builder()
                .name(name).classname(classname).time(time)
                .flaky(false)
                .build();
    }

    /**
     * A test that failed with an assertion error.
     */
    private static TestCaseEntity failed(String name, String classname, double time,
                                         String message, String type, String details) {
        var tc = TestCaseEntity.builder()
                .name(name).classname(classname).time(time)
                .flaky(false)
                .build();
        tc.setFailureMessage(message);
        tc.setFailureType(type);
        tc.setFailureDetails(details);
        return tc;
    }

    /**
     * A test that errored due to infrastructure / runtime exception.
     */
    private static TestCaseEntity error(String name, String classname, double time,
                                        String message, String type, String details) {
        // structurally identical to failed — the suite's `errors` count distinguishes it
        return failed(name, classname, time, message, type, details);
    }

    /**
     * A flaky test — the run where it failed.
     */
    private static TestCaseEntity flakyFailed(String name, String classname, double time,
                                              String message, String type, String details) {
        var tc = TestCaseEntity.builder()
                .name(name).classname(classname).time(time)
                .flaky(true)
                .build();
        tc.setFailureMessage(message);
        tc.setFailureType(type);
        tc.setFailureDetails(details);
        return tc;
    }

    /**
     * A flaky test — the retry run where it passed.
     */
    private static TestCaseEntity flakyPassed(String name, String classname, double time) {
        return TestCaseEntity.builder()
                .name(name).classname(classname).time(time)
                .flaky(true)
                .build();
    }
}
