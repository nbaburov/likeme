package fontys.sem3.likeme.repository.jpa.offer;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fontys.sem3.likeme.domain.offer.OfferType;
import fontys.sem3.likeme.repository.impl.entity.offer.OfferEntity;

public interface OfferRepositoryJPA extends JpaRepository<OfferEntity, Long> {
    List<OfferEntity> findByType(OfferType type);

    List<OfferEntity> findByCreatedById(Long influencerId);
}
