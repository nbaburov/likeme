package fontys.sem3.likeme.business.impl.offer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fontys.sem3.likeme.business.exception.offer.InvalidOfferDataException;
import fontys.sem3.likeme.business.exception.offer.OfferNotFoundException;
import fontys.sem3.likeme.business.exception.offer.OfferServiceException;
import fontys.sem3.likeme.business.interfaces.utils.FileStorageService;
import fontys.sem3.likeme.business.validator.offer.OfferValidator;
import fontys.sem3.likeme.domain.offer.Offer;
import fontys.sem3.likeme.domain.offer.OfferType;
import fontys.sem3.likeme.domain.user.influencer.Influencer;
import fontys.sem3.likeme.repository.interfaces.offer.OfferRepository;

@ExtendWith(MockitoExtension.class)
class OfferServiceImplTest {
    @Mock
    private OfferRepository offerRepository;
    @Mock
    private OfferValidator offerValidator;
    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private OfferServiceImpl offerService;

    private Offer testOffer;
    private Influencer testInfluencer;
    private static final Long OFFER_ID = 1L;
    private static final Long INFLUENCER_ID = 1L;
    private static final String TITLE = "Test Offer";
    private static final String DESCRIPTION = "Test Description";
    private static final String COVER_PHOTO_PATH = "path/to/photo.jpg";
    private static final Double PRICE = 100.0;
    private static final Date CREATED_ON = new Date();
    private static final Date UPDATED_ON = new Date();

    @BeforeEach
    void setUp() {
        testInfluencer = Influencer.builder()
                .id(INFLUENCER_ID)
                .build();

        testOffer = Offer.builder()
                .id(OFFER_ID)
                .title(TITLE)
                .description(DESCRIPTION)
                .coverPhotoPath(COVER_PHOTO_PATH)
                .type(OfferType.LIKE)
                .isActive(true)
                .createdOn(CREATED_ON)
                .updatedOn(UPDATED_ON)
                .createdBy(testInfluencer)
                .updatedBy(testInfluencer)
                .price(PRICE)
                .build();
    }

    @Test
    void createOffer_Success() {
        Offer offerToCreate = Offer.builder()
                .title(TITLE)
                .description(DESCRIPTION)
                .type(OfferType.LIKE)
                .price(PRICE)
                .build();

        when(offerRepository.save(offerToCreate)).thenReturn(testOffer);

        Offer actualOffer = offerService.createOffer(offerToCreate);

        assertEquals(testOffer.getId(), actualOffer.getId());
        assertEquals(testOffer.getTitle(), actualOffer.getTitle());
        assertEquals(testOffer.getDescription(), actualOffer.getDescription());
        assertEquals(testOffer.getType(), actualOffer.getType());
        assertEquals(testOffer.getPrice(), actualOffer.getPrice());

        verify(offerValidator).validateCreate(offerToCreate);
        verify(offerRepository).save(offerToCreate);
    }

    @Test
    void createOffer_WhenOfferIsNull_ThrowsInvalidOfferDataException() {
        InvalidOfferDataException exception = assertThrows(
                InvalidOfferDataException.class,
                () -> offerService.createOffer(null));

        assertEquals("Offer cannot be null", exception.getMessage());
        verify(offerValidator, never()).validateCreate(null);
        verify(offerRepository, never()).save(null);
    }

    @Test
    void updateOffer_Success() {
        Offer offerToUpdate = Offer.builder()
                .id(OFFER_ID)
                .title("Updated Title")
                .description("Updated Description")
                .type(OfferType.COMMENT)
                .price(150.0)
                .build();

        when(offerRepository.findById(OFFER_ID)).thenReturn(Optional.of(testOffer));
        when(offerRepository.save(offerToUpdate)).thenReturn(offerToUpdate);

        Offer actualOffer = offerService.updateOffer(offerToUpdate);

        assertEquals(offerToUpdate.getId(), actualOffer.getId());
        assertEquals(offerToUpdate.getTitle(), actualOffer.getTitle());
        assertEquals(offerToUpdate.getDescription(), actualOffer.getDescription());
        assertEquals(offerToUpdate.getType(), actualOffer.getType());
        assertEquals(offerToUpdate.getPrice(), actualOffer.getPrice());

        verify(offerRepository).findById(OFFER_ID);
        verify(offerRepository).save(offerToUpdate);
    }

    @Test
    void deleteOffer_Success() {
        when(offerRepository.findById(OFFER_ID)).thenReturn(Optional.of(testOffer));

        offerService.deleteOffer(OFFER_ID);

        verify(offerRepository).findById(OFFER_ID);
        verify(offerRepository).deleteById(OFFER_ID);
        verify(fileStorageService).deleteFile(COVER_PHOTO_PATH);
    }

    @Test
    void deleteOffer_WithoutCoverPhoto_Success() {
        Offer offerWithoutPhoto = Offer.builder()
                .id(OFFER_ID)
                .title(TITLE)
                .coverPhotoPath(null)
                .build();

        when(offerRepository.findById(OFFER_ID)).thenReturn(Optional.of(offerWithoutPhoto));

        offerService.deleteOffer(OFFER_ID);

        verify(offerRepository).findById(OFFER_ID);
        verify(offerRepository).deleteById(OFFER_ID);
        verify(fileStorageService, never()).deleteFile(any());
    }

    @Test
    void getOffer_Success() {
        when(offerRepository.findById(OFFER_ID)).thenReturn(Optional.of(testOffer));

        Offer actualOffer = offerService.getOffer(OFFER_ID);

        assertEquals(testOffer.getId(), actualOffer.getId());
        assertEquals(testOffer.getTitle(), actualOffer.getTitle());
        assertEquals(testOffer.getDescription(), actualOffer.getDescription());
        assertEquals(testOffer.getType(), actualOffer.getType());
        assertEquals(testOffer.getPrice(), actualOffer.getPrice());

        verify(offerRepository).findById(OFFER_ID);
    }

    @Test
    void getOffersByType_Success() {
        List<Offer> expectedOffers = List.of(testOffer);
        when(offerRepository.findByType(OfferType.LIKE)).thenReturn(expectedOffers);

        List<Offer> actualOffers = offerService.getOffersByType(OfferType.LIKE);

        assertEquals(expectedOffers.size(), actualOffers.size());
        assertEquals(expectedOffers.get(0), actualOffers.get(0));
        verify(offerRepository).findByType(OfferType.LIKE);
    }

    @Test
    void getOffersByInfluencer_Success() {
        List<Offer> expectedOffers = List.of(testOffer);
        when(offerRepository.findByCreatedInfluencerId(INFLUENCER_ID)).thenReturn(expectedOffers);

        List<Offer> actualOffers = offerService.getOffersByInfluencer(INFLUENCER_ID);

        assertEquals(expectedOffers.size(), actualOffers.size());
        assertEquals(expectedOffers.get(0), actualOffers.get(0));
        verify(offerRepository).findByCreatedInfluencerId(INFLUENCER_ID);
    }

    @Test
    void getAllOffers_Success() {
        List<Offer> expectedOffers = List.of(testOffer);
        when(offerRepository.findAll()).thenReturn(expectedOffers);

        List<Offer> actualOffers = offerService.getAllOffers();

        assertEquals(expectedOffers.size(), actualOffers.size());
        assertEquals(expectedOffers.get(0), actualOffers.get(0));
        verify(offerRepository).findAll();
    }

    @Test
    void deleteOffer_WhenFileStorageFailsButOfferDeleted() {
        when(offerRepository.findById(OFFER_ID)).thenReturn(Optional.of(testOffer));
        doThrow(new RuntimeException("File delete failed"))
                .when(fileStorageService).deleteFile(COVER_PHOTO_PATH);

        OfferServiceException exception = assertThrows(
                OfferServiceException.class,
                () -> offerService.deleteOffer(OFFER_ID));

        assertEquals("Failed to delete offer: " + OFFER_ID, exception.getMessage());
        verify(offerRepository).findById(OFFER_ID);
        verify(offerRepository).deleteById(OFFER_ID);
        verify(fileStorageService).deleteFile(COVER_PHOTO_PATH);
    }

    @Test
    void getOffer_WhenIdIsNull_ThrowsInvalidOfferDataException() {
        InvalidOfferDataException exception = assertThrows(
                InvalidOfferDataException.class,
                () -> offerService.getOffer(null));

        assertEquals("Offer ID cannot be null", exception.getMessage());
        verify(offerRepository, never()).findById(any());
    }

    @Test
    void getOffer_WhenNotFound_ThrowsOfferNotFoundException() {
        when(offerRepository.findById(OFFER_ID)).thenReturn(Optional.empty());

        OfferNotFoundException exception = assertThrows(
                OfferNotFoundException.class,
                () -> offerService.getOffer(OFFER_ID));

        assertEquals("Offer not found with id: " + OFFER_ID, exception.getMessage());
        verify(offerRepository).findById(OFFER_ID);
    }
}