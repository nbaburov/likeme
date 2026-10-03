package fontys.sem3.likeme.business.impl.offer;

import java.util.List;

import org.springframework.stereotype.Service;

import fontys.sem3.likeme.business.exception.offer.InvalidOfferDataException;
import fontys.sem3.likeme.business.exception.offer.OfferNotFoundException;
import fontys.sem3.likeme.business.exception.offer.OfferServiceException;
import fontys.sem3.likeme.business.interfaces.offer.OfferService;
import fontys.sem3.likeme.business.interfaces.utils.FileStorageService;
import fontys.sem3.likeme.business.validator.offer.OfferValidator;
import fontys.sem3.likeme.domain.offer.Offer;
import fontys.sem3.likeme.domain.offer.OfferType;
import fontys.sem3.likeme.repository.interfaces.offer.OfferRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OfferServiceImpl implements OfferService {
    private final OfferRepository offerRepository;
    private final OfferValidator offerValidator;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public Offer createOffer(Offer offer) {
        if (offer == null) {
            throw new InvalidOfferDataException("Offer cannot be null");
        }

        try {
            offerValidator.validateCreate(offer);
            return offerRepository.save(offer);
        } catch (InvalidOfferDataException e) {
            throw e;
        } catch (Exception e) {
            throw new OfferServiceException("Failed to create offer", e);
        }
    }

    @Override
    @Transactional
    public Offer updateOffer(Offer offer) {
        if (offer == null || offer.getId() == null) {
            throw new InvalidOfferDataException("Offer and ID cannot be null");
        }

        try {
            getOffer(offer.getId());
            return offerRepository.save(offer);
        } catch (OfferNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new OfferServiceException("Failed to update offer with id: " + offer.getId(), e);
        }
    }

    @Override
    public void deleteOffer(Long id) {
        try {
            Offer offer = getOffer(id);
            deleteOfferEntity(id);
            deleteOfferCoverPhoto(offer);
        } catch (OfferNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new OfferServiceException("Failed to delete offer: " + id, e);
        }
    }

    private void deleteOfferEntity(Long id) {
        offerRepository.deleteById(id);
    }

    private void deleteOfferCoverPhoto(Offer offer) {
        if (offer.getCoverPhotoPath() != null && !offer.getCoverPhotoPath().isEmpty()) {
            try {
                fileStorageService.deleteFile(offer.getCoverPhotoPath());
            } catch (Exception e) {
                throw new OfferServiceException("Offer deleted but failed to delete cover photo");
            }
        }
    }

    @Override
    public Offer getOffer(Long id) {
        if (id == null) {
            throw new InvalidOfferDataException("Offer ID cannot be null");
        }
        try {
            return offerRepository.findById(id)
                    .orElseThrow(() -> new OfferNotFoundException("Offer not found with id: " + id));
        } catch (OfferNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new OfferServiceException("Error retrieving offer with id: " + id, e);
        }
    }

    @Override
    public List<Offer> getOffersByType(OfferType type) {
        try {
            return offerRepository.findByType(type);
        } catch (Exception e) {
            throw new OfferServiceException("Failed to retrieve offers by type: " + type, e);
        }
    }

    @Override
    public List<Offer> getOffersByInfluencer(Long influencerId) {
        try {
            return offerRepository.findByCreatedInfluencerId(influencerId);
        } catch (Exception e) {
            throw new OfferServiceException("Failed to retrieve offers by influencer: " + influencerId, e);
        }
    }

    @Override
    public List<Offer> getAllOffers() {
        try {
            return offerRepository.findAll();
        } catch (Exception e) {
            throw new OfferServiceException("Failed to retrieve all offers", e);
        }
    }
}