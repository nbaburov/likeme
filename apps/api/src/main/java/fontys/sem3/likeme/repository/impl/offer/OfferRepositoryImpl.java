package fontys.sem3.likeme.repository.impl.offer;

import org.springframework.stereotype.Repository;

import fontys.sem3.likeme.domain.offer.Offer;
import fontys.sem3.likeme.domain.offer.OfferType;
import fontys.sem3.likeme.repository.impl.mapper.OfferMapper;
import fontys.sem3.likeme.repository.interfaces.offer.OfferRepository;
import fontys.sem3.likeme.repository.jpa.offer.OfferRepositoryJPA;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OfferRepositoryImpl implements OfferRepository {
    private final OfferRepositoryJPA offerRepositoryJPA;

    @Override
    public Offer save(Offer offer) {
        return OfferMapper.mapToDomain(
                offerRepositoryJPA.save(
                        OfferMapper.mapToEntity(offer)));
    }

    @Override
    public Optional<Offer> findById(Long id) {
        return offerRepositoryJPA.findById(id)
                .map(OfferMapper::mapToDomain);
    }

    @Override
    public List<Offer> findByType(OfferType type) {
        return offerRepositoryJPA.findByType(type)
                .stream()
                .map(OfferMapper::mapToDomain)
                .toList();
    }

    @Override
    public List<Offer> findByCreatedInfluencerId(Long influencerId) {
        return offerRepositoryJPA.findByCreatedById(influencerId)
                .stream()
                .map(OfferMapper::mapToDomain)
                .toList();
    }

    @Override
    public List<Offer> findAll() {
        return offerRepositoryJPA.findAll()
                .stream()
                .map(OfferMapper::mapToDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        offerRepositoryJPA.deleteById(id);
    }
}
