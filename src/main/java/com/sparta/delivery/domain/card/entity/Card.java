package com.sparta.delivery.domain.card.entity;

import com.sparta.delivery.domain.common.Timestamped;
import com.sparta.delivery.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
@Table(name = "p_card")
public class Card extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID cardId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String cardCompany;

    private String cardName;

    private String cardNumber;

}
