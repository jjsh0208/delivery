package com.sparta.delivery.paymentTest;

import com.sparta.delivery.config.global.exception.custom.PaymentAlreadyCompletedException;
import com.sparta.delivery.domain.card.entity.Card;
import com.sparta.delivery.domain.card.repository.CardRepository;
import com.sparta.delivery.domain.order.entity.Order;
import com.sparta.delivery.domain.order.enums.OrderStatus;
import com.sparta.delivery.domain.order.repository.OrderRepository;
import com.sparta.delivery.domain.payment.dto.PaymentDto;
import com.sparta.delivery.domain.payment.dto.RegisterPaymentDto;
import com.sparta.delivery.domain.payment.dto.SearchDto;
import com.sparta.delivery.domain.payment.entity.Payment;
import com.sparta.delivery.domain.payment.repository.PaymentRepository;
import com.sparta.delivery.domain.payment.service.PaymentService;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    private User testUser;
    private UUID cardId;

    private Order testOrder;
    private UUID orderId;
    private Card testCard;
    private Payment testPayment;
    private UUID paymentId;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        testUser = User.builder()
                .userId(UUID.randomUUID())
                .username("testuser")
                .build();
        cardId = UUID.randomUUID();
        testCard = Card.builder()
                .cardId(cardId)
                .cardCompany("국민")
                .cardNumber("1234")
                .cardName("국민카드")
                .user(testUser)
                .build();
        orderId = UUID.randomUUID();
        testOrder = Order.builder()
                .orderId(orderId)
                .orderStatus(OrderStatus.PAYMENT_WAIT)
                .user(testUser)
                .build();
        paymentId = UUID.randomUUID();
        testPayment = Payment.builder()
                .paymentId(paymentId)
                .user(testUser)
                .card(testCard)
                .order(testOrder)
                .amount(10000)
                .build();
    }

    @Test
    @DisplayName("결제 성공")
    void testIsRegisterPaymentSuccess() {
        RegisterPaymentDto registerPaymentDto = new RegisterPaymentDto(cardId, 10000,orderId);

        when(cardRepository.findByCardIdAndDeletedAtIsNullAndUser_Username(cardId,"testuser")).thenReturn(Optional.of(testCard));
        when(orderRepository.findByOrderIdAndDeletedAtIsNull(orderId)).thenReturn(Optional.of(testOrder));
        when(userRepository.findByUsernameAndDeletedAtIsNull("testuser")).thenReturn(Optional.of(testUser));
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);
        assertDoesNotThrow(() -> paymentService.isRegisterPayment(registerPaymentDto, "testuser"));
    }


    @Test
    @DisplayName("결제 실패 : 이미 결제된 주문")
    void testIsRegisterPaymentFailAlreadyPaid() {
        testOrder.setOrderStatus(OrderStatus.PAYMENT_COMPLETE);
        RegisterPaymentDto registerPaymentDto = new RegisterPaymentDto(cardId, 10000,orderId);

        when(cardRepository.findByCardIdAndDeletedAtIsNullAndUser_Username(cardId,"testuser")).thenReturn(Optional.of(testCard));
        when(orderRepository.findByOrderIdAndDeletedAtIsNull(orderId)).thenReturn(Optional.of(testOrder));
        when(userRepository.findByUsernameAndDeletedAtIsNull("testuser")).thenReturn(Optional.of(testUser));

        PaymentAlreadyCompletedException exception = assertThrows(PaymentAlreadyCompletedException.class,
                () -> paymentService.isRegisterPayment(registerPaymentDto, "testuser"));

        assertEquals("이미 결제된 주문입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("결제 내역 조회 성공")
    void testGetPaymentSuccess() {
        when(userRepository.findByUsernameAndDeletedAtIsNull("testuser")).thenReturn(Optional.of(testUser));
        when(paymentRepository.findByPaymentIdAndDeletedAtIsNullAndUser_Username(paymentId,"testuser")).thenReturn(Optional.of(testPayment));
        when(orderRepository.findByOrderIdAndDeletedAtIsNull(orderId)).thenReturn(Optional.of(testOrder));

        PaymentDto result = paymentService.getPayment(paymentId,"testuser");

        assertNotNull(result);
        assertEquals(paymentId, result.getPaymentId());
    }

    @Test
    @DisplayName("결제 내역 조회 실패 : 결제 내역 없음")
    void testGetPaymentFailNotFound() {
        when(userRepository.findByUsernameAndDeletedAtIsNull("testuser")).thenReturn(Optional.of(testUser));
        when(paymentRepository.findByPaymentIdAndDeletedAtIsNullAndUser_Username(paymentId,"testuser")).thenReturn(Optional.empty());

        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> paymentService.getPayment(paymentId,"testuser"));

        assertEquals("결제 내역이 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("전체 결제 내역 조회")
    void testGetPaymentsSuccess() {
        when(userRepository.findByUsernameAndDeletedAtIsNull("testuser")).thenReturn(Optional.of(testUser));
        when(paymentRepository.findByUser_UsernameAndDeletedAtIsNull("testuser")).thenReturn(List.of(testPayment));
        when(paymentRepository.findByPaymentIdAndDeletedAtIsNullAndUser_Username(paymentId,"testuser")).thenReturn(Optional.of(testPayment));
        when(orderRepository.findByOrderIdAndDeletedAtIsNull(orderId)).thenReturn(Optional.of(testOrder));

        List<PaymentDto> result = paymentService.getPayments("testuser");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(testPayment.getPaymentId(), result.get(0).getPaymentId());
    }

    @Test
    @DisplayName("전체 결제 내역 조회 실패 : 결제 내역 없음")
    void testGetPaymentsFailNoPayments() {
        when(userRepository.findByUsernameAndDeletedAtIsNull("testuser")).thenReturn(Optional.of(testUser));
        when(paymentRepository.findByUser_UsernameAndDeletedAtIsNull("testuser")).thenReturn(List.of());
        when(paymentRepository.findByPaymentIdAndDeletedAtIsNullAndUser_Username(paymentId,"testuser")).thenReturn(Optional.of(testPayment));
        when(orderRepository.findByOrderIdAndDeletedAtIsNull(orderId)).thenReturn(Optional.of(testOrder));

        List<PaymentDto> result = paymentService.getPayments("testuser");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("결제 내역 검색 성공")
    void testSearchPaymentsSuccess() {
        SearchDto searchDto = new SearchDto();
        when(userRepository.findByUsernameAndDeletedAtIsNull("testuser")).thenReturn(Optional.of(testUser));
        when(paymentRepository.searchPayments(searchDto, "testuser")).thenReturn(List.of(testPayment));
        when(paymentRepository.searchPayments(null, "testuser")).thenReturn(List.of(testPayment));

        List<PaymentDto> searchResult = paymentService.searchPayments(searchDto, "testuser");
        List<PaymentDto> searchNullResult = paymentService.searchPayments(null, "testuser");

        assertNotNull(searchResult);
        assertFalse(searchResult.isEmpty());
        assertEquals(testPayment.getPaymentId(), searchResult.get(0).getPaymentId());

        assertNotNull(searchNullResult);
        assertFalse(searchNullResult.isEmpty());
        assertEquals(testPayment.getPaymentId(), searchNullResult.get(0).getPaymentId());
    }

    @Test
    @DisplayName("결제 내역 삭제 성공")
    void testDeletePaymentSuccess() {
        when(userRepository.findByUsernameAndDeletedAtIsNull("testuser")).thenReturn(Optional.of(testUser));
        when(paymentRepository.findByPaymentIdAndDeletedAtIsNullAndUser_Username(paymentId,"testuser")).thenReturn(Optional.of(testPayment));
        assertDoesNotThrow(() -> paymentService.deletePayment(paymentId, "testuser"));
    }

    @Test
    @DisplayName("결제 내역 삭제 실패 : 결제 정보 없음")
    void testDeletePaymentFailNotFound() {
        when(userRepository.findByUsernameAndDeletedAtIsNull("testuser")).thenReturn(Optional.of(testUser));
        when(paymentRepository.findByPaymentIdAndDeletedAtIsNullAndUser_Username(paymentId,"testuser")).thenReturn(Optional.empty());

        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> paymentService.deletePayment(paymentId, "testuser"));

        assertEquals("결제 정보가 존재하지 않습니다.", exception.getMessage());
    }


}
