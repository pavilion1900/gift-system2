package ru.clevertec.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.clevertec.dto.OrderDto;
import ru.clevertec.entity.Order;
import ru.clevertec.exception.EntityNotFoundException;
import ru.clevertec.mapper.OrderMapper;
import ru.clevertec.repository.OrderRepository;
import ru.clevertec.service.impl.OrderServiceImpl;
import ru.clevertec.testdata.CertificateUtil;
import ru.clevertec.testdata.OrderUtil;
import ru.clevertec.testdata.UserUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CertificateService certificateService;

    @Mock
    private UserService userService;
    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void checkFindAll() {
        doReturn(new PageImpl<>(OrderUtil.orders()))
                .when(orderRepository).findAll(OrderUtil.pageable());
        Mockito.doReturn(OrderUtil.orderDtoWithId1())
                .when(orderMapper).toDto(OrderUtil.orderWithId1());
        Mockito.doReturn(OrderUtil.orderDtoWithId2())
                .when(orderMapper).toDto(OrderUtil.orderWithId2());
        Mockito.doReturn(OrderUtil.orderDtoWithId3())
                .when(orderMapper).toDto(OrderUtil.orderWithId3());
        Mockito.doReturn(OrderUtil.orderDtoWithId4())
                .when(orderMapper).toDto(OrderUtil.orderWithId4());
        List<OrderDto> actual = orderService.findAll(OrderUtil.pageable());
        Assertions.assertEquals(OrderUtil.dtoOrders(), actual);
        verify(orderRepository).findAll(OrderUtil.pageable());
        verify(orderMapper, times(4)).toDto(any(Order.class));
    }

    @Test
    void checkFindAllByUserId() {
        doReturn(Arrays.asList(OrderUtil.orderWithId1(), OrderUtil.orderWithId4()))
                .when(orderRepository).findAllByUserId(OrderUtil.pageable(), 1);
        Mockito.doReturn(OrderUtil.orderDtoWithId1())
                .when(orderMapper).toDto(OrderUtil.orderWithId1());
        Mockito.doReturn(OrderUtil.orderDtoWithId4())
                .when(orderMapper).toDto(OrderUtil.orderWithId4());
        List<OrderDto> actual = orderService.findAllByUserId(OrderUtil.pageable(), 1);
        List<OrderDto> expected = Arrays.asList(OrderUtil.orderDtoWithId1(), OrderUtil.orderDtoWithId4());
        assertEquals(expected, actual);
        verify(orderRepository).findAllByUserId(OrderUtil.pageable(), 1);
        verify(orderMapper, times(2)).toDto(any(Order.class));
    }

    @Test
    void checkFindByIdIfOrderIdExist() {
        doReturn(Optional.of(OrderUtil.orderWithId1()))
                .when(orderRepository).findById(1);
        Mockito.doReturn(OrderUtil.orderDtoWithId1())
                .when(orderMapper).toDto(OrderUtil.orderWithId1());
        OrderDto actual = orderService.findById(1);
        Assertions.assertEquals(OrderUtil.orderDtoWithId1(), actual);
        verify(orderRepository).findById(1);
        verify(orderMapper).toDto(OrderUtil.orderWithId1());
    }

    @Test
    void throwExceptionIfOrderIdNotExist() {
        doReturn(Optional.empty())
                .when(orderRepository).findById(1);
        assertThrows(EntityNotFoundException.class, () -> orderService.findById(1));
    }

    @Test
    void checkMakeOrderIfCertificateIdAndOrderIdExist() {
        doReturn(CertificateUtil.certificateDtoWithId3())
                .when(certificateService).findById(OrderUtil.orderDtoForMakeOrder().getCertificateId());
        Mockito.doReturn(UserUtil.userDtoWithId1())
                .when(userService).findById(OrderUtil.orderDtoForMakeOrder().getUserId());
        Mockito.doReturn(OrderUtil.orderWithoutId())
                .when(orderMapper).toEntity(OrderUtil.orderDtoForMakeOrderWithCost(), CertificateUtil.certificateDtoWithId3(), UserUtil.userDtoWithId1());
        Mockito.doReturn(OrderUtil.orderWithId1())
                .when(orderRepository).save(OrderUtil.orderWithoutId());
        Mockito.doReturn(OrderUtil.orderDtoWithId1())
                .when(orderMapper).toDto(OrderUtil.orderWithId1());
        OrderDto actual = orderService.makeOrder(OrderUtil.orderDtoForMakeOrder());
        Assertions.assertEquals(OrderUtil.orderDtoWithId1(), actual);
        verify(orderRepository).save(OrderUtil.orderWithoutId());
        verify(orderMapper).toDto(OrderUtil.orderWithId1());
    }

    @Test
    void throwExceptionWhenMakeOrderIfCertificateIdNotExist() {
        doThrow(EntityNotFoundException.class)
                .when(certificateService).findById(OrderUtil.orderDtoForMakeOrder().getCertificateId());
        assertThrows(EntityNotFoundException.class, () -> orderService.makeOrder(OrderUtil.orderDtoForMakeOrder()));
    }

    @Test
    void throwExceptionWhenMakeOrderIfUserIdNotExist() {
        doReturn(CertificateUtil.certificateDtoWithId3())
                .when(certificateService).findById(OrderUtil.orderDtoForMakeOrder().getCertificateId());
        doThrow(EntityNotFoundException.class)
                .when(userService).findById(OrderUtil.orderDtoForMakeOrder().getUserId());
        assertThrows(EntityNotFoundException.class, () -> orderService.makeOrder(OrderUtil.orderDtoForMakeOrder()));
    }
}
