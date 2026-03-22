package es.maryshopping.backend.shared_kernel.internal_api.reports.application;

import lombok.Builder;

@Builder
public record OrderConfirmedResult(
        EventResult result
) {
}
