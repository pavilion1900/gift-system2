package ru.clevertec.service;

import org.springframework.data.domain.Pageable;
import ru.clevertec.dto.OrderDto;

import java.util.List;

public interface OrderService {

    List<OrderDto> findAll(Pageable pageable);

    List<OrderDto> findAllByUserId(Pageable pageable, Integer userId);

    OrderDto findById(Integer id);

    Integer findLastSequenceValue();

    Integer setSequenceValue(Integer sequenceValue);

    OrderDto makeOrder(OrderDto orderDto);
}
