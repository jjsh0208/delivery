package com.sparta.delivery.paymentTest;

import com.sparta.delivery.domain.card.entity.Card;
import com.sparta.delivery.domain.card.repository.CardRepository;
import com.sparta.delivery.domain.order.entity.Order;
import com.sparta.delivery.domain.order.enums.OrderStatus;
import com.sparta.delivery.domain.order.repository.OrderRepository;
import com.sparta.delivery.domain.payment.dto.RegisterPaymentDto;
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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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

        when(cardRepository.findByCardIdAndDeletedAtIsNull(cardId)).thenReturn(Optional.of(testCard));
        when(orderRepository.findByOrderIdAndDeletedAtIsNull(orderId)).thenReturn(Optional.of(testOrder));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);

        String result = paymentService.isRegisterPayment(registerPaymentDto, "testuser");

        assertEquals("결제 성공", result);
    }


}
