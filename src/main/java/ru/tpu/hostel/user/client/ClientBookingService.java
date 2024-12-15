package ru.tpu.hostel.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.tpu.hostel.user.dto.response.ActiveEventDto;
import ru.tpu.hostel.user.enums.BookingStatus;

import java.util.List;
import java.util.UUID;

/**
 * @deprecated функционал перенесен в API Gateway
 */
@Deprecated
@Component
@FeignClient(name = "booking-bookingservice", url = "http://bookingservice:8080")
public interface ClientBookingService {

    @GetMapping("bookings/get/all/{status}/{userId}")
    List<ActiveEventDto> getAllByStatus(@PathVariable BookingStatus status, @PathVariable UUID userId);

}
