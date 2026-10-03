package fontys.sem3.likeme.repository.interfaces.offer;

import java.util.List;
import java.util.Optional;

import fontys.sem3.likeme.domain.offer.Offer;
import fontys.sem3.likeme.domain.offer.OfferType;

public interface OfferRepository {
    Offer save(Offer offer);

    Optional<Offer> findById(Long id);

    List<Offer> findByType(OfferType type);

    List<Offer> findByCreatedInfluencerId(Long influencerId);

    List<Offer> findAll();

    void deleteById(Long id);
}
