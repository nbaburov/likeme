// src/app/influencer/offers/[id]/page.tsx
"use client";
import { useParams, useRouter } from "next/navigation";
import { useOfferById, useOffers } from "@/hooks/useOffers";
import { usePublicFileUpload } from "@/hooks/useFileUpload";
import { useAuth } from "@/hooks/useAuth";
import { OfferInfo } from "@components/influencer/admin/offer/OfferInfo";
import { CustomSkeleton } from "@components/ui/CustomSkeleton";
import { UpdateOfferRequest } from "@/dto/OfferDTO";

export default function InfluencerOfferDetailPage() {
	const { id } = useParams();
	const router = useRouter();
	const { getUserId } = useAuth();
	const { data: offer, isLoading } = useOfferById(Number(id));
	const { update, delete: deleteOffer, isUpdating, isDeleting } = useOffers();
	const { upload, isUploading } = usePublicFileUpload();

	const influencerId = getUserId();

	// Redirect if not owner of the offer
	if (offer && offer.createdById !== influencerId) {
		router.push("/influencer/offers");
		return null;
	}

	const handleUpload = async (file: File) => {
		const result = await upload({ file, prefix: "offers" });
		return result.fileName;
	};

	const handleDelete = async () => {
		await deleteOffer(Number(id));
		router.push("/influencer/offers");
	};

	const handleUpdate = async (data: UpdateOfferRequest) => {
		await update({
			id: Number(id),
			offer: { ...data, updatedById: influencerId! },
		});
	};

	if (isLoading || !offer) {
		return <CustomSkeleton count={3} />;
	}

	return (
		<div className="p-6">
			<OfferInfo
				offer={offer}
				onDelete={handleDelete}
				onUpdate={handleUpdate}
				onBack={() => router.push("/influencer/offers")}
				isUpdating={isUpdating}
				isDeleting={isDeleting}
				handleUpload={handleUpload}
				isUploading={isUploading}
			/>
		</div>
	);
}
