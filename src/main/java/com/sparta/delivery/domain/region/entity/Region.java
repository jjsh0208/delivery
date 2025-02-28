package com.sparta.delivery.domain.region.entity;

import com.sparta.delivery.domain.common.Timestamped;
import com.sparta.delivery.domain.store.entity.Stores;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "p_region")
public class Region extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID regionId;

    @Column(nullable = false)//(단위 : 도)
    private String province;
    @Column(nullable = false)//(단위 : 시)
    private String city;
    @Column(nullable = false)//(단위 : 동)
    private String locality;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Stores stores;
}
