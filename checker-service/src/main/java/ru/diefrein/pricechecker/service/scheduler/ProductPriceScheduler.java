package ru.diefrein.pricechecker.service.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.diefrein.pricechecker.configuration.parameters.SchedulerParameterProvider;
import ru.diefrein.pricechecker.service.ProductService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ProductPriceScheduler {

    private static final Logger log = LoggerFactory.getLogger(ProductPriceScheduler.class);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final ProductService productService;

    public ProductPriceScheduler(ProductService productService) {
        this.productService = productService;
        this.scheduler.scheduleAtFixedRate(
                this::runProductPriceUpdateJob,
                SchedulerParameterProvider.PRODUCT_PRICE_JOB_INITIAL_DELAY_SECONDS,
                SchedulerParameterProvider.PRODUCT_PRICE_JOB_RATE_SECONDS,
                TimeUnit.SECONDS
        );
    }

    private void runProductPriceUpdateJob() {
        try {
            log.info("Check for product price update: start");
            productService.checkForUpdates();
            log.info("Check for product price update: finish");
        } catch (Exception e) {
            log.error("Exception in scheduled task", e);
        }
    }
}
