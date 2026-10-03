package fontys.sem3.likeme.business.interfaces.offer;

import java.util.List;

import fontys.sem3.likeme.domain.offer.Offer;
import fontys.sem3.likeme.domain.offer.OfferType;

public interface OfferService {
    Offer createOffer(Offer offer);
    
    Offer updateOffer(Offer offer);
    
    void deleteOffer(Long id);
    
    Offer getOffer(Long id);
    
    List<Offer> getOffersByType(OfferType type);
    
    List<Offer> getOffersByInfluencer(Long influencerId);
    
    List<Offer> getAllOffers();
} 