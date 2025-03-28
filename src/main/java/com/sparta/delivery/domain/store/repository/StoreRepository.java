package com.sparta.delivery.domain.store.repository;

import com.sparta.delivery.domain.store.entity.Stores;
import com.sparta.delivery.domain.store.enums.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.parameters.P;

import javax.swing.plaf.synth.Region;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<Stores, UUID>, StoreRepositoryCustom {

    List<Stores> findByCategoryAndDeletedAtIsNull(Category category, String sortBy, String order);

    boolean existsByStoreIdAndDeletedAtIsNull(UUID storeId);

    List<Stores> findByCategory(Category category);

    Optional<Stores> findByStoreIdAndDeletedAtIsNull(UUID id);// 가게 단건검색

    Page<Stores> findAllByDeletedAtIsNull(Pageable pageable);

    Page<Stores> findAll(Pageable pageable);

}
